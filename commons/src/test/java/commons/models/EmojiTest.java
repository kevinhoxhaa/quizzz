package commons.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class EmojiTest {
    private Emoji emoji;

    @BeforeEach
    public void setup() {
        emoji = new Emoji("image", "user");
    }

    @Test
    public void constructorConstructsValidObject() {
        assertNotNull(emoji);
    }

    @Test
    public void getUsernameReturnsUsername() {
        assertEquals("user", emoji.getUsername());
    }

    @Test
    public void setUsernameSetsUsername() {
        emoji.setUsername("other");
        assertEquals("other", emoji.getUsername());
    }

    @Test
    public void getImageNameReturnsImageName() {
        assertEquals("image", emoji.getImageName());
    }

    @Test
    public void setImagePathSetsImagePath() {
        emoji.setImageName("other");
        assertEquals("other", emoji.getImageName());
    }

    @Test
    public void equalsReturnsTrueForSameObjects() {
        assertEquals(emoji, emoji);
    }

    @Test
    public void toStringReturnsStringRepresentation() {
        assertEquals("Emoji{imageName='image', username='user'}", emoji.toString());
    }

    @Test
    public void hashCodeSameForSameObjects() {
        assertEquals(emoji.hashCode(), emoji.hashCode());
    }
}
