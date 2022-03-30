package client.scenes;

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
    protected Text activityText;
    @FXML
    protected Text answer;
    @FXML
    protected Text answerResponse;
    @FXML
    protected Text currentScore;
    @FXML
    protected ProgressIndicator countdownCircle;

    protected AbstractAnswerCtrl(MainCtrl mainCtrl) {
        super(mainCtrl);
    }

    protected void setup(Question prevQuestion, long points){
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

        currentScore.setText(String.valueOf(points));

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
    public void setupConsumptionAnswer(Question prevQuestion) {
        ConsumptionQuestion prevConsQuestion = (ConsumptionQuestion) prevQuestion;

        this.activityText.setText(
                String.format("How much energy does %s cost?",
                        prevConsQuestion.getActivity().title)
        );

        this.answer.setText(Long.toString(prevConsQuestion.getActivity().consumption));
    }

    /**
     * Sets up the previous question and correct answer for an answer page of a comparison question.
     *
     * @param prevQuestion The question that has just been asked to the players.
     */
    public void setupComparisonAnswer(Question prevQuestion) {
        ComparisonQuestion prevCompQuestion = (ComparisonQuestion) prevQuestion;

        this.activityText.setText(
                String.format("Does %s use more, less, or the same amount of energy as %s?",
                        prevCompQuestion.getFirstActivity().title,
                        prevCompQuestion.getSecondActivity().title)
        );

        if (prevCompQuestion.getFirstActivity().consumption > prevCompQuestion.getSecondActivity().consumption) {
            this.answer.setText("MORE");
        } else if (prevCompQuestion.getFirstActivity().consumption < prevCompQuestion.getSecondActivity().consumption) {
            this.answer.setText("LESS");
        } else {
            this.answer.setText("EQUAL");
        }
    }

    /**
     * Sets up the previous question and correct answer for an answer page of a choice question.
     *
     * @param prevQuestion The question that has just been asked to the players.
     */
    public void setupChoiceAnswer(Question prevQuestion) {
        ChoiceQuestion prevChoiceQuestion = (ChoiceQuestion) prevQuestion;

        this.activityText.setText(
                String.format("What could you do instead of %s to consume less energy?",
                        prevChoiceQuestion.getComparedActivity().title)
        );

        this.answer.setText(prevChoiceQuestion.getAnswer().title);
    }

    /**
     * Sets up the previous question and correct answer for an answer page of an estimation question.
     *
     * @param prevQuestion The question that has just been asked to the players.
     */
    public void setupEstimationAnswer(Question prevQuestion) {
        EstimationQuestion prevEstimQuestion = (EstimationQuestion) prevQuestion;

        this.activityText.setText(
                String.format("How much energy do you think that %s consumes?",
                        prevEstimQuestion.getActivity().title)
        );

        this.answer.setText(Long.toString(prevEstimQuestion.getActivity().consumption));
    }

    protected abstract void startTimer();
}
