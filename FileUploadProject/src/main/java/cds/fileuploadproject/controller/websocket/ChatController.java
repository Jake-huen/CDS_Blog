package cds.fileuploadproject.controller.websocket;

import cds.fileuploadproject.dto.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/sendMessage")
    @SendTo("/topic/messages")
    public String sendMessage(String message) {
        return message;
    }

    // 연결했을때 메시지 보내기
    @MessageMapping("/hello")
    @SendTo("/topic/hello")
    public String loginMessage(String message) {
        return message + "님이 로그인하였습니다.";
    }

    // 연결이 끊겼을때 메시지 보내기
    @MessageMapping("/bye")
    @SendTo("/topic/bye")
    public String logoutMessage(String message) {
        return message + "님이 로그아웃하였습니다.";
    }

    @MessageMapping("/edit")
    @SendTo("/topic/edit")
    public String uploadMessage(String message) {
        return message + "파일을 업로드 하였습니다.";
    }
}
