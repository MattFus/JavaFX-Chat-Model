package application.whatsup.Server;

import java.util.ArrayList;
import java.util.HashMap;

public class UsersHandler {

    private static HashMap<String, ConnectionHandler> users = new HashMap<String, ConnectionHandler>();

    public synchronized static boolean insertUser(String username, ConnectionHandler handler){
        if(users.containsKey(username))
            return false;
        users.put(username, handler);
        return true;
    }

    public synchronized static void removeUser(String username){
        users.remove(username);

    }

    public synchronized static ArrayList<String> allUsers(){
        ArrayList<String> onlineUsers = new ArrayList<String>();
        for(String s: users.keySet())
            onlineUsers.add(s);
        return onlineUsers;
    }

    public synchronized static void sendMessage(String protocol, String fromUser, String toUser, Object ob){
        for(String s : users.keySet()){
            if(s.equalsIgnoreCase(toUser)){
                ConnectionHandler handler = users.get(s);
                handler.sendObject(protocol);
                handler.sendObject(fromUser);
                handler.sendObject(ob);
            }
        }
    }

    public synchronized static void sendMessageToAll(Object ob){
        for(String s : users.keySet()){
                ConnectionHandler handler = users.get(s);
                handler.sendObject(ob);
        }
    }
}
