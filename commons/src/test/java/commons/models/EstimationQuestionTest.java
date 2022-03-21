package commons.models;

import commons.entities.Activity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class EstimationQuestionTest {
    private static final long POSITIVE = 8;
    private static final long TOTAL = 1089;

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
        assertEquals(Long.valueOf(-1), question.getUserAnswer().generateAnswer());
    }

    @Test
    public void setUserAnswerSetsUserAnswer() {
        question.setUserAnswer(new Answer(POSITIVE * POSITIVE), POSITIVE);
        assertEquals(POSITIVE * POSITIVE, question.getUserAnswer().generateAnswer());
    }

    @Test
    public void getSecondsReturnsSeconds() {
        question.setUserAnswer(new Answer(POSITIVE * POSITIVE), POSITIVE);
        assertEquals(POSITIVE, question.getSeconds());
    }

    @Test
    public void getPointsReturnsPoints() {
        question.setUserAnswer(new Answer(POSITIVE), POSITIVE);
        long expected = TOTAL;
        assertEquals(expected, question.calculatePoints());
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
