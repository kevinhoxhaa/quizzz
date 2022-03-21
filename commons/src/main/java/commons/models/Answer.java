package commons.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import commons.entities.Activity;
import commons.utils.AnswerType;
import commons.utils.CompareType;

import java.util.Objects;

import static commons.utils.AnswerType.ACTIVITY;
import static commons.utils.AnswerType.COMPARETYPE;
import static commons.utils.AnswerType.LONG;

public class Answer {
    @JsonInclude
    private Long longAnswer;
    @JsonInclude
    private Activity activity;
    @JsonInclude
    private CompareType compareType;
    @JsonInclude
    private AnswerType answerType;

    @SuppressWarnings("unused")
    private Answer() {
        // for object mapper
    }

    /**
     * A constructor for a long type answer. The type of this instance is *not* tied to just long.
     * @param longAnswer the answer itself
     */
    public Answer(Long longAnswer){
        this.longAnswer = longAnswer;
        this.answerType = LONG;
    }

    /**
     * A constructor for an activity type answer. The type of this instance is *not* tied to just activity.
     * @param activity the answer itself
     */
    public Answer(Activity activity){
        this.activity = activity;
        this.answerType = ACTIVITY;
    }

    /**
     * A constructor for a compare type answer. The type of this instance is *not* tied to just compare type.
     * @param compareType the answer itself
     */
    public Answer(CompareType compareType){
        this.compareType = compareType;
        this.answerType = COMPARETYPE;
    }

    /**
     * A getter for the type of the answer.
     * @return the type of the answer
     */
    public AnswerType getAnswerType() {
        return answerType;
    }

    /**
     * A getter for the answer
     * @return the answer
     */
    public Object generateAnswer(){
        switch (answerType) {
            case LONG:
                return longAnswer;
            case ACTIVITY:
                return activity;
        }
        return compareType;
    }

    /**
     * A setter for the answer (long)
     * @param answer the new answer
     */
    public void setLongAnswer(Long answer){
        this.longAnswer = answer;
        this.answerType = LONG;
    }

    /**
     * A setter for the answer (activity)
     * @param answer the new answer
     */
    public void setActivityAnswer(Activity answer){
        this.activity = answer;
        this.answerType = ACTIVITY;
    }

    /**
     * A setter for the answer (compare type)
     * @param answer the new answer
     */
    public void setCompareAnswer(CompareType answer){
        this.compareType = answer;
        this.answerType = COMPARETYPE;
    }

    public Activity getActivity() {
        return activity;
    }

    public CompareType getCompareType() {
        return compareType;
    }

    public Long getLongAnswer() {
        return longAnswer;
    }

    /**
     * A method that determines whether this is equal to another answer
     * Two answers are equal if they have the same type and equal values for that type.
     * @param o the other answer
     * @return whether they are equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Answer answer = (Answer) o;
        switch (this.answerType){
            case COMPARETYPE:
                return answer.getAnswerType() == COMPARETYPE && this.compareType.equals(answer.compareType);
            case LONG:
                return answer.getAnswerType() == LONG && this.longAnswer.equals(answer.longAnswer);
            case ACTIVITY:
                return answer.getAnswerType() == ACTIVITY && this.activity.equals(answer.activity);
        }
        return false;
    }

    /**
     * A hashCode method for answers
     * Two answers have the same hashCode if they are equal.
     * @return the hash code
     */
    @Override
    public int hashCode() {
        switch (this.answerType){
            case COMPARETYPE:
                return Objects.hash(answerType, compareType);
            case LONG:
                return Objects.hash(answerType, longAnswer);
            case ACTIVITY:
                return Objects.hash(answerType, activity);
        }
        return Objects.hash(answerType);
    }
}
