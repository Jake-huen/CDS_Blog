package cds.fileuploadproject.service.member;

import cds.fileuploadproject.domain.Member;
import cds.fileuploadproject.dto.MemberDto;
import cds.fileuploadproject.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberDto login(String userName, String password) {
        Optional<Member> member = memberRepository.findByUserName(userName);
        MemberDto memberDto = MemberDto.builder()
                .id(member.get().getId())
                .userName(member.get().getUserName())
                .password(member.get().getPassword())
                .build();
        if (memberDto.getPassword().equals(password)) {
            return memberDto;
        } else {
            return null;
        }
    }

    public MemberDto signUp(String userName, String password) {
        Optional<Member> check = memberRepository.findByUserName(userName);
        if (check.isPresent()) {
            throw new RuntimeException("이미 존재하는 사용자이름입니다");
        }
        Member member = Member.builder()
                .userName(userName)
                .password(password)
                .build();
        memberRepository.save(member);
        MemberDto memberDto = MemberDto.builder()
                .id(member.getId())
                .userName(member.getUserName())
                .password(member.getPassword())
                .build();
        return memberDto;
    }
}
