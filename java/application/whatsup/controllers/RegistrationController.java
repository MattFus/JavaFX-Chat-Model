package application.whatsup.controllers;

import application.whatsup.Client.Client;
import application.whatsup.Common.Protocol;
import application.whatsup.SceneHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistrationController implements Initializable {

    @FXML
    private Button cancelButton;

    @FXML
    private Button confirmButton;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label resultLabel;

    @FXML
    private TextField usernameField;

    @FXML
    void onClickCancel(ActionEvent event) throws IOException {
        SceneHandler.getInstance().init((Stage) resultLabel.getScene().getWindow());
    }

    @FXML
    void onClickConfirm(ActionEvent event) throws IOException {
        if(checkField()){
            String res = Client.getInstance().connect(usernameField.getText(), passwordField.getText(), emailField.getText(), Protocol.REGISTRATION);
            if(res.equalsIgnoreCase(Protocol.RECEIVED)) {
                resultLabel.setText("Registrazione avvenuta con successo.");
                SceneHandler.getInstance().setChatWindow();
            }
            else{
                Client.getInstance().reset();
                System.out.println("niente registrazione");
                resultLabel.setText(res);
            }
        }
        resetFields();
    }


    private void resetFields() {
        usernameField.setText("");
        passwordField.setText("");
        emailField.setText("");
    }

    private boolean checkField() {
        //TODO: VARI CONTROLLI SULLA PASSWORD
        if(passwordField.getText().length() < 8) {
            resultLabel.setText("Password must be at least 8 letter");
            return false;
        }
        else if(!containsUpperCase(passwordField.getText())){
            resultLabel.setText("Password must contains an uppercase letter");
            return false;
        }
        else if(!containsSpecialChar(passwordField.getText())){
            return false;
        }
        return true;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        resultLabel.setWrapText(true);
    }

    private boolean containsUpperCase(String password) {
        Pattern pattern = Pattern.compile("[A-Z]"); //<-- at least one
        Matcher matcher = pattern.matcher(password);
        return matcher.find();
    }

    private boolean containsSpecialChar(String password) {
        Pattern pattern = Pattern.compile("[*.!@#$%^&(){}\\[\\]:;<>,.?/~_+=|]"); //<-- at least one
        Matcher matcher = pattern.matcher(password);
        return matcher.find();
    }
}