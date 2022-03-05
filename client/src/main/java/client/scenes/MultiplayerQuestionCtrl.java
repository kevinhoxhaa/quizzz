package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.entities.Activity;
import commons.models.Answer;
import commons.models.Question;
import commons.models.ConsumptionQuestion;
import commons.models.ChoiceQuestion;
import commons.models.ComparisonQuestion;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

import static commons.utils.CompareType.SMALLER;
import static commons.utils.CompareType.EQUAL;
import static commons.utils.CompareType.LARGER;


public class MultiplayerQuestionCtrl {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    private Question currentQuestion;

    private double startTime;

    @FXML
    private StackPane answerTop;
    @FXML
    private StackPane answerMid;
    @FXML
    private StackPane answerBot;
    @FXML
    private Text answerTopText;
    @FXML
    private Text answerMidText;
    @FXML
    private Text answerBotText;

    private Answer answerTopAnswer;
    private Answer answerMidAnswer;
    private Answer answerBotAnswer;

    private List<StackPane> answerButtons;
    private StackPane selectedAnswerButton;

    private double secondsTaken;
    private Answer userAnswer;

    @FXML
    private Text activityText;
    @FXML
    private Text questionNum;

    @FXML
    private Arc timer;
    @FXML
    private Text remainingSeconds;

    @FXML
    private HBox circles;

    @FXML
    private StackPane doublePoints;
    @FXML
    private StackPane disableIncorrect;
    @FXML
    private StackPane reduceTime;

    /**
     * Creates a controller for the multiplayer question screen, with the given server and main controller.
     * Creates the list answerButtons for iterating through all of these.
     * @param server
     * @param mainCtrl
     */
    @Inject

    public MultiplayerQuestionCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

    /**
     * Sets up the question page scene:
     *  - Sets up the question/answers according to the type of the question given
     *  - Fills the answerButtons list for iterations
     *  - Resets all buttons to their default colors
     * @param question the question instance upon which the setup is based
     */
    protected void setup(Question question) {
        this.currentQuestion = question;

        switch (question.getType()){
            case CONSUMPTION:
                setupConsumptionQuestion(question);
                break;
            case COMPARISON:
                setupComparisonQuestion(question);
                break;
            case CHOICE:
                setupChoiceQuestion(question);
                break;
            case ESTIMATION:
                setupEstimationQuestion(question);
                break;
        }

        this.answerButtons = new ArrayList<>();
        this.answerButtons.add(answerTop);
        this.answerButtons.add(answerMid);
        this.answerButtons.add(answerBot);

        resetAnswerColors();
    }

    /**
     * Sets up the questions and answers on the page for the given comparison question
     * @param generalQuestion the given question
     */
    private void setupComparisonQuestion(Question generalQuestion) {
        ComparisonQuestion question = (ComparisonQuestion) generalQuestion;

        activityText.setText(
                String.format("Does %s use more, less, or the same amount of energy as %s?",
                        question.getFirstActivity().title, question.getSecondActivity().title)
        );
        answerTopText.setText("MORE");
        answerMidText.setText("EQUAL");
        answerBotText.setText("LESS");

        answerTopAnswer = new Answer(LARGER);
        answerMidAnswer = new Answer(EQUAL);
        answerBotAnswer = new Answer(SMALLER);
    }

