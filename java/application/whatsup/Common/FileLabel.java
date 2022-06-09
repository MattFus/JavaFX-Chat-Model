package application.whatsup.Common;

import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.stage.FileChooser;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileLabel extends Label {
    private File file;
    private byte[] fileData;
    private FontIcon fileIcon = new FontIcon("mdi-file");

    public FileLabel(byte[] fileData){
        this.fileData = fileData;
        fileIcon.setIconColor(Color.WHITE);
        fileIcon.setCursor(Cursor.HAND);
        fileIcon.setIconSize(40);
        this.setGraphic(fileIcon);
        fileIcon.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                FileChooser chooser = new FileChooser();
                chooser.setTitle("Save file");
                chooser.getExtensionFilters().addAll(
                            new FileChooser.ExtensionFilter("all file", "*.*"),
                            new FileChooser.ExtensionFilter("txt file", "*.txt"));
                File fileDownload = chooser.showSaveDialog(null);
                try{
                    FileOutputStream fileOutputStream = new FileOutputStream(fileDownload);
                    fileOutputStream.write(fileData);
                    fileOutputStream.close();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
    }
}
