package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.entities.User;
import commons.models.SoloGame;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;

import java.util.ArrayList;

public class SoloResultsCtrl {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private ObservableList<User> users;

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
        this.users = new FilteredList<>();
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
    private TableColumn<User, Long> tableScores;

    @FXML
    private TableView<User> scoreTable;


    /**
     * Setups the page quit button that redirects to the main page, and fills in the score and personal best
     */

    public void setup() {
        quit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                mainCtrl.showHome();
            }
        });

        restart.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //TODO : start a new solo game thorugh the main controller
            }
        });

        score.setText(String.format("%d", mainCtrl.getSoloScore()));
        //TODO : add personal best to server side and link it
    }



    /**
     * sets up the table for the solo users result page consisting of users with their username
     * and scores in descending order
     */
    public void setTable() {
        tableUsers.setCellValueFactory(new PropertyValueFactory<>("username"));
        tableScores.setCellValueFactory(new PropertyValueFactory<>("points"));
        ArrayList<User> test  = server.getAllUsersByScore(server.getURL());
        for(User u: test){
            users.add(u);
        }

        scoreTable.setItems(users);

    }
}
