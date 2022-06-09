package application.whatsup.Client;

import javafx.util.Pair;
import java.util.Vector;

public class Contact {

    private String username;
    private Vector<Pair<String, byte[]>> userAudioMessages;
    private Vector<Pair<String, String>> userMessages;
    private Vector<Pair<String, String>> userEmojis;
    private Vector<Pair<String, byte[]>> userFileMessages;
    private Vector<Pair<String, byte[]>> userImageMessages;

    public Contact(String username){
        this.username = username;
        userMessages = new Vector<>();
        userAudioMessages = new Vector<>();
        userEmojis = new Vector<>();
        userFileMessages = new Vector<>();
        userImageMessages = new Vector<>();
    }

    public synchronized void addMessage(String fromUser, String message){
        userMessages.add(new Pair<String, String>(fromUser,message));
    }

    public synchronized void addAudioMessage(String fromUser, byte[] message){
        userAudioMessages.add(new Pair<String, byte[]>(fromUser, message));
    }

    public synchronized void addEmojiMessage(String fromUser, String ikon){
        userEmojis.add(new Pair<String, String>(fromUser, ikon));
    }

    public synchronized void addFileMessage(String fromUser, byte[] fileData){
        userFileMessages.add(new Pair<String, byte[]>(fromUser, fileData));
    }

    public synchronized void addImageMessage(String fromUser, byte[] imageData){
        userImageMessages.add(new Pair<String, byte[]>(fromUser, imageData));
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
        userMessages.clear();
        return tmp;
    }

    public synchronized Vector<Pair<String, byte[]>> getUserAudioMessages(){
        Vector<Pair<String, byte[]>> tmp = new Vector<>();
        if(!userAudioMessages.isEmpty())
            for(Pair<String, byte[]> p : userAudioMessages){
                tmp.add(p);
            }
        userAudioMessages.clear();
        return tmp;
    }

    public synchronized Vector<Pair<String, String>> getUserEmoji(){
        Vector<Pair<String, String>> tmp = new Vector<>();
        if(!userEmojis.isEmpty())
            for(Pair<String, String> p : userEmojis){
                tmp.add(p);
            }
        userEmojis.clear();
        return tmp;
    }

    public synchronized Vector<Pair<String, byte[]>> getUserFileMessages(){
        Vector<Pair<String, byte[]>> tmp = new Vector<>();
        if(!userFileMessages.isEmpty())
            for(Pair<String, byte[]> p : userFileMessages){
                tmp.add(p);
            }
        userFileMessages.clear();
        return tmp;
    }

    public synchronized Vector<Pair<String, byte[]>> getUserImageMessages(){
        Vector<Pair<String, byte[]>> tmp = new Vector<>();
        if(!userImageMessages.isEmpty())
            for(Pair<String, byte[]> p : userImageMessages){
                tmp.add(p);
            }
        userImageMessages.clear();
        return tmp;
    }
}
