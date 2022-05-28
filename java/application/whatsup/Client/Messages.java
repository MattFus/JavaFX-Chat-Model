package application.whatsup.Client;

import java.util.ArrayList;

public class Messages {

    private static ArrayList<String> allMessages = new ArrayList<String>();

    public synchronized static void addMessage(String message) {
        allMessages.add(message);
    }

    public synchronized static ArrayList<String> readMessages() {
        ArrayList<String> tmp = new ArrayList<String>();
        for(String mess : allMessages)
            tmp.add(mess);
        allMessages.clear();
        return tmp;
    }
}
