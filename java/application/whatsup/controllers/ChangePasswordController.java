package application.whatsup.controllers;

import application.whatsup.Client.Client;

import application.whatsup.Common.Protocol;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChangePasswordController implements Initializable{

    @FXML
    private PasswordField confirmNewPasswordField;

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private PasswordField oldPasswordField;

    @FXML
    private Label resultLabel;

    @FXML
    void onClickConfirm(ActionEvent event) {
        if(checkPasswords()){
            Client.getInstance().resetPassword(oldPasswordField.getText(), newPasswordField.getText());
            resultLabel.setText("Password Changed");
        }
    }

    @FXML
    void onClickExit(ActionEvent event) {
        Stage stg = (Stage) oldPasswordField.getScene().getWindow();
        stg.close();
    }

    private boolean checkPasswords(){
        return checkField();
    }

    private boolean checkField() {
        //TODO: VARI CONTROLLI SULLA PASSWORD
        if(oldPasswordField.getText().isEmpty() || newPasswordField.getText().isEmpty() || confirmNewPasswordField.getText().isEmpty()){
            resultLabel.setText("Fill all fields");
            return false;
        }
        else if(!newPasswordField.getText().equals(confirmNewPasswordField.getText())){
            resultLabel.setText("Passwords do not match");
            return false;
        }
        else if(newPasswordField.getText().length() < 8) {
            resultLabel.setText("Password must be at least 8 letter");
            return false;
        }
        else if(!containsUpperCase(newPasswordField.getText())){
            resultLabel.setText("Password must contain an uppercase letter");
            return false;
        }
        else if(!containsSpecialChar(newPasswordField.getText())){
            resultLabel.setText("Password must contain a symbol");
            return false;
        }
        else if(!Client.getInstance().checkPsw(newPasswordField.getText())){
            resultLabel.setText(Protocol.RESETERROR);
        }
        return true;
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        resultLabel.setText("");
    }
}