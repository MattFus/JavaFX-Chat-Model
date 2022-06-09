package application.whatsup.controllers;

import application.whatsup.SceneHandler;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class AudioController implements Runnable{

    private boolean stop;
    private static AudioController instance;
    private ByteArrayOutputStream out;
    private TargetDataLine microphone;

    private AudioController(){
        stop = false;
    }

    public static AudioController getInstance() {
        if(instance == null)
            instance = new AudioController();
        return instance;
    }

    public void start(){
        stop = false;
        Thread t = new Thread(this);
        t.setDaemon(true);
        t.start();
    }

    public void stop(){
        this.stop=true;
    }

    public byte[] getData(){
        if(out == null)
            return null;
        return out.toByteArray();
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
    public void run() {
        //System.out.println("Recording...");
        try {
            AudioFormat format = getAudioFormat();
            microphone = AudioSystem.getTargetDataLine(format);

            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            microphone = (TargetDataLine) AudioSystem.getLine(info);
            microphone.open(format);

            out = new ByteArrayOutputStream();
            int numBytesRead;
            int CHUNK_SIZE = 1024;
            byte[] data = new byte[microphone.getBufferSize() / 5];
            microphone.start();

            int bytesRead = 0;

            try {
                while (!stop) {
                    // Here application starts recording data
                    numBytesRead = microphone.read(data, 0, CHUNK_SIZE);
                    bytesRead = bytesRead + numBytesRead;
                    System.out.println(bytesRead);
                    out.write(data, 0, numBytesRead);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            microphone.stop();
            microphone.close();
            System.out.println("Stopped recording! Saving...");
        } catch (LineUnavailableException ex) {
            return;
            //System.out.println("Cannot find any microphone");
        }
    }

    public void reproduceAudio(byte[] data){
        AudioInputStream audioInputStream = null;
        SourceDataLine sourceDataLine = null;
        try {
            byte audioData[] = data;
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
    }

    public void setAudioWindow(String fromUser, String username) throws IOException {
        SceneHandler.getInstance().showCallWindow(fromUser, username);
    }

    public void setChatScene() throws IOException {
        SceneHandler.getInstance().setChatWindow();
    }
}
