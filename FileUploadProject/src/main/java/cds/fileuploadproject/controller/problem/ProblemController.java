package cds.fileuploadproject.controller.problem;

import cds.fileuploadproject.domain.problem.Problem;
import cds.fileuploadproject.domain.problem.ProblemRepository;
import cds.fileuploadproject.domain.uploadFile.UploadFile;
import cds.fileuploadproject.file.FileStore;
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

    private final ProblemRepository problemRepository;
    private final FileStore fileStore;

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
        UploadFile attachFile = fileStore.storeFile(form.getAttachFile());
        List<UploadFile> storeImageFiles = fileStore.storeFiles(form.getImageFiles());

        // 데이터베이스에 저장
        Problem problem = new Problem();
        problem.setProblemName(form.getProblemName());
        problem.setAttachFile(attachFile);
        problem.setImageFiles(storeImageFiles);
        problemRepository.save(problem);

        redirectAttributes.addAttribute("problemId", problem.getId());

        return "redirect:/problems/{problemId}";
    }

    @GetMapping("/problems/{id}")
    public String items(@PathVariable Long id, Model model) {
        Problem problem = problemRepository.findById(id);
        model.addAttribute("problem", problem);
        return "problem/problem-view";
    }

    @ResponseBody
    @GetMapping("/images/{filename}")
    public Resource downloadImage(@PathVariable String filename) throws MalformedURLException {
        return new UrlResource("file: " + fileStore.getFullPath(filename));
    }

    @GetMapping("/attach/{problemId}")
    public ResponseEntity<Resource> downloadAttach(@PathVariable Long problemId) throws MalformedURLException {
        Problem problem = problemRepository.findById(problemId);
        String storeFileName = problem.getAttachFile().getStoreFileName();
        String uploadFileName = problem.getAttachFile().getUploadFileName();

        UrlResource urlResource = new UrlResource("file: " + fileStore.getFullPath(storeFileName));

        log.info("uploadFileName={}", uploadFileName);

        String encodedUploadFileName = UriUtils.encode(uploadFileName, StandardCharsets.UTF_8);
        String contentDisposition = "attachment; filename=\"" + encodedUploadFileName + "\"";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .body(urlResource);
    }
}
