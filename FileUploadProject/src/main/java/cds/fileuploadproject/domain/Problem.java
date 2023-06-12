package cds.fileuploadproject.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.net.URL;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Problem {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String fileName;

    @Column(length = 2000)
    private URL fileUrl;

    @OneToOne
    private Member member;

    private LocalDateTime createdTime;

    private int updatedTime;
}
