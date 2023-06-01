package cds.fileuploadproject.dto;

import lombok.Data;

@Data
public class UploadFileDto {

    private String uploadFileName;
    private String storeFileName;

    public UploadFileDto(String uploadFileName, String storeFileName) {
        this.uploadFileName = uploadFileName;
        this.storeFileName = storeFileName;
    }
}
