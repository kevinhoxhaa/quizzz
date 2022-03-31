package commons.models;

import com.fasterxml.jackson.annotation.JsonTypeName;
import commons.entities.Activity;
import commons.utils.QuestionType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

@JsonTypeName(value = "consumption")
public class ConsumptionQuestion extends Question {
    private static final long TRUE_FACTOR = 500;
    private static final long TIME_FACTOR = 800;

    private Activity activity;
    private List<Long> answers;
    private Random random;

    private Long firstAlternative;
    private Long secondAlternative;

    @SuppressWarnings("unused")
    public ConsumptionQuestion() {
        super(QuestionType.CONSUMPTION);
        this.random = new Random();
        // for object mapper
    }

    /**
     * Constructs a new consumption question object based
     * on the provided activity
     * Generates a list of alternative answers to the question
     * @param activity the activity this question is based on
     * @param random the random generator to use
     */
    public ConsumptionQuestion(Activity activity, Random random) {
        super(QuestionType.CONSUMPTION);
        this.activity = activity;
        this.seconds = 1;
        this.userAnswer = new Answer(Long.valueOf(-1));
        this.random = random;
        this.imagePath = activity.imagePath;
        loadAnswers(activity.consumption);
    }

    /**
     * Returns the activity the question is based on
     * @return the activity the question is based on
     */
    public Activity getActivity() {
        return activity;
    }

    /**
     * Sets the activity this question is based on
     * @param activity the activity the question
     *                 is based on
     */
    public void setActivity(Activity activity) {
        this.activity = activity;
        loadAnswers(activity.consumption);
    }


    /**
     * Generates two alternative answers.
     * @param correctAnswer The correct answer (after being divided by the coefficient)
     * @param coefficient The coefficient
     * @return A list with 3 items:
     *  - Index 0: the correct answer
     *  - Index 1: the first alternative
     *  - Index 2: the second alternative
     */
    // CHECKSTYLE:OFF
    private List<Long> generateAlternatives(long correctAnswer, long coefficient){

        int tries = 0;

        long firstAlternative = 0;
        long secondAlternative = 0;

        do {
            tries++;
            firstAlternative = (long) (correctAnswer +
                    (random.nextBoolean() ? -1 : 1) * correctAnswer * 0.6 * random.nextDouble());
        } while ((correctAnswer == firstAlternative || firstAlternative <= 0L) && tries < 30);

        do {
            tries++;
            secondAlternative = (long) (correctAnswer +
                    (random.nextBoolean() ? -1 : 1) * correctAnswer * 0.6 * random.nextDouble());
        } while ((correctAnswer == secondAlternative
                || firstAlternative == secondAlternative || secondAlternative <= 0L) && tries < 30);

        if(tries >= 30){
            return null;
        }

        List<Long> out = new ArrayList<>();
        out.add(correctAnswer);
        out.add(firstAlternative);
        out.add(secondAlternative);
        return out;
    }

    /**
     * Returns a list of two numbers which are a little
     * greater or smaller than the correct answer, in order
     * to confuse the user
     * Also returns a list of incorrect answers for the remove
     * incorrect answer joker
     * Note: ignoring checkstyle because of too many
     * magic numbers
     * @param correctAnswer the correct answer
     */

    private void loadAnswers(long correctAnswer) {
        answers = new ArrayList<>();

        long coefficient = 1L;

        while(correctAnswer % 10 == 0){
            correctAnswer = correctAnswer / 10;
            coefficient *= 10;
        }

        if(coefficient == 1 && correctAnswer % 5 == 0){
            correctAnswer = correctAnswer / 5;
            coefficient *= 5;
        }

        List<Long> alternatives = generateAlternatives(correctAnswer, coefficient);

        while(alternatives == null){
            if(coefficient == 1){
                answers.add(correctAnswer);
                answers.add(correctAnswer + 1);
                answers.add(correctAnswer + 2);
                Collections.shuffle(answers);
                return;
            }
            if(coefficient % 5 == 0){
                coefficient /= 5;
                correctAnswer *= 5;
                alternatives = generateAlternatives(correctAnswer, coefficient);
            }
        }

        correctAnswer = alternatives.get(0);
        long firstAlternative = alternatives.get(1);
        long secondAlternative = alternatives.get(2);

        correctAnswer *= coefficient;
        firstAlternative *= coefficient;
        secondAlternative *= coefficient;

        answers.add(correctAnswer);
        answers.add(firstAlternative);
        answers.add(secondAlternative);
        Collections.shuffle(answers);
        this.firstAlternative = firstAlternative;
        this.secondAlternative = secondAlternative;
    }
    // CHECKSTYLE:ON

    public void setAnswers(List<Long> answers) {
        this.answers = answers;
    }

    /**
     * Returns the generated answers to the question
     * @return the question alternative answers
     */
    public List<Long> getAnswers() {
        return answers;
    }

    /**
     * Calculates the points based on whether
     * the user's answer is correct and the time
     * it took them to answer the question
     * @return the current answer points
     */
    @Override
    public long calculatePoints() {
        return ((hasCorrectUserAnswer() ? 1 : 0) * Math.round(TRUE_FACTOR + TIME_FACTOR / (seconds + 1)));
    }

    @Override
    public boolean hasCorrectUserAnswer() {
        if (userAnswer == null || userAnswer.generateAnswer() == null) {
            return false;
        }

        return activity.consumption == (long) userAnswer.generateAnswer();
    }

    /**
     * Returns a string representation of the given object
     * @return a string representation of the given object
     */
    @Override
    public String toString() {
        return "ConsumptionQuestion{" +
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
        ConsumptionQuestion that = (ConsumptionQuestion) o;
        return activity.equals(that.activity);
    }

    /**
     * Generates a hash code for the given object based
     * on its fields
     * @return object hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), activity, userAnswer, seconds, answers);
    }
}
