package client.scenes;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;

import javax.inject.Inject;

public class DeleteActivityCtrl {
    private AdminPanelCtrl adminPanelCtrl;

    @FXML
    private TextField id;


    @Inject
    public DeleteActivityCtrl ( AdminPanelCtrl adminPanelCtrl ) {
        this.adminPanelCtrl = adminPanelCtrl;
    }

    /**
     * Adds the new activity to the repo
     *
     */
    @FXML
    public void onOkButton () {
        if ( id.getText().isEmpty() ) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText("All fields ( except id ) have to be completed!");
            alert.showAndWait();
        } else {
            adminPanelCtrl.deleteActivity(
                    Integer.parseInt(id.getText())
            );
            cancel();
        }
    }

    /**
     * Cancel button that clears the fields and closes the stage
     *
     */

    @FXML
    public void cancel() {
        clearFields();
        adminPanelCtrl.getActivityDelete().close();
    }

    /**
     * Clears all the fields
     *
     */

    private void clearFields() {
       id.clear();
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
