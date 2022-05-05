package live.notjacob.chatappclient.client;
import live.notjacob.chatappclient.Packet;

import javax.sound.sampled.*;
import java.io.*;
import java.net.Socket;

public class VoiceController {

    private static final float SAMPLE_RATE = 16000f;
    private static final int SAMPLE_SIZE_IN_BITS = 8;
    private static final int CHANNELS = 2;
    private static final boolean SIGNED = true;
    private static final boolean BIG_ENDIAN = true;

    private TargetDataLine mic;
    private final AudioFormat format = new AudioFormat(SAMPLE_RATE, SAMPLE_SIZE_IN_BITS, CHANNELS, SIGNED, BIG_ENDIAN);

    public VoiceController() {
    }

    public void start() throws LineUnavailableException, IOException, UnsupportedAudioFileException {
        DataLine.Info inf = new DataLine.Info(TargetDataLine.class, format);

        if (!AudioSystem.isLineSupported(inf)) {
            // dont allow calling
        }
        mic = (TargetDataLine) AudioSystem.getLine(inf);
        mic.open(format);
        mic.start();

        AudioInputStream ais = new AudioInputStream(mic);
        Socket voiceConnection = new Socket("127.0.0.1", 4322);
        Thread read = new Thread(() -> {
            try {
                AudioInputStream in = AudioSystem.getAudioInputStream(voiceConnection.getInputStream());
                Clip clip = AudioSystem.getClip();
                clip.open(in);
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                e.printStackTrace();
            }
        });
        Thread write = new Thread(() -> {
            try {
                AudioSystem.write(ais, AudioFileFormat.Type.WAVE, voiceConnection.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        read.start();
        write.start();

    }

}
