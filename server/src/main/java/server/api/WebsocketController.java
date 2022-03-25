package server.api;

import commons.models.Emoji;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import commons.entities.User;

@Controller
public class WebsocketController {

    @MessageMapping("/emoji/{gameId}")
    @SendTo("/topic/emoji/{gameId}")
    public Emoji addEmoji(Emoji emoji, @DestinationVariable String gameId){
        return emoji;
    }

    @MessageMapping("/halftime/{gameId}")
    @SendTo("/topic/halftime{gameId}")
    public User halfTime ( User user, @DestinationVariable String gameId ) {
        return user;
    }

}
