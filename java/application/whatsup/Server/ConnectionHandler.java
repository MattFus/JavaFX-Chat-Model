package application.whatsup.Server;

import application.whatsup.Common.Protocol;
import application.whatsup.Common.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.SecureRandom;
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
            in = new ObjectInputStream(clientSocket.getInputStream());  //User receives protocol -> username -> password
            String req = (String) in.readObject();
            String username = (String) in.readObject();
            String password = (String) in.readObject();
            this.username = username;
            //CASE: LOGIN
            if(req.equalsIgnoreCase(Protocol.LOGIN)){
                User user = new User(username,password);
                if(!DataBase.getInstance().checkUser(user)) {
                    sendObject(Protocol.LOGIN_ERROR);
                    clientSocket.close();
                    return;
                }
            }
            //CASE: REGISTRATION
            else if(req.equalsIgnoreCase(Protocol.REGISTRATION)){
                String email = (String) in.readObject();
                User user = new User(username, password, email);
                if(!DataBase.getInstance().insertUser(user)){
                    sendObject(Protocol.REGISTRATION_ERROR);
                    clientSocket.close();
                    return;
                }
            }
            else{                                                  //------->ALTRO
                sendObject(Protocol.CONNECTION_ERROR);
                clientSocket.close();
                return;
            }
            //IF I'M HERE THEN IM LOGGED IN
            //TODO: USERHANDLER CHE GESTISCE LE PERSONE CHE ENTRANO
            if(!UsersHandler.insertUser(username, this) || clientSocket.isClosed()) {
                sendObject(Protocol.USER_ALREADY_LOGGED_IN);
                clientSocket.close();
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
                if(protocol.equalsIgnoreCase(Protocol.RESETPASSWORD)){
                    String oldPassword = toUser;
                    String newPassword = (String) message;
                    if(DataBase.getInstance().changePsw(username,oldPassword,newPassword)){
                        UsersHandler.sendMessage(protocol, username, username, Protocol.OK);
                    }
                    else{
                        UsersHandler.sendMessage(protocol, username, username, Protocol.RESETERROR);
                    }
                    continue;
                }
                UsersHandler.sendMessage(protocol, fromUser, toUser, message);
            }
        } catch (IOException | ClassNotFoundException | SQLException e) {
            UsersHandler.removeUser(username);
            UsersHandler.sendMessageToAll(Protocol.USERHANDLER);
            UsersHandler.sendMessageToAll(UsersHandler.allUsers());
            return;
        }finally {
            UsersHandler.removeUser(username);
            UsersHandler.sendMessageToAll(Protocol.USERHANDLER);
            UsersHandler.sendMessageToAll(UsersHandler.allUsers());
            return;
        }
    }

    private String generatePassword() {
        String alphabet = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        SecureRandom rnd = new SecureRandom();
        StringBuilder sb = new StringBuilder(10);
        for(int i = 0; i < 10; i++){
            sb.append(alphabet.charAt(rnd.nextInt(alphabet.length())));
        }
        return sb.toString();
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
            UsersHandler.sendMessageToAll(Protocol.USERHANDLER);
            UsersHandler.sendMessageToAll(UsersHandler.allUsers());
            return false;
        }
        return false;
    }

    private void resetFields() throws IOException {
        UsersHandler.removeUser(username);
        UsersHandler.sendMessageToAll(Protocol.USERHANDLER);
        UsersHandler.sendMessageToAll(UsersHandler.allUsers());
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
        Thread.currentThread().interrupt();
    }
}
