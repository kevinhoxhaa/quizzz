package commons.models;

import commons.entities.Activity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ConsumptionQuestionTest {
    private static final long POSITIVE = 8;
    private static final long ZERO = 0;
    private static final long NEGATIVE = -1;
    private static final long HASH = 761096326;

    private ConsumptionQuestion question;
    private Activity activity = new Activity("act", POSITIVE, "src");

    @BeforeEach
    public void startup() {
        question = new ConsumptionQuestion(activity);
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
    public void getAnswerReturnsUserAnswer() {
        assertEquals(NEGATIVE, question.getAnswer());
    }

    @Test
    public void setAnswerSetsUserAnswer() {
        question.setAnswer(POSITIVE * POSITIVE, POSITIVE);
        assertEquals(POSITIVE * POSITIVE, question.getAnswer());
    }

    @Test
    public void getSecondsReturnsSecondsItTook() {
        question.setAnswer(POSITIVE * POSITIVE, POSITIVE);
        assertEquals(POSITIVE, question.getSeconds());
    }

    @Test
    public void getSecondsReturnsPositiveIfNotAnswered() {
        assertEquals(-NEGATIVE, question.getSeconds());
    }

    @Test
    public void getAnswersReturnsDifferentAnswers() {
        System.out.println(question.getAnswers());
        List<Long> answers = question.getAnswers();
        long firstAnswer = answers.remove((int) ZERO);
        long secondAnswer = answers.remove((int) ZERO);
        long thirdAnswer = answers.remove((int) ZERO);

        assertNotEquals(firstAnswer, secondAnswer);
        assertNotEquals(secondAnswer, thirdAnswer);
        assertNotEquals(firstAnswer, thirdAnswer);
    }

    @Test
    public void toStringReturnsStringRepresentation() {
        assertEquals(
                String.format("ConsumptionQuestion{activity=%s}",
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
