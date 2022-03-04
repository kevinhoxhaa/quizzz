package commons.models;

import commons.entities.Activity;
import commons.utils.AnswerType;
import commons.utils.CompareType;

import static commons.utils.AnswerType.*;

public class Answer {
    private Long longAnswer;
    private Activity activity;
    private CompareType compareType;
    private AnswerType answerType;

    public Answer(Long longAnswer){
        this.longAnswer = longAnswer;
        this.answerType = LONG;
    }

    public Answer(Activity activity){
        this.activity = activity;
        this.answerType = ACTIVITY;
    }

    public Answer(CompareType compareType){
        this.compareType = compareType;
        this.answerType = COMPARETYPE;
    }

    public AnswerType getAnswerType() {
        return answerType;
    }

    public Object getAnswer(){
        switch (answerType) {
            case LONG:
                return longAnswer;
            case ACTIVITY:
                return activity;
        }
        return compareType;
    }
}
