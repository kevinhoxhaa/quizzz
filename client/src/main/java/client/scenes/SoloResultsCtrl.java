package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.entities.SoloUser;
import commons.entities.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import commons.models.SoloGame;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;

import java.util.List;

public class SoloResultsCtrl implements QuestionNumController {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private ObservableList<SoloUser> users;

    @FXML
    private Text scoreTableUserName;
    @FXML
    private Text scoreTableUserScore;

    @FXML
    private Text personalBest;

    @FXML
    private Button quitButton;

    @FXML
    private Button restartButton;

    @FXML
    private Text questionNum;

    @FXML
    private HBox circles;

    @FXML
    private Text ranking1stPlayer;

    @FXML
    private Text ranking2ndPlayer;

    @FXML
    private Text ranking3rdPlayer;

    private SoloGame game;

    /**
     * Creates a controller for the solo results page screen, with the given server and mainCtrl parameters.
     *
     * @param server
     * @param mainCtrl
     */
    @Inject
    public SoloResultsCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }


    /**
     * Setups the page quit button that redirects to the main page, and fills in the score and personal best
     *
     * @param game
     * @param colors
     */

    protected void setup(SoloGame game, List<Color> colors) {
        this.game = game;

        updateQuestionNumber();
        updateCircleColor(colors);

        scoreTableUserName.setText(String.format("%s", mainCtrl.getUser().username));
        scoreTableUserScore.setText(String.format("%d", mainCtrl.getSoloScore()));
        server.addUserSolo(mainCtrl.getServerUrl(), (SoloUser) mainCtrl.getUser());

        scoreTableUserName.setText( String.format( "%s", mainCtrl.getUser().username) );
        scoreTableUserScore.setText( String.format( "%d", mainCtrl.getSoloScore()) );
        setTable();
        ranking1stPlayer.setText(users.get ( 0 ).username );
        ranking2ndPlayer.setText(users.get ( 1 ).username );
        ranking3rdPlayer.setText(users.get ( 2 ).username );
        //TODO : add personal best to server side and link it
    }

    /**
     * Starts another game when restart button is clicked
     */
    @FXML
    protected void onRestartButton() {
        mainCtrl.startSoloGame();
    }

    /**
     * Quits the solo game, unbinds the user and redirects the user to the home page.
     */
    @FXML
    protected void onQuitButton() {
        mainCtrl.showHome();
    }

    @FXML
    private TableColumn<User, String> tableUsers;

    @FXML
    private TableColumn<User, Long> tableScore;

    @FXML
    private TableView<SoloUser> scoreTable;


    /**
     * Getter for the current question number
     *
     * @return questionNum
     */
    public Text getQuestionNum() {
        return questionNum;
    }

    /**
     * Getter for the circles bar
     *
     * @return circles
     */
    public HBox getCirclesHBox() {
        return circles;
    }

    /**
     * Updates the color of the past questions' circles on the circle bar
     * (green/red depending on the correctness of the answer)
     *
     * @param colors Is the list of colors of previous answers(green/red depending on their correctness)
     */
    @Override
    public void updateCircleColor(List<Color> colors) {
        for (int i = 0; i < colors.size(); i++) {
            Circle c = (Circle) getCirclesHBox().getChildren().get(i);
            c.setFill(colors.get(i));
        }
    }

    /**
     * Resets the circles colors every time the game starts
     */
    @Override
    public void resetCircleColor() {
        for (int i = 0; i < mainCtrl.getQuestionsPerGame(); i++) {
            Circle circle = (Circle) getCirclesHBox().getChildren().get(i);
            circle.setFill(Color.LIGHTGRAY);
        }
    }

    /**
     * sets up the table for the solo users result page consisting of users with their username
     * and scores in descending order
     */
    public void setTable() {
        tableUsers.setCellValueFactory(new PropertyValueFactory<User, String>("username"));
        tableScore.setCellValueFactory(new PropertyValueFactory<User, Long>("points"));
        this.users = FXCollections.observableList(server.getAllUsersByScore(server.getURL()));

        scoreTable.setItems(users);

    }
    /**
     * Updates the number of the current question (e.g 11/20)
     */
    @Override
    public void updateQuestionNumber() {
        getQuestionNum().setText("" + (game.getCurrentQuestionNum()));
    }
}
