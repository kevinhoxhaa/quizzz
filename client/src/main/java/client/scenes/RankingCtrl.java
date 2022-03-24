package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.models.Question;
import commons.entities.MultiplayerUser;
import jakarta.ws.rs.WebApplicationException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Modality;

import java.util.List;


public class RankingCtrl implements SceneController, QuestionNumController {


    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private MultiplayerGameCtrl gameCtrl;

    @FXML
    private HBox circles;
    @FXML
    private ProgressIndicator countdownCircle;
    @FXML
    private Text questionNum;
    @FXML
    private TableView<MultiplayerUser> scoreTable;

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
     * Creates a controller for the ranking page screen, with the given server and mainCtrl parameters.
     *
     * @param server
     * @param mainCtrl
     */
    @Inject
    public RankingCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

    /**
     * Initiates the timer countdown and animation
     */
    public void startTimer() {
        mainCtrl.startTimer(countdownCircle, this);
    }

    /**
     * Getter for the circles that show past questions' correctness
     *
     * @return circles
     */
    public HBox getCircles() {
        return circles;
    }

    /**
     * Getter for the current question number
     *
     * @return questionNum
     */
    public Text getQuestionNum() {
        return questionNum;
    }

    /**
     * Fetches the users in the current waiting room and updates
     * the list view and the users on the podium
     * @param serverUrl the url of the server to fetch the users from
     */
    public void fetchUsers(String serverUrl) {
        scoreTable = new TableView();
        try {
            List<MultiplayerUser> users = server.getUsers(serverUrl);
            TableColumn usersColumn = new TableColumn ( "Players" );
            usersColumn.setCellValueFactory( new PropertyValueFactory<>( "username" ) );
            TableColumn scoreColumn = new TableColumn ( "Score" );
            scoreColumn.setCellValueFactory( new PropertyValueFactory<>( "points") );
            scoreTable.getColumns().addAll( usersColumn, scoreColumn );
            for(MultiplayerUser user : users) {
                scoreTable.getItems().add(user);
            }
            scoreColumn.setSortType ( TableColumn.SortType.DESCENDING );
            scoreTable.getSortOrder().add ( scoreColumn );
            ranking1stPlayer.setText( users.get(0).username ) ;
            ranking2ndPlayer.setText( users.get(1).username ) ;
            ranking3rdPlayer.setText( users.get(2).username ) ;

        } catch (WebApplicationException e) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            return;
        }
    }

//    /**
//     * Sets up a timeline with keyFrames that have an interval of one second. This allows us to create a
//     * visual countdown timer.
//     * @param location
//     * @param resources
//     */
//    @Override
//    public void initialize(URL location, ResourceBundle resources) {
//
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

    /**
     * Sets the current game controller
     * @param gameCtrl the current game controller
     */
    public void setGameCtrl(MultiplayerGameCtrl gameCtrl) {
        this.gameCtrl = gameCtrl;
    }

    @Override
    public void redirect() {
        Question nextQuestion = gameCtrl.fetchQuestion();
        gameCtrl.showQuestion(nextQuestion);
    }

    @Override
    public void onQuit() {
        server.removeMultiplayerUser(server.getURL(), gameCtrl.getUser());
        mainCtrl.quitGame(false, true);
        mainCtrl.bindUser(null);
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
        for (int i = 0; i < mainCtrl.getQuestionsPerGame(); i++) {
            Circle circle = (Circle) getCircles().getChildren().get(i);
            circle.setFill(Color.LIGHTGRAY);
        }
    }

    @Override
    public void updateQuestionNumber() {
        getQuestionNum().setText("" + (mainCtrl.getAnswerCount()));
    }
}
