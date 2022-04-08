package commons.entities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MultiplayerUserTest {

    @Test
    public void checkConstructor() {
        var u = new MultiplayerUser("f");
        assertEquals("f", u.username);
    }

    @Test
    public void resetScoreResetsScore() {
        var u = new MultiplayerUser("f");
        u.resetScore();
        assertEquals(0, u.unansweredQuestions);
    }

    @Test
    public void equalsTrue() {
        var a = new MultiplayerUser("a");
        var b = new MultiplayerUser("a");
        assertEquals(a, b);
    }

    @Test
    public void hashCodeEquals() {
        var a = new MultiplayerUser("a");
        var b = new MultiplayerUser("a");
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    public void notEquals() {
        var a = new MultiplayerUser("a");
        var b = new MultiplayerUser("b");
        assertNotEquals(a, b);
    }

    @Test
    public void hashCodeNotEquals() {
        var a = new MultiplayerUser("a");
        var b = new MultiplayerUser("b");
        assertNotEquals(a.hashCode(), b.hashCode());
    }

    @Test
    public void hasToString() {
        var actual = new MultiplayerUser("a").toString();
        assertTrue(actual.contains(User.class.getSimpleName()));
    }
}
