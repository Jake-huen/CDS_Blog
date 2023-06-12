package cds.fileuploadproject.service.problem;

import cds.fileuploadproject.controller.problem.ProblemForm;
import cds.fileuploadproject.domain.Member;
import cds.fileuploadproject.domain.Problem;
import cds.fileuploadproject.dto.MemberDto;
import cds.fileuploadproject.dto.ProblemDto;
import cds.fileuploadproject.repository.MemberRepository;
import cds.fileuploadproject.repository.ProblemRepository;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProblemService {

    private final AmazonS3Client amazonS3Client;
    private final ProblemRepository problemRepository;
    private final MemberRepository memberRepository;

    // 소켓 통신을 위함
    private StompSession stompSession;
    List<Transport> transports = new ArrayList<>();
    @Value("${cloud.aws.s3.bucket}")
    public String bucket;

    public void upload(ProblemForm form, String dirName) throws IOException {
        File uploadFile = convert(form.getAttachFile()).orElseThrow(() -> new IllegalArgumentException("파일 전환 실패"));
        upload(uploadFile, form.getProblemName() + "번 문제", dirName, 0);

        for (MultipartFile multipartFile : form.getImageFiles()) {
            File uploadImage = convert(multipartFile).orElseThrow(() -> new IllegalArgumentException("이미지 파일 전환 실패"));
            upload(uploadImage, dirName + "님의 제출한 문제이미지", dirName, 0);
        }
    }

    // S3로 파일 업로드하기
    private String upload(File uploadFile, String dirName, String userName, int editCount) {
        String fileName = dirName + "/" + uploadFile.getName(); // S3에 저장된 파일 이름
        String uploadImageUrl = putS3(uploadFile, fileName); // s3로 업로드
        URL url = amazonS3Client.getUrl(bucket, fileName);
        Optional<Member> member = memberRepository.findByUserName(userName);
        Problem problem = Problem.builder()
                .fileName(fileName)
                .fileUrl(url)
                .createdTime(0)
                .updatedTime(editCount)
                .member(member.get())
                .build();
        problemRepository.save(problem);
        removeNewFile(uploadFile);
        return uploadImageUrl;
    }

    // S3로 업로드
    private String putS3(File uploadFile, String fileName) {
        amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, uploadFile).withCannedAcl(CannedAccessControlList.PublicRead));
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    // 로컬에 저장된 이미지 지우기
    private void removeNewFile(File targetFile) {
        if (targetFile.delete()) {
            log.info("File delete success");
            return;
        }
        log.info("File delete fail");
    }

    private Optional<File> convert(MultipartFile multipartFile) throws IOException {
        File convertFile = new File(System.getProperty("user.dir") + "/" + multipartFile.getOriginalFilename());
        // 바로 위에서 지정한 경로에 File이 생성됨 (경로가 잘못되었다면 생성 불가능)
        if (convertFile.createNewFile()) {
            try (FileOutputStream fos = new FileOutputStream(convertFile)) { // FileOutputStream 데이터를 파일에 바이트 스트림으로 저장하기 위함
                fos.write(multipartFile.getBytes());
            }
            return Optional.of(convertFile);
        }
        return Optional.empty();
    }

    // 파일 보여주기
    public List<ProblemDto> getAllProblems(String userName) {
        List<Problem> problems = problemRepository.findAll();
        List<ProblemDto> problemDtos = new ArrayList<>();
        problems.stream().forEach(problem -> {
            if(problem.getMember().getUserName().equals(userName) || problem.getFileName().contains("번 문제")) {
                ProblemDto problemDto = ProblemDto.builder()
                        .problemName(problem.getFileName())
                        .problemURL(problem.getFileUrl())
                        .userName(problem.getMember().getUserName())
                        .createdTime(problem.getCreatedTime())
                        .updatedTime(problem.getUpdatedTime())
                        .build();
                problemDtos.add(problemDto);
            }
        });
        return problemDtos;
    }

    // 파일 수정
    public void updateProblem(String problemName, MultipartFile multipartFile, String dirName) throws IOException {
        Problem oldProblem = problemRepository.findByFileName(problemName);
        int editCount = oldProblem.getUpdatedTime() + 1; // 한번 업데이트 할 때마다 업데이트 횟수 기록
        String oldProblemName = oldProblem.getFileName();
        File editedFile = convert(multipartFile).orElseThrow(() -> new IllegalArgumentException("이미지 파일 전환 실패"));
        upload(editedFile, dirName + "님의 파일 목록입니다.", dirName, editCount);
        problemRepository.delete(oldProblem);
        log.info("잘 업데이트되었습니다");

        // 소켓 통신
        sendToSocketUpdate(dirName, oldProblemName, editedFile);
    }

    // 파일 삭제
    public void deleteProblem(String problemName) {
        Problem problem = problemRepository.findByFileName(problemName);
        String userName = problem.getMember().getUserName();
        problemRepository.delete(problem);
        log.info("잘 삭제되었습니다.");
        // 소켓 통신
        sendToSocketDelete(problemName, userName);

    }

    private void sendToSocketUpdate(String dirName, String oldProblemName, File editedFile) {
        List<Transport> transports = new ArrayList<>();
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        SockJsClient sockJsClient = new SockJsClient(transports);
        WebSocketStompClient stompClient = new WebSocketStompClient(sockJsClient);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        String url = "ws://localhost:8081/chat"; // 로컬 WebScoket URL
        // String url = "ws://cdsfileupload.ap-northeast-2.elasticbeanstalk.com/chat"; // WebSocket 서버 URL
        stompClient.connect(url, new StompSessionHandlerAdapter() {
            @Override
            public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                stompSession = session;
                sendChatMessage(editedFile.getName(), oldProblemName, dirName);
            }
        });
    }

    private void sendToSocketDelete(String problemName, String userName) {
        List<Transport> transports = new ArrayList<>();
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        SockJsClient sockJsClient = new SockJsClient(transports);
        WebSocketStompClient stompClient = new WebSocketStompClient(sockJsClient);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        String url = "ws://localhost:8081/chat"; // 로컬 WebScoket URL
        // String url = "ws://cdsfileupload.ap-northeast-2.elasticbeanstalk.com/chat"; // WebSocket 서버 URL
        stompClient.connect(url, new StompSessionHandlerAdapter() {
            @Override
            public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                stompSession = session;
                sendDeleteMessage(problemName, userName);
            }
        });
    }

    public void sendChatMessage(String filename, String oldFileName, String userName) {
        if (stompSession != null && stompSession.isConnected()) {
            stompSession.send("/app/sendMessage", userName+"님이 " + oldFileName+"문제를 " + filename + "문제로 업데이트하였습니다.");
        }
    }

    public void sendDeleteMessage(String filename, String userName) {
        if (stompSession != null && stompSession.isConnected()) {
            stompSession.send("/app/sendMessage", userName+"님이 " + filename + "문제를 삭제하였습니다.");
        }
    }
}
