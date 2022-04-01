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
    private TextField title;

    @FXML
    private TextField source;

    @FXML
    private TextField answer;


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
                title.getText(),
                Integer.parseInt( title.getText() ),
                source.getText()
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
