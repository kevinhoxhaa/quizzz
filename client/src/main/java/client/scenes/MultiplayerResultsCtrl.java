package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import java.util.List;

public class MultiplayerResultsCtrl implements QuestionNumController, SceneController {

    private static final int POLLING_DELAY = 0;
    private static final int POLLING_INTERVAL = 500;

    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private MultiplayerGameCtrl gameCtrl;

    private static final double CIRCLE_BORDER_SIZE = 1.7;
    private static final double STANDARD_CIRCLE_BORDER_SIZE = 1.0;

    private boolean rematch;

    @FXML
    private Text scoreTableUserName;
    @FXML
    private Text scoreTableUserScore;

    @FXML
    private Text personalBest;

    @FXML
    private Button quitButton;

    @FXML
    private Button rematchButton;

    @FXML
    private Text questionNum;

    @FXML
    private HBox circles;

    @FXML
    private ProgressIndicator countdownCircle;

    /**
     * Creates a controller for the multiplayer results page screen, with the given server and mainCtrl parameters.
     * @param server
     * @param mainCtrl
     */
    @Inject
    public MultiplayerResultsCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

    /**
     * Setups the page quit button that redirects to the main page, and fills in the score and personal best
     */

    protected void setup() {
        enableRematchButton();
        scoreTableUserName.setText( String.format( "%s", mainCtrl.getUser().username) );
        scoreTableUserScore.setText( String.format( "%d", mainCtrl.getSoloScore()) );
        //TODO: Show all players in the leaderboard.
    }

    /**
     * Sets the current game controller
     * @param gameCtrl the current game controller
     */
    public void setGameCtrl(MultiplayerGameCtrl gameCtrl) {
        this.gameCtrl = gameCtrl;
    }

    /**
     * Indicates that the player wants (or doesn't want) to rematch the players from the last game.
     */
    @FXML
    protected void onRematchButton(){
        rematch = !rematch;
        if (rematch) {
            server.addRestartUserID(server.getURL(), gameCtrl.getGameIndex(), gameCtrl.getUser().id);
            rematchButton.setBackground(new Background(
                    new BackgroundFill(Color.DARKCYAN, CornerRadii.EMPTY, Insets.EMPTY)));
        } else {
            server.removeRestartUserID(server.getURL(), gameCtrl.getGameIndex(), gameCtrl.getUser().id);
            rematchButton.setBackground(new Background(
                    new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
        }
    }
    /**
     * Redirects the user to the home page when the quit button is clicked.
     */
    @Override
    @FXML
    public void onQuit(){
        server.removeMultiplayerUser(server.getURL(), gameCtrl.getUser());
        mainCtrl.quitGame(false, true);
        mainCtrl.bindUser(null);
    }

    /**
     * Redirects the user to the question page if the user clicked rematch
     * and after a new game has started, otherwise, the user is redirected to the home page.
     */
    @Override
    public void redirect() {
        if (rematch) {
            gameCtrl.resetGameCtrl();
            rematch = false;
            rematchButton.setBackground(new Background(
                    new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
            String serverUrl = mainCtrl.getServerUrl();
            gameCtrl.showQuestion(server.restartGame(serverUrl, gameCtrl.getGameIndex(),
                    gameCtrl.getUser().id));
        } else {
            disableRematchButton();
        }
    }

    public void disableRematchButton() {
        rematchButton.setOnAction(null);
        rematchButton.setDisable(true);
    }

    public void enableRematchButton() {
        rematchButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                onRematchButton();
            }
        });
        rematchButton.setDisable(false);
    }

    /**
     * Initiates the timer countdown and animation
     */
    public void startTimer() {
        mainCtrl.startTimer(countdownCircle, this);
    }

    /**
     * Getter for the current question number
     * @return questionNum
     */
    public Text getQuestionNum(){
        return questionNum;
    }

    /**
     * Getter for the circles bar
     * @return circles
     */
    public HBox getCirclesHBox(){
        return circles;
    }

    /**
     * Updates the color of the past questions' circles on the circle bar
     * (green/red depending on the correctness of the answer)
     *
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
     * Resets the circles colors every time the game starts
     */
    @Override
    public void resetCircleColor() {
        for(int i=0; i<mainCtrl.getQuestionsPerGame();i++){
            Circle circle = (Circle) getCirclesHBox().getChildren().get(i);
            circle.setFill(Color.LIGHTGRAY);
        }
    }

    /**
     * Updates the number of the current question
     */
    @Override
    public void updateQuestionNumber() {
        getQuestionNum().setText("" + gameCtrl.getAnswerCount());
    }

    /**
     * Highlights current question so the user is aware which circle corresponds to his current question
     */
    public void highlightCurrentCircle() {
        Circle c = (Circle) circles.getChildren().get(gameCtrl.getAnswerCount());
        c.setFill(Color.DARKGRAY);
        c.setStrokeWidth(CIRCLE_BORDER_SIZE);
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
}
