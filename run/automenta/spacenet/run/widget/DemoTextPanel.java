/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package automenta.spacenet.run.widget;

import automenta.spacenet.space.widget.text.TextPanel;
import automenta.spacenet.run.ArdorSpacetime;
import automenta.spacenet.run.DemoDefaults;
import automenta.spacenet.space.geom.ProcessBox;
import automenta.spacenet.space.widget.text.TextEditPanel;

public class DemoTextPanel extends ProcessBox {

    String paragraph = "SpaceNet is a system of linked transformable objects that connect human thought, sensory representation, communication, and computational possibility.   The semantic meaning of present-moment thought, alone, generates simple, direct, and efficient human-computer interaction experiences.";


    @Override
    protected void start() {
        TextPanel tp = add(new TextPanel(DemoDefaults.font, paragraph, 26));
        tp.move(-0.5, 0, 0);

        TextEditPanel te = add(new TextEditPanel(DemoDefaults.font, paragraph));
        te.move(0.5, 0, 0);
    }

    public static void main(String[] args) {
        ArdorSpacetime.newWindow(new DemoTextPanel());
    }



}
