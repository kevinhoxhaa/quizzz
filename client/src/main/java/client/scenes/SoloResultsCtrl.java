package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.entities.User;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import javax.swing.text.TableView;
import java.util.List;

public class SoloResultsCtrl {

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    private HBox circles;
    @FXML
    private Text questionNum;

    @FXML
    private TableView scoreTable;
    @FXML
    private Text scoreTableUserName;
    @FXML
    private Text scoreTableUserScore;

    @FXML
    private Text ranking1stPlayer;
    @FXML
    private Text ranking2ndPlayer;
    @FXML
    private Text ranking3rdPlayer;

    @FXML
    private Button restartButton;
    @FXML
    private Button quitButton;

    /**
     * Creates a controller for the solo results screen, with the given server and main controller.
     * @param server
     * @param mainCtrl
     */
    @Inject
    public SoloResultsCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

    protected void setup(List<User> users) {
        //TODO: Set the solo results screen up with all the users and the corresponding scores in
        // the repository.
    }
}
