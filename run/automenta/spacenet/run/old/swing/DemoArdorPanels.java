/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.spacenet.run.old.swing;

import automenta.spacenet.run.old.story.SwingWindow;
import automenta.spacenet.run.old.ArdorPanel;
import automenta.spacenet.run.widget.DemoButton;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

/**
 *
 * @author seh
 */
public class DemoArdorPanels {
    //TODO this depends on finishing ArdorWindow and eliminate static-ness of current ArdorSpacetime

    public static void main(String[] args) {
        JPanel xp = new JPanel();
        xp.setLayout(new BoxLayout(xp, BoxLayout.PAGE_AXIS));
        
        xp.add(new ArdorPanel(new DemoButton()));
        xp.add(new ArdorPanel(new DemoButton()));

        //xp.add(new ArdorPanel(new DemoNeuroph()));

        new SwingWindow(xp, 600, 600, true);


    }
}
