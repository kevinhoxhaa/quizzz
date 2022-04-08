package commons.models;

import java.util.ArrayList;
import java.util.List;

public class SoloGame extends Game {
    private List<Boolean> answers;
    private int currentQuestionNum;

    public SoloGame() {
        this.answers = new ArrayList<>();
        this.currentQuestionNum = 0; // ATTENTION! Indexing starts from 0!
    }

    /**
     * A getter for the map of whether each of the previous answers were right or wrong.
     * @return the map
     */
    public List<Boolean> getAnswers() {
        return answers;
    }

    /**
     * A getter for the number of the current question
     * @return the number
     */
    public int getCurrentQuestionNum() {
        return currentQuestionNum;
    }

    /**
     * A getter for the current question
     * @return the current question
     */
    public Question loadCurrentQuestion() {
        return questions.get(currentQuestionNum);
    }

    /**
     * Increments the current question num and returns the new value
     * @return the new value for the current question number, after incrementing it
     */
    public int incrementCurrentQuestionNum() {
        currentQuestionNum++;
        return currentQuestionNum;
    }


}
