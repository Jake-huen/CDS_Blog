package cds.fileuploadproject.file;

import cds.fileuploadproject.domain.uploadFile.UploadFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class FileStore {

    @Value("${file.dir}")
    private String fileDir;

    public String getFullPath(String filename) {
        return fileDir + filename;
    }

    public List<UploadFile> storeFiles(List<MultipartFile> multipartFiles) throws IOException {
        List<UploadFile> storeFileResult = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            if (!multipartFile.isEmpty()) {
                UploadFile uploadFile = storeFile(multipartFile);
                storeFileResult.add(uploadFile);
            }
        }
        return storeFileResult;
    }

    public UploadFile storeFile(MultipartFile multipartFile) throws IOException {
        if (multipartFile.isEmpty()) {
            return null;
        }
        String originalFilename = multipartFile.getOriginalFilename();
        // String storeFileName = createStoreFileName(originalFilename); // 서버에 저장하는 파일이름 나중을 위해서 그냥 같은 이름으로 저장하기.
        multipartFile.transferTo(new File(getFullPath(originalFilename)));
        return new UploadFile(originalFilename, originalFilename);
    }

//    private String createStoreFileName(String originalFilename) {
//        // 서버에 저장하는 파일명
//        String uuid = UUID.randomUUID().toString();
//        String ext = extractExt(originalFilename);
//        return uuid + "." + ext;
//    }

    private String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    }

}