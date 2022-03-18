package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.entities.SoloUser;
import commons.entities.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;

public class SoloResultsCtrl {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private ObservableList<SoloUser> users;

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

    @FXML
    private Text score;

    @FXML
    private Text personalBest;

    @FXML
    private Button quit;

    @FXML
    private Button restart;

    @FXML
    private TableColumn<User, String> tableUsers;

    @FXML
    private TableColumn<User, Long> tableScore;

    @FXML
    private TableView<SoloUser> scoreTable;


    /**
     * Setups the page quit button that redirects to the main page, and fills in the score and personal best
     */

    public void setup() {
        quit.setOnAction(new EventHandler<javafx.event.ActionEvent>() {
            @Override
            public void handle(javafx.event.ActionEvent event) {
                mainCtrl.showHome();
            }
        });

        restart.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                // TODO : restart the solo game
            }
        });

        score.setText(String.format("%d", mainCtrl.getSoloScore()));
        // TODO : add personal best to server side and link it
        setTable();
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
}
