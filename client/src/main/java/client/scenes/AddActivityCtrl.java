package client.scenes;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;

import javax.inject.Inject;

public class AddActivityCtrl {
    private AdminPanelCtrl adminPanelCtrl;
    private MainCtrl mainCtrl;

    @FXML
    private TextField identifier;

    @FXML
    private TextField title;

    @FXML
    private TextField source;

    @FXML
    private TextField answer;

    @FXML
    private TextField imagePath;

    @FXML
    private TextField id;

    @Inject
    public AddActivityCtrl ( AdminPanelCtrl adminPanelCtrl, MainCtrl mainCtrl ) {

        this.adminPanelCtrl = adminPanelCtrl;
        this.mainCtrl = mainCtrl;
    }

    /**
     * Adds the new activity to the repo
     *
     */
    @FXML
    public void onOkButton () {
        if ( identifier.getText().isEmpty() ||
             title.getText().isEmpty() ||
             answer.getText().isEmpty() ||
             source.getText().isEmpty() ||
             imagePath.getText().isEmpty() ) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert = mainCtrl.setAlertStyle(alert);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText ( "All fields ( except id ) have to be completed!" );
            alert.showAndWait();
        } else {
            String idValue = id.getText();
            int idInteger;
            if (idValue.isEmpty()) {
                idInteger = 0;
            } else {
                idInteger = Integer.parseInt(idValue);
            }
            
            int consumption;
            try {
                consumption = Integer.parseInt(answer.getText());
                adminPanelCtrl.addNewActivity(
                        identifier.getText(),
                        title.getText(),
                        consumption,
                        source.getText(),
                        imagePath.getText(),
                        idInteger
                );

                cancel();
            } catch ( Exception e ){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert = mainCtrl.setAlertStyle(alert);
                alert.initModality(Modality.APPLICATION_MODAL);
                alert.setContentText ( "The answer must be a number!" );
                alert.showAndWait();
            }
        }
    }

    /**
     * Cancel button that clears the fields and closes the stage
     *
     */

    @FXML
    public void cancel() {
        clearFields();
        adminPanelCtrl.getActivityAdd().close();
    }

    /**
     * Clears all the fields
     *
     */

    private void clearFields() {
        identifier.clear();
        title.clear();
        answer.clear();
        source.clear();
        imagePath.clear();
    }

    public void keyPressed(KeyEvent e) {
        switch (e.getCode()) {
            case ENTER:
                onOkButton();
                break;
            case ESCAPE:
                cancel();
                break;
            default:
                break;
        }
    }
}
