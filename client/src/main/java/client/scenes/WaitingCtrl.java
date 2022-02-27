package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.util.Duration;

public class WaitingCtrl {

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
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(1000), startButton);
        scaleTransition.setFromX(1.0);
        scaleTransition.setFromY(1.0);
        scaleTransition.setByX(0.2);
        scaleTransition.setByY(0.2);
        SequentialTransition transition = new SequentialTransition(
                new PauseTransition(Duration.millis(1000)),
                scaleTransition
        );
        transition.setAutoReverse(true);
        transition.setCycleCount(Animation.INDEFINITE);
        transition.play();
    }
}
