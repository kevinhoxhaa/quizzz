package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.entities.MultiplayerUser;
import commons.models.Question;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;


public class RankingCtrl extends AbstractRankingCtrl {
    @FXML
    protected ProgressIndicator countdownCircle;

    @FXML
    private TableView<MultiplayerUser> scoreTable;
    @FXML
    private TableColumn<MultiplayerUser, String> usernameColumn;
    @FXML
    private TableColumn<MultiplayerUser, Long> pointsColumn;

    /**
     * Creates a controller for the ranking page screen, with the given server and mainCtrl parameters.
     *
     * @param server
     * @param mainCtrl
     */
    @Inject
    public RankingCtrl(ServerUtils server, MainCtrl mainCtrl) {
        super(server, mainCtrl);
    }

    /**
     * Initiates the timer countdown and animation
     */
    public void startTimer() {
        mainCtrl.startTimer(countdownCircle, this);
    }

    /**
     * Displays the ranked users on the table view according
     * to their points
     *
     * @param users the ranked users to display
     */
    public void setup(List<MultiplayerUser> users) {
        scoreTableUserName.setText(String.format("%s", gameCtrl.getUser().username));
        scoreTableUserScore.setText(String.format("%d", gameCtrl.getUser().points));

        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        pointsColumn.setCellValueFactory(new PropertyValueFactory<>("points"));

        ranking1stPlayer.setText(users.size() > 0 ? users.get(0).username : "");
        ranking2ndPlayer.setText(users.size() > 1 ? users.get(1).username : "");
        ranking3rdPlayer.setText(users.size() > 2 ? users.get(2).username : "");

        gameCtrl.populateRanking(scoreTable, users);
        scoreTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    /**
     * Sets the current game controller
     *
     * @param gameCtrl the current game controller
     */
    public void setGameCtrl(MultiplayerGameCtrl gameCtrl) {
        this.gameCtrl = gameCtrl;
    }

    /**
     * Redirects the player to the next question page.
     * Called when the timer is up.
     */
    @Override
    public void redirect() {
        Question nextQuestion = gameCtrl.fetchQuestion();
        gameCtrl.showQuestion(nextQuestion);
    }

    /**
     * Quits the game.
     * Called when the timer is up.
     */
    @Override
    public void onQuit() {
        mainCtrl.quitGame(false, true);
    }

    /**
     * Updates the question number shown on screen.
     */
    @Override
    public void updateQuestionNumber() {
        questionNum.setText("" + (mainCtrl.getAnswerCount()));
    }

    /**
     * Highlights current question so the user is aware which circle corresponds to his current question
     */
    @Override
    public void highlightCurrentCircle() {
        highlightCurrentCircle(gameCtrl.getAnswerCount());
    }
}
