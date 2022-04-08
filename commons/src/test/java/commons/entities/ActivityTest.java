package commons.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
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
    public void constructorConstructsWithImagePath() {
        activity = new Activity("q1", CONSUMPTION, "src", "image");
        assertNotNull(activity);
    }

    @Test
    public void constructorConstructsWithId() {
        activity = new Activity("q1", "title" , CONSUMPTION, "image", "image");
        assertNotNull(activity);
    }

    @Test
    public void getIdentifierReturnsIdentifier() {
        assertNull(activity.getIdentifier());
    }

    @Test
    public void setIdentifierSetsIdentifier() {
        activity.setIdentifier("ident");
        assertEquals("ident", activity.getIdentifier());
    }

    @Test
    public void getConsumptionReturnsConsumption() {
        assertEquals(CONSUMPTION, activity.getConsumption());
    }

    @Test
    public void setSourceSetsSource() {
        activity.setSource("source");
        assertEquals("source", activity.getSource());
    }

    @Test
    public void setConsumptionSetsConsumption() {
        activity.setConsumption(CONSUMPTION + CONSUMPTION);
        assertEquals(CONSUMPTION + CONSUMPTION, activity.getConsumption());
    }

    @Test
    public void getImagePathReturnsImagePath() {
        activity = new Activity("q1", CONSUMPTION, "src", "image");
        assertEquals("image", activity.getImagePath());
    }

    @Test
    public void setImagePathSetsImagePath() {
        activity.setImagePath("imagepath");
        assertEquals("imagepath", activity.getImagePath());
    }

    @Test
    public void getSourceReturnsSource() {
        assertEquals("src", activity.getSource());
    }

    @Test
    public void getIdReturnsId() {
        assertEquals(0L, activity.getId());
    }

    @Test
    public void setIdSetsId() {
        activity.setId(CONSUMPTION);
        assertEquals(CONSUMPTION, activity.getId());
    }

    @Test
    public void getTitleReturnsTitle() {
        assertEquals("q1", activity.getTitle());
    }

    @Test
    public void setTitleSetsTitle() {
        activity.setTitle("q2");
        assertEquals("q2", activity.getTitle());
    }

    @Test
    public void equalsReturnsTrueForSame() {
        assertEquals(activity, activity);
    }

    @Test
    public void equalsReturnsTrueForEqualFields() {
        activity = new Activity("q1", CONSUMPTION, "src", "image");
        Activity expected = new Activity("q1", CONSUMPTION, "src", "image");
        assertEquals(expected, activity);
    }

    @Test
    public void equalsReturnsFalseForDifferentFields() {
        activity = new Activity("q1", CONSUMPTION, "src", "image");
        Activity expected = new Activity("q2", CONSUMPTION, "src", "image");
        assertNotEquals(expected, activity);
    }

    @Test
    public void equalsReturnsFalseForDifferentType() {
        assertNotEquals("str", activity);
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
