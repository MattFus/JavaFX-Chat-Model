package application.whatsup;

import application.whatsup.Server.MainServer;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        SceneHandler.getInstance().init(stage);
    }

    public static void main(String[] args) {
        //MainServer.main(args) //to start server with client
        launch();
    }
}