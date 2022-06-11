package application.whatsup.Common;

import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import org.apache.commons.io.FilenameUtils;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.*;

public class FileLabel extends Label {
    private File file;
    private String extension;
    private byte[] fileData;
    private FontIcon fileIcon = new FontIcon("mdi-file");

    public FileLabel(File f) {
        this.file = f;
        this.extension = FilenameUtils.getExtension(file.getName());
        fileIcon.setIconColor(Color.WHITE);
        fileIcon.setCursor(Cursor.HAND);
        fileIcon.setIconSize(40);
        this.setGraphic(fileIcon);
        this.setText(file.getName());
        this.setTextFill(Color.WHITE);
        try {
            FileInputStream in = new FileInputStream(file);
            fileData = new byte[(int) file.length()];
            in.read(fileData);
        }catch(IOException e){
            return;
        }
        fileIcon.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                FileChooser chooser = new FileChooser();
                chooser.setTitle("Save file");
                chooser.setInitialFileName(file.getName());
                chooser.getExtensionFilters().addAll(
                            new FileChooser.ExtensionFilter("All Files", "*.*"),
                            new FileChooser.ExtensionFilter("Text Files", "*.txt"),
                            new FileChooser.ExtensionFilter(extension + " Files", "*." + extension));
                File fileDownload = chooser.showSaveDialog(null);
                if(fileDownload != null) {
                    try {
                        FileOutputStream fileOutputStream = new FileOutputStream(fileDownload);
                        fileOutputStream.write(fileData);
                        fileOutputStream.close();
                    } catch (Exception e) {
                        return;
                    }
                }
            }
        });
    }
}
