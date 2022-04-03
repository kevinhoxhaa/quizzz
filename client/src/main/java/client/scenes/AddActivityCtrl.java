package client.scenes;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

import javax.inject.Inject;

public class AddActivityCtrl {
    private AdminPanelCtrl adminPanelCtrl;

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
    public AddActivityCtrl ( AdminPanelCtrl adminPanelCtrl ) {
        this.adminPanelCtrl = adminPanelCtrl;
    }

    /**
     * Adds the new activity to the repo
     *
     */
    @FXML
    public void onOkButton () {

        String idValue = id.getText();
        int idInteger;
        if ( idValue.isEmpty() ) {
            idInteger = 0;
        } else {
            idInteger = Integer.parseInt(idValue);
        }
        
        adminPanelCtrl.addNewActivity(
                identifier.getText(),
                title.getText(),
                Integer.parseInt( answer.getText() ),
                source.getText(),
                imagePath.getText(),
                idInteger
        );
        cancel();
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
