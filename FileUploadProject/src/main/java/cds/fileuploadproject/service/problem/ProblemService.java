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
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

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

    @Value("${cloud.aws.s3.bucket}")
    public String bucket;

    public void upload(ProblemForm form, String dirName) throws IOException {
        File uploadFile = convert(form.getAttachFile()).orElseThrow(() -> new IllegalArgumentException("파일 전환 실패"));
        upload(uploadFile, form.getProblemName() + "번 문제 제출 파일들", dirName);

        for (MultipartFile multipartFile : form.getImageFiles()) {
            File uploadImage = convert(multipartFile).orElseThrow(() -> new IllegalArgumentException("이미지 파일 전환 실패"));
            upload(uploadImage, dirName + "님의 파일 목록입니다.", dirName);
        }
    }

    // S3로 파일 업로드하기
    private String upload(File uploadFile, String dirName, String userName) {
        String fileName = dirName + "/" + uploadFile.getName(); // S3에 저장된 파일 이름
        String uploadImageUrl = putS3(uploadFile, fileName); // s3로 업로드
        URL url = amazonS3Client.getUrl(bucket, fileName);
        Optional<Member> member = memberRepository.findByUserName(userName);
        Problem problem = Problem.builder()
                .fileName(fileName)
                .fileUrl(url)
                .createdTime(0)
                .updatedTime(0)
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
    public List<ProblemDto> getAllProblems() {
        List<Problem> problems = problemRepository.findAll();
        List<ProblemDto> problemDtos = new ArrayList<>();
        problems.stream().forEach(problem -> {
            ProblemDto problemDto = ProblemDto.builder()
                    .problemName(problem.getFileName())
                    .problemURL(problem.getFileUrl())
                    .userName(problem.getMember().getUserName())
                    .createdTime(problem.getCreatedTime())
                    .updatedTime(problem.getUpdatedTime())
                    .build();
            problemDtos.add(problemDto);
        });
        return problemDtos;
    }

    // 파일 수정
    public void updateProblem(String problemName, MultipartFile multipartFile, String dirName) throws IOException {
        Problem oldProblem = problemRepository.findByFileName(problemName);
        File editedFile = convert(multipartFile).orElseThrow(() -> new IllegalArgumentException("이미지 파일 전환 실패"));
        upload(editedFile, dirName + "님의 파일 목록입니다.", dirName);
        problemRepository.delete(oldProblem);
        log.info("잘 업데이트되었습니다");
    }

    // 파일 삭제
    public void deleteProblem(String problemName){
        Problem problem = problemRepository.findByFileName(problemName);
        problemRepository.delete(problem);
        log.info("잘 삭제되었습니다.");
    }
}
