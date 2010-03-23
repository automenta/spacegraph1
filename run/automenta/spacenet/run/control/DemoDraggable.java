/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.spacenet.run.control;

import automenta.spacenet.run.ArdorSpacetime;
import automenta.spacenet.space.geom.ProcessBox;
import automenta.spacenet.space.widget.window.Window;

/**
 *
 * @author seh
 */
public class DemoDraggable extends ProcessBox {

    @Override
    protected void start() {
        int n = 2;
        for (int x = -n; x < n; x++) {
            for (int y = -n; y < n; y++) {
                //add(new WindowRect().move(x * 2, y * 2, (double) (x ^ y)));
                add(new Window().move(x * 2, y * 2, (double) (x ^ y)));
            }
        }
    }

    public static void main(String[] args) {
        ArdorSpacetime.newWindow(new DemoDraggable());
    }
}
