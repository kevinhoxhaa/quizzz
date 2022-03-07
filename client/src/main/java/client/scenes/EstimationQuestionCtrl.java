package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.models.Question;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.text.Text;

public class EstimationQuestionCtrl {

    private static final double TIMEOUT = 8.0;
    private static final double START_TIME = 7.95;
    private static final double INTERVAL = 0.05;
    private static final int MILLIS = 50;

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
        mainCtrl.startTimer(countdownCircle);
    }
}
