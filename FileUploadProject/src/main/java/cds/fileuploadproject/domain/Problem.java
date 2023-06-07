package cds.fileuploadproject.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.net.URL;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Problem {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String file_name;

    private URL file_url;

    @OneToOne
    private Member member;

    private int createdTime;

    private int updatedTime;
}
