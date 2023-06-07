package cds.fileuploadproject.service.problem;

import cds.fileuploadproject.controller.problem.ProblemForm;
import cds.fileuploadproject.domain.Member;
import cds.fileuploadproject.domain.Problem;
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

    public List<ProblemForm> upload(ProblemForm form, String dirName) throws IOException {
        System.out.println("ProblemService.upload first");
        File uploadFile = convert(form.getAttachFile()).orElseThrow(() -> new IllegalArgumentException("파일 전환 실패"));
        upload(uploadFile, form.getProblemName() + "번 문제 제출 파일들", dirName);

        for (MultipartFile multipartFile : form.getImageFiles()) {
            File uploadImage = convert(multipartFile).orElseThrow(() -> new IllegalArgumentException("이미지 파일 전환 실패"));
            upload(uploadImage, dirName + "님의 파일 목록입니다.", dirName);
        }


        List<Problem> problems = problemRepository.findAll();
        List<ProblemForm> problemForms = new ArrayList<>();
        problems.stream().forEach(problem -> {
            ProblemForm problemForm = ProblemForm.builder()
                    .problemId(problem.getId())
                    .problemName(problem.getFile_name())
                    .build();
            problemForms.add(problemForm);
        });
        return problemForms;
    }

    // S3로 파일 업로드하기
    private String upload(File uploadFile, String dirName, String userName) {
        System.out.println("ProblemService.upload second");
        String fileName = dirName + "/" + uploadFile.getName(); // S3에 저장된 파일 이름
        String uploadImageUrl = putS3(uploadFile, fileName); // s3로 업로드
        URL url = amazonS3Client.getUrl(bucket, fileName);
        Optional<Member> member = memberRepository.findByUserName(userName);
        Problem problem = Problem.builder()
                .file_name(fileName)
                .file_url(url)
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
        System.out.println("ProblemService.putS3");
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

    // s3의 모든 파일 가져오기
    public void saveFilesToProblemRepository() {
        List<Bucket> buckets = amazonS3Client.listBuckets();
        for (Bucket bucket : buckets) {
            System.out.println("Bucket Name: " + bucket.getName());
        }

        // 버킷의 모든 파일들 가져오기
        ObjectListing objectListing = amazonS3Client.listObjects(bucket);
        List<S3ObjectSummary> objectSummaries = objectListing.getObjectSummaries();
        List<S3Object> s3Objects = new ArrayList<>();

        while (objectListing.isTruncated()) {
            objectListing = amazonS3Client.listNextBatchOfObjects(objectListing);
            objectSummaries.addAll(objectListing.getObjectSummaries());
        }

        for (S3ObjectSummary objectSummary : objectSummaries) {
            String key = objectSummary.getKey();
            S3Object s3Object = amazonS3Client.getObject(bucket, key);
            s3Objects.add(s3Object);
        }
        // 버킷의 파일들을 problem에 저장하기
        // [S3Object [key=1번 문제 제출 파일들/1차 최종보고서.docx,bucket=cds-project-bucket], S3Object [key=2번 문제 제출 파일들/2018 수능성적표.pdf,bucket=cds-project-bucket], S3Object [key=3번 문제 제출 파일들/2023_졸업프로젝트1_요구사항 분석서 형식.pdf,bucket=cds-project-bucket], S3Object [key=tae77777@naver.com님의 파일 목록입니다./IMG_8418.heic,bucket=cds-project-bucket], S3Object [key=tae77777@naver.com님의 파일 목록입니다./IMG_8422.heic,bucket=cds-project-bucket], S3Object [key=tae77777@naver.com님의 파일 목록입니다./IMG_8437.HEIC,bucket=cds-project-bucket], S3Object [key=tae77777@naver.com님의 파일 목록입니다./IMG_8441.HEIC,bucket=cds-project-bucket], S3Object [key=tae77777@naver.com님의 파일 목록입니다./IMG_8448.HEIC,bucket=cds-project-bucket]]
    }
}
