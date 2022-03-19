package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.entities.MultiplayerUser;
import commons.entities.User;
import commons.models.Question;
import jakarta.ws.rs.WebApplicationException;
import javafx.animation.Animation;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Modality;
import javafx.util.Duration;

import java.util.List;

public class WaitingCtrl {

    public static final double SCALE_START = 1.0;
    public static final double SCALE_END = 0.2;
    public static final int SCALE_DELAY = 1000;

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    private Button startButton;

    @FXML
    private ListView usersList;

    @FXML
    private Label counterLabel;

    /**
     * Creates a new waiting controller instance
     * @param server the server util object containing
     *               necessary REST API functionality
     * @param mainCtrl the main controller used for changing
     *                 scenes in the application
     */
    @Inject
    public WaitingCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

    /**
     * Scales the start button on regular intervals to make the static waiting room page
     * more interactive
     */
    public void scaleButton() {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(SCALE_DELAY), startButton);
        scaleTransition.setFromX(SCALE_START);
        scaleTransition.setFromY(SCALE_START);
        scaleTransition.setByX(SCALE_END);
        scaleTransition.setByY(SCALE_END);
        SequentialTransition transition = new SequentialTransition(
                new PauseTransition(Duration.millis(SCALE_DELAY)),
                scaleTransition
        );
        transition.setAutoReverse(true);
        transition.setCycleCount(Animation.INDEFINITE);
        transition.play();
    }

    /**
     * Fetches the users in the current waiting room and updates
     * the list view
     */
    public void fetchUsers() {
        String serverUrl = mainCtrl.getServerUrl();
        usersList.getItems().clear();
        long userId = mainCtrl.getUser().id;
        try {
            List<MultiplayerUser> users = server.getUsers(serverUrl);

            if(!users.contains(mainCtrl.getUser())) {
                Integer gameIndex = server.findGameIndex(serverUrl, userId);
                mainCtrl.setGameIndex(gameIndex);
                Question firstQuestion = server.getQuestion(serverUrl, gameIndex, 0);
                mainCtrl.stopWaitingTimer();
                mainCtrl.showQuestion(firstQuestion);
            }

            for(MultiplayerUser user : users) {
                usersList.getItems().add(user.username);
            }
            counterLabel.setText(String.format("%d player(s) in this room:", users.size()));
        } catch (WebApplicationException e) {
            System.out.println("User not found!");
        }
    }

    /**
     * Remove the user from the waiting room and redirect
     * them to the home scene, while deleting them from the database
     */
    @FXML
    protected void onBackButtonClick() {
        User user= mainCtrl.getUser();
        server.removeMultiplayerUser(server.getURL(),user);
        mainCtrl.bindUser(null);
        mainCtrl.showHome();
        mainCtrl.stopWaitingTimer();
    }


    /**
     * Start a game on the server and redirect all participants
     * to their first question scene
     */
    @FXML
    protected void onStartButtonClick() {
        try {
            String serverUrl = mainCtrl.getServerUrl();
            Integer gameIndex = server.startGame(serverUrl);
            Question firstQuestion = server.getQuestion(serverUrl, gameIndex, 0);
            mainCtrl.stopWaitingTimer();
            mainCtrl.showQuestion(firstQuestion);
        } catch (WebApplicationException e) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }
}
