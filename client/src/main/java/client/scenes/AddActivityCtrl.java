package client.scenes;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

import javax.inject.Inject;

public class AddActivityCtrl {
    private AdminPanelCtrl adminPanelCtrl;

    @FXML
    private TextField title;

    @FXML
    private TextField source;

    @FXML
    private TextField answer;

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
        adminPanelCtrl.addNewActivity(
                title.getText(),
                Integer.parseInt( answer.getText() ),
                source.getText()
        );
        cancel();
    }


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
        title.clear();
        answer.clear();
        source.clear();
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
