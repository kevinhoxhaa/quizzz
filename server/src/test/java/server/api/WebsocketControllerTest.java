package server.api;

import commons.entities.MultiplayerUser;
import commons.models.Emoji;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class WebsocketControllerTest{

    private WebsocketController websocketController;

    @BeforeEach
    public void setup(){
        websocketController = new WebsocketController();
    }

    @Test
    public void addEmojiReturnsEmoji(){
        Emoji emoji = new Emoji("image", "user");
        assertEquals(emoji, websocketController.addEmoji(emoji, "42"));
    }

    @Test
    public void halfTimeReturnsMultiplayerUser() {
        MultiplayerUser user = new MultiplayerUser("urs");
        assertEquals(user, websocketController.halfTime(user, "game"));
    }
}
