package application.whatsup.controllers;

import application.whatsup.Client.Client;
import application.whatsup.Client.Contact;
import application.whatsup.Common.AudioLabel;
import application.whatsup.Common.Protocol;
import application.whatsup.SceneHandler;
import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.sound.sampled.LineUnavailableException;

public class ChatController extends AnimationTimer implements Initializable {
    //utilities
    private Vector<Contact> contacts;
    private static String username;
    private Image userImg;


    //Chat----------------------------------------------------------
    @FXML
    private VBox allMessages;

    @FXML
    private ScrollPane allMessagesScrollArea;

    @FXML
    private Button callButton;

    @FXML
    private SplitPane chatInfoSplitPane;

    @FXML
    private GridPane contact_call_videoCallGridPane;

    @FXML
    private Label contact_nameField;

    @FXML
    private Button emoButton;

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
    private Label contactNameLabelField;
    @FXML
    private SplitPane contacts_chat_splitPane;

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
            Vector<Contact> tmp = Client.getInstance().getUsers();
            contacts.clear();
            for(Contact c : tmp){
                contacts.add(c);
            }
            contactsVBox.getChildren().clear();
            for(Contact c : contacts){
                if(!c.getUsername().equalsIgnoreCase(username)){
                    contactsVBox.getChildren().add(addContact(c.getUsername()));
                    contactsVBox.getChildren().add(new Separator());
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
                //TODO: MESSAGES
                if(!messages.isEmpty() && allMessages != null){
                    Pair<String, String> parsed = messages.get(0);
                    System.out.println(parsed.getKey() + " " + parsed.getValue() + System.lineSeparator());
                    if(parsed.getKey().equalsIgnoreCase(username)){
                        //MY MESSAGE
                        StackPane flow = messagesMaker(parsed.getValue(), true);
                        flow.setAlignment(Pos.CENTER_RIGHT);
                        flow.setPadding(new Insets(10));
                        allMessages.getChildren().add(flow);
                    }
                    else if(parsed.getKey().equalsIgnoreCase(userChat)){
                        //RECEIVED FROM ANOTHER USER
                        StackPane flow = messagesMaker(parsed.getValue(), false);
                        flow.setPadding(new Insets(10));
                        flow.setAlignment(Pos.CENTER_LEFT);
                        allMessages.getChildren().add(flow);
                    }
                }
                //TODO: AUDIO MESSAGES
                if(!audioMessages.isEmpty() && allMessages != null){
                    Pair<String, byte[]> parsed = audioMessages.get(0);
                    if(parsed.getKey().equalsIgnoreCase(username)){
                        //MY AUDIO MESSAGE
                        StackPane flow = audioMessageMaker(parsed, true);
                        flow.setAlignment(Pos.CENTER_RIGHT);
                        flow.setPadding(new Insets(10));
                        allMessages.getChildren().add(flow);
                    }
                    else if(parsed.getKey().equalsIgnoreCase(userChat)){
                        //RECEIVED FROM ANOTHER USER
                        StackPane flow = audioMessageMaker(parsed, false);
                        flow.setAlignment(Pos.CENTER_LEFT);
                        flow.setPadding(new Insets(10));
                        allMessages.getChildren().add(flow);
                    }
                }
                //TODO: EMOTICON MESSAGES
                if(!emojiMessages.isEmpty() && allMessages != null){
                    Pair<String, String> parsed = emojiMessages.get(0);
                    if(parsed.getKey().equalsIgnoreCase(username)){
                        StackPane flow = emojiMessagesMaker(parsed, true);
                        flow.setAlignment(Pos.CENTER_RIGHT);
                        flow.setPadding(new Insets(10));
                        allMessages.getChildren().add(flow);
                    }
                    else if(parsed.getKey().equalsIgnoreCase(userChat)){
                        StackPane flow = emojiMessagesMaker(parsed, false);
                        flow.setAlignment(Pos.CENTER_LEFT);
                        flow.setPadding(new Insets(10));
                        allMessages.getChildren().add(flow);
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
            System.out.println("Invio messaggio a " + toUser + System.lineSeparator());
            Client.getInstance().addMessage(username, toUser, messageArea.getText());
            Client.getInstance().sendMessageTo(Protocol.MESSAGE,username,toUser, messageArea.getText());
            messageArea.setText("");
        }
    }
                //TODO: AUDIOMESSAGE BUTTON
    @FXML
    void onClickRecord(ActionEvent event) { //BEADS-PROJECT??
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
                    //StackPane audioPane = audioMessageMaker(out);
                    //audioPane.setAlignment(Pos.CENTER_RIGHT);
                    //allMessages.getChildren().add(audioPane);
                    String toUser = contact_nameField.getText();
                    Client.getInstance().addAudioMessage(username, toUser, out);
                    Client.getInstance().sendMessageTo(Protocol.AUDIO_MESSAGE, username, toUser, out);
                }
            });
        }
    }

                //TODO: CALL BUTTON
    @FXML
    void onClickCall(ActionEvent event) {
        if(!contact_nameField.getText().isEmpty()){
            String toUser = contact_nameField.getText();
            Client.getInstance().sendMessageTo(Protocol.CALL, username, toUser, Protocol.CALL);
        }
    }

                //TODO: EMOTICONS
    @FXML
    void onClickShowEmoticons(ActionEvent event) throws IOException {
        SceneHandler.getInstance().openEmojiTable(username, contact_nameField.getText());
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
        allMessages.setPadding(new Insets(5));
        allMessagesScrollArea.vvalueProperty().bind(allMessages.heightProperty());
        //panel allocator
        contactInfo = new AnchorPane();
        contactInfo.setMinSize(200,chatInfoSplitPane.getHeight());
        contactInfo.setVisible(false);

        //EventHandlers
        contact_nameField.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                contactInfo.setVisible(true);
            }
        });

        userAccountImage.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Image img = SceneHandler.getInstance().showImagePicker();
                if(img != null){
                    //TODO: Need to insert and retrieve image from database
                    userAccountImage.setFill(new ImagePattern(img));
                }
            }
        });

        messageArea.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode().equals(KeyCode.ENTER))
                    onClickSendMessage(new ActionEvent());
            }
        });
        //FOR TESTING PURPOSE


    }

    private Node addContact(String user) {
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
        pane.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                contact_call_videoCallGridPane.setVisible(true);
                allMessagesScrollArea.setVisible(true);
                if(!contact_nameField.getText().equalsIgnoreCase(user)) //if contact changes, chat will reset
                    allMessages.getChildren().clear();
                contact_nameField.setText(user);
            }
        });
        return pane;
    }

    private StackPane messagesMaker(String parsed, boolean user) {
        StackPane stack = new StackPane();
        System.out.println(Character.toChars(0x1F621));
        //byte[] emojiByteCode = new byte[]{(byte)0xF0, (byte)0x9F, (byte)0x98, (byte)0x81};
        //String emoji = new String(emojiByteCode, StandardCharsets.UTF_8);
        //TextFlow flow = new TextFlow();
        Label label = new Label();
        label.setWrapText(true);
        label.setText(parsed);
        label.setTextFill(Color.WHITE);
        label.setPadding(new Insets(5));
        label.setMinHeight(30);
        if(user)
            label.setStyle("-fx-background-radius: 20 20 20 20;-fx-background-color: #84C69B; -fx-font-size: 20px; -fx-font-fill: white;");
        else
            label.setStyle("-fx-background-radius: 20 20 20 20;-fx-background-color: #423F3E; -fx-font-size: 20px; -fx-font-fill: white;");
        stack.getChildren().add(label);
        return stack;
    }


    private StackPane audioMessageMaker(Pair<String, byte[]> parsed, boolean user) {
        StackPane stack = new StackPane();
        AudioLabel label = new AudioLabel(parsed.getValue());
        label.setPadding(new Insets(5));
        label.setMinHeight(30);
        if(user)
            label.setStyle("-fx-background-radius: 20 20 20 20;-fx-background-color: #84C69B; -fx-font-size: 20px; -fx-font-fill: white;");
        else
            label.setStyle("-fx-background-radius: 20 20 20 20;-fx-background-color: #423F3E; -fx-font-size: 20px; -fx-font-fill: white;");
        stack.getChildren().add(label);
        return stack;
    }

    private StackPane emojiMessagesMaker(Pair<String, String> parsed, boolean user){
        StackPane stack = new StackPane();
        Label label = new Label();
        label.setPadding(new Insets(5));
        label.setMinHeight(35);
        if(user)
            label.setStyle("-fx-background-radius: 20 20 20 20;-fx-background-color: #84C69B; -fx-font-size: 20px; -fx-font-fill: white;");
        else
            label.setStyle("-fx-background-radius: 20 20 20 20;-fx-background-color: #423F3E; -fx-font-size: 20px; -fx-font-fill: white;");
        FontIcon icon = new FontIcon(parsed.getValue());
        icon.setIconColor(Color.WHITE);
        icon.setIconSize(35);
        label.setGraphic(icon);
        stack.getChildren().add(label);
        return stack;
    }

}
