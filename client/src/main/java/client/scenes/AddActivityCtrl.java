package client.scenes;

import client.utils.ServerUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

public class AddActivityCtrl {
    private ServerUtils server;
    private AdminPanelCtrl adminPanelCtrl;

    @FXML
    private Button okButton;

    @FXML
    private TextField Title;

    @FXML
    private TextField Source;

    @FXML
    private TextField Answer;


    public AddActivityCtrl ( ServerUtils server, AdminPanelCtrl adminPanelCtrl ) {
        this.server = server;
        this.adminPanelCtrl = adminPanelCtrl;
    }

    /**
     * Adds the new activity to the repo
     *
     */
    @FXML
    public void onOkButton () {
        adminPanelCtrl.addNewActivity(
                Title.getText(),
                Integer.parseInt( Title.getText() ),
                Source.getText()
        );
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
        Title.clear();
        Answer.clear();
        Source.clear();
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
