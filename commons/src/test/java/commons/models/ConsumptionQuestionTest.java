package commons.models;

import commons.entities.Activity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConsumptionQuestionTest {
    private static final long POSITIVE = 80;
    private static final long ZERO = 0;
    private static final long NEGATIVE = -1;
    private static final long TOTAL = 900;
    private static final double THREE_QUARTERS = 0.75;
    private static final double ONE_QUARTER = 0.25;
    private static final double TWO_FIFTHS = 0.4;

    private ConsumptionQuestion question;
    private Activity activity = new Activity("act", POSITIVE, "src");

    private class MyRandom extends Random {

        private int counter;

        protected MyRandom(){
            this.counter = 0;
        }
        // CHECKSTYLE:OFF
        @Override
        public double nextDouble(){
            double out = counter % 3 == 0 ? THREE_QUARTERS : counter % 3 == 1 ? ONE_QUARTER : TWO_FIFTHS;
            counter++;
            return out;
        }
        // CHECKSTLYE:ON
    }

    @BeforeEach
    public void startup() {
        question = new ConsumptionQuestion(activity, new MyRandom());
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
        assertEquals(NEGATIVE, question.getUserAnswer().generateAnswer());
    }

    @Test
    public void setUserAnswerSetsUserAnswer() {
        question.setUserAnswer(new Answer(POSITIVE * POSITIVE), POSITIVE);
        assertEquals(POSITIVE * POSITIVE, question.getUserAnswer().generateAnswer());
    }

    @Test
    public void getSecondsReturnsSecondsItTook() {
        question.setUserAnswer(new Answer(POSITIVE * POSITIVE), POSITIVE);
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
    public void answerGenerationWorks() {
        List<Long> answers = question.getAnswers();

        assertTrue(answers.contains(POSITIVE));

        // CHECKSTYLE:OFF
        long expectedAlternativeFirst = (long) 
                (POSITIVE + (THREE_QUARTERS < 0.5 ? -1 : 1) * POSITIVE * 0.6 * ONE_QUARTER);
        long expectedAlternativeSecond = (long)
                (POSITIVE + (TWO_FIFTHS < 0.5 ? -1 : 1) * POSITIVE * 0.6 * THREE_QUARTERS);
        // CHECKSTYLE:ON

        assertTrue(answers.contains(expectedAlternativeFirst));
        assertTrue(answers.contains(expectedAlternativeSecond));
    }

    @Test
    public void getPointsShouldReturnPointsForAnswer() {
        question.setUserAnswer(new Answer(POSITIVE), 1);
        long expected = TOTAL;
        assertEquals(expected, question.calculatePoints());
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
