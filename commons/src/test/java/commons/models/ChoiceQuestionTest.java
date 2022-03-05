package commons.models;

import commons.entities.Activity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ChoiceQuestionTest {
    private static final long POSITIVE = 40;
    private static final long TOTAL = 900;

    private ChoiceQuestion question;
    private List<Activity> activities;

    @BeforeEach
    public void startup() {
        activities = new ArrayList<>();
        activities.add(new Activity("q1", POSITIVE, "src"));
        activities.add(new Activity("q2", POSITIVE + POSITIVE, "src"));
        activities.add(new Activity("q3", POSITIVE + POSITIVE + POSITIVE, "src"));
        activities.add(new Activity("q4", POSITIVE + POSITIVE + POSITIVE + POSITIVE, "src"));
        question = new ChoiceQuestion(activities);
    }

    @Test
    public void constructorConstructsValidObject() {
        assertNotNull(question);
    }

    @Test
    public void getActivitiesReturnsActivities() {
        assertEquals(activities, question.getActivities());
    }

    @Test
    public void setActivitiesSetsActivities() {
        List<Activity> newActivities = new ArrayList<>();
        newActivities.add(new Activity("q1", POSITIVE, "src"));
        newActivities.add(new Activity("q2", POSITIVE, "src"));
        newActivities.add(new Activity("q3", POSITIVE, "src"));
        newActivities.add(new Activity("q4", POSITIVE, "src"));
        question.setActivities(newActivities);
        assertEquals(newActivities, question.getActivities());
    }

    @Test
    public void getComparedActivityReturnsComparedActivity() {
        assertEquals("q2", question.getComparedActivity().title);
    }

    @Test
    public void getAnswerReturnsMinConsumptionActivity() {
        assertEquals("q1", question.getAnswer().title);
    }

    @Test
    public void getUserAnswerReturnsUserAnswer() {
        assertNull(question.getUserAnswer());
    }

    @Test
    public void setUserAnswerSetsUserAnswer() {
        question.setUserAnswer(new Answer(question.getActivities().get(0)), POSITIVE);
        assertEquals("q1", ((Activity) question.getUserAnswer().getAnswer()).title);
    }

    @Test
    public void getSecondsReturnsSeconds() {
        question.setUserAnswer(new Answer(question.getActivities().get(0)), POSITIVE);
        assertEquals(POSITIVE, question.getSeconds());
    }

    @Test
    public void getPointsCalculatesPoints() {
        question.setUserAnswer(new Answer(question.getActivities().get(0)), 1);
        assertEquals(TOTAL, question.getPoints());
    }

    @Test
    public void toStringReturnsStringRepresentation() {
        assertEquals(
                String.format("ChoiceQuestion{activities=%s}",
                        question.getActivities().toString()), question.toString()
        );
    }

    @Test
    public void equalsReturnsTrueIfSame() {
        assertEquals(question, question);
    }

    @Test
    public void hashCodeReturnsSameForSameFields() {
        assertEquals(question.hashCode(), question.hashCode());
    }
}
