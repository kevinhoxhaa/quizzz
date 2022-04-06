package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.entities.Activity;
import commons.models.Answer;
import commons.models.ChoiceQuestion;
import commons.models.ComparisonQuestion;
import commons.models.ConsumptionQuestion;
import commons.models.Question;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static commons.utils.CompareType.EQUAL;
import static commons.utils.CompareType.LARGER;
import static commons.utils.CompareType.SMALLER;

public abstract class AbstractMultichoiceQuestionCtrl extends AbstractQuestionCtrl{
    protected Question currentQuestion;

    protected Answer answerTopAnswer;
    protected Answer answerMidAnswer;
    protected Answer answerBotAnswer;

    protected List<StackPane> answerButtons;
    protected StackPane selectedAnswerButton;
    protected List<Pair<StackPane, Answer>> answerButtonPairs;
    protected StackPane disabledAnswer;

    @FXML
    protected StackPane answerTop;
    @FXML
    protected StackPane answerMid;
    @FXML
    protected StackPane answerBot;
    @FXML
    protected Label answerTopText;
    @FXML
    protected Label answerMidText;
    @FXML
    protected Label answerBotText;

    private static final double TIMER_LENGTH = 10.0;

    /**
     * Creates a controller for the question screen, with the given server and main controller.
     *
     * @param server
     * @param mainCtrl
     */
    @Inject
    protected AbstractMultichoiceQuestionCtrl(ServerUtils server, MainCtrl mainCtrl) {
        super(server, mainCtrl);
    }

    /**
     * Sets up the question page scene: <br>
     * - Sets up the question/answers according to the type of the question given <br>
     * - Fills the answerButtons list for iterations <br>
     * - Resets all buttons to their default colors
     *
     * @param question the question instance upon which the setup is based
     * @param points the score of the player
     */
    protected void setup(Question question, long points) {
        this.currentQuestion = question;
        this.selectedAnswerButton = null;

        super.setup(question, points);

        switch (question.getType()) {
            case CONSUMPTION:
                setupConsumptionQuestion(question);
                break;
            case COMPARISON:
                setupComparisonQuestion(question);
                break;
            case CHOICE:
                setupChoiceQuestion(question);
                break;
        }

        this.answerButtons = new ArrayList<>();
        answerButtons.add(answerTop);
        answerButtons.add(answerMid);
        answerButtons.add(answerBot);

        this.answerButtonPairs = new ArrayList<>();
        answerButtonPairs.add(new Pair(answerTop, answerTopAnswer));
        answerButtonPairs.add(new Pair(answerMid, answerMidAnswer));
        answerButtonPairs.add(new Pair(answerBot, answerBotAnswer));

        resetAnswerButtonHighlights();
        resetAnswerColors();
    }

    /**
     * Sets up the questions and answers on the page for the given comparison question
     *
     * @param generalQuestion the given question
     */
    private void setupComparisonQuestion(Question generalQuestion) {
        ComparisonQuestion question = (ComparisonQuestion) generalQuestion;

        answerTopText.setText(question.getFirstActivity().title);
        answerMidText.setText("EQUAL");
        answerBotText.setText(question.getSecondActivity().title);

        answerTopAnswer = new Answer(LARGER);
        answerMidAnswer = new Answer(EQUAL);
        answerBotAnswer = new Answer(SMALLER);
    }

    /**
     * Sets up the questions and answers on the page for the given consumption question
     *
     * @param generalQuestion the given question
     */
    private void setupConsumptionQuestion(Question generalQuestion) {
        ConsumptionQuestion question = (ConsumptionQuestion) generalQuestion;

        List<Long> answers = question.getAnswers();

        answerTopText.setText(answers.get(0).toString());
        answerMidText.setText(answers.get(1).toString());
        answerBotText.setText(answers.get(2).toString());

        answerTopAnswer = new Answer(answers.get(0));
        answerMidAnswer = new Answer(answers.get(1));
        answerBotAnswer = new Answer(answers.get(2));
    }

    /**
     * Sets up the questions and answers on the page for the given choice question
     *
     * @param generalQuestion the given question
     */
    private void setupChoiceQuestion(Question generalQuestion) {
        ChoiceQuestion question = (ChoiceQuestion) generalQuestion;

        List<Activity> answers = question.getActivities();
        answers.remove(question.getComparedActivity());
        Collections.shuffle(answers);

        answerTopText.setText(answers.get(0).title);
        answerMidText.setText(answers.get(1).title);
        answerBotText.setText(answers.get(2).title);

        answerTopAnswer = new Answer(answers.get(0));
        answerMidAnswer = new Answer(answers.get(1));
        answerBotAnswer = new Answer(answers.get(2));
    }

