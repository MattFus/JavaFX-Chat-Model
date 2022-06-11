package application.whatsup.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Runnable{

    private ServerSocket server;
    private ExecutorService executor;

    public void startServer() throws IOException {
        server = new ServerSocket(8000);
        executor = Executors.newCachedThreadPool();
        Thread t = new Thread(this);
        t.start();
    }

    @Override
    public void run() {
        while(!Thread.currentThread().isInterrupted()){
            //System.out.println("[SERVER] Waiting for connections...");
            try {
                Socket clientSocket = server.accept();
                ConnectionHandler handle = new ConnectionHandler(clientSocket);
                executor.submit(handle);
                //System.out.println("Connected!");
            } catch (IOException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
        return;
    }
}
