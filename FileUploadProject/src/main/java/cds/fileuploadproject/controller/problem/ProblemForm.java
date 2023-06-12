package cds.fileuploadproject.controller.problem;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@Builder
public class ProblemForm {

    private Long problemId;
    private String problemName;
    private MultipartFile attachFile;
    private List<MultipartFile> imageFiles;
}
