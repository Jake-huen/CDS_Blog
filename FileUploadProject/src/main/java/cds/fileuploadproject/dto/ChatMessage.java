package cds.fileuploadproject.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ChatMessage {
    private String user;
    private String content;
}
