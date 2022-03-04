package commons.models;

import commons.entities.Activity;
import commons.utils.CompareType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ComparisonQuestionTest {
    private static final long POSITIVE = 8;
    private static final long TOTAL = 588;

    private ComparisonQuestion question;
    private Activity firstActivity;
    private Activity secondActivity;

    @BeforeEach
    public void startup() {
        firstActivity = new Activity("q1", POSITIVE, "src");
        secondActivity = new Activity("q2", POSITIVE + POSITIVE, "src");
        question = new ComparisonQuestion(firstActivity, secondActivity);
    }

    @Test
    public void constructorConstructsValidObject() {
        assertNotNull(question);
    }

    @Test
    public void getFirstActivityReturnsFirstActivity() {
        assertEquals(firstActivity, question.getFirstActivity());
    }

    @Test
    public void setFirstActivitySetsFirstActivity() {
        question.setFirstActivity(secondActivity);
        assertEquals(secondActivity, question.getFirstActivity());
    }

    @Test
    public void getSecondActivityReturnsSecondActivity() {
        assertEquals(secondActivity, question.getSecondActivity());
    }

    @Test
    public void setSecondActivitySetsSecondActivity() {
        question.setSecondActivity(firstActivity);
        assertEquals(firstActivity, question.getSecondActivity());
    }

    @Test
    public void getUserAnswerReturnsUserAnswer() {
        assertNull(question.getUserAnswer());
    }

    @Test
    public void setUserAnswerChangesUserAnswer() {
        question.setUserAnswer(new Answer(CompareType.EQUAL), POSITIVE);
        assertEquals(CompareType.EQUAL, question.getUserAnswer());
    }

    @Test
    public void getPointsReturnsPoints() {
        question.setUserAnswer(new Answer(CompareType.SMALLER), POSITIVE);
        assertEquals(TOTAL, question.getPoints());
    }

    @Test
    public void toStringReturnsStringRepresentation() {
        assertEquals(
                String.format("ComparisonQuestion{firstActivity=%s,secondActivity=%s}",
                        question.getFirstActivity().toString(),
                        question.getSecondActivity().toString()), question.toString()
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
