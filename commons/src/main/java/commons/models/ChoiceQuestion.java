package commons.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import commons.entities.Activity;
import commons.utils.QuestionType;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ChoiceQuestion extends Question {
    private static final long TRUE_FACTOR = 500;
    private static final long TIME_FACTOR = 800;

    private Activity comparedActivity;
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    private List<Activity> activities;
    private Activity answer;

    @SuppressWarnings("unused")
    private ChoiceQuestion() {
        super(QuestionType.CHOICE);
        // for object mapper
    }

    /**
     * Constructs a choice question (what will you do instead of...?)
     * from a list of activities
     * @param activities the list of activities this question
     *                   is based on
     */
    public ChoiceQuestion(List<Activity> activities) {
        super(QuestionType.CHOICE);
        Collections.shuffle(activities);
        setActivities(activities);
        this.userAnswer = null;
    }

    /**
     * Sets the answer to the question as the activity
     * with the minimal consumption
     * @param activities the activities this question
     *                   is based on
     */
    private void loadAnswer(List<Activity> activities) {
        long minConsumption = Integer.MAX_VALUE;
        for(Activity a : activities) {
            if(a.consumption < minConsumption) {
                answer = a;
                minConsumption = a.consumption;
            }
        }
    }

    /**
     * Removes the compared activity from the list of activities belonging to the question.
     * (Should be called only on the frontend)
     */
    public void removeComparedFromActivities(){
        activities.remove(comparedActivity);
    }

    /**
     * A setter for the answer activity
     * @param answer
     */
    public void setAnswer(Activity answer) {
        this.answer = answer;
    }

    /**
     * Returns the activity the question is based on
     * @return the activity the question is based on
     */
    public Activity getComparedActivity() {
        return comparedActivity;
    }

    /**
     * Sets the activity this question is based on
     * @param activities the activities the question
     *                   is based on, from which the compared
     *                   activity is chosen as the second
     *                   maximal
     */
    private void loadComparedActivity(List<Activity> activities) {
        long minConsumption = Integer.MAX_VALUE;
        for(Activity a : activities) {
            if(a.consumption < minConsumption && a != answer) {
                comparedActivity = a;
                this.imagePath = comparedActivity.imagePath;
                minConsumption = a.consumption;
            }
        }
    }

    /**
     * A setter for the compared activity
     * @param activity the compared activity to be set
     */
    public void setComparedActivity(Activity activity) {
        this.comparedActivity = activity;
    }

    /**
     * Returns the question answer
     * @return question correct answer
     */
    public Activity getAnswer() {
        return answer;
    }


    /**
     * Returns the activities this question is based on
     * @return the activities this question is based on
     */
    public List<Activity> getActivities() {
        return activities;
    }

    /**
     * Sets the activities this question is based on
     * @param activities the activities the question
     *                   is based on
     */
    public void setActivities(List<Activity> activities) {
        this.activities = activities;
        loadAnswer(activities);
        loadComparedActivity(activities);
    }

    /**
     * Calculates the points based on whether
     * the user's answer is correct and the time
     * it took them to answer the question
     * @return the current answer points
     */
    @Override
    public long getPoints() {
        return ((hasCorrectUserAnswer() ? 1 : 0) * Math.round(TRUE_FACTOR + TIME_FACTOR / (seconds + 1)));
    }

    /**
     * Returns true if the answer chosen by the user
     * is the correct option for this multiple-choice
     * question
     * @return true if user answer chosen is correct
     */
    @Override
    public boolean hasCorrectUserAnswer() {
        if (userAnswer == null) {
            return false;
        }
        return answer.equals(userAnswer.getAnswer());
    }

    /**
     * Returns a string representation of the given object
     * @return a string representation of the given object
     */
    @Override
    public String toString() {
        return "ChoiceQuestion{" +
                "activities=" + activities +
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
        ChoiceQuestion that = (ChoiceQuestion) o;
        return comparedActivity.equals(that.comparedActivity) &&
                activities.equals(that.activities) && answer.equals(that.answer);
    }

    /**
     * Generates a hash code for the given object based
     * on its fields
     * @return object hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), comparedActivity, activities, answer);
    }
}
