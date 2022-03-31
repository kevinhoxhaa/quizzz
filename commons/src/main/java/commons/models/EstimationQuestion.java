package commons.models;

import com.fasterxml.jackson.annotation.JsonTypeName;
import commons.entities.Activity;
import commons.utils.QuestionType;

import java.util.Objects;

@JsonTypeName(value = "estimation")
public class EstimationQuestion extends Question {
    private static final long POINTS = 1000;
    private static final long TIME_FACTOR = 800;
    private static final double ERROR_MARGIN = 0.05;

    private Activity activity;

    @SuppressWarnings("unused")
    private EstimationQuestion() {
        super(QuestionType.ESTIMATION);
        // for object mapper
    }

    /**
     * Constructs a new estimation question object based
     * on a certain activity from the database
     * @param activity the activity the question is based on
     */
    public EstimationQuestion(Activity activity) {
        super(QuestionType.ESTIMATION);
        this.activity = activity;
        this.userAnswer = new Answer(Long.valueOf(-1));
        this.imagePath = activity.imagePath;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    /**
     * Returns the activity the question is based on
     * @return the activity the question is based on
     */
    public Activity getActivity() {
        return activity;
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
    public long calculatePoints() {
        if(userAnswer.generateAnswer() == null || !hasCorrectUserAnswer()) {
            return 0L;
        }

        long answerPoints = (POINTS - Math.round(
                 (double) Math.abs(activity.consumption - ((long) userAnswer.generateAnswer())) / activity.consumption
        ) * POINTS);
        long timePoints = Math.round((TIME_FACTOR / (seconds + 1)));
        return answerPoints < 0 ? 0 : (answerPoints + timePoints);
    }

    /**
     * Checks whether the user's answer is within 5% range of the correct
     * one, which is the game's criteria for a correct estimation
     * question answer
     * @return true if the answer is close to the correct one
     */
    @Override
    public boolean hasCorrectUserAnswer() {
        if (userAnswer == null || userAnswer.generateAnswer() == null) {
            return false;
        }

        return Math.abs(activity.consumption - (long) userAnswer.generateAnswer()) < ERROR_MARGIN
                * activity.consumption;
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

    /**
     * Returns the text representation of the question
     * @return the text representation of the question
     */
    @Override
    public String generateQuestionText(){
        return String.format("How much energy do you think %s consumes?",
                getActivity().title);
    }
}
