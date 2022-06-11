package application.whatsup.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class AboutController {

    @FXML
    private Button closeButton;

    @FXML
    private Label label;

    @FXML
    void onClickClose(ActionEvent event) {
        Stage stg = (Stage) label.getScene().getWindow();
        stg.close();
    }

}
