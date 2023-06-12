package cds.fileuploadproject.controller.problem;

import cds.fileuploadproject.controller.session.SessionConst;
import cds.fileuploadproject.domain.Problem;
import cds.fileuploadproject.dto.MemberDto;
import cds.fileuploadproject.dto.ProblemDto;
import cds.fileuploadproject.service.problem.ProblemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
    public String newProblem(@ModelAttribute ProblemForm form) {
        return "problem/problem-form";
    }

    @PostMapping("/problems/new")
    public String saveProblem(@ModelAttribute ProblemForm form, HttpServletRequest request) throws IOException {
        MemberDto memberDto = (MemberDto) request.getSession().getAttribute(SessionConst.LOGIN_MEMBER);
        problemService.upload(form, memberDto.getUserName());
        return "redirect:/problem/problem-view";
    }

    @GetMapping("/problem/problem-view")
    public String showProblemView(Model model, HttpServletRequest request) {
        MemberDto memberDto = (MemberDto) request.getSession().getAttribute(SessionConst.LOGIN_MEMBER);
        List<ProblemDto> problems = problemService.getAllProblems(memberDto.getUserName());
        model.addAttribute("problems", problems);
        return "problem/problem-view";
    }

    @PostMapping("/problem/problem-view")
    public String problemView() {
        return "problem/problem-view";
    }

    // 수정하기
    @PostMapping("/problem/edit")
    public String handleEditProblem(@RequestParam("problemName") String problemName,
                                    @RequestParam("attachFile") MultipartFile attachFile, HttpServletRequest request) throws IOException {
        MemberDto memberDto = (MemberDto) request.getSession().getAttribute(SessionConst.LOGIN_MEMBER);
        problemService.updateProblem(problemName, attachFile, memberDto.getUserName());
        return "redirect:/problem/problem-view";
    }

    // 삭제하기
    @PostMapping("/problem/delete")
    public String handleDeleteProblem(@RequestParam("problemName") String problemName){
        problemService.deleteProblem(problemName);
        return "redirect:/problem/problem-view";
    }
}
