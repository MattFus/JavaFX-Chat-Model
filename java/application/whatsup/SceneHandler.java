package application.whatsup;

import application.whatsup.Client.Client;
import application.whatsup.controllers.ChatController;
import application.whatsup.controllers.EmojiController;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

public class SceneHandler {

    private Stage stage;
    private Scene scene;
    private static SceneHandler instance = null;

    private SceneHandler(){}

    public static SceneHandler getInstance() {
        if(instance == null)
            instance = new SceneHandler();
        return instance;
    }

    public void init(Stage primaryStage) throws IOException {
        this.stage = primaryStage;
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("login.fxml"));
        scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(this.getClass().getResource("signUpStyle.css").toExternalForm());
        stage.setTitle("Welcome");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public void setRegistrationWindow() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("registration.fxml"));
        stage.setTitle("Sign-Up");
        stage.getScene().setRoot((Parent) fxmlLoader.load());
        stage.show();
    }

    public void setChatWindow() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("chatFrame.fxml"));
        Parent root = (Parent) fxmlLoader.load();
        ChatController controller = fxmlLoader.getController();
        Thread t = new Thread(Client.getInstance());
        t.setDaemon(true); //NEEDED TO CLOSE THREAD WHEN APP CLOSES
        t.start();
        controller.start();
        scene.setRoot(root);
        scene.getStylesheets().clear();
        scene.getStylesheets().add(this.getClass().getResource("chatStyle.css").toExternalForm());
        stage.hide();
        stage.setFullScreen(false);
        stage.setResizable(false);
        stage.setWidth(1130);
        stage.setHeight(680);
        stage.setTitle("WhatsUp!");
        stage.show();
    }


    public void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText("");
        alert.setContentText(message);
        alert.show();
    }

    public void showChangePassword() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("changePassword.fxml"));
        Parent root = (AnchorPane) fxmlLoader.load();
        root.setEffect(new DropShadow());
        Stage tmp = new Stage();
        tmp.setAlwaysOnTop(true);
        tmp.setScene(new Scene(root));
        tmp.setResizable(false);
        tmp.getScene().setFill(Color.TRANSPARENT);
        tmp.initStyle(StageStyle.UNDECORATED);
        tmp.setX(stage.getWidth()/2);
        tmp.setY(stage.getY()+(stage.getHeight()/4));
        tmp.show();
        root.setOnMousePressed(pressEvent -> {
            root.setOnMouseDragged(dragEvent -> {
                tmp.setX(dragEvent.getScreenX() - pressEvent.getSceneX());
                tmp.setY(dragEvent.getScreenY() - pressEvent.getSceneY());
            });
        });
    }

    public void showInfos() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("infos.fxml"));
        Parent root = (AnchorPane) fxmlLoader.load();
        root.setEffect(new DropShadow());
        Stage tmp = new Stage();
        tmp.setAlwaysOnTop(true);
        tmp.setScene(new Scene(root));
        tmp.setResizable(false);
        tmp.getScene().setFill(Color.TRANSPARENT);
        tmp.initStyle(StageStyle.UNDECORATED);
        tmp.setX(stage.getWidth()/2);
        tmp.setY(stage.getY()+(stage.getHeight()/3));
        tmp.show();
        root.setOnMousePressed(pressEvent -> {
            root.setOnMouseDragged(dragEvent -> {
                tmp.setX(dragEvent.getScreenX() - pressEvent.getSceneX());
                tmp.setY(dragEvent.getScreenY() - pressEvent.getSceneY());
            });
        });
    }

    public void showImage(byte[] imgbytes) throws IOException {
        Dialog<Image> dialog = new Dialog<Image>();
        dialog.setTitle("Image");
        Image img = new Image(new ByteArrayInputStream(imgbytes), 700, 600, true, true);
        ImageView image = new ImageView(img);
        dialog.getDialogPane().getChildren().add(image);
        dialog.getDialogPane().setMinHeight(img.getHeight());
        dialog.getDialogPane().setMinWidth(img.getWidth());
        dialog.setX(stage.getX());
        dialog.setY(stage.getY());
        Window window = dialog.getDialogPane().getScene().getWindow();
        window.setOnCloseRequest(event -> window.hide());
        dialog.showAndWait();
    }

    public File showImagePicker() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose an image");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG files", "*.png"));
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JPG files", "*.jpg"));
        File file = chooser.showOpenDialog(stage);
        if(file != null){
            return file;
        }
        return null;
    }

    public File showFilePicker() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose a file to send");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("all files", "*.*"));
        File file = chooser.showOpenDialog(stage);
        if(file != null)
            return file;
        return null;
    }

    public void openEmojiTable(String username, String toUser) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("emojiTable.fxml"));
        Parent root = (BorderPane) fxmlLoader.load();
        EmojiController.getInstance().setUsername(username);
        EmojiController.getInstance().setToUser(toUser);
        Stage tmp = new Stage();
        tmp.setAlwaysOnTop(true);
        tmp.setScene(new Scene(root));
        tmp.setResizable(false);
        tmp.initStyle(StageStyle.UTILITY);
        tmp.setX(stage.getWidth()/2);
        tmp.setY(stage.getY()+(stage.getHeight()/3));
        tmp.show();
    }

}
