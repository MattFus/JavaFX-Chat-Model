package application.whatsup.Client;

import application.whatsup.Common.Protocol;
import application.whatsup.SceneHandler;
import javafx.util.Pair;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Vector;

public class Client implements Runnable{

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private static Client instance = null;
    private String username;
    private String password;
    private Vector<Contact> contacts;

    private boolean contactsModified;

    private Client(){
        try {
            socket = new Socket("localhost",8000);
            out = new ObjectOutputStream(socket.getOutputStream());
            Thread t = new Thread(this);
            contacts = new Vector<Contact>();
            contactsModified = false;
            t.start();
        } catch (IOException e) {
            out = null;
        }
    }

    public static Client getInstance() {
        if(instance == null)
            instance = new Client();
        return instance;
    }

    public String getUsername(){
        return username;
    }

    public Vector<Pair<String, String>> getMessages(String user){
        for(Contact c : contacts){
            if(c.getUsername().equalsIgnoreCase(user))
                return c.getUserMessages();
        }
        return null;
    }

    public Vector<Pair<String, byte[]>> getAudioMessages(String user){
        for(Contact c : contacts){
            if(c.getUsername().equalsIgnoreCase(user))
                return c.getUserAudioMessages();
        }
        return null;
    }

    public Vector<Pair<String, String>> getEmojiMessages(String user){
        for(Contact c : contacts){
            if(c.getUsername().equalsIgnoreCase(user))
                return c.getUserEmoji();
        }
        return null;
    }

    public Vector<Pair<String, File>> getFileMessages(String user){
        for(Contact c : contacts){
            if(c.getUsername().equalsIgnoreCase(user))
                return c.getUserFileMessages();
        }
        return null;
    }

    public Vector<Pair<String, byte[]>> getImageMessages(String user){
        for(Contact c : contacts){
            if(c.getUsername().equalsIgnoreCase(user))
                return c.getUserImageMessages();
        }
        return null;
    }

    public void addMessage(String fromUser, String toUser, String text) {
        for(Contact c : contacts){
            if(c.getUsername().equalsIgnoreCase(toUser))
                c.addMessage(fromUser, text);
        }
    }

    public void addAudioMessage(String fromUser, String toUser, byte[] message){
        for(Contact c : contacts){
            if(c.getUsername().equalsIgnoreCase(toUser))
                c.addAudioMessage(fromUser, message);
        }
    }

    public void addEmojiMessage(String fromUser, String toUser, String iconCode){
        for(Contact c : contacts){
            if(c.getUsername().equalsIgnoreCase(toUser))
                c.addEmojiMessage(fromUser, iconCode);
        }
    }

    public void addFileMessage(String fromUser, String toUser, File file){
        for(Contact c : contacts){
            if(c.getUsername().equalsIgnoreCase(toUser))
                c.addFileMessage(fromUser, file);
        }
    }

    public void addImageMessage(String fromUser, String toUser, byte[] fileData){
        for(Contact c : contacts){
            if(c.getUsername().equalsIgnoreCase(toUser))
                c.addImageMessage(fromUser, fileData);
        }
    }

    public Vector<Contact> getUsers(){
        contactsModified = false;
        return contacts;
    }

    public boolean getContactsModified(){
        return contactsModified;
    }

