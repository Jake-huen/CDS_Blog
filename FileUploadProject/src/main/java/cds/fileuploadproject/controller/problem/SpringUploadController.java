package cds.fileuploadproject.controller.problem;

import cds.fileuploadproject.service.problem.ProblemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/spring")
@RequiredArgsConstructor
public class SpringUploadController {

    private final ProblemService problemService;

    @GetMapping("/upload") // 파일 업로드 사이트
    public String newFile() {
        return "problem/upload-form";
    }

    @PostMapping("/upload")
    public String saveFile(@RequestParam("imageFiles") List<MultipartFile> multipartFiles, HttpServletRequest request) throws IOException {
        problemService.upload(multipartFiles, request.getSession().getAttribute("userName").toString());
        return "problem/upload-form";
    }
}
