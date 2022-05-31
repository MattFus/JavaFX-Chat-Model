package application.whatsup.Common;


import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.javafx.FontIcon;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class AudioLabel extends Label implements Runnable{

    private FontIcon playIcon;
    private byte[] out;
    private AudioInputStream audioInputStream;
    private SourceDataLine sourceDataLine;
    private boolean started;

    public AudioLabel(byte[] out) {
        this.out = out;
        playIcon = new FontIcon("mdi-play");
        playIcon.setIconColor(Color.WHITE);
        playIcon.setCursor(Cursor.HAND);
        playIcon.setIconSize(40);
        playIcon.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(!started)
                    threadStart();
                else stop();
            }
        });
        //setEventHandler(playIcon);
        this.setText("");
        this.setGraphic(playIcon);
    }

    private void threadStart() {
        Thread t = new Thread(this);
        t.setDaemon(true);
        t.start();
    }

    private AudioFormat getAudioFormat() {
        float sampleRate = 16000;
        int sampleSizeInBits = 8;
        int channels = 2;
        boolean signed = true;
        boolean bigEndian = true;
        AudioFormat format = new AudioFormat(sampleRate, sampleSizeInBits,
                channels, signed, bigEndian);
        return format;
    }

    private void stop(){
        Thread.currentThread().interrupt();
    }

    @Override
    public void run() {
        started = true;
        try {
            byte audioData[] = out;
            // Get an input stream on the byte array
            // containing the data
            AudioFormat format = getAudioFormat();
            InputStream byteArrayInputStream = new ByteArrayInputStream(audioData);
            audioInputStream = new AudioInputStream(byteArrayInputStream,format, audioData.length / format.getFrameSize());
            DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, format);
            sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
            sourceDataLine.open(format);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
        sourceDataLine.start();
        int cnt = 0;
        byte tempBuffer[] = new byte[10000];
        try {
            while ((cnt = audioInputStream.read(tempBuffer, 0,tempBuffer.length)) != -1) {
                if (cnt > 0) {
                    // Write data to the internal buffer of
                    // the data line where it will be
                    // delivered to the speaker.
                    sourceDataLine.write(tempBuffer, 0, cnt);
                }// end if
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Block and wait for internal buffer of the
        // data line to empty.
        sourceDataLine.drain();
        sourceDataLine.close();
        Thread.currentThread().interrupt();
    }
}
