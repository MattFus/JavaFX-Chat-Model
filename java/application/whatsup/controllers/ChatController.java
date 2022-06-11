package application.whatsup.controllers;

import application.whatsup.Client.Client;
import application.whatsup.Client.Contact;
import application.whatsup.Common.AudioLabel;
import application.whatsup.Common.FileLabel;
import application.whatsup.Common.Protocol;
import application.whatsup.SceneHandler;
import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.util.Pair;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Vector;

public class ChatController extends AnimationTimer implements Initializable {
    //utilities
    private Vector<Contact> contacts;
    private static String username;
    private Image userImg;
    private Vector<Pane> contactLabels;


    //Chat----------------------------------------------------------
    @FXML
    private VBox allMessages;

    @FXML
    private ScrollPane allMessagesScrollArea;

    @FXML
    private SplitPane chatInfoSplitPane;

    @FXML
    private GridPane contact_include_gridPane;

    @FXML
    private Label contact_nameField;

    @FXML
    private Button micButton;

    @FXML
    private Circle recDotIndicator;

    @FXML
    private TextArea messageArea;

    @FXML
    private Button sendButton;

    @FXML
    private BorderPane chatBorderPane;

    //Contacts------------------------------------------

    @FXML
    private Circle userAccountImage;

    @FXML
    private VBox contactsVBox;

    @FXML
    private ScrollPane contactsScrollPane;

    @FXML
    private TextField searchField;

    @FXML
    private AnchorPane contactsAnchorPane;

    //ContactInfo panel ------------------------------------------
    private AnchorPane contactInfo;

    private long previousTime = 0;
    private long frequency = 500 * 1000000;

    @Override
    public void handle(long now) {
        if (Client.getInstance().getContactsModified()) {
            contacts = Client.getInstance().getUsers();
            contactsVBox.getChildren().clear();
            contactLabels = new Vector<Pane>();
            for (Contact c : contacts) {
                if (!c.getUsername().equalsIgnoreCase(username)) {
                    contactLabels.add(addContact(c.getUsername()));
                }
            }
        }
        //TODO: SEARCHFIELD
        if (contactLabels != null){
            for (Pane p : contactLabels) {
                if (!p.getId().contains(searchField.getText())) {
                    contactsVBox.getChildren().remove(p);
                } else {
                    if (!contactsVBox.getChildren().contains(p)) {
                        contactsVBox.getChildren().add(p);
                    }
                }
            }
        }
        if(now - previousTime >= frequency){
            if(!contact_nameField.getText().isEmpty()) {
                //TODO: NEED TO TAKE ONLY "contact_nameField" MESSAGES
                String userChat = contact_nameField.getText();
                Vector<Pair<String, String>> messages = Client.getInstance().getMessages(userChat); //<-- HERE
                Vector<Pair<String, byte[]>> audioMessages = Client.getInstance().getAudioMessages(userChat); //<-- HERE
                Vector<Pair<String, String>> emojiMessages = Client.getInstance().getEmojiMessages(userChat); //<-- HERE
                Vector<Pair<String, File>> fileMessages = Client.getInstance().getFileMessages(userChat);
                Vector<Pair<String, byte[]>> imageMessages = Client.getInstance().getImageMessages(userChat);
                //TODO: MESSAGES
                if(messages != null && !messages.isEmpty() && allMessages != null){
                    for(Pair<String,String> parsed : messages) {
                        if (parsed.getKey().equalsIgnoreCase(username)) {
                            //MY MESSAGE
                            StackPane flow = messagesMaker(parsed.getValue(), true);
                            allMessages.getChildren().add(flow);
                        } else if (parsed.getKey().equalsIgnoreCase(userChat)) {
                            //RECEIVED FROM ANOTHER USER
                            StackPane flow = messagesMaker(parsed.getValue(), false);
                            allMessages.getChildren().add(flow);
                        }
                    }
                }
                //TODO: AUDIO MESSAGES
                if(audioMessages != null && !audioMessages.isEmpty() && allMessages != null){
                    for(Pair<String, byte[]> parsed : audioMessages) {
                        if (parsed.getKey().equalsIgnoreCase(username)) {
                            //MY AUDIO MESSAGE
                            StackPane flow = audioMessageMaker(parsed, true);
                            allMessages.getChildren().add(flow);
                        } else if (parsed.getKey().equalsIgnoreCase(userChat)) {
                            //RECEIVED FROM ANOTHER USER
                            StackPane flow = audioMessageMaker(parsed, false);
                            allMessages.getChildren().add(flow);
                        }
                    }
                }
                //TODO: EMOTICON MESSAGES
                if(emojiMessages != null && !emojiMessages.isEmpty() && allMessages != null){
                    for(Pair<String, String> parsed : emojiMessages) {
                        if (parsed.getKey().equalsIgnoreCase(username)) {
                            StackPane flow = emojiMessagesMaker(parsed, true);
                            allMessages.getChildren().add(flow);
                        } else if (parsed.getKey().equalsIgnoreCase(userChat)) {
                            StackPane flow = emojiMessagesMaker(parsed, false);
                            allMessages.getChildren().add(flow);
                        }
                    }
                }
                //TODO: FILE MESSAGES
                if(fileMessages != null && !fileMessages.isEmpty() && allMessages != null){
                    for(Pair<String, File> parsed : fileMessages){
                        if(parsed.getKey().equalsIgnoreCase(username)){
                            StackPane flow = fileMessagesMaker(parsed, true);
                            allMessages.getChildren().add(flow);
                        } else if(parsed.getKey().equalsIgnoreCase(userChat)){
                            StackPane flow = fileMessagesMaker(parsed, false);
                            allMessages.getChildren().add(flow);
                        }
                    }
                }
                //TODO: IMAGE MESSAGE
                if(imageMessages != null && !imageMessages.isEmpty() && allMessages != null){
                    for(Pair<String, byte[]> parsed : imageMessages){
                        System.out.println(parsed.getKey() + System.lineSeparator());
                        if(parsed.getKey().equalsIgnoreCase(username)){
                            StackPane flow = imageMessagesMaker(parsed, true);
                            allMessages.getChildren().add(flow);
                        } else if(parsed.getKey().equalsIgnoreCase(userChat)){
                            StackPane flow = imageMessagesMaker(parsed, false);
                            allMessages.getChildren().add(flow);
                        }
                    }
                }
            }
            previousTime = now;
        }
    }


