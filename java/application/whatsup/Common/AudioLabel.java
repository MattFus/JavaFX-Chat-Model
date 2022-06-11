package application.whatsup.Common;

import application.whatsup.controllers.AudioController;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.javafx.FontIcon;


public class AudioLabel extends Label {

    private FontIcon playIcon;
    private byte[] out;
    private ProgressBar progressBar = new ProgressBar();

    public AudioLabel(byte[] out) {
        this.out = out;
        playIcon = new FontIcon("mdi-play");
        playIcon.setIconColor(Color.WHITE);
        playIcon.setCursor(Cursor.HAND);
        playIcon.setIconSize(40);
        playIcon.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                AudioController.getInstance().reproduceAudio(out);
            }
        });
        this.setText("");
        this.setGraphic(playIcon);
        this.getChildren().add(progressBar);
    }
}