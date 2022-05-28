package application.whatsup.Server;

import java.io.IOException;

public class MainServer {
    public static void main(String[] args) {
        Server server = new Server();
        try {
            server.startServer();
        } catch (IOException e) {
            return;
        }
    }
}
