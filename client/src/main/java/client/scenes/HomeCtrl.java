package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;

public class HomeCtrl {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;

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
}
