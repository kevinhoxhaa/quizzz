package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class RankingCtrl implements Initializable {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    private static Timeline countdown;
    private static final int RANKING_TIMEOUT = 10;

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
    private Text countdownText;

    @FXML
    private Text questionNum;

    @FXML
    private TableView<?> scoreTable;

    @FXML
    private Text userScore;

    /**
     * Resets timer value back to 10, and initializes the countdown sequence.
     */
    public static void startTimeline() {
        countdown.play();
    }

    /**
     * Sets up a timeline with keyFrames that have an interval of one second. This allows us to create a
     * visual countdown timer.
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        countdownText.setText(String.valueOf(RANKING_TIMEOUT));
        countdown = new Timeline(new KeyFrame(Duration.seconds(1), e ->{
            int timeLeft = Integer.parseInt(countdownText.getText());
            countdownText.setText(String.valueOf(timeLeft-1));
        }));

        countdown.setCycleCount(RANKING_TIMEOUT);
        countdown.onFinishedProperty().set(event -> {
            mainCtrl.showQuestion();
            countdownText.setText("10");
        });

    }
}
