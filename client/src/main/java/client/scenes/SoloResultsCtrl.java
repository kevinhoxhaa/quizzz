package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.entities.SoloUser;
import commons.entities.User;
import commons.models.SoloGame;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.ImageCursor;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class SoloResultsCtrl extends AbstractRankingCtrl {
    private SoloGame game;

    private ObservableList<SoloUser> users;

    @FXML
    private Button restartButton;

    @FXML
    private TableColumn<User, String> tableUsers;
    @FXML
    private TableColumn<User, Long> tableScore;
    @FXML
    private TableView<SoloUser> scoreTable;


    /**
     * Creates a controller for the solo results page screen, with the given server and mainCtrl parameters.
     *
     * @param server
     * @param mainCtrl
     */
    @Inject
    public SoloResultsCtrl(ServerUtils server, MainCtrl mainCtrl) {
        super(server, mainCtrl);
    }


    /**
     * Sets up the page quit button that redirects to the main page, and fills in the score and personal best,
     * based on the given game object
     *
     * @param game the game object
     */
    public void setup(SoloGame game) {
        this.game = game;

        scoreTableUserName.setText(String.format("%s", mainCtrl.getUser().username));
        scoreTableUserScore.setText(String.format("%d", mainCtrl.getSoloScore()));
        server.addUserSolo(mainCtrl.getServerUrl(), (SoloUser) mainCtrl.getUser());

        scoreTableUserName.setText(String.format("%s", mainCtrl.getUser().username));
        scoreTableUserScore.setText(String.format("%d", mainCtrl.getSoloScore()));
        setTable();
        ranking1stPlayer.setText(users.size() > 0 ? users.get(0).username : "");
        ranking2ndPlayer.setText(users.size() > 1 ? users.get(1).username : "");
        ranking3rdPlayer.setText(users.size() > 2 ? users.get(2).username : "");
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
    public void onQuit() {
        mainCtrl.quitGame(false, false);
    }

    /**
     * Sets up the table for the solo users result page consisting of users with their username
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
        questionNum.setText("" + (game.getCurrentQuestionNum()));
    }

    /**
     * Highlights current question so the user is aware which circle corresponds to his current question
     */
    @Override
    public void highlightCurrentCircle() {
        highlightCurrentCircle(game.getCurrentQuestionNum());
    }

    /**
     * Sets the hover cursor to every button to hand
     */
    @Override
    public void setupHoverCursor() {
        quitButton.setCursor(new ImageCursor(mainCtrl.getHandCursorImage()));
        restartButton.setCursor(new ImageCursor(mainCtrl.getHandCursorImage()));
    }
}
