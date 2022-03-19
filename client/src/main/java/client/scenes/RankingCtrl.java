package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import java.util.List;


public class RankingCtrl implements SceneController,QuestionNumController {


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
     * Getter for the circles that show past questions' correctness
     * @return circles
     */
    public HBox getCircles(){
        return circles;
    }
    /**
     * Getter for the current question number
     * @return questionNum
     */
    public Text getQuestionNum(){
        return questionNum;
    }

//    /**
//     * Sets up a timeline with keyFrames that have an interval of one second. This allows us to create a
//     * visual countdown timer.
//     * @param location
//     * @param resources
//     */
//    @Override
//    public void initialize(URL location, ResourceBundle resources) {
//        startTimer();
//        countdownCircle.progressProperty().addListener((ov, oldValue, newValue) -> {
//            countdownCircle.applyCss();
//            Text text = (Text) countdownCircle.lookup(".text.percentage");
//            String progress = text.getText();
//            if(progress.equals("Timeout")) {
//                // TODO: handle next question
//            }
//        });
//    }


    @Override
    public void redirect() {
        mainCtrl.showQuestion();
    }

    @Override
    public void onQuit() {
        mainCtrl.bindUser(null);
        mainCtrl.quitGame(false);
    }

    @Override
    public void updateCircleColor(List<Color> colors) {
        for (int i = 0; i < mainCtrl.getAnswerCount(); i++) {
            Circle circle = (Circle) getCircles().getChildren().get(i);
            circle.setFill(colors.get(i));
        }
    }

    @Override
    public void resetCircleColor() {
        for(int i=0; i<mainCtrl.getQuestionsPerGame();i++){
            Circle circle = (Circle) getCircles().getChildren().get(i);
            circle.setFill(Color.LIGHTGRAY);
        }
    }

    @Override
    public void updateQuestionNumber(){
        getQuestionNum().setText("" + (mainCtrl.getAnswerCount()));
    }
}
