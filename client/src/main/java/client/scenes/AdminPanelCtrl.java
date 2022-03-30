package client.scenes;

import client.utils.ServerUtils;
import commons.entities.Activity;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class AdminPanelCtrl {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    private Button addActivity;

    public AdminPanelCtrl ( ServerUtils server, MainCtrl mainCtrl ) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

    @FXML
    public void onAddActivityButton ( String title, int consumption, String source ) {
        server.addActivity( mainCtrl.getServerUrl(), mainCtrl.getGameIndex(), new Activity( title, consumption, source ) );
    }

}
