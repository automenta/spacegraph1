/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package automenta.spacenet.run.old.story;

import automenta.spacenet.var.action.Actions;
import automenta.spacenet.var.graph.map.ScalarGraphMap;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;


public class NowWindow extends JFrame {

    public NowWindow(ScalarGraphMap m, Actions a) {
        super();
        getContentPane().add(new NowPanel(m, a));
        setSize(600, 400);
        setVisible(true);
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                System.exit(0);
            }
        });
    }



}
