package application.whatsup.controllers;

import application.whatsup.Client.Client;
import application.whatsup.Common.Protocol;
import application.whatsup.SceneHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML
    private Button loginButton;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label resultLabel;

    @FXML
    private Button registerButton;

    @FXML
    private TextField usernameField;

    @FXML
    void onClickLogin(ActionEvent event) throws IOException {
        if(!usernameField.getText().isEmpty() || !passwordField.getText().isEmpty()) {
            String res = Client.getInstance().connect(usernameField.getText(), passwordField.getText(), null, Protocol.LOGIN);
            if (res.equalsIgnoreCase(Protocol.RECEIVED)) {
                SceneHandler.getInstance().setChatWindow();
            } else {
                Client.getInstance().reset();
                resultLabel.setWrapText(true);
                resultLabel.setText(res);
            }
            usernameField.setText("");
            passwordField.setText("");
        }
    }

    @FXML
    void onClickRegister(ActionEvent event) throws IOException {
        SceneHandler.getInstance().setRegistrationWindow();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        resultLabel.setWrapText(true);
    }
}
