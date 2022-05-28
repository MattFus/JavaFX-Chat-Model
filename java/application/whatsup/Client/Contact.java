package application.whatsup.Client;

import javafx.scene.layout.VBox;
import javafx.util.Pair;

import java.util.Vector;

public class Contact {

    private String username;
    private Vector<Pair<String,byte[]>> userAudioMessages;
    private Vector<String> userMessages;

    private VBox messagesArea;
    private boolean update;
    private boolean audioUpdate;

    public Contact(String username){
        this.username = username;
        userMessages = new Vector<>();
        userAudioMessages = new Vector<>();
        messagesArea = new VBox();
    }

    public synchronized void addMessage(String fromUser, String message){
        userMessages.add(fromUser+":"+message);
        update = true;
    }

    public synchronized void addAudioMessage(String fromUser, byte[] message){
        userAudioMessages.add(new Pair<String, byte[]>(fromUser, message));
        audioUpdate = true;
    }

    public synchronized String getUsername() {
        return username;
    }

    public synchronized Vector<String> getUserMessages() {
        Vector<String> tmp = new Vector<String>();
        if(!userMessages.isEmpty())
            for(String str : userMessages)
                tmp.add(str);
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
}
