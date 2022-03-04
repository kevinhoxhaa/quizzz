package commons.models;

import commons.entities.Activity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class EstimationQuestionTest {
    private static final long POSITIVE = 8;
    private static final long TOTAL = 1000;

    private EstimationQuestion question;
    private Activity activity = new Activity("act", POSITIVE, "src");

    @BeforeEach
    public void startup() {
        question = new EstimationQuestion(activity);
    }

    @Test
    public void constructorShouldConstructValidObject() {
        assertNotNull(question);
    }

    @Test
    public void getActivityReturnsActivity() {
        assertEquals(activity, question.getActivity());
    }

    @Test
    public void getUserAnswerReturnsUserAnswer() {
        assertEquals(-1, question.getUserAnswer());
    }

    @Test
    public void setUserAnswerSetsUserAnswer() {
        question.setUserAnswer(POSITIVE * POSITIVE, POSITIVE);
        assertEquals(POSITIVE * POSITIVE, question.getUserAnswer());
    }

    @Test
    public void getSecondsReturnsSeconds() {
        question.setUserAnswer(POSITIVE * POSITIVE, POSITIVE);
        assertEquals(POSITIVE, question.getSeconds());
    }

    @Test
    public void getPointsReturnsPoints() {
        question.setUserAnswer(POSITIVE, POSITIVE);
        long expected = TOTAL;
        assertEquals(expected, question.getPoints());
    }

    @Test
    public void toStringReturnsStringRepresentation() {
        assertEquals(
                String.format("EstimationQuestion{activity=%s}",
                        question.getActivity().toString()), question.toString()
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
