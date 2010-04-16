package automenta.spacenet.run.old.audio;

import java.io.IOException;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

public class JavaSoundAudio implements Runnable {

    private static final int BUFFER_SIZE = 128000;
    SourceDataLine line = null;
    private boolean running;
    byte[] abData;
    AudioFormat audioFormat;
    int nWaveformType = Oscillator.WAVEFORM_SINE;
    float fSampleRate = 44100.0F;
    float fSignalFrequency = 200.0F;
    float fAmplitude = 0.7F;
    private Oscillator oscillator;

    //TODO support multiple oscillators
    public JavaSoundAudio() {
        super();

    }


    public void start() throws IOException {
        audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, fSampleRate, 16, 2, 4, fSampleRate, false);
        oscillator = new Oscillator(nWaveformType, fSignalFrequency, fAmplitude, audioFormat, AudioSystem.NOT_SPECIFIED);
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
        try {
            line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(audioFormat);
        } catch (Exception e) {
            e.printStackTrace();
        }
        new Thread(this).start();
    }

    public void run() {
        line.start();
        abData = new byte[BUFFER_SIZE];
        running = true;
        while (running) {
            try {
                int nRead = oscillator.read(abData);
                int nWritten = line.write(abData, 0, nRead);
            } catch (IOException e) {
                System.err.println(e);
            }
        }
        if (line != null) {
            line.stop();
        }
    }

    public void stop() {
        System.out.println("stopping");
        running = false;
    }

    public void add(Oscillator o) {
    }

    public void remove(Oscillator o) {
    }
}
