/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.spacenet.run.old.audio;

import java.io.IOException;

/**
 *
 * @author seh
 */
public class TestJavaAudio {
    float fSignalFrequency = 200.0F;
    float fAmplitude = 0.7F;

    public static void main(String[] args) throws IOException {
        JavaSoundAudio audio = new JavaSoundAudio();


        audio.start();

        System.in.read();

        audio.stop();
    }

}
