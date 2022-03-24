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
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static commons.utils.CompareType.EQUAL;
import static commons.utils.CompareType.LARGER;
import static commons.utils.CompareType.SMALLER;


public class MultiplayerQuestionCtrl implements SceneController, QuestionNumController {
    private static final double MILLISECONDS_PER_SECONDS = 1000.0;
    private static final double CIRCLE_BORDER_SIZE = 1.7;
    private static final double STANDARD_SIZE = 1.0;

    private static final int POLLING_DELAY = 0;
    private static final int POLLING_INTERVAL = 500;

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    private MultiplayerGameCtrl gameCtrl;
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

    private List<String> correctPlayers;
    private List<StackPane> jokers;

    @FXML
    private Text activityText;
    @FXML
    private Text questionNum;
    @FXML
    private ImageView questionImg;

    @FXML
    private ProgressIndicator countdownCircle;

    @FXML
    private HBox circles;

    @FXML
    private StackPane doublePoints;
    @FXML
    private StackPane removeIncorrect;
    @FXML
    private StackPane reduceTime;
    @FXML
    private Text currentScore;
    @FXML
    private ImageView x2image;
    @FXML
    private ImageView minus1image;
    @FXML
    private ImageView shortenTimeImage;

    /**
     * Creates a controller for the multiplayer question screen, with the given server and main controller.
     * Creates the list answerButtons for iterating through all of these.
     *
     * @param server
     * @param mainCtrl
     */
    @Inject

    public MultiplayerQuestionCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

    /**
     * Sets up the question page scene: <br>
     * - Sets up the question/answers according to the type of the question given <br>
     * - Fills the answerButtons list for iterations <br>
     * - Resets all buttons to their default colors
     *
     * @param question the question instance upon which the setup is based
     */
    protected void setup(Question question) {
        jokers=new ArrayList<>();
        jokers.add(doublePoints);
        jokers.add(removeIncorrect);
        jokers.add(reduceTime);

        for(StackPane joker:jokers){
            if(gameCtrl.getUsedJokers().contains(joker.idProperty().getValue())){
                gameCtrl.disableJokerButton(joker);
            }
        }

        selectedAnswerButton = null;
        this.currentQuestion = question;
        currentScore.setText("Score: " + gameCtrl.getUser().points);

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
        this.answerButtons.add(answerTop);
        this.answerButtons.add(answerMid);
        this.answerButtons.add(answerBot);

        for (StackPane answerBtnLoop : answerButtons) {
            answerBtnLoop.setStyle("-fx-border-width: 1; -fx-border-color: black");
            answerBtnLoop.getChildren().get(0).setStyle("-fx-font-weight: normal");
        }

        resetAnswerColors();
        x2image.setVisible(false);
    }

