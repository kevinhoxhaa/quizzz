package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.entities.Activity;
import commons.models.Answer;
import commons.models.ChoiceQuestion;
import commons.models.ComparisonQuestion;
import commons.models.ConsumptionQuestion;
import commons.models.Question;
import commons.models.SoloGame;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
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
import java.util.List;

import static commons.utils.CompareType.EQUAL;
import static commons.utils.CompareType.LARGER;
import static commons.utils.CompareType.SMALLER;


public class SoloQuestionCtrl implements SceneController, QuestionNumController {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    private static final double MILLISECONDS_PER_SECONDS = 1000.0;
    private static final double CIRCLE_BORDER_SIZE = 1.7;
    private static final double STANDARD_CIRCLE_BORDER_SIZE = 1.0;

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
    @FXML
    private Text currentScore;

    private Answer answerTopAnswer;
    private Answer answerMidAnswer;
    private Answer answerBotAnswer;

    private List<StackPane> answerButtons;
    private StackPane selectedAnswerButton;

    private double secondsTaken;
    private Answer userAnswer;

    private List<String> correctPlayers;

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
    private SoloGame game;

    /**
     * Creates a controller for the solo question screen, with the given server and main controller.
     * Creates the list answerButtons for iterating through all of these.
     * @param server
     * @param mainCtrl
     */
    @Inject

    public SoloQuestionCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

    /**
     * Sets up the question page scene: <br>
     *  - Sets up the question/answers according to the type of the question given <br>
     *  - Fills the answerButtons list for iterations <br>
     *  - Resets all buttons to their default colors
     * @param soloGame the game instance
     * @param colors the list of colors corresponding to past questions
     */
    protected void setup(SoloGame soloGame, List<Color> colors) {
        this.game = soloGame;
        currentScore.setText( String.format( "Score: %d", mainCtrl.getSoloScore()) );
        Question question = soloGame.getCurrentQuestion();
        this.currentQuestion = question;
        questionImg.setImage(new Image(currentQuestion.getImagePath()));

        updateCircleColor(colors);
        resetHighlight();
        highlightCurrentCircle();

        selectedAnswerButton = null;

        setStartTime();

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

        updateQuestionNumber();
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
     * @param answerButton The answer button pressed.
     * @param answer The answer corresponding to the answer button.
     */
    private void onAnswerClicked(StackPane answerButton, Answer answer){

        if(!answerButton.equals(selectedAnswerButton)) {

            currentQuestion.setUserAnswer(answer, getSeconds());

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
        return (System.currentTimeMillis() - startTime)/MILLISECONDS_PER_SECONDS;
    }

    /**
     * Called when the timer is up.
     * Responsible for:
     *  - Disabling inputs
     *  - Sending the question instance back to the server
     *  - Making sure the answer page has all the necessary information
     *  - Redirecting to the answer page
     */
    private void finalizeAndSend(){
        disableAnswers();
        mainCtrl.showAnswerPage(currentQuestion);
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
    protected void onAnswerTopClicked(){
        onAnswerClicked(answerTop, answerTopAnswer);
    }

    /**
     * The method called when the button answerMid is clicked.
     * Calls the generic method for clicking an answer, specifying that it was the middle button.
     */
    @FXML
    protected void onAnswerMidClicked(){
        onAnswerClicked(answerMid, answerMidAnswer);
    }

    /**
     * The method called when the button answerBot is clicked.
     * Calls the generic method for clicking an answer, specifying that it was the bottom button.
     */
    @FXML
    protected void onAnswerBotClicked(){
        onAnswerClicked(answerBot, answerBotAnswer);
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
                answerBtn.setStyle("-fx-border-width: 1; -fx-border-color: black");
                ((Text) answerBtn.getChildren().get(0)).setStyle("-fx-font-weight: normal");
            }
        }
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
    private void disableAnswers() {
        answerTop.setOnMouseEntered(null);
        answerMid.setOnMouseEntered(null);
        answerBot.setOnMouseEntered(null);
        answerTop.setOnMouseClicked(null);
        answerMid.setOnMouseClicked(null);
        answerBot.setOnMouseClicked(null);
    }

    /**
     * Called by the timer running out in MainCtrl. Redirects to the answer page.
     */
    @Override
    public void redirect() {
        mainCtrl.showSoloAnswerPage(game);
    }

    /**
     * Getter for the circles bar
     * @return circles
     */
    public HBox getCirclesHBox(){
        return circles;
    }

    /**
     * Getter for the text node containing the current question number
     * @return questionNum
     */
    public Text getQuestionNum(){
        return questionNum;
    }

    /**
     * Highlights current question so the user is aware which circle corresponds to his current question
     */
    public void highlightCurrentCircle() {
        Circle c = (Circle) circles.getChildren().get(game.getCurrentQuestionNum());
        c.setFill(Color.DARKGRAY);
        c.setStrokeWidth(CIRCLE_BORDER_SIZE);
    }

    /**
     * Resets the highlighting of the circle borders
     */
    public void resetHighlight(){
        if(game.getCurrentQuestionNum()>0){
            Circle c = (Circle) circles.getChildren().get(game.getCurrentQuestionNum()-1);
            c.setStrokeWidth(STANDARD_CIRCLE_BORDER_SIZE);
        }
    }

    /**
     * Updates the colors of the little circles based on the array given
     * @param colors Is the list of colors of previous answers(green/red depending on their correctness)
     */
    @Override
    public void updateCircleColor(List<Color> colors) {
        for (int i = 0; i < colors.size(); i++) {
            Circle c = (Circle) getCirclesHBox().getChildren().get(i);
            c.setFill(colors.get(i));
        }
    }

    /**
     * Resets the colors of the little circles to gray.
     */
    @Override
    public void resetCircleColor() {
        for(int i=0; i<mainCtrl.getQuestionsPerGame();i++){
            Circle circle = (Circle) getCirclesHBox().getChildren().get(i);
            circle.setFill(Color.LIGHTGRAY);
        }
    }

    /**
     * Updates the question number on the top of the screen.
     */
    @Override
    public void updateQuestionNumber(){
        getQuestionNum().setText("" + (game.getCurrentQuestionNum()+ 1));
    }
    
    /**
     * Handles the user clicking the quit button.
     */
    @Override
    @FXML
    public void onQuit(){
        mainCtrl.killThread();
        mainCtrl.showHome();
    }
}
