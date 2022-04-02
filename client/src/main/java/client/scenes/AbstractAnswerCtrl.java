package client.scenes;

import commons.entities.Activity;
import commons.models.ChoiceQuestion;
import commons.models.ComparisonQuestion;
import commons.models.ConsumptionQuestion;
import commons.models.EstimationQuestion;
import commons.models.Question;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public abstract class AbstractAnswerCtrl extends QuestionNumController{
    @FXML
    protected VBox answerPane;
    @FXML
    protected Text questionText;
    @FXML
    protected Text answer;
    @FXML
    protected Text answerResponse;
    @FXML
    protected Text currentScore;
    @FXML
    protected ProgressIndicator countdownCircle;

    /**
     * A constructor for this class
     * @param mainCtrl
     */
    protected AbstractAnswerCtrl(MainCtrl mainCtrl) {
        super(mainCtrl);
    }

    /**
     * Sets up the answer scene:<br>
     *  - Sets the background color and the color of the score to red/green<br>
     *  - Sets the response text appropriately<br>
     *  - Sets the question text<br>
     *  - Updates the points displayed<br>
     *  - Calls the appropriate one from the following:<br>
     *      - {@link #setupChoiceAnswer(Question)}<br>
     *      - {@link #setupComparisonAnswer(Question)}<br>
     *      - {@link #setupChoiceAnswer(Question)}<br>
     *      - {@link #setupEstimationAnswer(Question)}<br>
     *  - Starts the timer circle
     *
     * @param prevQuestion the corresponding question object
     */
    protected void setup(Question prevQuestion){
        mainCtrl.addScore(mainCtrl.getUser(),prevQuestion);
        if (prevQuestion.hasCorrectUserAnswer()) {
            currentScore.setFill(Color.GREEN);
            this.answerResponse.setText("Well done!");
            answerPane.setBackground(new Background(
                    new BackgroundFill(Color.LIGHTGREEN, CornerRadii.EMPTY, Insets.EMPTY)));
        } else {
            this.answerResponse.setText("By making mistakes, we learn!");
            currentScore.setFill(Color.DARKRED);
            answerPane.setBackground(new Background(
                    new BackgroundFill(Color.LIGHTCORAL, CornerRadii.EMPTY, Insets.EMPTY)));
        }

        questionText.setText(prevQuestion.generateQuestionText());

        currentScore.setText(String.valueOf(mainCtrl.getUser().getPoints()));

        switch (prevQuestion.getType()) {
            case CONSUMPTION:
                setupConsumptionAnswer(prevQuestion);
                break;
            case COMPARISON:
                setupComparisonAnswer(prevQuestion);
                break;
            case CHOICE:
                setupChoiceAnswer(prevQuestion);
                break;
            case ESTIMATION:
                setupEstimationAnswer(prevQuestion);
                break;
        }

        startTimer();
    }

    /**
     * Sets up the previous question and correct answer for an answer page of a consumption question.
     *
     * @param prevQuestion The question that has just been asked to the players.
     */
    private void setupConsumptionAnswer(Question prevQuestion) {
        ConsumptionQuestion prevConsQuestion = (ConsumptionQuestion) prevQuestion;

        this.answer.setText(Long.toString(prevConsQuestion.getActivity().consumption));
    }

    /**
     * Sets up the previous question and correct answer for an answer page of a comparison question.
     *
     * @param prevQuestion The question that has just been asked to the players.
     */
    private void setupComparisonAnswer(Question prevQuestion) {
        ComparisonQuestion prevCompQuestion = (ComparisonQuestion) prevQuestion;
        Activity firstActivity = prevCompQuestion.getFirstActivity();
        Activity secondActivity = prevCompQuestion.getSecondActivity();

        questionText.setText(String.format("Which one consumes more energy? %s or %s?",
                firstActivity.title, secondActivity.title));

        if (firstActivity.consumption > secondActivity.consumption) {
            this.answer.setText(firstActivity.title);
        } else if (firstActivity.consumption < secondActivity.consumption) {
            this.answer.setText(secondActivity.title);
        } else {
            this.answer.setText("They consume equal amounts of energy.");
        }
    }

    /**
     * Sets up the previous question and correct answer for an answer page of a choice question.
     *
     * @param prevQuestion The question that has just been asked to the players.
     */
    private void setupChoiceAnswer(Question prevQuestion) {
        ChoiceQuestion prevChoiceQuestion = (ChoiceQuestion) prevQuestion;

        this.answer.setText(prevChoiceQuestion.getAnswer().title);
    }

    /**
     * Sets up the previous question and correct answer for an answer page of an estimation question.
     *
     * @param prevQuestion The question that has just been asked to the players.
     */
    private void setupEstimationAnswer(Question prevQuestion) {
        EstimationQuestion prevEstimQuestion = (EstimationQuestion) prevQuestion;

        this.answer.setText(Long.toString(prevEstimQuestion.getActivity().consumption));
    }

    /**
     * Starts the timer circle
     */
    protected abstract void startTimer();
}
