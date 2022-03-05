package commons.models;

import commons.entities.Activity;
import commons.utils.AnswerType;
import commons.utils.CompareType;

import static commons.utils.AnswerType.LONG;
import static commons.utils.AnswerType.ACTIVITY;
import static commons.utils.AnswerType.COMPARETYPE;

public class Answer {
    private Long longAnswer;
    private Activity activity;
    private CompareType compareType;
    private AnswerType answerType;

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
    public Object getAnswer(){
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
    public void setAnswer(Long answer){
        this.longAnswer = answer;
        this.answerType = LONG;
    }

    /**
     * A setter for the answer (activity)
     * @param answer the new answer
     */
    public void setAnswer(Activity answer){
        this.activity = answer;
        this.answerType = ACTIVITY;
    }

    /**
     * A setter for the answer (compare type)
     * @param answer the new answer
     */
    public void setAnswer(CompareType answer){
        this.compareType = answer;
        this.answerType = COMPARETYPE;
    }
}
