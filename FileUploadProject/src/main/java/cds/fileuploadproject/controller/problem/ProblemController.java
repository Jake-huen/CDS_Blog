package cds.fileuploadproject.controller.problem;

import cds.fileuploadproject.dto.ProblemDto;
import cds.fileuploadproject.dto.UploadFileDto;
import cds.fileuploadproject.service.file.FileService;
import cds.fileuploadproject.service.file.S3Uploader;
import cds.fileuploadproject.service.problem.ProblemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ProblemController {

    private final ProblemService problemService;
    private final FileService fileService;
    private final S3Uploader s3Uploader;

    @GetMapping("/problems")
    public String problemHome(){
        return "problem/index";
    }

    @GetMapping("/problems/new")
    public String newItem(@ModelAttribute ProblemForm form) {
        return "problem/problem-form";
    }

    @PostMapping("/problems/new")
    public String saveItem(@ModelAttribute ProblemForm form, RedirectAttributes redirectAttributes) throws IOException {
        UploadFileDto attachFile = fileService.storeFile(form.getAttachFile());
        List<UploadFileDto> storeImageFiles = fileService.storeFiles(form.getImageFiles());

        // 데이터베이스에 저장
        ProblemDto problemDto = new ProblemDto();
        problemDto.setProblemName(form.getProblemName());
        problemDto.setAttachFile(attachFile);
        problemDto.setImageFiles(storeImageFiles);
        problemService.save(problemDto);

        redirectAttributes.addAttribute("problemId", problemDto.getId());

        return "redirect:/problems/{problemId}";
    }

    @GetMapping("/problems/{id}")
    public String items(@PathVariable Long id, Model model) {
        ProblemDto problemDto = problemService.findById(id);
        model.addAttribute("problem", problemDto);
        return "problem/problem-view";
    }

    @ResponseBody
    @GetMapping("/images/{filename}")
    public Resource downloadImage(@PathVariable String filename) throws MalformedURLException {
        return new UrlResource("file: " + fileService.getFullPath(filename));
    }

    @GetMapping("/attach/{problemId}")
    public ResponseEntity<Resource> downloadAttach(@PathVariable Long problemId) throws MalformedURLException {
        ProblemDto problemDto = problemService.findById(problemId);
        String storeFileName = problemDto.getAttachFile().getStoreFileName();
        String uploadFileName = problemDto.getAttachFile().getUploadFileName();

        UrlResource urlResource = new UrlResource("file: " + fileService.getFullPath(storeFileName));

        log.info("uploadFileName={}", uploadFileName);

        String encodedUploadFileName = UriUtils.encode(uploadFileName, StandardCharsets.UTF_8);
        String contentDisposition = "attachment; filename=\"" + encodedUploadFileName + "\"";
        log.info("문제가 잘 제출되었습니다.");
        System.out.println("문제가 잘 제출되었습니다.");

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .body(urlResource);
    }
}
