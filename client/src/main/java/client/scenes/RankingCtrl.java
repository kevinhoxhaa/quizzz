package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class RankingCtrl implements Initializable, SceneController {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    /**
     * Creates a controller for the ranking page screen, with the given server and mainCtrl parameters.
     * @param server
     * @param mainCtrl
     */
    @Inject
    public RankingCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

    @FXML
    private HBox circles;

    @FXML
    private ProgressIndicator countdownCircle;

    @FXML
    private Text questionNum;

    @FXML
    private TableView<?> scoreTable;

    @FXML
    private Text scoreTableUserSore;

    @FXML
    private Text ranking1stPlayer;

    @FXML
    private Text ranking2ndPlayer;

    @FXML
    private Text ranking3rdPlayer;

    @FXML
    private Text scoreTableUserName;

    /**
     * Initiates the timer countdown and animation
     */
    public void startTimer() {
        mainCtrl.startTimer(countdownCircle, this);
    }

    /**
     * Sets up a timeline with keyFrames that have an interval of one second. This allows us to create a
     * visual countdown timer.
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //startTimer();
        countdownCircle.progressProperty().addListener((ov, oldValue, newValue) -> {
            countdownCircle.applyCss();
            Text text = (Text) countdownCircle.lookup(".text.percentage");
            String progress = text.getText();
            if(progress.equals("Timeout")) {
                // TODO: handle next question
            }
        });
    }

    @Override
    public void redirect() {
        //TODO
    }
}
