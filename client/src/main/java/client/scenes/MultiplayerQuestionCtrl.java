package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
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

import java.awt.event.MouseEvent;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;


public class MultiplayerQuestionCtrl {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    private static final int ANSWER_TOP_ID = 1;
    private static final int ANSWER_MID_ID = 2;
    private static final int ANSWER_BOT_ID = 3;

    @FXML
    private StackPane answerTop;
    @FXML
    private StackPane answerMid;
    @FXML
    private StackPane answerBot;

    @FXML
    private Text question;
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

    private double startTime;

    /**
     * Creates a controller for the multiplayer question screen, with the given server and main controller.
     * @param server
     * @param mainCtrl
     */
    @Inject
    public MultiplayerQuestionCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }


    /**
     * A placeholder method that navigates the user from the WIP static question
     * screen to the WIP static answer screen. In the future, this should just
     * save the game state, as the transition would happen when the timer runs out.
     * @param answerNum The corresponding "ID" of the answer button pressed:
     *                   - answerTop: 1
     *                   - answerMid: 2
     *                   - answerBot: 3
     */
    private void onAnswerClicked(int answerNum){
        //TODO figure out state-transit between the scenes:
        //  - what parameters are we passing through
        //  - which side checks the correctness of the answer
        //  - the code structure of the transit itself
        // also, the body of this method should actually be called when the
        // timer runs out, on click, only a sort of saving (and point-calculation)
        // should occur
        double answeringTime = System.currentTimeMillis() - startTime;
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
        onAnswerClicked(ANSWER_TOP_ID);
    }

    /**
     * The method called when the button answerMid is clicked.
     * Calls the generic method for clicking an answer, specifying that it was the middle button.
     */
    @FXML
    protected void onAnswerMidClicked(){
        onAnswerClicked(ANSWER_MID_ID);
    }

    /**
     * The method called when the button answerBot is clicked.
     * Calls the generic method for clicking an answer, specifying that it was the bottom button.
     */
    @FXML
    protected void onAnswerBotClicked(){
        onAnswerClicked(ANSWER_BOT_ID);
    }

    /**
     * The method called when the cursor enters the button answerTop.
     * Sets answerTop's background color to dark gray.
     */
    @FXML
    protected void enterAnswerTop(){
        answerTop.setBackground(new Background(new BackgroundFill(Color.DARKGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
    }

    /**
     * The method called when the cursor enters the button answerMid.
     * Sets answerMid's background color to dark gray.
     */
    @FXML
    protected void enterAnswerMid(){
        answerMid.setBackground(new Background(new BackgroundFill(Color.DARKGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
    }

    /**
     * The method called when the cursor enters the button answerBot.
     * Sets answerBot's background color to dark gray.
     */
    @FXML
    protected void enterAnswerBot(){
        answerBot.setBackground(new Background(new BackgroundFill(Color.DARKGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
    }

    /**
     * The method called upon loading the question scene, and when the cursor leaves either one of the answer buttons.
     * Resets all answer boxes' background color to light gray.
     */
    @FXML
    protected void resetAnswerColors(){
        answerTop.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
        answerMid.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
        answerBot.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
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
