package cds.fileuploadproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FileUploadProjectApplication {

    static {
        System.setProperty("com.amazonaws.sdk.disableEc2Metadata", "true");
    }

    public static void main(String[] args) {
        SpringApplication.run(FileUploadProjectApplication.class, args);
    }

}
