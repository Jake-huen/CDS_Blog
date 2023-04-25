package cds.fileuploadproject.controller.websocket;

import org.apache.tomcat.util.json.JSONParser;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class GreetingController {

    @MessageMapping("/hello")
    @SendTo("/topic/hello")
    public String loginMessage(String message) throws InterruptedException {
        return message + "님이 로그인하였습니다.";
    }

    @MessageMapping("/bye")
    @SendTo("/topic/bye")
    public String logoutMessage(String message) throws InterruptedException {
        return message + "님이 로그아웃하였습니다.";
    }
}
