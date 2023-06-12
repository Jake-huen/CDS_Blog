package cds.fileuploadproject.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class MemberDto {
    private Long id;
    private String userName; // 사용자 이름
    private String password; // 비밀번호
}
