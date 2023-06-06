package cds.fileuploadproject.controller.problem;

import cds.fileuploadproject.service.problem.ProblemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class SpringUploadController {

    private final ProblemService problemService;

    @GetMapping("/problems/new")
    public String newProblem(@ModelAttribute ProblemForm form){
        return "problem/problem-form";
    }

    @PostMapping("/problems/new")
    public String saveProblem(@ModelAttribute ProblemForm form, HttpServletRequest request, RedirectAttributes redirectAttributes) throws IOException {
        problemService.upload(form, "주소주소");
        return "problem/index";
    }
}
