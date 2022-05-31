package application.whatsup.Client;

import javafx.scene.layout.VBox;
import javafx.util.Pair;
import org.kordamp.ikonli.javafx.FontIcon;

import java.awt.*;
import java.util.Vector;

public class Contact {

    private String username;
    private Vector<Pair<String,byte[]>> userAudioMessages;
    private Vector<Pair<String,String>> userMessages;
    private Vector<Pair<String, String>> userEmojis;

    private boolean update;
    private boolean audioUpdate;
    private boolean emojiUpdate;

    public Contact(String username){
        this.username = username;
        userMessages = new Vector<>();
        userAudioMessages = new Vector<>();
        userEmojis = new Vector<>();
    }

    public synchronized void addMessage(String fromUser, String message){
        userMessages.add(new Pair<String, String>(fromUser,message));
        update = true;
    }

    public synchronized void addAudioMessage(String fromUser, byte[] message){
        userAudioMessages.add(new Pair<String, byte[]>(fromUser, message));
        audioUpdate = true;
    }

    public synchronized void addEmojiMessage(String fromUser, String ikon){
        userEmojis.add(new Pair<String, String>(fromUser, ikon));
        emojiUpdate = true;
    }

    public synchronized String getUsername() {
        return username;
    }

    public synchronized Vector<Pair<String, String>> getUserMessages() {
        Vector<Pair<String, String>> tmp = new Vector<>();
        if(!userMessages.isEmpty())
            for(Pair<String, String> p : userMessages){
                tmp.add(p);
            }
        update = false;
        userMessages.clear();
        return tmp;
    }

    public synchronized Vector<Pair<String, byte[]>> getUserAudioMessages(){
        Vector<Pair<String, byte[]>> tmp = new Vector<>();
        if(!userAudioMessages.isEmpty())
            for(Pair<String, byte[]> p : userAudioMessages){
                tmp.add(p);
            }
        audioUpdate = false;
        userAudioMessages.clear();
        return tmp;
    }

    public synchronized Vector<Pair<String, String>> getUserEmoji(){
        Vector<Pair<String, String>> tmp = new Vector<>();
        if(!userEmojis.isEmpty())
            for(Pair<String, String> p : userEmojis){
                tmp.add(p);
            }
        emojiUpdate = false;
        userEmojis.clear();
        return tmp;
    }
}
