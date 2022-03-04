package commons.models;

import commons.entities.Activity;
import commons.utils.QuestionType;

import java.util.Objects;

public class EstimationQuestion extends Question {
    private static final long POINTS = 1000;

    private Activity activity;
    private long userAnswer;
    private long seconds;

    /**
     * Constructs a new estimation question object based
     * on a certain activity from the database
     * @param activity the activity the question is based on
     */
    public EstimationQuestion(Activity activity) {
        super(QuestionType.ESTIMATION);
        this.activity = activity;
        this.userAnswer = -1;
        this.seconds = 1;
    }

    /**
     * Returns the activity the question is based on
     * @return the activity the question is based on
     */
    public Activity getActivity() {
        return activity;
    }

    /**
     * Returns the user answer
     * @return the user answer
     */
    public long getUserAnswer() {
        return userAnswer;
    }

    /**
     * Sets the user answer to the given question and
     * the seconds it took the user to answer it
     * @param userAnswer the user answer
     * @param seconds the time it took the user to answer
     *                it in seconds
     */
    public void setUserAnswer(long userAnswer, long seconds) {
        this.userAnswer = userAnswer;
        this.seconds = seconds;
    }

    /**
     * The time it took the user to answer the question
     * in seconds
     * @return time in seconds
     */
    public long getSeconds() {
        return seconds;
    }

    /**
     * Calculates the points the user got from answering
     * the question
     * The points are calculated using the formula
     * points = max(0, total - (difference / actual) * total)
     * which returns 0 points if the difference is more than
     * or equal to the actual
     * @return the calculated points
     */
    @Override
    public long getPoints() {
        long points = (long) (POINTS - (
                 (double) Math.abs(activity.consumption - userAnswer) / activity.consumption
        ) * POINTS);
        return points < 0 ? 0 : points;
    }

    /**
     * Returns a string representation of the given object
     * @return a string representation of the given object
     */
    @Override
    public String toString() {
        return "EstimationQuestion{" +
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
        EstimationQuestion that = (EstimationQuestion) o;
        return activity.equals(that.activity);
    }

    /**
     * Generates a hash code for the given object based
     * on its fields
     * @return object hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), activity, userAnswer, seconds);
    }
}
