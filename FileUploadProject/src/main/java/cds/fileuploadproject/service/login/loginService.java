package cds.fileuploadproject.service.login;

import cds.fileuploadproject.dto.MemberDto;
import cds.fileuploadproject.repository.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class loginService {

    private final MemberService memberService;

    public MemberDto login(String loginId, String password) {
        Optional<MemberDto> findMemberOptional = memberService.findByLoginId(loginId);
        MemberDto memberDto = findMemberOptional.get();
        if (memberDto.getPassword().equals(password)) {
            return memberDto;
        } else {
            return null;
        }
    }
}