    /**
     * Sets up the questions and answers on the page for the given comparison question
     *
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
     *
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
     *
     * @param generalQuestion the given question
     */
    private void setupChoiceQuestion(Question generalQuestion) {
        ChoiceQuestion question = (ChoiceQuestion) generalQuestion;

        activityText.setText(
                String.format("What could you do instead of %s to consume less energy?",
                        question.getComparedActivity().title)
        );

        List<Activity> answers = new ArrayList<>();
        for (Activity activity : question.getActivities()) {
            if (!activity.equals(question.getComparedActivity())) {
                answers.add(activity);
            }
        }
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
    private void onAnswerClicked(StackPane answerButton, Answer answer) {
        if (!answerButton.equals(selectedAnswerButton)) {
            currentQuestion.setUserAnswer(answer, getSeconds());

            selectedAnswerButton = answerButton;
            resetAnswerColors();
            answerButton.setBackground(new Background(
                    new BackgroundFill(Color.DARKCYAN, CornerRadii.EMPTY, Insets.EMPTY)));

            for (StackPane answerBtnLoop : answerButtons) {
                answerBtnLoop.setStyle("-fx-border-width: 1; -fx-border-color: black");
                answerBtnLoop.getChildren().get(0).setStyle("-fx-font-weight: normal");
            }
            answerButton.getChildren().get(0).setStyle("-fx-font-weight: bold");
            answerButton.setStyle("-fx-border-width: 2; -fx-border-color: black");
        }

    }

    /**
     * Returns the time since the timer started, in seconds.
     * For now, a placeholder method.
     *
     * @return the time since the timer started, in seconds.
     */
    private double getSeconds() {
        return (System.currentTimeMillis() - startTime) / MILLISECONDS_PER_SECONDS;
    }

    /**
     * Called when the timer is up.
     * Responsible for:
     * - Disabling inputs
     * - Sending the question instance back to the server
     * - Waiting for the list of people who got it right
     * - Making sure the answer page has all the necessary information
     * - Redirecting to the answer page
     */
    public void finalizeAndSend() {
        gameCtrl.postAnswer(currentQuestion);
    }


    /**
     * Captures the exact time the question page started showing used for measuring the time
     * players needed for answering the question.
     */
    protected void setStartTime() {
        startTime = System.currentTimeMillis();
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
     * The method called when the cursor enters the button double points.
     * Sets double points' background color according to whether it is selected.
     */
    @FXML
    protected void enterDoublePoints(){
        enterJoker(doublePoints);
    }

    /**
     * The method called when the cursor enters the button remove incorrect question.
     * Sets remove incorrect question's background color according to whether it is selected.
     */
    @FXML
    protected void enterRemoveIncorrect(){
        enterJoker(removeIncorrect);
    }

    /**
     * The method called when the cursor enters the button reduce time for others.
     * Sets reduce time for others' background color according to whether it is selected.
     */
    @FXML
    protected void enterReduceTime(){
        enterJoker(reduceTime);
    }

    /**
     * A general method for setting an answer button's background color upon the cursor enters it,
     * according to whether it is selected.
     *
     * @param answerBtn The answer button to be recolor.
     */
    private void enterAnswer(StackPane answerBtn) {
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
    public void resetAnswerColors() {

        for (StackPane answerBtn : answerButtons) {
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
     * A general method for setting an answer button's background color upon the cursor enters it,
     * according to whether it is selected.
     *
     * @param jokerBtn The joker button to be recolored.
     */
    private void enterJoker(StackPane jokerBtn) {
        if (!gameCtrl.getUsedJokers().contains(jokerBtn.idProperty().getValue())) {
            jokerBtn.setBackground(new Background(
                    new BackgroundFill(Color.DARKGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
        }
    }

    /**
     * The method called upon loading the question scene, and when the cursor leaves either one of the answer buttons.
     * Resets all answer boxes' background color according to whether they are selected.
     */
    @FXML
    public void resetJokerColors() {

        for (StackPane joker : jokers) {
            if (!gameCtrl.getUsedJokers().contains(joker.idProperty().getValue())) {
                joker.setBackground(new Background(
                        new BackgroundFill(Color.color(gameCtrl.RGB_VALUE,gameCtrl.RGB_VALUE,gameCtrl.RGB_VALUE),
                                CornerRadii.EMPTY, Insets.EMPTY)));
            }
        }
    }

    /**
     * This method is called when the double points joker is clicked.
     * It gives double points for the current question if the answer is correct.
     */
    @FXML
    public void useDoublePoints(){
        gameCtrl.setIsActiveDoublePoints(true);
        gameCtrl.useJoker(doublePoints,x2image);
    }

    /**
     * This method resets the double point jokers so that it can be used again when another game starts
     */
    public void resetDoublePoints(){
        doublePoints.setOnMouseClicked(event -> useDoublePoints());
        gameCtrl.resetJoker(doublePoints);
    }

    /**
     * Initiates the timer countdown and animation
     */
    public void startTimer() {
        mainCtrl.startTimer(countdownCircle, this);
    }

    /**
     * Disables all interaction with the answer buttons.
     */
    public void disableAnswers() {
        answerTop.setOnMouseEntered(null);
        answerMid.setOnMouseEntered(null);
        answerBot.setOnMouseEntered(null);
        answerTop.setOnMouseClicked(null);
        answerMid.setOnMouseClicked(null);
        answerBot.setOnMouseClicked(null);
    }

    public void enableAnswers() {
        answerTop.setOnMouseClicked(event -> onAnswerTopClicked());
        answerMid.setOnMouseClicked(event -> onAnswerMidClicked());
        answerBot.setOnMouseClicked(event -> onAnswerBotClicked());
    }

    @Override
    public void redirect() {
        finalizeAndSend();
    }

    @Override
    public void onQuit() {
        mainCtrl.quitGame(false, true);
        mainCtrl.bindUser(null);
    }

    /**
     * Getter for the circles bar
     *
     * @return circles
     */
    public HBox getCirclesHBox() {
        return circles;
    }

    /**
     * Getter for the text node containing the current question number
     *
     * @return questionNum
     */
    public Text getQuestionNum() {
        return questionNum;
    }

    /**
     * Highlights current question so the user is aware which circle corresponds to his current question
     */
    public void highlightCurrentCircle() {
        Circle circle = (Circle) circles.getChildren().get(gameCtrl.getAnswerCount());
        circle.setFill(Color.DARKGRAY);
        circle.setStrokeWidth(CIRCLE_BORDER_SIZE);
    }

    /**
     * Resets the highlighting of the circle borders
     */
    public void resetHighlight() {
        for (int i = 0; i < circles.getChildren().size(); i++) {
            Circle circle = (Circle) circles.getChildren().get(i);
            circle.setStrokeWidth(STANDARD_SIZE);
        }
    }

    /**
     * Sets the current game controller
     * @param gameCtrl the current game controller
     */
    public void setGameCtrl(MultiplayerGameCtrl gameCtrl) {
        this.gameCtrl = gameCtrl;
    }

    @Override
    public void updateCircleColor(List<Color> colors) {
        for (int i = 0; i < gameCtrl.getAnswerCount(); i++) {
            Circle circle = (Circle) getCirclesHBox().getChildren().get(i);
            circle.setFill(colors.get(i));
        }
    }

    @Override
    public void resetCircleColor() {
        for (int i = 0; i < mainCtrl.getQuestionsPerGame(); i++) {
            Circle circle = (Circle) getCirclesHBox().getChildren().get(i);
            circle.setFill(Color.LIGHTGRAY);
        }
    }

    @Override
    public void updateQuestionNumber() {
        getQuestionNum().setText("" + (gameCtrl.getAnswerCount() + 1));
    }
}
