package application.whatsup.controllers;

import application.whatsup.Client.Client;
import application.whatsup.Common.Protocol;
import application.whatsup.SceneHandler;
import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class CallController extends AnimationTimer implements Initializable {

    @FXML
    private Button closeButton;
    private long previousTime = 0;
    private long frequency = 500 * 1000000;
    private String toUser;
    private String username;

    private boolean stop;
    private static AudioController instance;
    private ByteArrayOutputStream out;
    private TargetDataLine microphone;
    int numBytesRead;
    int CHUNK_SIZE = 1024;
    byte[] data = null;

    @FXML
    void onClickCloseCall(ActionEvent event) {
        Thread.currentThread().interrupt();
        try {
            Client.getInstance().sendMessageTo(Protocol.CLOSECALL, username, toUser, Protocol.CLOSECALL);
            stop();
            SceneHandler.getInstance().setChatWindow();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void stop(){
        this.stop=true;
    }

    private AudioFormat getAudioFormat(){
        float sampleRate = 16000;
        int sampleSizeInBits = 8;
        int channels = 2;
        boolean signed = true;
        boolean bigEndian = true;
        AudioFormat format = new AudioFormat(sampleRate, sampleSizeInBits,
                channels, signed, bigEndian);
        return format;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try{
            AudioFormat format = getAudioFormat();
            microphone = AudioSystem.getTargetDataLine(format);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            microphone = (TargetDataLine) AudioSystem.getLine(info);
            microphone.open(format);
            out = new ByteArrayOutputStream();
            CHUNK_SIZE = 1024;
            data = new byte[microphone.getBufferSize() / 5];
            microphone.start();
            stop = false;
        }catch(Exception e) {
            return;
        }
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setToUser(String toUser) {
        this.toUser = toUser;
    }

    @Override
    public void handle(long now) {
        int numBytesRead;
        int bytesRead = 0;
        if(now - previousTime >= frequency){
            try {
                while (!stop) {
                    // Here application starts recording data
                    numBytesRead = microphone.read(data, 0, CHUNK_SIZE);
                    bytesRead = bytesRead + numBytesRead;
                    System.out.println(bytesRead);
                    out.write(data, 0, numBytesRead);
                    Client.getInstance().sendMessageTo(Protocol.AUDIO, username, toUser,data);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            microphone.stop();
            microphone.close();
            data = new byte[microphone.getBufferSize() / 5];
        }
        Thread.currentThread().interrupt();
        previousTime = now;
    }
}
