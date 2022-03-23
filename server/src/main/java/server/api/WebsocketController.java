package server.api;

import commons.models.Emoji;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebsocketController {

    @MessageMapping("/emoji")
    @SendTo("/topic/emoji")
    public Emoji addEmoji(Emoji emoji){
        System.out.println("Received emoji: " + emoji);
        return emoji;
    }
}
