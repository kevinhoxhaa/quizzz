package client.scenes;

import client.utils.ServerUtils;
import commons.entities.Activity;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.inject.Inject;
import java.util.List;

public class AdminPanelCtrl {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;

    @FXML
    private Stage activityAdd;

    @FXML
    private Stage activityDelete;

    @FXML
    private TableView activityTable;
    @FXML
    private TableColumn id;
    @FXML
    private TableColumn title;
    @FXML
    private TableColumn consumption;
    @FXML
    private TableColumn source;
    @FXML
    private TableColumn imagePath;

    @Inject
    public AdminPanelCtrl (ServerUtils server, MainCtrl mainCtrl ) {
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
     * Getter for the activity delete stage
     *
     * @return the stage
     */

    public Stage getActivityDelete() {
        return this.activityDelete;
    }

    /**
     * Opens the Activity add Screen
     */

    @FXML
    public void onAddActivityButton () {
        if ( activityAdd != null) {
            activityAdd.show();
            return;
        }

        activityAdd = new Stage();
        activityAdd.initModality(Modality.APPLICATION_MODAL);
        activityAdd.setResizable(false);

        activityAdd.setScene(mainCtrl.getAddActivityScene());
        activityAdd.show();
    }

    /**
     * Opens the Activity delete Screen
     */

    @FXML
    public void onDeleteActivityButton () {
        if ( activityDelete != null) {
            activityDelete.show();
            return;
        }

        activityDelete = new Stage();
        activityDelete.initModality(Modality.APPLICATION_MODAL);
        activityDelete.setResizable(false);

        activityDelete.setScene(mainCtrl.getDeleteActivityScene());
        activityDelete.show();
    }

    /**
     * Adds a new activity to the repo
     *
     * @param source source of the information
     * @param consumption the consumption answer
     * @param title the title of the activity
     * @param imagePath the path to the image
     */

    public void addNewActivity ( String source, int consumption, String title, String imagePath ) {
        server.addActivityToRepo (
                mainCtrl.getServerUrl(),
                new Activity ( title, consumption, source, imagePath )
        );
    }

    /**
     * Populates the table with all the activities from the database
     *
     */

    @FXML
    public void showActivities() {
        List<Activity> activityList = server.getActivities ( server.getURL() );

        ObservableList<Activity> observableList = FXCollections.observableArrayList(activityList);
        activityTable.setItems(observableList);

        id = new TableColumn ( "ID" );
        id.setCellValueFactory( new PropertyValueFactory<>( "id" ) );
        title = new TableColumn ( "Title" );
        title.setCellValueFactory( new PropertyValueFactory<>( "title" ) );
        consumption = new TableColumn ( "Consumption" );
        consumption.setCellValueFactory( new PropertyValueFactory<>( "consumption" ) );
        source = new TableColumn ( "Source" );
        source.setCellValueFactory( new PropertyValueFactory<>( "source" ) );
        imagePath = new TableColumn ( "Image Path" );
        imagePath.setCellValueFactory( new PropertyValueFactory<>( "imagePath" ) );

        activityTable.getColumns().setAll ( id, title, consumption, source, imagePath );
    }

    /**
     * Deletes the activity with the given id
     *
     * @param id the id of the activity to be deleted
     */


    public void deleteActivity ( int id ) {
        Activity activity = server.findActivityByID ( server.getURL(), id );
        server.deleteActivityFromRepo ( server.getURL(), activity );
    }

    /**
     * Returns to the home screen
     *
      */

    @FXML
    public void onQuit() {
        mainCtrl.showHome();
    }

}
