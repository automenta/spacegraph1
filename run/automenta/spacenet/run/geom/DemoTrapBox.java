/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package automenta.spacenet.run.geom;

import automenta.spacenet.run.ArdorSpacetime;
import automenta.spacenet.space.geom.ProcessBox;
import automenta.spacenet.space.geom.TrapBox;

/**
 *
 * @author seh
 */
public class DemoTrapBox extends ProcessBox {

    @Override protected void start() {
        for (int x = -1; x <= 1; x++)
            for (int y = -1; y <= 1; y++)
                add(new TrapBox(0.75).move(x*2, y*2, 0).scale(1,1,0.5));
    }

    public static void main(String[] args) {
        ArdorSpacetime.newWindow(new DemoTrapBox());
    }


}
