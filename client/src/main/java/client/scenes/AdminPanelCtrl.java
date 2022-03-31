package client.scenes;

import client.utils.ServerUtils;
import commons.entities.Activity;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.beans.EventHandler;
import java.io.IOException;

public class AdminPanelCtrl {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    private Stage activityAdd;

    public AdminPanelCtrl ( ServerUtils server, MainCtrl mainCtrl ) {
        this.server = server;
        this.mainCtrl = mainCtrl;
    }

    /**
     * Getter for the activity add stage
     *
     * @return the stage
     */

    public Stage getActivityAdd () {
        return this.activityAdd;
    }

    /**
     * Opens the Activity add Screen
     *
     * @throws IOException
     */

    @FXML
    public void onAddActivityButton () throws IOException {

        if ( activityAdd != null ) {
            activityAdd.show();
            return ;
        }

        activityAdd = new Stage();
        activityAdd.initModality(Modality.APPLICATION_MODAL);
        activityAdd.setResizable(false);

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/client/scenes/AddActivity.fxml"));
        loader.setController(this);

        ScrollPane addActivityPane = loader.load();
        Scene addActivityScene = new Scene(addActivityPane);
        activityAdd.setScene(addActivityScene);
        activityAdd.show();

    }

    /**
     * Adds a new activity to the repo
     *
     * @param source source of the information
     * @param consumption the consumption answer
     * @param title the title of the activity
     */

    public void addNewActivity ( String source, int consumption, String title ) {
        server.addActivity (
                mainCtrl.getServerUrl(),
                mainCtrl.getGameIndex(),
                new Activity ( title, consumption, source  )
        );
    }



}
