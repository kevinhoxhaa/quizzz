package client.scenes;

import commons.models.Emoji;

public interface EmojiController {
    /**
     * Displays the emoji to the given controller
     * emoji views
     * @param emoji the emoji to display
     */
    void displayEmoji(Emoji emoji);

    /**
     * Hides the emoji from the given controller
     * emoji views
     */
    void hideEmoji();
}
