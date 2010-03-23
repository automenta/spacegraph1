/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package automenta.spacenet.run.geom.layout;

import automenta.spacenet.run.ArdorSpacetime;
import automenta.spacenet.run.control.ZoomableRect;
import automenta.spacenet.space.geom.ProcessBox;
import automenta.spacenet.space.geom.layout.ColRect;
import automenta.spacenet.space.geom.layout.RowRect;

/**
 *
 * @author seh
 */
public class DemoRowCol extends ProcessBox {

    @Override protected void start() {
        add(new ColRect(0.1, 0.1,
            new RowRect(0.05, 0.05, new ZoomableRect(), new ZoomableRect()),
            new RowRect(0.05, 0.05, new ZoomableRect(), new ZoomableRect(),new ZoomableRect()),
            new RowRect(0.05, 0.05, new ZoomableRect(), new ZoomableRect()),
            new RowRect(0.05, 0.05, new ZoomableRect(), new ZoomableRect())
        ));
    }

    public static void main(String[] args) {
        ArdorSpacetime.newWindow(new DemoRowCol());
    }
}
