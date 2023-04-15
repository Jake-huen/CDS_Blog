package cds.fileuploadproject.controller.login;

import cds.fileuploadproject.controller.session.SessionConst;
import cds.fileuploadproject.domain.login.LoginService;
import cds.fileuploadproject.domain.member.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Slf4j
@Controller
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @GetMapping("/login")
    public String loginForm(@ModelAttribute("loginForm") LoginForm loginForm) {
        return "login/loginForm";
    }

    @PostMapping("/login")
    public String loginV3(@Validated @ModelAttribute LoginForm loginForm, BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return "login/loginForm";
        }

        Member loginMember = loginService.login(loginForm.getLoginId(), loginForm.getPassword());

        if (loginMember == null) {
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            return "login/loginForm";
        }

        // 로그인 성공 처리
        // 세션이 있으면 있는 세션 반환, 없으면 신규 세션 생성
        HttpSession session = request.getSession();
        // 세션에 로그인 회원 정보를 보관
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);
        return "redirect:/";
    }

    @PostMapping("/logout")
    public String logoutV3(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/";
    }
}


// @PostMapping("/login")
//    public String login(@Validated @ModelAttribute LoginForm loginForm, BindingResult bindingResult, HttpServletResponse response) {
//        if(bindingResult.hasErrors()){
//            return "login/loginForm";
//        }
//
//        Member loginMember = loginService.login(loginForm.getLoginId(), loginForm.getPassword());
//        if (loginMember == null) {
//            bindingResult.reject("loginFail","아이디 또는 비밀번호가 맞지 않습니다.");
//            return "login/loginForm";
//        }
//
//        // 로그인 성공 처리
//        Cookie idCookie = new Cookie("memberId", String.valueOf(loginMember.getId()));
//        response.addCookie(idCookie);
//        return "redirect:/";
//    }

//    @PostMapping("/login")
//    public String loginV2(@Validated @ModelAttribute LoginForm loginForm, BindingResult bindingResult, HttpServletResponse response) {
//        if (bindingResult.hasErrors()) {
//            return "login/loginForm";
//        }
//
//        Member loginMember = loginService.login(loginForm.getLoginId(), loginForm.getPassword());
//        if (loginMember == null) {
//            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
//            return "login/loginForm";
//        }
//
//        // 로그인 성공 처리
//        // 세션 관리자를 통해 세션을 생성하고, 회원 데이터를 보관
//        sessionManager.createSession(loginMember, response);
//        return "redirect:/";
//    }


// 로그아웃
//    @PostMapping("/logout")
//    public String logout(HttpServletResponse response) {
//        expireCookie(response,"memberId");
//        return "redirect:/";
//    }

//    private static void expireCookie(HttpServletResponse response, String memberId) {
//        Cookie cookie = new Cookie(memberId, null);
//        cookie.setMaxAge(0);
//        response.addCookie(cookie);
//    }
