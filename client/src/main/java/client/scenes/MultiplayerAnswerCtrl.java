package client.scenes;

import com.google.inject.Inject;

import client.utils.ServerUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Arc;
import javafx.scene.text.Text;

public class MultiplayerAnswerCtrl {
	private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    private Text question;
    @FXML
    private Text answer;
    @FXML
    private Text answerResponse;
    @FXML
    private Text questionNum;

    @FXML
    private Arc timer;
    @FXML
    private Text remainingSeconds;

    @FXML
    private HBox circles;

    @FXML
    private Text correctPlayer1;
    @FXML
    private Text correctPlayer2;
    @FXML
    private Text correctPlayer3;
    @FXML
    private Text correctPlayer4;
    @FXML
    private Slider slider;
    
    @FXML
    private ImageView thumbsup;
    @FXML
    private ImageView thumbsdown;
    @FXML
    private ImageView sad;
    @FXML
    private ImageView heart;
    @FXML
    private ImageView xd;
    @FXML
    private ImageView angry;


    /**
     * Creates a controller for the multiplayer answer screen, with the given server and main controller.
     * @param server
     * @param mainCtrl
     */
    @Inject
    public MultiplayerAnswerCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }
}
