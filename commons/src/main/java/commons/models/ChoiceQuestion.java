package commons.models;

import commons.entities.Activity;
import commons.utils.QuestionType;

import java.util.List;
import java.util.Objects;

public class ChoiceQuestion extends Question {
    private static final long TRUE_FACTOR = 500;
    private static final long TIME_FACTOR = 800;

    private Activity comparedActivity;
    private List<Activity> activities;
    private Activity answer;
    private Activity userAnswer;
    private long seconds;

    /**
     * Constructs a choice question (what will you do instead of...?)
     * from a list of activities
     * @param activities the list of activities this question
     *                   is based on
     */
    public ChoiceQuestion(List<Activity> activities) {
        super(QuestionType.CHOICE);
        setActivities(activities);
        this.userAnswer = null;
        this.seconds = 1;
    }

    /**
     * Sets the answer to the question as the activity
     * with the minimal consumption
     * @param activities the activities this question
     *                   is based on
     */
    private void setAnswer(List<Activity> activities) {
        long minConsumption = Integer.MAX_VALUE;
        for(Activity a : activities) {
            if(a.consumption < minConsumption) {
                answer = a;
                minConsumption = a.consumption;
            }
        }
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
    private void setComparedActivity(List<Activity> activities) {
        long minConsumption = Integer.MAX_VALUE;
        for(Activity a : activities) {
            if(a.consumption < minConsumption && a != answer) {
                comparedActivity = a;
                minConsumption = a.consumption;
            }
        }
    }

    /**
     * Returns the question answer
     * @return question correct answer
     */
    public Activity getAnswer() {
        return answer;
    }

    /**
     * Returns the consumption guessed by the user
     * @return the user's answer to the question
     */
    public Activity getUserAnswer() {
        return userAnswer;
    }

    /**
     * Sets the user's answer and the time it took them
     * in seconds to answer the question
     * @param answer the user's answer
     * @param seconds the time it took the user to answer the
     *                question in seconds
     */
    public void setUserAnswer(Activity answer, long seconds) {
        this.userAnswer = answer;
        this.seconds = seconds;
    }

    /**
     * Returns the seconds it took the user to answer
     * the question; if it has not already been answered,
     * the returned value is -1
     * @return the seconds it took the user to answer
     */
    public long getSeconds() {
        return seconds;
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
        setAnswer(activities);
        setComparedActivity(activities);
    }

    /**
     * Calculates the points based on whether
     * the user's answer is correct and the time
     * it took them to answer the question
     * @return the current answer points
     */
    @Override
    public long getPoints() {
        return (answer == userAnswer ? 1 : 0) * (TRUE_FACTOR + TIME_FACTOR / (seconds + 1));
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
