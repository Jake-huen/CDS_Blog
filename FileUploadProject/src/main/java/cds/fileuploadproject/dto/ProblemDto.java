package cds.fileuploadproject.dto;

import lombok.Builder;
import lombok.Data;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ProblemDto {
    private Long id;
    private String problemName;
    private URL problemURL;
    private String userName;
    private LocalDateTime createdTime;
    private int updatedTime;
}
