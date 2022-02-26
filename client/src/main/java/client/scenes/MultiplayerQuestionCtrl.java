package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Arc;
import javafx.scene.text.Text;


public class MultiplayerQuestionCtrl {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    private StackPane answerTop;
    @FXML
    private StackPane answerMid;
    @FXML
    private StackPane answerBot;

    @FXML
    private Text question;
    @FXML
    private Text questionNum;

    @FXML
    private Arc timer;
    @FXML
    private Text remainingSeconds;

    @FXML
    private HBox circles;

    @FXML
    private StackPane doublePoints;
    @FXML
    private StackPane disableIncorrect;
    @FXML
    private StackPane reduceTime;


    /** Creates a controller for the multiplayer question screen, with the given server and main controller.
     * @param server
     * @param mainCtrl
     */
    @Inject
    public MultiplayerQuestionCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }
}
