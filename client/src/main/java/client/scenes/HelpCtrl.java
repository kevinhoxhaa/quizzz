package client.scenes;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class HelpCtrl {

    @FXML
    private Button closeHelpButton;

    @FXML
    void closeHelp(ActionEvent event) {
        Stage stage = (Stage) closeHelpButton.getScene().getWindow();
        stage.close();
    }

}
