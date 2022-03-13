package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.models.Question;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressIndicator;

public class EstimationQuestionCtrl implements SceneController {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private Question currentQuestion;
    private double startTime;

    @FXML
    private ProgressIndicator countdownCircle;

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
        //TODO figure out if the estimation question has been called by a solo game
        // OR create a new, solo estimation question scene
    }

    @Override
    public void redirect() {
        //TODO
    }
}
