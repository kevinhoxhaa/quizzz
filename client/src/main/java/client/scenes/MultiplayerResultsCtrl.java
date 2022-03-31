package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class MultiplayerResultsCtrl extends AbstractRankingCtrl{

    @FXML
    private Button rematchButton;

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

        scoreTableUserName.setText( String.format( "%s", mainCtrl.getUser().username) );
        // scoreTableUserScore.setText( String.format( "%d", mainCtrl.getSoloScore()) );
        //TODO: Show all players in the leaderboard.
    }

    /**
     * Indicates that the player wants (or doesn't want) to rematch the players from the last game.
     */
    @FXML
    protected void onRematchButton(){
        //TODO: Make a working rematch button.
    }
    /**
     * Redirects the user to the home page when the quit button is clicked.
     */
    @FXML
    public void onQuit(){
        mainCtrl.quitGame(false, true);
        mainCtrl.bindUser(null);
    }

    /**
     * Updates the number of the current question
     */
    @Override
    public void updateQuestionNumber() {
        questionNum.setText("" + mainCtrl.getAnswerCount());
    }

    /**
     * Highlights current question so the user is aware which circle corresponds to his current question
     */
    @Override
    public void highlightCurrentCircle() {
        highlightCurrentCircle(gameCtrl.getAnswerCount());
    }
}
