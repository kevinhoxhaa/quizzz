package client.scenes;

import client.utils.ResourceUtils;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.entities.MultiplayerUser;
import commons.entities.SoloUser;
import commons.entities.User;
import jakarta.ws.rs.WebApplicationException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.ImageCursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;

public class HomeCtrl {

    private static final double HELP_WIDTH = 532.0;
    private static final double HELP_HEIGHT = 404.0;
    private static final int UNAUTHORIZED = 401;
    private static final int FORBIDDEN = 403;
    private static final int USERNAME_LENGTH = 15;
    private static final int TITLE_SIZE = 84;
    private static final int BUTTON_TEXT_SIZE = 42;

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    private Stage dialog;

    @FXML
    private ImageView bulbView;
    @FXML
    private Text logoTitle;

    @FXML
    private Text soloText;

    @FXML
    private Text multiplayerText;

    @FXML
    private Button soloButton;

    @FXML
    private Button multiplayerButton;

    @FXML
    private Button helpButton;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField urlField;

    /**
     * Creates a new home controller instance.
     *
     * @param server   the server utils used for communication
     *                 with the Spring server.
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
     *
     * @throws IOException in case the static how-to layout file is not found
     */
    @FXML
    protected void onHelpButtonClick() throws IOException {
        if (dialog != null) {
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
        dialog.getScene().setCursor(new ImageCursor(new Image("client/images/arrowcursor.png")));
        dialog.show();
    }

    /**
     * Returns a new solo user with the given name
     *
     * @return a solo user
     */
    public SoloUser getSoloUser() {
        String username = usernameField.getText();
        return new SoloUser(username);
    }

    /**
     * Returns a new multiplayer user with the given name
     *
     * @return a multiplayer user
     */
    public MultiplayerUser getMultiplayerUser() {
        String username = usernameField.getText();
        return new MultiplayerUser(username);
    }

    /**
     * Getter for the server
     *
     * @return server
     */
    public ServerUtils getServer() {
        return server;
    }

    /**
     * Returns the server URL the user has connected to
     *
     * @return the server URL
     */
    protected String getServerUrl() {
        return urlField.getText();
    }

    /**
     * Adds the user to the database and redirects them to the first solo game question scene.
     * An error will occur when the filled in server URL or username are invalid.
     */
    @FXML
    protected void onSoloButtonClick() {
        try {
            String serverUrl = urlField.getText();
            SoloUser user = getSoloUser();
            if (!isValidUsername(user)) {
                return;
            }
            mainCtrl.setServerUrl(serverUrl);
            mainCtrl.bindUser(getSoloUser());
        } catch (WebApplicationException e) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);

            switch (e.getResponse().getStatus()) {
                case FORBIDDEN:
                    alert.setContentText("Username cannot be null or empty!");
                    break;
                default:
                    alert.setContentText(e.getMessage());
            }

            alert.showAndWait();
            return;
        } catch (Exception e) {
            invalidURL();
            return;
        }
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
            MultiplayerUser user = getMultiplayerUser();
            if (!isValidUsername(user)) {
                return;
            }
            mainCtrl.setServerUrl(serverUrl.toLowerCase(Locale.ROOT));
            mainCtrl.bindUser(server.addUserMultiplayer(serverUrl, user));
        } catch (WebApplicationException e) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);

            switch (e.getResponse().getStatus()) {
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
        } catch (Exception e) {
            invalidURL();
            return;
        }
        mainCtrl.showWaiting();
    }

    /**
     * Alerts the user about the invalid URL
     */
    private void invalidURL() {
        var alert = new Alert(Alert.AlertType.ERROR);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setContentText("Invalid server URL!");
        alert.showAndWait();
        return;
    }

    /**
     * Checks if the given user object has a valid username
     * @param user the user in question
     * @return a boolean representation of the username's validity
     */
    private boolean isValidUsername(User user) {
        if (user.username.contains(" ") || user.username.length() > USERNAME_LENGTH) {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText("Invalid username!");
            alert.showAndWait();
            return false;
        }
        return true;
    }

    /**
     * Configures the fonts of the home page to the specified
     * pixel-art font resource
     */
    public void setFonts() {

        soloText.setFont(Font.loadFont(
                ResourceUtils.getClientResource("fonts/arcadeclassic.ttf").getPath(),
                BUTTON_TEXT_SIZE
        ));

        multiplayerText.setFont(Font.loadFont(
                ResourceUtils.getClientResource("fonts/arcadeclassic.ttf").getPath(),
                BUTTON_TEXT_SIZE
        ));

        logoTitle.setFont(Font.loadFont(
                ResourceUtils.getClientResource("fonts/ka1.ttf").getPath(),
                TITLE_SIZE
        ));
    }

    /**
     * This method sets the hand cursor when the mouse is hovering the buttons
     */
    @FXML
    public void onButtonHover(){
        Image image = new Image("client/images/handcursor.png");  //pass in the image path
        soloButton.setCursor(new ImageCursor(image));
        multiplayerButton.setCursor(new ImageCursor(image));
        helpButton.setCursor(new ImageCursor(image));
    }
}
