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
    }

    @Test
    public void getAnswerReturnsGoodValues(){
        assertEquals(POSITIVE, longAnswer.generateAnswer());
    }

    @Test
    public void generateAnswerReturnsCompareAnswer() {
        longAnswer.setCompareAnswer(CompareType.LARGER);
        assertEquals(CompareType.LARGER, longAnswer.generateAnswer());
    }

    @Test
    public void generateAnswerReturnsLongAnswer() {
        activityAnswer.setLongAnswer(POSITIVE+POSITIVE);
        assertEquals(POSITIVE+POSITIVE, activityAnswer.generateAnswer());
    }

    @Test
    public void generateAnswerReturnsActivityAnswer() {
        compareTypeAnswer.setActivityAnswer(activity);
        assertEquals(activity, compareTypeAnswer.generateAnswer());
    }

    @Test
    public void getAnswerTypeReturnsAnswerType(){
        assertEquals(AnswerType.LONG, longAnswer.getAnswerType());
    }

    @Test
    public void setLongAnswerSetsLongAnswer() {
        activityAnswer.setLongAnswer(POSITIVE+POSITIVE);
        assertEquals(AnswerType.LONG, activityAnswer.getAnswerType());
    }

    @Test
    public void getActivityReturnsActivity() {
        assertEquals(activity, activityAnswer.getActivity());
    }

    @Test
    public void getCompareTypeReturnsCompareType() {
        assertEquals(CompareType.SMALLER, compareTypeAnswer.getCompareType());
    }

    @Test
    public void getLongAnswerReturnsLongAnswer() {
        assertEquals(POSITIVE, longAnswer.getLongAnswer());
    }

    @Test
    public void setActivityAnswerReturnsActivityAnswer() {
        compareTypeAnswer.setActivityAnswer(activity);
        assertEquals(AnswerType.ACTIVITY, compareTypeAnswer.getAnswerType());
    }

    @Test
    public void setCompareTypeAnswerReturnsCompareAnswer() {
        longAnswer.setCompareAnswer(CompareType.LARGER);
        assertEquals(AnswerType.COMPARETYPE, longAnswer.getAnswerType());
    }

    @Test
    public void equalsReturnsTrueLongAnswer() {
        assertEquals(longAnswer, new Answer(POSITIVE));
    }

    @Test
    public void equalsReturnsTrueActivityAnswer() {
        assertEquals(activityAnswer, new Answer(activity));
    }

    @Test
    public void equalsReturnsTrueCompareAnswer() {
        assertEquals(compareTypeAnswer, new Answer(CompareType.SMALLER));
    }

    @Test
    public void equalsReturnsTrueSameObjects() {
        assertEquals(compareTypeAnswer, compareTypeAnswer);
    }

    @Test
    public void equalsAfterSetter(){
        longAnswer.setActivityAnswer(activity);
        assertEquals(activityAnswer, longAnswer);
    }

    @Test
    public void equalsDifferentType(){
        assertNotEquals(longAnswer, activityAnswer);
    }

    @Test
    public void equalsReturnsFalseLongAnswer() {
        assertNotEquals(longAnswer, new Answer(Long.valueOf(POSITIVE+POSITIVE)));
    }

    @Test
    public void equalsReturnsFalseActivityAnswer() {
        assertNotEquals(activityAnswer, new Answer(
                new Activity("title", POSITIVE+POSITIVE, "src")));
    }

    @Test
    public void equalsReturnsFalseCompareAnswer() {
        assertNotEquals(compareTypeAnswer, new Answer(CompareType.EQUAL));
    }

    @Test
    public void hashCodeSameEqualsLongAnswer() {
        assertEquals(longAnswer.hashCode(), longAnswer.hashCode());
    }

    @Test
    public void hashCodeSameEqualsCompareTypeAnswer() {
        assertEquals(compareTypeAnswer.hashCode(), compareTypeAnswer.hashCode());
    }

    @Test
    public void hashCodeSameEqualsActivityAnswer() {
        assertEquals(activityAnswer.hashCode(), activityAnswer.hashCode());
    }
}
