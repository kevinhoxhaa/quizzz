package commons.entities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SoloUserTest {

    @Test
    public void checkConstructor() {
        var u = new SoloUser("f");
        assertEquals("f", u.username);
    }

    @Test
    public void equalsTrue() {
        var a = new SoloUser("a");
        var b = new SoloUser("a");
        assertEquals(a, b);
    }

    @Test
    public void hashCodeEquals() {
        var a = new SoloUser("a");
        var b = new SoloUser("a");
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    public void notEquals() {
        var a = new SoloUser("a");
        var b = new SoloUser("b");
        assertNotEquals(a, b);
    }

    @Test
    public void hashCodeNotEquals() {
        var a = new SoloUser("a");
        var b = new SoloUser("b");
        assertNotEquals(a.hashCode(), b.hashCode());
    }

    @Test
    public void hasToString() {
        var actual = new SoloUser("a").toString();
        assertTrue(actual.contains(User.class.getSimpleName()));
    }
}
