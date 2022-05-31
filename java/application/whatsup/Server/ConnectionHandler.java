package application.whatsup.Server;

import application.whatsup.Common.Protocol;
import application.whatsup.Common.User;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.SQLException;

public class ConnectionHandler implements Runnable{

    private Socket clientSocket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private String username;

    public ConnectionHandler(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        try {
            this.out = new ObjectOutputStream(clientSocket.getOutputStream());
        } catch (IOException e) {
            this.out = null;
            clientSocket.close();
        }
    }

    @Override
    public void run() {
        try {
            in = new ObjectInputStream(clientSocket.getInputStream());  //I receve protocol -> username -> password
            String req = (String) in.readObject();
            String username = (String) in.readObject();
            String password = (String) in.readObject();
            System.out.println("SERVER RECEIVED: "+ req +" -> " +username+", "+password);
            this.username = username;
            //CASE: LOGIN
            if(req.equalsIgnoreCase(Protocol.LOGIN)){
                User user = new User(username,password);
                if(!DataBase.getInstance().checkUser(user)){
                    sendObject(Protocol.LOGIN_ERROR);
                    resetFields();
                    return;
                }
            }
            //CASE: REGISTRATION
            else if(req.equalsIgnoreCase(Protocol.REGISTRATION)){
                String email = (String) in.readObject();
                User user = new User(username, password, email);
                if(!DataBase.getInstance().insertUser(user)){
                    sendObject(Protocol.REGISTRATION_ERROR);
                    resetFields();
                    return;
                }
            }
            else{                                                  //------->ALTRO
                sendObject(Protocol.CONNECTION_ERROR);
                resetFields();
                return;
            }
            //IF I'M HERE THEN IM LOGGED IN
            //TODO: USERHANDLER CHE GESTISCE LE PERSONE CHE ENTRANO
            if(!UsersHandler.insertUser(username, this) || clientSocket.isClosed()) {
                sendObject(Protocol.USER_ALREADY_LOGGED_IN);
                username = null;
                resetFields();
                return;
            }
            sendObject(Protocol.RECEIVED); //TODO: è ANDATO TUTTO BENE
            UsersHandler.sendMessageToAll(Protocol.USERHANDLER);
            UsersHandler.sendMessageToAll(UsersHandler.allUsers());

            while(!Thread.currentThread().isInterrupted()){
                //TODO: SERVER NEEDS TO HANDLE MESSAGE BETWEEN USERS
                String protocol = (String) in.readObject();
                String fromUser = (String) in.readObject();
                String toUser = (String) in.readObject();
                Object message = in.readObject();
                UsersHandler.sendMessage(protocol, fromUser, toUser, message);
                System.out.println("Client wrote -> " + protocol +" FROM "+ fromUser + " TO "+ toUser + " -> " + message + System.lineSeparator());
            }
        } catch (IOException | ClassNotFoundException | SQLException e) {
            //e.printStackTrace();
            UsersHandler.removeUser(username);
            out = null;
            return;
        }
        return;
    }

    public boolean sendObject(Object ob){
        if(out == null)
            return false;
        try {
            out.writeObject(ob);
            out.flush();
        } catch (IOException e) {
            out = null;
            UsersHandler.removeUser(username);
            return false;
        }
        return false;
    }

    private void resetFields() throws IOException {
        if (out != null)
            out.close();
        out = null;
        if (in != null)
            in.close();
        in = null;
        if (clientSocket != null)
            clientSocket.close();
        clientSocket = null;
        username = null;
    }
}
