package server.api;

import commons.entities.MultiplayerUser;
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
        return emoji;
    }

    @MessageMapping("/halfTime/{gameId}")
    @SendTo("/topic/halfTime/{gameId}")
    public MultiplayerUser halfTime (MultiplayerUser user, @DestinationVariable String gameId ) {
        return user;
    }

}