    //TODO: SEND MESSAGE BUTTON
    @FXML
    void onClickSendMessage(ActionEvent event) {
        if(!contact_nameField.getText().isEmpty() && !messageArea.getText().isEmpty()){
            String toUser = contact_nameField.getText();
            Client.getInstance().addMessage(username, toUser, messageArea.getText());
            Client.getInstance().sendMessageTo(Protocol.MESSAGE,username,toUser, messageArea.getText());
            messageArea.setText("");
        }
    }
                //TODO: AUDIOMESSAGE BUTTON
    @FXML
    void onClickRecord(ActionEvent event) {
        if(!contact_nameField.getText().isEmpty()) {
            ByteArrayOutputStream out;
            micButton.setDisable(true);
            AudioController.getInstance().start();
            recDotIndicator.setVisible(true);
            recDotIndicator.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    byte[] out;
                    AudioController.getInstance().stop();
                    recDotIndicator.setVisible(false);
                    micButton.setDisable(false);
                    out = AudioController.getInstance().getData();
                    String toUser = contact_nameField.getText();
                    Client.getInstance().addAudioMessage(username, toUser, out);
                    Client.getInstance().sendMessageTo(Protocol.AUDIO_MESSAGE, username, toUser, out);
                }
            });
        }
    }

                //TODO: CALL BUTTON
    @FXML
    void onClickCall(ActionEvent event) throws IOException {

    }
                //TODO: SEND FILE BUTTON (FILE OBJECT IS SERIALIZABLE)
    @FXML
    void onClickSendFile(ActionEvent event) throws IOException {
        File file = SceneHandler.getInstance().showFilePicker();
        if(file != null && !contact_nameField.getText().isEmpty() && !file.getName().contains("\\/.:")) { //to avoid path traversal
            Client.getInstance().addFileMessage(username, contact_nameField.getText(), file);
            Client.getInstance().sendMessageTo(Protocol.FILE, username, contact_nameField.getText(), file);
        }
    }
                //TODO: SEND IMAGE BUTTON (IMAGE ISN'T SERIALIZABLE)
    @FXML
    void onClickSendImage(ActionEvent event) throws IOException {
        File file = SceneHandler.getInstance().showImagePicker();
        if(file != null && !contact_nameField.getText().isEmpty()){
            FileInputStream in = new FileInputStream(file.getAbsolutePath());
            byte[] imageData = new byte[(int)file.length()];
            in.read(imageData);
            Client.getInstance().addImageMessage(username, contact_nameField.getText(), imageData);
            Client.getInstance().sendMessageTo(Protocol.IMAGE, username, contact_nameField.getText(), imageData);
            System.out.println("Invio immagine a "+ contact_nameField.getText() + System.lineSeparator());
        }
    }

                //TODO: EMOTICONS
    @FXML
    void onClickShowEmoticons(ActionEvent event) throws IOException {
        SceneHandler.getInstance().openEmojiTable(username, contact_nameField.getText());
    }


    @FXML
    void onClickChangeImage(ActionEvent event) {
        File file = SceneHandler.getInstance().showImagePicker();
        if(file != null){
            //TODO: Need to insert and retrieve image from database
            userAccountImage.setFill(new ImagePattern(new Image(file.toURI().toString())));
        }
    }

    @FXML
    void onClickChangePassword(ActionEvent event) throws IOException {
        SceneHandler.getInstance().showChangePassword();
    }

    @FXML
    void onClickShowInfo(ActionEvent event) throws IOException {
        SceneHandler.getInstance().showInfos();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //To not resize on MasterWindow resizing
        SplitPane.setResizableWithParent(contactsAnchorPane, false);
        SplitPane.setResizableWithParent(chatInfoSplitPane, false);
        //Local variables allocation
        username = Client.getInstance().getUsername();
        contacts = new Vector<Contact>();
        userImg = new Image(getClass().getResource("/application/whatsup/Images/images.png").toString());
        userAccountImage.setFill(new ImagePattern(userImg));
        messageArea.setWrapText(true);
        //panel adjustments
        contactsVBox.prefWidthProperty().bind(contactsScrollPane.widthProperty());
        contactsScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        allMessages.prefWidthProperty().bind(allMessagesScrollArea.widthProperty());
        allMessages.setSpacing(5);
        allMessages.setPadding(new Insets(10));
        allMessagesScrollArea.vvalueProperty().bind(allMessages.heightProperty());
        //panel allocator
        contactInfo = new AnchorPane();
        contactInfo.setMinSize(200,chatInfoSplitPane.getHeight());
        contactInfo.setVisible(false);

        messageArea.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode().equals(KeyCode.ENTER))
                    onClickSendMessage(new ActionEvent());
            }
        });

        //FOR TESTING PURPOSE

    }

    private Pane addContact(String user) {
        BorderPane pane = new BorderPane();
        pane.setPrefSize(150,30);
        pane.setId(user);
        FontIcon icon = new FontIcon("mdi-account");
        icon.setIconSize(40);
        icon.setIconColor(Color.WHITE);
        Label label = new Label();
        label.setText(user);
        label.setFont(new Font(18));
        label.setTextFill(Color.WHITE);

        pane.setLeft(icon);
        pane.setCenter(label);
        pane.setBottom(new Separator());
        pane.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                contact_include_gridPane.setVisible(true);
                allMessagesScrollArea.setVisible(true);
                if(!contact_nameField.getText().equalsIgnoreCase(user)) //if contact changes, chat will reset
                    allMessages.getChildren().clear();
                contact_nameField.setText(user);
                EmojiController.getInstance().setToUser(user);
            }
        });
        pane.setId(user);
        return pane;
    }

    private StackPane messagesMaker(String parsed, boolean user) {
        StackPane stack = new StackPane();
        Label label = new Label();
        label.setWrapText(true);
        label.setText(parsed);
        label.setTextFill(Color.WHITE);
        label.setPadding(new Insets(5));
        label.setMinHeight(30);
        if(user) {
            label.setStyle("-fx-background-radius: 20 20 20 20;-fx-background-color: #84C69B; -fx-font-size: 20px; -fx-font-fill: white;");
            stack.setAlignment(Pos.CENTER_RIGHT);
        }
        else {
            label.setStyle("-fx-background-radius: 20 20 20 20;-fx-background-color: #423F3E; -fx-font-size: 20px; -fx-font-fill: white;");
            stack.setAlignment(Pos.CENTER_LEFT);
        }
        stack.setPadding(new Insets(5));
        stack.getChildren().add(label);
        return stack;
    }


    private StackPane audioMessageMaker(Pair<String, byte[]> parsed, boolean user) {
        StackPane stack = new StackPane();
        AudioLabel label = new AudioLabel(parsed.getValue());
        label.setPadding(new Insets(5));
        label.setMinHeight(30);
        if(user) {
            label.setStyle("-fx-background-radius: 20 20 20 20;-fx-background-color: #84C69B; -fx-font-size: 20px; -fx-font-fill: white;");
            stack.setAlignment(Pos.CENTER_RIGHT);
        }
        else {
            label.setStyle("-fx-background-radius: 20 20 20 20;-fx-background-color: #423F3E; -fx-font-size: 20px; -fx-font-fill: white;");
            stack.setAlignment(Pos.CENTER_LEFT);
        }
        stack.setPadding(new Insets(5));
        stack.getChildren().add(label);
        return stack;
    }

    private StackPane emojiMessagesMaker(Pair<String, String> parsed, boolean user){
        StackPane stack = new StackPane();
        Label label = new Label();
        label.setPadding(new Insets(5));
        label.setMinHeight(35);
        if(user) {
            label.setStyle("-fx-background-radius: 20 20 20 20;-fx-background-color: #84C69B; -fx-font-size: 20px; -fx-font-fill: white;");
            stack.setAlignment(Pos.CENTER_RIGHT);
        }
        else {
            label.setStyle("-fx-background-radius: 20 20 20 20;-fx-background-color: #423F3E; -fx-font-size: 20px; -fx-font-fill: white;");
            stack.setAlignment(Pos.CENTER_LEFT);
        }
        FontIcon icon = new FontIcon(parsed.getValue());
        icon.setIconColor(Color.WHITE);
        icon.setIconSize(35);
        label.setGraphic(icon);
        stack.setPadding(new Insets(5));
        stack.getChildren().add(label);
        return stack;
    }

    private StackPane fileMessagesMaker(Pair<String, File> parsed, boolean user){
        StackPane stack = new StackPane();
        FileLabel label = new FileLabel(parsed.getValue());
        label.setPadding(new Insets(5));
        label.setMinHeight(30);
        if(user) {
            label.setStyle("-fx-background-radius: 20 20 20 20;-fx-background-color: #84C69B; -fx-font-size: 20px; -fx-font-fill: white;");
            stack.setAlignment(Pos.CENTER_RIGHT);
        }
        else {
            label.setStyle("-fx-background-radius: 20 20 20 20;-fx-background-color: #423F3E; -fx-font-size: 20px; -fx-font-fill: white;");
            stack.setAlignment(Pos.CENTER_LEFT);
        }
        stack.setPadding(new Insets(5));
        stack.getChildren().add(label);
        return stack;
    }

    private StackPane imageMessagesMaker(Pair<String, byte[]> parsed, boolean user){
        StackPane stack = new StackPane();
        Label label = new Label();
        if(user) {
            label.setStyle("-fx-background-radius: 20 20 20 20;-fx-background-color: #84C69B; -fx-font-size: 20px; -fx-font-fill: white;");
            stack.setAlignment(Pos.CENTER_RIGHT);
        }
        else {
            label.setStyle("-fx-background-radius: 20 20 20 20;-fx-background-color: #423F3E; -fx-font-size: 20px; -fx-font-fill: white;");
            stack.setAlignment(Pos.CENTER_LEFT);
        }
        Image image = new Image(new ByteArrayInputStream(parsed.getValue()), 350,350,true,true);
        ImageView img = new ImageView(image);
        label.setCursor(Cursor.HAND);
        label.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                try {
                    SceneHandler.getInstance().showImage(parsed.getValue());
                } catch (IOException e) {
                    return;
                }
            }
        });
        label.setGraphic(new ImageView(image));
        label.setPadding(new Insets(15));
        stack.getChildren().add(label);
        return stack;
    }
}
