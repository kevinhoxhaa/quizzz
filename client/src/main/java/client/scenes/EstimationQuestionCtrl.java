package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.entities.MultiplayerUser;
import commons.models.EstimationQuestion;
import commons.models.Question;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class EstimationQuestionCtrl implements SceneController, QuestionNumController {

    private static final double CIRCLE_BORDER_SIZE = 1.7;
    private static final double STANDARD_CIRCLE_BORDER_SIZE = 1.0;

    private static final int POLLING_DELAY = 0;
    private static final int POLLING_INTERVAL = 500;

    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private Question currentQuestion;
    private double startTime;

    @FXML
    private Text questionNum;

    @FXML
    private HBox circles;

    @FXML
    private ProgressIndicator countdownCircle;

    @FXML
    private Text questionDescription;

    /**
     * Creates a controller for the estimation question screen,
     * with the given server and main controller
     * @param server
     * @param mainCtrl
     */
    @Inject
    public EstimationQuestionCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

    /**
     * Initiates the timer countdown and animation
     */
    public void startTimer() {
        mainCtrl.startTimer(countdownCircle, this);
    }

    public void loadQuestion(EstimationQuestion question) {
        currentQuestion = question;
        questionDescription.setText("How much energy in Wh does " + question.getActivity().title + " use?");
    }

    @Override
    public void redirect() {
        Timer answerTimer = new Timer();
        answerTimer.schedule(
                new TimerTask() {

                    @Override
                    public void run() {
                        List<MultiplayerUser> correctUsers =
                                server.answerQuestion(mainCtrl.getServerUrl(), mainCtrl.getGameIndex(),
                                        mainCtrl.getUser().id, mainCtrl.getAnswerCount(), currentQuestion);

                        if(correctUsers.size() == 0) {
                            return;
                        }

                        Platform.runLater(() -> {
                            mainCtrl.showAnswerPage(currentQuestion, mainCtrl.getCorrectPlayersMock());
                        });
                        answerTimer.cancel();
                    }
                }, POLLING_DELAY, POLLING_INTERVAL);
    }

    @Override
    public void onQuit() {
        mainCtrl.bindUser(null);
        mainCtrl.killThread();
        mainCtrl.showHome();
    }

    /**
     * Getter for the circles bar
     * @return circles
     */
    public HBox getCirclesHBox(){
        return circles;
    }

    /**
     * Highlights current question so the user is aware which circle corresponds to his current question
     */
    public void highlightCurrentCircle() {
        Circle circle = (Circle) circles.getChildren().get(mainCtrl.getAnswerCount());
        circle.setFill(Color.DARKGRAY);
        circle.setStrokeWidth(CIRCLE_BORDER_SIZE);
    }

    /**
     * Resets the highlighting of the circle borders
     */
    public void resetHighlight(){
        for(int i=0;i<circles.getChildren().size();i++){
            Circle circle = (Circle) circles.getChildren().get(i);
            circle.setStrokeWidth(STANDARD_CIRCLE_BORDER_SIZE);
        }
    }

    @Override
    public void updateCircleColor(List<Color> colors) {
        for (int i = 0; i < mainCtrl.getAnswerCount(); i++) {
            Circle circle = (Circle) getCirclesHBox().getChildren().get(i);
            circle.setFill(colors.get(i));
        }
    }

    @Override
    public void resetCircleColor() {
        for(int i=0; i<mainCtrl.getQuestionsPerGame();i++){
            Circle circle = (Circle) getCirclesHBox().getChildren().get(i);
            circle.setFill(Color.LIGHTGRAY);
        }
    }

    @Override
    public void updateQuestionNumber(){
        questionNum.setText("" + (mainCtrl.getAnswerCount() + 1));
    }
}
