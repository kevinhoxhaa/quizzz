package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class HomeCtrl {

    private static final double HELP_WIDTH = 532.0;
    private static final double HELP_HEIGHT = 404.0;

    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    private Stage dialog;

    @FXML
    private ImageView bulbView;

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
}
