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
import javafx.scene.paint.Color;

public class MultiplayerResultsCtrl extends AbstractRankingCtrl{

    private boolean rematch;

    @FXML
    private Button rematchButton;
    @FXML
    private ProgressIndicator countdownCircle;

    /**
     * Creates a controller for the multiplayer results page screen, with the given server and mainCtrl parameters.
     * @param server
     * @param mainCtrl
     */
    @Inject
    public MultiplayerResultsCtrl(ServerUtils server, MainCtrl mainCtrl) {
        super(server, mainCtrl);
    }

    /**
     * Setups the page quit button that redirects to the main page, and fills in the score and personal best
     */
    public void setup() {

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
        if (rematch) {
            rematch = false;
            server.removeRestartUserID(server.getURL(), gameCtrl.getGameIndex(), gameCtrl.getUser().id);
            rematchButton.setBackground(new Background(
                    new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
        }
        mainCtrl.quitGame(false, true);
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

    /**
     * Disables the rematch button.
     */
    public void disableRematchButton() {
        rematchButton.setOnAction(null);
        rematchButton.setDisable(true);
    }

    /**
     * Enables the rematch button.
     */
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
     * Updates the number of the current question
     */
    @Override
    public void updateQuestionNumber() {
        questionNum.setText("" + gameCtrl.getAnswerCount());
    }

    /**
     * Highlights current question so the user is aware which circle corresponds to his current question
     */
    @Override
    public void highlightCurrentCircle() {
        highlightCurrentCircle(gameCtrl.getAnswerCount());
    }
}
