package cds.fileuploadproject.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Member {
    @Id @GeneratedValue
    private Long id;

    private String userName;

    private String password;

    public Member(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }
}