    /**
     * Sets up the questions and answers on the page for the given consumption question
     * @param generalQuestion the given question
     */
    private void setupConsumptionQuestion(Question generalQuestion) {
        ConsumptionQuestion question = (ConsumptionQuestion) generalQuestion;

        activityText.setText(
                String.format("How much energy does %s cost?",
                        question.getActivity().title)
        );

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
     * @param generalQuestion the given question
     */
    private void setupChoiceQuestion(Question generalQuestion) {
        ChoiceQuestion question = (ChoiceQuestion) generalQuestion;

        activityText.setText(
                String.format("What could you do instead of %s to consume less energy?",
                        question.getComparedActivity().title)
        );

        List<Activity> answers = question.getActivities();

        //TODO figure out how the answers work exactly (shuffling)
        answerTopText.setText(answers.get(0).toString());
        answerMidText.setText(answers.get(1).toString());
        answerBotText.setText(answers.get(2).toString());

        answerTopAnswer = new Answer(answers.get(0));
        answerMidAnswer = new Answer(answers.get(1));
        answerBotAnswer = new Answer(answers.get(2));
    }

    /**
     * Sets up the questions and answers on the page for the given estimation question
     * Needs to be thought through, will probably be in a different class
     * @param generalQuestion the given question
     */
    private void setupEstimationQuestion(Question generalQuestion) {
        //TODO Deal with estimation questions (they need a whole different scene most probably)
    }


    /**
     * Saves the answer selected last by the user, as well as the amount of time it took.
     * Changes the scene visuals accordingly.
     * @param answerButton the answer button pressed.
     */
    private void onAnswerClicked(StackPane answerButton){

        if(!answerButton.equals(selectedAnswerButton)) {

            currentQuestion.setUserAnswer(userAnswer, getSeconds());

            selectedAnswerButton = answerButton;
            resetAnswerColors();
            answerButton.setBackground(new Background(
                    new BackgroundFill(Color.DARKCYAN, CornerRadii.EMPTY, Insets.EMPTY)));

            for (StackPane answerBtnLoop: answerButtons) {
                answerBtnLoop.setStyle("-fx-border-width: 1; -fx-border-color: black");
                ((Text) answerBtnLoop.getChildren().get(0)).setStyle("-fx-font-weight: normal");
            }
            ((Text) answerButton.getChildren().get(0)).setStyle("-fx-font-weight: bold");
            answerButton.setStyle("-fx-border-width: 2; -fx-border-color: black");
        }

    }

    /**
     * Returns the time since the timer started, in seconds.
     * For now, a placeholder method.
     * @return the time since the timer started, in seconds.
     */
    private double getSeconds() {
        return Math.random(); //placeholder for timer value
    }

    /**
     * Called when the timer is up.
     * Responsible for:
     *  - Disabling inputs
     *  - Sending the question instance back to the server
     *  - Waiting for the list of people who got it right
     *  - Making sure the answer page has all the necessary information
     *  - Redirecting to the answer page
     */
    private void finalizeAndSend(){
        //TODO sending the question instance back to the server
        // and waiting for the list of people who got it right
        mainCtrl.showAnswerPage();
    }

    protected void setStartTime() {
        startTime = System.currentTimeMillis();
    }

    /**
     * The method called when the button answerTop is clicked.
     * Calls the generic method for clicking an answer, specifying that it was the top button.
     */
    @FXML
    protected void onAnswerTopClicked(){
        onAnswerClicked(answerTop);
    }

    /**
     * The method called when the button answerMid is clicked.
     * Calls the generic method for clicking an answer, specifying that it was the middle button.
     */
    @FXML
    protected void onAnswerMidClicked(){
        onAnswerClicked(answerMid);
    }

    /**
     * The method called when the button answerBot is clicked.
     * Calls the generic method for clicking an answer, specifying that it was the bottom button.
     */
    @FXML
    protected void onAnswerBotClicked(){
        onAnswerClicked(answerBot);
    }

    /**
     * The method called when the cursor enters the button answerTop.
     * Sets answerTop's background color according to whether it is selected.
     */
    @FXML
    protected void enterAnswerTop(){
        enterAnswer(answerTop);
    }

    /**
     * The method called when the cursor enters the button answerMid.
     * Sets answerMid's background color according to whether it is selected.
     */
    @FXML
    protected void enterAnswerMid(){
        enterAnswer(answerMid);
    }

    /**
     * The method called when the cursor enters the button answerBot.
     * Sets answerBot's background color according to whether it is selected.
     */
    @FXML
    protected void enterAnswerBot(){
        enterAnswer(answerBot);
    }

    /**
     * A general method for setting an answer button's background color upon the cursor enters it,
     * according to whether it is selected.
     * @param answerBtn The answer button to be recolor.
     */
    private void enterAnswer(StackPane answerBtn){
            if (answerBtn.equals(selectedAnswerButton)) {
                answerBtn.setBackground(new Background(
                        new BackgroundFill(Color.DARKCYAN, CornerRadii.EMPTY, Insets.EMPTY)));
            } else {
                answerBtn.setBackground(new Background(
                        new BackgroundFill(Color.DARKGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
            }
    }

    /**
     * The method called upon loading the question scene, and when the cursor leaves either one of the answer buttons.
     * Resets all answer boxes' background color according to whether they are selected.
     */
    @FXML
    protected void resetAnswerColors(){

        for (StackPane answerBtn: answerButtons) {
            if (answerBtn.equals(selectedAnswerButton)) {
                answerBtn.setBackground(new Background(
                        new BackgroundFill(Color.LIGHTSEAGREEN, CornerRadii.EMPTY, Insets.EMPTY)));
            } else {
                answerBtn.setBackground(new Background(
                        new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
            }
        }
    }

    /**
     * Counts down from a specific amount of seconds to 0 and shows this on the question page
     * to indicate the amount of time a player has left to answer the question. <br>
     * If the timer reaches zero, the player will no longer be able to interact with the answer buttons
     * and will send its received Question object back to the server. <br>
     * The player will be automatically redirected to the answer page when the information about which
     * players got the question right is received from the server.
     * @param totalSeconds The total amount of seconds a players has to answer the question.
     */
    protected void countDown(int totalSeconds) {
        remainingSeconds.setText(Integer.toString(totalSeconds));
        Timeline questionTimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            int newRemainingSeconds = Integer.parseInt(remainingSeconds.getText()) - 1;
            remainingSeconds.setText(Integer.toString(newRemainingSeconds));
            if (newRemainingSeconds == 0) {
                resetAnswerColors();
                disableAnswers();
                //TODO:
                // - Send Question Object back to the server.
                // - Wait for information about which players answered correctly.
                mainCtrl.showAnswerPage();
            }
        }));
        questionTimeline.setCycleCount(totalSeconds);
        questionTimeline.play();
    }

    private void disableAnswers() {
        answerTop.setOnMouseEntered(null);
        answerMid.setOnMouseEntered(null);
        answerBot.setOnMouseEntered(null);
        answerTop.setOnMouseClicked(null);
        answerMid.setOnMouseClicked(null);
        answerBot.setOnMouseClicked(null);
    }
}
