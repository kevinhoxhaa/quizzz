package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.entities.User;
import jakarta.ws.rs.WebApplicationException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class HomeCtrl {

    private static final double HELP_WIDTH = 532.0;
    private static final double HELP_HEIGHT = 404.0;
    private static final int UNAUTHORIZED = 401;
    private static final int FORBIDDEN = 403;
    private static final int USERNAME_LENGTH = 15;

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    private Stage dialog;

    @FXML
    private ImageView bulbView;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField urlField;

    /**
     * Creates a new home controller instance.
     * @param server the server utils used for communication
     *               with the Spring server.
     * @param mainCtrl the main controller used for transition
     *                 between different scenes.
     */
    @Inject
    public HomeCtrl(ServerUtils server, MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
        this.server = server;
    }

    /**
     * Shows a dialog displaying the rules of the game.
     * @throws IOException in case the static how-to layout file is not found
     */
    @FXML
    protected void onHelpButtonClick() throws IOException {
        if(dialog != null) {
            dialog.show();
            return;
        }

        dialog = new Stage();
        dialog.setMinHeight(HELP_HEIGHT);
        dialog.setMinWidth(HELP_WIDTH);
        dialog.setMaxHeight(HELP_HEIGHT);
        dialog.setMaxWidth(HELP_WIDTH);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setResizable(false);

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/client/scenes/Help.fxml"));
        loader.setController(this);

        ScrollPane dialogPane = loader.load();
        Scene dialogScene = new Scene(dialogPane);
        dialog.setScene(dialogScene);
        dialog.show();
    }

    /**
     * Parses the info from the user input form and creates
     * a User object with the given username
     * @return parsed User object
     */
    public User getUser() {
        String username = usernameField.getText();
        return new User(username);
    }

    /**
     * Getter for the server
     * @return server
     */
    public ServerUtils getServer() {
       return server;
    }

    /**
     * Returns the server URL the user has connected to
     * @return the server URL
     */
    protected String getServerUrl() {
        return urlField.getText();
    }

    /**
     * Adds the user to the database and redirects them to the
     * first solo game question scene
     */
    @FXML
    protected void onSoloButtonClick() {
        // TODO: check if the server is valid and
        //  add the user to the database
        mainCtrl.resetSoloGame();
        mainCtrl.startSoloGame();
    }

    /**
     * Adds the user to the waiting room database and redirects them
     * to the first multiplayer game question scene
     */
    @FXML
    protected void onMultiplayerButtonClick() {
        try {
            String serverUrl = urlField.getText();
            User user = getUser();

            if(user.username.contains(" ") || user.username.length() > USERNAME_LENGTH) {
                var alert = new Alert(Alert.AlertType.ERROR);
                alert.initModality(Modality.APPLICATION_MODAL);
                alert.setContentText("Invalid username!");
                alert.showAndWait();
                return;
            }
            mainCtrl.bindUser(server.addUser(serverUrl, user));
        } catch (WebApplicationException e) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);

            switch(e.getResponse().getStatus()) {
                case UNAUTHORIZED:
                    alert.setContentText("A user with that name already exists on the server!");
                    break;
                case FORBIDDEN:
                    alert.setContentText("Username cannot be null or empty!");
                    break;
                default:
                    alert.setContentText(e.getMessage());
            }

            alert.showAndWait();
            return;
        } catch(Exception e) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText("Invalid server URL!");
            alert.showAndWait();
            return;
        }
        mainCtrl.showWaiting();
        mainCtrl.onClose();
    }
}
