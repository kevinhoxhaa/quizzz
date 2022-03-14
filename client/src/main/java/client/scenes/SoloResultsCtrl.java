package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;

public class SoloResultsCtrl {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

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

    @FXML
    private Text score;

    @FXML
    private Text personalBest;

    @FXML
    private Button quit;

    @FXML
    private Button restart;

    /**
     * Setups the page quit button that redirects to the main page, and fills in the score and personal best
     *
     */

    public void setup() {
        quit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                mainCtrl.showHome();
            }
        });

        restart.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //TODO : start a new solo game thorugh the main controller
            }
        });

        score.setText( String.format( "%d", mainCtrl.getSoloScore()) );
        //TODO : add personal best to server side and link it
    }

}
