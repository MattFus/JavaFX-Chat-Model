package application.whatsup.controllers;

import application.whatsup.SceneHandler;
import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class CallController extends AnimationTimer implements Initializable {

    @FXML
    private Button closeButton;

    @FXML
    void onClickCloseCall(ActionEvent event) {
        Thread.currentThread().interrupt();
        try {
            SceneHandler.getInstance().setChatWindow();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @Override
    public void handle(long l) {

    }
}
