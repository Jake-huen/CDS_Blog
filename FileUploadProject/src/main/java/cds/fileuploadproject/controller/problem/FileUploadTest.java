package cds.fileuploadproject.controller.problem;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class FileUploadTest {

    private final S3Uploader s3Uploader;

    @PostMapping("/images/test")
    public String upload(@RequestParam("image") MultipartFile multipartFile) throws IOException {
        s3Uploader.upload(multipartFile,"static");
        return "파일 업로드 성공";
    }
}
