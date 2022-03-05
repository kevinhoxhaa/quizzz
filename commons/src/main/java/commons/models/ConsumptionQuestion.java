package commons.models;

import commons.entities.Activity;
import commons.utils.QuestionType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class ConsumptionQuestion extends Question {
    private static final long TRUE_FACTOR = 500;
    private static final long TIME_FACTOR = 800;

    private Activity activity;
    private List<Long> answers;
    private final Random random;

    /**
     * Constructs a new consumption question object based
     * on the provided activity
     * Generates a list of alternative answers to the question
     * @param activity the activity this question is based on
     * @param random the random generator to use
     */
    public ConsumptionQuestion(Activity activity, Random random) {
        super(QuestionType.CONSUMPTION);
        this.activity = activity;
        this.seconds = 1;
        this.userAnswer = new Answer(Long.valueOf(-1));
        this.random = random;
        setAnswers(activity.consumption);
    }

    /**
     * Returns the activity the question is based on
     * @return the activity the question is based on
     */
    public Activity getActivity() {
        return activity;
    }

    /**
     * Sets the activity this question is based on
     * @param activity the activity the question
     *                 is based on
     */
    public void setActivity(Activity activity) {
        this.activity = activity;
        setAnswers(activity.consumption);
    }

    /**
     * Returns a list of two numbers which are a little
     * greater or smaller than the correct answer, in order
     * to confuse the user
     * Note: ignoring checkstyle because of too many
     * magic numbers
     * @param correctAnswer the correct answer
     */
    // CHECKSTYLE:OFF
    public void setAnswers(long correctAnswer) {
        answers = new ArrayList<>();

        long firstAlternative;
        long secondAlternative;

        do {
             firstAlternative = (long) (correctAnswer +
                    (random.nextDouble() < 0.5 ? -1 : 1) * correctAnswer * 0.6 * random.nextDouble());
        } while (correctAnswer == firstAlternative);

        do {
            secondAlternative = (long) (correctAnswer +
                    (random.nextDouble() < 0.5 ? -1 : 1) * correctAnswer * 0.6 * random.nextDouble());
        } while (correctAnswer == secondAlternative || firstAlternative == secondAlternative);

        answers.add(correctAnswer);
        answers.add(firstAlternative);
        answers.add(secondAlternative);
        Collections.shuffle(answers);
    }
    // CHECKSTYLE:ON

    /**
     * Returns the generated answers to the question
     * @return the question alternative answers
     */
    public List<Long> getAnswers() {
        return answers;
    }

    /**
     * Calculates the points based on whether
     * the user's answer is correct and the time
     * it took them to answer the question
     * @return the current answer points
     */
    @Override
    public long getPoints() {
        return (long) ((hasCorrectUserAnswer() ? 1 : 0) * (TRUE_FACTOR + TIME_FACTOR / (seconds + 1)));
    }

    @Override
    public boolean hasCorrectUserAnswer() {
        return activity.consumption == (Long) userAnswer.getAnswer();
    }

    /**
     * Returns a string representation of the given object
     * @return a string representation of the given object
     */
    @Override
    public String toString() {
        return "ConsumptionQuestion{" +
                "activity=" + activity +
                '}';
    }

    /**
     * Checks whether another object is of the same instance
     * and has fields equal to the given object
     * @param o the object to compare the current object with
     * @return true of the two objects are the same or are of
     * the same instance and have equal fields
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        ConsumptionQuestion that = (ConsumptionQuestion) o;
        return activity.equals(that.activity);
    }

    /**
     * Generates a hash code for the given object based
     * on its fields
     * @return object hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), activity, userAnswer, seconds, answers);
    }
}
