package cds.fileuploadproject.controller.problem;

import cds.fileuploadproject.dto.ProblemDto;
import cds.fileuploadproject.service.file.S3Uploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/spring")
@RequiredArgsConstructor
public class SpringUploadController {

    private final S3Uploader s3Uploader;

    @Value("${file.dir}")
    private String fileDir;

    @GetMapping("/upload") // 파일 업로드 사이트
    public String newFile() {
        return "problem/upload-form";
    }

    @PostMapping("/upload")
    public String saveFile(@RequestParam("file") List<MultipartFile> multipartFiles, HttpServletRequest request) throws IOException {
        s3Uploader.upload(multipartFiles, request.getSession().getAttribute("userName").toString());
        return "problem/upload-form";
    }
}
