package cds.fileuploadproject.service.file;

import cds.fileuploadproject.dto.UploadFileDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class FileService {

    @Value("${file.dir}")
    private String fileDir;

    public String getFullPath(String filename) {
        return fileDir + filename;
    }

    public List<UploadFileDto> storeFiles(List<MultipartFile> multipartFiles) throws IOException {
        List<UploadFileDto> storeFileResult = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            if (!multipartFile.isEmpty()) {
                UploadFileDto uploadFileDto = storeFile(multipartFile);
                storeFileResult.add(uploadFileDto);
            }
        }
        return storeFileResult;
    }

    public UploadFileDto storeFile(MultipartFile multipartFile) throws IOException {
        if (multipartFile.isEmpty()) {
            return null;
        }
        String originalFilename = multipartFile.getOriginalFilename();
        multipartFile.transferTo(new File(getFullPath(originalFilename)));
        return new UploadFileDto(originalFilename, originalFilename);
    }
}
