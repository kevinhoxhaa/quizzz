package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.entities.MultiplayerUser;
import commons.models.Question;
import jakarta.ws.rs.WebApplicationException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;

import java.util.List;


public class RankingCtrl extends AbstractRankingCtrl {
    @FXML
    protected ProgressIndicator countdownCircle;

    @FXML
    private TableView<MultiplayerUser> scoreTable;

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

    /**
     * Sets the current game controller
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
