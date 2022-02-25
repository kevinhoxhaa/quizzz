package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Arc;
import javafx.scene.text.Text;


public class MultiplayerQuestionCtrl {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    private StackPane answer1;
    @FXML
    private StackPane answer2;
    @FXML
    private StackPane answer3;

    @FXML
    private ImageView questionImg;
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
    private GridPane emojiBoard;

    @FXML
    private ImageView thumbsup;
    @FXML
    private ImageView thumbsdown;
    @FXML
    private ImageView sad;
    @FXML
    private ImageView heart;
    @FXML
    private ImageView angry;
    @FXML
    private ImageView xd;

    @FXML
    private StackPane doublePoints;
    @FXML
    private StackPane disableIncorrect;
    @FXML
    private StackPane reduceTime;


    @Inject
    public MultiplayerQuestionCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }
}
