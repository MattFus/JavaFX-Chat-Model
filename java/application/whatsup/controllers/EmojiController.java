package application.whatsup.controllers;

import application.whatsup.Client.Client;
import application.whatsup.Common.Protocol;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import org.kordamp.ikonli.javafx.FontIcon;

public class EmojiController {
    private static String username;
    private static String toUser;
    private static EmojiController instance = null;

    public EmojiController(){
    }

    public static EmojiController getInstance() {
        if(instance == null)
            instance = new EmojiController();
        return instance;
    }

    @FXML
    void onClickSendEmoji(MouseEvent event) {
        FontIcon ikon = (FontIcon) event.getSource();
        sendEmoji(ikon.getIconLiteral());
    }

    public void sendEmoji(String iconCode){
        username = Client.getInstance().getUsername();
        Client.getInstance().addEmojiMessage(username, toUser, iconCode);
        Client.getInstance().sendMessageTo(Protocol.EMOJI,username,toUser, iconCode);
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setToUser(String toUser) {
        this.toUser = toUser;
    }
}
