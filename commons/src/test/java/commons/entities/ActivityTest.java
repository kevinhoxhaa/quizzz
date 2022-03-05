package commons.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ActivityTest {
    private static final long CONSUMPTION = 1000;

    private Activity activity;

    @BeforeEach
    public void startup() {
        activity = new Activity("q1", CONSUMPTION, "src");
    }

    @Test
    public void constructorConstructsValidObject() {
        assertNotNull(activity);
    }

    @Test
    public void equalsReturnsTrueForSame() {
        assertEquals(activity, activity);
    }

    @Test
    public void toStringReturnsStringRepresentation() {
        assertTrue(activity.toString().contains("consumption=1000"));
    }

    @Test
    public void hashCodeReturnsSameForEqualObjects() {
        assertEquals(activity.hashCode(), activity.hashCode());
    }
}
