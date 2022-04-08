package commons.models;

import commons.entities.Activity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ChoiceQuestionTest {
    private static final long POSITIVE = 40;
    private static final long TOTAL = 900;

    private ChoiceQuestion question;
    private List<Activity> activities;
    private Activity firstActivity;

    @BeforeEach
    public void startup() {
        activities = new ArrayList<>();
        firstActivity = new Activity("q1", POSITIVE, "src");
        activities.add(firstActivity);
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
        assertEquals(new Answer((Activity) null), question.getUserAnswer());
    }

    @Test
    public void setUserAnswerSetsUserAnswer() {
        question.setUserAnswer(new Answer(question.getActivities().get(0)), POSITIVE);
        assertEquals("q1", ((Activity) question.getUserAnswer().generateAnswer()).title);
    }

    @Test
    public void getSecondsReturnsSeconds() {
        question.setUserAnswer(new Answer(firstActivity), POSITIVE);
        assertEquals(POSITIVE, question.getSeconds());
    }

    @Test
    public void getPointsCalculatesPoints() {
        question.setUserAnswer(new Answer(question.getActivities().get(0)), 1);
        assertEquals(TOTAL, question.calculatePoints());
    }

    @Test
    public void toStringReturnsStringRepresentation() {
        assertEquals(
                String.format("ChoiceQuestion{activities=%s}",
                        question.getActivities().toString()), question.toString()
        );
    }

    @Test
    public void setAnswerSetsQuestionAnswer() {
        Activity activity = new Activity("sdf", POSITIVE, "sajdl");
        question.setAnswer(activity);
        assertEquals(activity, question.getAnswer());
    }

    @Test
    public void getIncorrectActivitiesReturnsIncorrectList() {
        assertTrue(question.getIncorrectActivities().size() > 0);
    }

    @Test
    public void setComparedActivitySetsComparedActivity() {
        Activity activity = new Activity("sdf", POSITIVE, "sajdl");
        question.setComparedActivity(activity);
        assertEquals(activity, question.getComparedActivity());
    }

    @Test
    public void generateQuestionTextReturnsQuestionText() {
        assertEquals(
                "What could you do instead of q2 to consume less energy?",
                question.generateQuestionText()
        );
    }

    @Test
    public void generateCorrectAnswerNotNull() {
        assertNotNull(question.generateCorrectAnswer());
    }

    @Test
    public void equalsReturnsTrueIfSame() {
        assertEquals(question, question);
    }

    @Test
    public void equalsReturnsTrueIfEqualFields() {
        ChoiceQuestion equalQuestion = new ChoiceQuestion(activities);
        question = new ChoiceQuestion(activities);
        assertEquals(equalQuestion, question);
    }

    @Test
    public void equalsReturnsFalseIfDifferentInstances() {
        ConsumptionQuestion notEqual = new ConsumptionQuestion();
        assertNotEquals(notEqual, question);
    }

    @Test
    public void hashCodeReturnsSameForSameFields() {
        assertEquals(question.hashCode(), question.hashCode());
    }
}
