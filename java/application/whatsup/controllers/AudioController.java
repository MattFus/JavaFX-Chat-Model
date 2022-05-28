package application.whatsup.controllers;

import javax.sound.sampled.*;
import java.io.ByteArrayOutputStream;

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
        System.out.println("Recording...");
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
                while (!stop) { // Just so I can test if recording
                    // my mic works...
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
        System.out.println("Cannot find any microphone");
        }
    }
}
