package commons.models;

import commons.entities.Activity;
import commons.utils.AnswerType;
import commons.utils.CompareType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AnswerTest {

    private Answer longAnswer;
    private Activity activity;
    private Answer activityAnswer;
    private Answer compareTypeAnswer;
    private static final Long POSITIVE = Long.valueOf(42);

    @BeforeEach
    public void setup(){
        this.longAnswer = new Answer(Long.valueOf(POSITIVE));
        this.activity = new Activity("testing answers", POSITIVE, "I did it again");
        this.activityAnswer = new Answer(activity);
        this.compareTypeAnswer = new Answer(CompareType.SMALLER);
    }

    @Test
    public void constructor(){
        assertNotNull(longAnswer);
        assertNotNull(activityAnswer);
        assertNotNull(compareTypeAnswer);
    }

    @Test
    public void getAnswerReturnsGoodValues(){
        assertEquals(POSITIVE, longAnswer.getAnswer());
        assertEquals(activity, activityAnswer.getAnswer());
        assertEquals(CompareType.SMALLER, compareTypeAnswer.getAnswer());
    }

    @Test
    public void setter(){
        longAnswer.setCompareAnswer(CompareType.LARGER);
        assertEquals(CompareType.LARGER, longAnswer.getAnswer());
        activityAnswer.setLongAnswer(POSITIVE+POSITIVE);
        assertEquals(POSITIVE+POSITIVE, activityAnswer.getAnswer());
        compareTypeAnswer.setActivityAnswer(activity);
        assertEquals(activity, compareTypeAnswer.getAnswer());
    }

    @Test
    public void answerTypeConstruction(){
        assertEquals(AnswerType.LONG, longAnswer.getAnswerType());
        assertEquals(AnswerType.ACTIVITY, activityAnswer.getAnswerType());
        assertEquals(AnswerType.COMPARETYPE, compareTypeAnswer.getAnswerType());
    }

    @Test
    public void setterAnswerType(){
        longAnswer.setCompareAnswer(CompareType.LARGER);
        assertEquals(AnswerType.COMPARETYPE, longAnswer.getAnswerType());
        activityAnswer.setLongAnswer(POSITIVE+POSITIVE);
        assertEquals(AnswerType.LONG, activityAnswer.getAnswerType());
        compareTypeAnswer.setActivityAnswer(activity);
        assertEquals(AnswerType.ACTIVITY, compareTypeAnswer.getAnswerType());
    }

    @Test
    public void equalsItself(){
        assertEquals(longAnswer,longAnswer);
        assertEquals(activityAnswer,activityAnswer);
        assertEquals(compareTypeAnswer,compareTypeAnswer);
    }

    @Test
    public void equalsTrue(){
        assertEquals(longAnswer, new Answer(POSITIVE));
        assertEquals(activityAnswer, new Answer(activity));
        assertEquals(compareTypeAnswer, new Answer(CompareType.SMALLER));
    }

    @Test
    public void equalsAfterSetter(){
        longAnswer.setActivityAnswer(activity);
        assertEquals(activityAnswer, longAnswer);
    }

    @Test
    public void equalsDifferentType(){
        assertNotEquals(longAnswer, activityAnswer);
        assertNotEquals(longAnswer, compareTypeAnswer);
        assertNotEquals(compareTypeAnswer, activityAnswer);
    }

    @Test
    public void equalsSameTypeDifferentContent(){
        assertNotEquals(longAnswer, new Answer(Long.valueOf(POSITIVE+POSITIVE)));
        assertNotEquals(activityAnswer, new Answer(
                new Activity("title", POSITIVE+POSITIVE, "src")));
        assertNotEquals(compareTypeAnswer, new Answer(CompareType.EQUAL));
    }
}
