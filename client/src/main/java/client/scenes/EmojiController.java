package client.scenes;

import commons.models.Emoji;

public interface EmojiController {

    /**
     * Displays the emoji to the given controller
     * emoji views
     * @param emoji the emoji to display
     */
    void displayEmoji(Emoji emoji);
}