    /**
     * Saves the answer selected last by the user, as well as the amount of time it took.
     * Changes the scene visuals accordingly.
     *
     * @param answerButton The answer button pressed.
     * @param answer       The answer corresponding to the answer button.
     */
    protected void onAnswerClicked(StackPane answerButton, Answer answer) {
        if (!answerButton.equals(selectedAnswerButton)) {
            currentQuestion.setUserAnswer(answer, getSeconds());

            selectedAnswerButton = answerButton;
            resetAnswerColors();
            answerButton.setBackground(new Background(
                    new BackgroundFill(Color.web("#D2B4DE"), CornerRadii.EMPTY, Insets.EMPTY)));

            resetAnswerButtonHighlights();
            answerButton.setStyle("-fx-border-width: 5; -fx-border-color: black");
        }
    }

    /**
     * The method called when the button answerTop is clicked.
     * Calls the generic method for clicking an answer, specifying that it was the top button.
     */
    @FXML
    protected void onAnswerTopClicked() {
        onAnswerClicked(answerTop, answerTopAnswer);
    }

    /**
     * The method called when the button answerMid is clicked.
     * Calls the generic method for clicking an answer, specifying that it was the middle button.
     */
    @FXML
    protected void onAnswerMidClicked() {
        onAnswerClicked(answerMid, answerMidAnswer);
    }

    /**
     * The method called when the button answerBot is clicked.
     * Calls the generic method for clicking an answer, specifying that it was the bottom button.
     */
    @FXML
    protected void onAnswerBotClicked() {
        onAnswerClicked(answerBot, answerBotAnswer);
    }

    /**
     * The method called when the cursor enters the button answerTop.
     * Sets answerTop's background color according to whether it is selected.
     */
    @FXML
    protected void enterAnswerTop() {
        enterAnswer(answerTop);
    }

    /**
     * The method called when the cursor enters the button answerMid.
     * Sets answerMid's background color according to whether it is selected.
     */
    @FXML
    protected void enterAnswerMid() {
        enterAnswer(answerMid);
    }

    /**
     * The method called when the cursor enters the button answerBot.
     * Sets answerBot's background color according to whether it is selected.
     */
    @FXML
    protected void enterAnswerBot() {
        enterAnswer(answerBot);
    }

    /**
     * A general method for setting an answer button's background color upon the cursor enters it,
     * according to whether it is selected.
     *
     * @param answerBtn The answer button to recolor
     */
    protected void enterAnswer(StackPane answerBtn) {
        if (answerBtn.equals(selectedAnswerButton)) {
            answerBtn.setBackground(new Background(
                    new BackgroundFill(Color.web("#D2B4DE"), CornerRadii.EMPTY, Insets.EMPTY)));
        } else if (answerBtn.equals(disabledAnswer)) {
            answerBtn.setBackground(new Background(
                    new BackgroundFill(Color.web("#F5B7B1"), CornerRadii.EMPTY, Insets.EMPTY)));
        } else {
            answerBtn.setBackground(new Background(
                    new BackgroundFill(Color.web("#85C1E9"), CornerRadii.EMPTY, Insets.EMPTY)));
        }
    }

    /**
     * The method called upon loading the question scene, and when the cursor leaves either one of the answer buttons.
     * Resets all answer boxes' background color according to whether they are selected.
     */
    @FXML
    protected void resetAnswerColors() {
        for (StackPane answerBtn : answerButtons) {
            resetAnswerColors(answerBtn);
        }
    }

    /**
     * The method called upon loading the question scene, and when the cursor leaves the given answer button .
     * Resets the given answer box's background color according to whether they are selected.
     * @param answerBtn The answer button to be recolored.
     */
    @FXML
    protected void resetAnswerColors(StackPane answerBtn) {
        if (answerBtn.equals(selectedAnswerButton)) {
            answerBtn.setBackground(new Background(
                    new BackgroundFill(Color.web("#D2B4DE"), CornerRadii.EMPTY, Insets.EMPTY)));
        } else if (answerBtn.equals(disabledAnswer)) {
            answerBtn.setBackground(new Background(
                    new BackgroundFill(Color.web("#F5B7B1"), CornerRadii.EMPTY, Insets.EMPTY)));
        } else {
            answerBtn.setBackground(new Background(
                    new BackgroundFill(Color.web("#D6EAF8"), CornerRadii.EMPTY, Insets.EMPTY)));
            answerBtn.setStyle("-fx-border-width: 4; -fx-border-color: black");
        }
    }

    /**
     * Resets all answer buttons such that they have "regular" border thickness
     * and the text inside is also regular (instead of being bold).
     */
    private void resetAnswerButtonHighlights() {
        for (StackPane answerBtn : answerButtons) {
            answerBtn.setStyle("-fx-border-width: 4; -fx-border-color: black");
        }
    }

    /**
     * Returns the length of the timer in this scene
     * @return the length of the timer in this scene
     */
    @Override
    public double getTimerLength(){
        return TIMER_LENGTH;
    }
}
