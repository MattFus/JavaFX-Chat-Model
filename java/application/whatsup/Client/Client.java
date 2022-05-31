package application.whatsup.Client;

import application.whatsup.Common.Protocol;
import application.whatsup.SceneHandler;
import javafx.util.Pair;
import org.kordamp.ikonli.javafx.FontIcon;

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
    private Vector<Contact> contacts;

    private boolean contactsModified;

    private Client(){
        try {
            socket = new Socket("localhost",8000);
            out = new ObjectOutputStream(socket.getOutputStream());
            Thread t = new Thread(this);
            //contacts = new HashMap<Contact, Messages>();
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
                //TODO:If I receive "USERHANDLER" then i have to update contacts
                if(((String) res).equalsIgnoreCase(Protocol.USERHANDLER)){
                    String str = (String) res;
                    System.out.println("CLIENT RICEVE USERHANDLER");
                    ArrayList<String> contactsList = (ArrayList<String>) in.readObject();
                    System.out.println(contactsList);
                    contacts.clear();
                    for(String u : contactsList){
                        Contact user = new Contact(u);
                        boolean inserisci = true;
                        for(Contact c : contacts){
                            if(c.getUsername().equalsIgnoreCase(u))
                                inserisci = false;
                        }
                        contacts.add(user);
                        System.out.println("Aggiungo " + user.getUsername());
                    }
                    System.out.println("CLIENT INSERISCE I CONTATTI");
                    contactsModified = true;
                }
                else if(res instanceof String && ((String) res).equalsIgnoreCase(Protocol.MESSAGE)){
                    String fromUser =(String) in.readObject();
                    String mex = (String) in.readObject();
                    System.out.println("Ho ricevuto " + res + " FROM " + fromUser + " : " + mex + System.lineSeparator());
                    //Messages.addMessage(fromUser + ":" + mex);
                    for(Contact c : contacts){
                        if(c.getUsername().equalsIgnoreCase(fromUser))
                            c.addMessage(fromUser, mex);
                    }
                }
                else if(res instanceof String && ((String) res).equalsIgnoreCase(Protocol.AUDIO_MESSAGE)){
                    String fromUser = (String) in.readObject();
                    byte[] audio = (byte[]) in.readObject();
                    System.out.println("Ho ricevuto un messaggio vocale");
                    for(Contact c : contacts){
                        if(c.getUsername().equalsIgnoreCase(fromUser))
                            c.addAudioMessage(fromUser, audio);
                    }
                }
                else if(res instanceof String && ((String) res).equalsIgnoreCase(Protocol.EMOJI)){
                    String fromUser = (String) in.readObject();
                    String ikon = (String) in.readObject();
                    System.out.println("Ho ricevuto una emoji");
                    for(Contact c : contacts){
                        if(c.getUsername().equalsIgnoreCase(fromUser))
                            c.addEmojiMessage(fromUser, ikon);
                    }
                }
                else if(res instanceof String && ((String) res).equalsIgnoreCase(Protocol.CALL)){
                    String fromUser = (String) in.readObject();
                    String mex = (String) in.readObject();
                    sendMessageTo(Protocol.ACCEPT_CALL, username, fromUser, Protocol.ACCEPT_CALL);
                    SceneHandler.getInstance().showCallWindow();
                }
                else if(res instanceof String && ((String) res).equalsIgnoreCase(Protocol.ACCEPT_CALL)){
                    String fromUser = (String) in.readObject();
                    String mex = (String) in.readObject();
                    SceneHandler.getInstance().showCallWindow();
                }
                else{
                    System.out.println("SERVER WROTE -> " + res + System.lineSeparator());
                }
            } catch (IOException | ClassNotFoundException |ArrayIndexOutOfBoundsException e) {
                return;
            }
        }
        return;
    }


    //TODO: LOGIN/REGISTRATION METHOD
    public synchronized String connect(String username, String password, String email, String protocol) {
        //TODO: ADD RESET PASSWORD
        String res = "";
        if(protocol.equalsIgnoreCase(Protocol.LOGIN)){
            sendObject(Protocol.LOGIN); //INVIO PROTOCOLLO
            this.username = username;
            sendObject(username);
            sendObject(password);
            try {
                in = new ObjectInputStream(socket.getInputStream());
                res = (String) in.readObject();  //RICEVO CONFERMA
                System.out.println("SERVER WROTE: " + res + System.lineSeparator());
            } catch (IOException | ClassNotFoundException | NullPointerException e) {
                reset();
                return Protocol.CONNECTION_ERROR;
            }
        }
        else if(protocol.equalsIgnoreCase(Protocol.REGISTRATION)) {
            sendObject(Protocol.REGISTRATION); //INVIO PROTOCOLLO
            this.username = username;
            sendObject(username);
            sendObject(password);
            sendObject(email);
            try {
                in = new ObjectInputStream(socket.getInputStream());
                res = (String) in.readObject();  //RICEVO CONFERMA
                System.out.println("SERVER WROTE: " + res + System.lineSeparator());
            } catch (IOException | ClassNotFoundException | NullPointerException e) {
                reset();
                return Protocol.CONNECTION_ERROR;
            }
        }
        if(!res.equalsIgnoreCase(Protocol.RECEIVED)){
            reset();
        }
        return res;
    }

    public boolean sendObject(Object ob){
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

    public boolean sendMessageTo(String protocol, String fromUser, String toUser, Object mex){
        if(out == null)
            return false;
        try {
            if(mex instanceof FontIcon){
                System.out.println("INVIO EMOJI");
            }
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

    public void reset() {
        try{
            if(instance != null)
                instance = null;
            if(socket != null)
                socket.close();
        } catch (IOException e) {
            return;
        }

    }
}