    @Override
    public void run() {
        while(out != null && in != null){
            try {
                Object res = in.readObject();
                //TODO: If I receive "USERHANDLER" then I have to update contacts
                if(((String) res).equalsIgnoreCase(Protocol.USERHANDLER)){
                    String str = (String) res;
                    ArrayList<String> contactsList = (ArrayList<String>) in.readObject();
                    contacts.clear();
                    for(String u : contactsList){
                        Contact user = new Contact(u);
                        contacts.add(user);
                    }
                    contactsModified = true;
                }
                //TODO: If I receive "MESSAGE" then I have to update user messages
                else if(res instanceof String && ((String) res).equalsIgnoreCase(Protocol.MESSAGE)){
                    String fromUser =(String) in.readObject();
                    String mex = (String) in.readObject();
                    for(Contact c : contacts){
                        if(c.getUsername().equalsIgnoreCase(fromUser))
                            c.addMessage(fromUser, mex);
                    }
                }
                //TODO: If I receive "AUDIO_MESSAGE" then I have to update userAudio messages
                else if(res instanceof String && ((String) res).equalsIgnoreCase(Protocol.AUDIO_MESSAGE)){
                    String fromUser = (String) in.readObject();
                    byte[] audio = (byte[]) in.readObject();
                    for(Contact c : contacts){
                        if(c.getUsername().equalsIgnoreCase(fromUser))
                            c.addAudioMessage(fromUser, audio);
                    }
                }
                //TODO: If I receive "EMOJI" then I have to update userEmojis
                else if(res instanceof String && ((String) res).equalsIgnoreCase(Protocol.EMOJI)){
                    String fromUser = (String) in.readObject();
                    String ikon = (String) in.readObject();
                    for(Contact c : contacts){
                        if(c.getUsername().equalsIgnoreCase(fromUser))
                            c.addEmojiMessage(fromUser, ikon);
                    }
                }
                //TODO: If I receive "FILE" then I have to update userFiles
                else if(res instanceof String && (((String) res).equalsIgnoreCase(Protocol.FILE))){
                    String fromUser = (String) in.readObject();
                    Object file = in.readObject();
                    for(Contact c : contacts){
                        if(c.getUsername().equalsIgnoreCase(fromUser))
                            c.addFileMessage(fromUser, (File) file);
                    }
                }
                //TODO: If I receive "IMAGE" then I have to update userImages
                else if(res instanceof String && ((String) res).equalsIgnoreCase(Protocol.IMAGE)){
                    String fromUser = (String) in.readObject();
                    Object data = in.readObject();
                    for(Contact c : contacts){
                        if(c.getUsername().equalsIgnoreCase(fromUser))
                            c.addImageMessage(fromUser, (byte[])data);
                    }
                }
                else if(res instanceof String && ((String) res).equalsIgnoreCase(Protocol.RESETPASSWORD)){
                    String result = (String) in.readObject();
                    String data = (String) in.readObject();
                    SceneHandler.getInstance().showError("Password Changed!");
                    this.password = data;
                }
                //TODO: All other cases ->
                else{
                    return;
                }
            } catch (IOException | ClassNotFoundException |ArrayIndexOutOfBoundsException e) {
                return;
            }
        }
        return;
    }

    //TODO: LOGIN/REGISTRATION METHOD
    public synchronized String connect(String username, String password, String email, String protocol) throws IOException {
        String res = "";
        if(protocol.equalsIgnoreCase(Protocol.LOGIN)){
            sendObject(Protocol.LOGIN); //INVIO PROTOCOLLO
            this.username = username;
            this.password = password;
            sendObject(username);
            sendObject(password);
            try {
                in = new ObjectInputStream(socket.getInputStream());
                res = (String) in.readObject();  //RICEVO CONFERMA
            } catch (IOException | ClassNotFoundException | NullPointerException e) {
                try{
                    socket.close();
                }catch(Exception ex){
                    return Protocol.CONNECTION_ERROR;
                }
                instance = null;
                return Protocol.CONNECTION_ERROR;
            }
        }
        else if(protocol.equalsIgnoreCase(Protocol.REGISTRATION)) {
            sendObject(Protocol.REGISTRATION); //INVIO PROTOCOLLO
            this.username = username;
            this.password = password;
            sendObject(username);
            sendObject(password);
            sendObject(email);
            try {
                in = new ObjectInputStream(socket.getInputStream());
                res = (String) in.readObject();  //RICEVO CONFERMA
            } catch (IOException | ClassNotFoundException | NullPointerException e) {
                try{
                    socket.close();
                }catch(Exception ex){
                    return Protocol.CONNECTION_ERROR;
                }
                instance = null;
                return Protocol.CONNECTION_ERROR;
            }
        }
        return res;
    }

    public void resetPassword(String oldPassword, String newPassword){
        sendMessageTo(Protocol.RESETPASSWORD, username, oldPassword, newPassword);
    }

    public boolean checkPsw(String psw){
        return psw.equals(password);
    }

    public synchronized boolean sendObject(Object ob){
        if(out == null)
            return false;
        try {
            out.writeObject(ob);
            out.flush();
        } catch (IOException e) {
            out = null;
            return false;
        }
        return false;
    }

    public synchronized boolean sendMessageTo(String protocol, String fromUser, String toUser, Object mex){
        if(out == null)
            return false;
        try {
            out.writeObject(protocol);
            out.writeObject(fromUser);
            out.writeObject(toUser);
            out.writeObject(mex);
            out.flush();
        } catch (IOException e) {
            out = null;
            return false;
        }
        return false;
    }

    public void reset() throws IOException {
        try {
            socket.close();
        }catch(Exception e) {
            return;
        }
        instance = null;
    }
}
