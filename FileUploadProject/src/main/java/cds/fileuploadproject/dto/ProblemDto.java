package cds.fileuploadproject.dto;

import lombok.Data;

import java.util.List;

@Data
public class ProblemDto {
    private Long id;
    private String problemName;
    private UploadFileDto attachFile; // 업로드하는 파일
    private List<UploadFileDto> imageFiles; // 업로드하는 이미지파일들
}
