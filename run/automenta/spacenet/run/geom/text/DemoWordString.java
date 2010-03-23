/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.spacenet.run.geom.text;

import automenta.spacenet.run.ArdorSpacetime;
import automenta.spacenet.run.DemoDefaults;
import automenta.spacenet.space.geom.Box;
import automenta.spacenet.space.geom.ProcessBox;
import automenta.spacenet.space.geom.text3d.Font3D;
import automenta.spacenet.space.geom.text3d.Text3D;

/**
 *
 * @author seh
 */
public class DemoWordString extends ProcessBox {

    double wScale = 0.1;
    
    Font3D font = DemoDefaults.font;
    String text =
        "In the province of the mind, what one believes to be true either is true or becomes true within certain limits, to be found experientially and experimentally. These limits are beliefs to be transcended. " +
        "Hidden from one's self is a covert set of beliefs that control on's thinking, one's actions, and one's feelings." +
        "The covert set of hidden beliefs is the limiting set of beliefs to be transcended." +
        "To transcend one's limiting set, one establishes an open-ended set of beliefs about the unknown." +
        "The unknown exists in one's goals for changing one's self, int he means for changing, in th euse of others for the change, in one's capacity to change, in one's orientation toward change, in one's elimination of hindrances to change, in one's assimilation of the aids to change, in one's use of the impulse to change, in one's need for changing, in the possibilities of change, in the form of change itself, and in the substance of change and of changing.";

    public class WordSpace extends Box {

        public WordSpace(String text) {
            super(BoxShape.Empty);
            add(new Text3D(font, text));
        }
    }

    public class SentenceSpace extends Box {

        public SentenceSpace(String text) {
            super(BoxShape.Empty);

            double x = 0;
            String[] words = text.split(" ");
            for (String w : words) {
                WordSpace ws = add(new WordSpace(w));
                //double l = w.length() + 1;
                double l = w.length();
                
                ws.scale( l * wScale/1.6 , 1.0 * wScale, 1.0 * wScale);
                ws.move(x, 0, 0);
                x += (l+1.0) * wScale;
            }
        }
    }

    @Override
    protected void start() {
        add(new SentenceSpace(text));
    }

    public static void main(String[] args) {
        ArdorSpacetime.newWindow(new DemoWordString());
    }
}
