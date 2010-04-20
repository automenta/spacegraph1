/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package automenta.spacenet.run.widget;

import automenta.spacenet.run.ArdorSpacetime;
import automenta.spacenet.space.geom.ProcessBox;
import automenta.spacenet.space.widget.panel.Panel;
import automenta.spacenet.var.Maths;

/**
 *
 * @author seh
 */
public class DemoPanel extends ProcessBox {


    @Override protected void start() {

        for (double x = -4; x < 4; x+=2.5) {
            for (double y = -4; y < 4; y+=1.5) {
                add(new Panel().move(x, y, 0).scale(Maths.PHI, 1.0));
            }
        }
        
    }


    public static void main(String[] args) {
        ArdorSpacetime.newWindow(new DemoPanel());
    }
}
