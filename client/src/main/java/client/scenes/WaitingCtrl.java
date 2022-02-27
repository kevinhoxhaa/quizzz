package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import javafx.animation.Animation;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.util.Duration;

public class WaitingCtrl {

    public static final double SCALE_START = 1.0;
    public static final double SCALE_END = 0.2;
    public static final int SCALE_DELAY = 1000;

    private ServerUtils server;
    private MainCtrl mainCtrl;

    @FXML
    private Button startButton;

    /**
     * Creates a new waiting controller instance
     * @param server the server util object containing
     *               necessary REST API functionality
     * @param mainCtrl the main controller used for changing
     *                 scenes in the application
     */
    @Inject
    public WaitingCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

    /**
     * Scales the start button on regular intervals to make the static waiting room page
     * more interactive
     */
    public void scaleButton() {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(SCALE_DELAY), startButton);
        scaleTransition.setFromX(SCALE_START);
        scaleTransition.setFromY(SCALE_START);
        scaleTransition.setByX(SCALE_END);
        scaleTransition.setByY(SCALE_END);
        SequentialTransition transition = new SequentialTransition(
                new PauseTransition(Duration.millis(SCALE_DELAY)),
                scaleTransition
        );
        transition.setAutoReverse(true);
        transition.setCycleCount(Animation.INDEFINITE);
        transition.play();
    }

    /**
     * Remove the user from the waiting room and redirect
     * them to the home scene
     */
    @FXML
    protected void onBackButtonClick() {
        // TODO: remove user from the waiting room in the database
        mainCtrl.showHome();
    }

    /**
     * Start a game on the server and redirect all participants
     * to their first question scene
     */
    @FXML
    protected void onStartButtonClick() {
        // TODO: start a game on the server
        mainCtrl.showQuestion();
    }
}
