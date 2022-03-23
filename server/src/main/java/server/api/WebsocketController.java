package server.api;

import commons.models.Emoji;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebsocketController {

    @MessageMapping("/emoji/{gameId}")
    @SendTo("/topic/emoji/{gameId}")
    public Emoji addEmoji(Emoji emoji, @DestinationVariable String gameId){
        System.out.println("Received emoji: " + emoji);
        return emoji;
    }

}
