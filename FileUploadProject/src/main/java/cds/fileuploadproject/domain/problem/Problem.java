package cds.fileuploadproject.domain.problem;

import cds.fileuploadproject.domain.uploadFile.UploadFile;
import lombok.Data;

import java.util.List;

@Data
public class Problem {

    private Long id;
    private String problemName;
    private UploadFile attachFile;
    private List<UploadFile> imageFiles;
}
