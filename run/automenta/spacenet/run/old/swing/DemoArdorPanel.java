/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.spacenet.run.old.swing;

import automenta.spacenet.run.old.ArdorPanel;
import automenta.spacenet.run.widget.DemoButton;
import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author seh
 */
public class DemoArdorPanel {

    public static void main(String[] args) {
        JPanel xp = new JPanel(new BorderLayout());
        xp.add(new JButton("X"), BorderLayout.WEST);

        ArdorPanel jp = new ArdorPanel(new DemoButton());
        xp.add(jp, BorderLayout.CENTER);

        JFrame f = new JFrame("x");
        f.setSize(600, 600);
        f.setVisible(true);
        f.getContentPane().add(xp);


    }
}
