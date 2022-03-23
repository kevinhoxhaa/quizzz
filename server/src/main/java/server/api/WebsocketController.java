package server.api;

import commons.models.Emoji;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;

public class WebsocketController {

    @MessageMapping("/emoji")
    @SendTo("/topic/emoji")
    public Emoji addEmoji(Emoji emoji){
        // processing
        return emoji;
    }
}
