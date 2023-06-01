package cds.fileuploadproject.dto;

import lombok.Data;

@Data
public class MemberDto {
    private Long id;
    private String loginId; // 로그인 ID
    private String name; // 사용자 이름
    private String password; // 비밀번호
}
