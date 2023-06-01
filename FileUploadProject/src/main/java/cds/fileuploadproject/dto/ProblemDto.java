package cds.fileuploadproject.dto;

import lombok.Data;

import java.util.List;

@Data
public class ProblemDto {

    private Long id;
    private String problemName;
    private UploadFileDto attachFile;
    private List<UploadFileDto> imageFiles;
}
