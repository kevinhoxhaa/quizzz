package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;

public class SoloResultsCtrl{

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    private Text scoreTableUserName;
    @FXML
    private Text scoreTableUserScore;

    @FXML
    private Text personalBest;

    @FXML
    private Button quitButton;

    @FXML
    private Button restartButton;

    /**
     * Creates a controller for the solo results page screen, with the given server and mainCtrl parameters.
     * @param server
     * @param mainCtrl
     */
    @Inject
    public SoloResultsCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }


    /**
     * Setups the page quit button that redirects to the main page, and fills in the score and personal best
     *
     */

    protected void setup() {
        //TODO : Fix this so it shows user's username
        scoreTableUserName.setText( String.format( "%s", "Kevin") );
        scoreTableUserScore.setText( String.format( "%d", mainCtrl.getSoloScore()) );
        //TODO : add personal best to server side and link it
    }

    /**
     * Starts another game when restart button is clicked
     */
    @FXML
    protected void onRestartButton(){
        mainCtrl.resetSoloGame();
        mainCtrl.showQuestion();
    }
    /**
     * Redirects the user to the home page when the quit button is clicked
     */
    @FXML
    protected void onQuitButton(){
        mainCtrl.showHome();
    }
}
