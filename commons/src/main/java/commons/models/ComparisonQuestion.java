package commons.models;

import commons.entities.Activity;
import commons.utils.CompareType;
import commons.utils.QuestionType;

import java.util.Objects;

public class ComparisonQuestion extends Question {
    private static final long TRUE_FACTOR = 500;
    private static final long TIME_FACTOR = 800;

    private Activity firstActivity;
    private Activity secondActivity;
    private CompareType userAnswer;

    /**
     * Constructs a new comparison question object with the given
     * activities to compare (whether their consumptions are equal,
     * larger or smaller)
     * @param firstActivity the first activity to compare
     * @param secondActivity the second activity to compare
     */
    public ComparisonQuestion(Activity firstActivity, Activity secondActivity) {
        super(QuestionType.COMPARISON);
        this.firstActivity = firstActivity;
        this.secondActivity = secondActivity;
        this.userAnswer = null;
    }

    /**
     * Returns the first activity
     * @return the first activity to compare
     */
    public Activity getFirstActivity() {
        return firstActivity;
    }

    /**
     * Sets the first activity to compare
     * @param firstActivity the first activity
     */
    public void setFirstActivity(Activity firstActivity) {
        this.firstActivity = firstActivity;
    }

    /**
     * Returns the second activity
     * @return the second activity to compare
     */
    public Activity getSecondActivity() {
        return secondActivity;
    }

    /**
     * Sets the second activity to compare
     * @param secondActivity the second activity
     */
    public void setSecondActivity(Activity secondActivity) {
        this.secondActivity = secondActivity;
    }

    /**
     * Returns the user answer
     * @return user answer
     */
    public CompareType getUserAnswer() {
        return userAnswer;
    }

    /**
     * Sets the user answer and the time it took them in seconds
     * to answer the question
     * @param userAnswer the user answer
     * @param seconds the time it took them to answer in seconds
     */
    public void setUserAnswer(CompareType userAnswer, long seconds) {
        this.userAnswer = userAnswer;
        this.seconds = seconds;
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
        return (hasCorrectUserAnswer() ? 1 : 0) *
                (TRUE_FACTOR + TIME_FACTOR / (seconds + 1));
    }

    /**
     * Checks whether the user answer matches the correct answer
     * @return true if user answer is correct
     */
    @Override
    public boolean hasCorrectUserAnswer() {
        return userAnswer != null && (
                (userAnswer.equals(CompareType.EQUAL) &&
                        firstActivity.consumption == secondActivity.consumption)
                        || (userAnswer.equals(CompareType.SMALLER) &&
                        firstActivity.consumption < secondActivity.consumption)
                        || (userAnswer.equals(CompareType.LARGER) &&
                        firstActivity.consumption > secondActivity.consumption)
        );
    }

    /**
     * Returns a string representation of the given object
     * @return a string representation of the given object
     */
    @Override
    public String toString() {
        return "ComparisonQuestion{" +
                "firstActivity=" + firstActivity +
                ",secondActivity=" + secondActivity +
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
        ComparisonQuestion that = (ComparisonQuestion) o;
        return firstActivity.equals(that.firstActivity) && secondActivity.equals(that.secondActivity);
    }

    /**
     * Generates a hash code for the given object based
     * on its fields
     * @return object hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), firstActivity, secondActivity, userAnswer, seconds);
    }
}
