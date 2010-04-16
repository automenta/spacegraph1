/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.spacenet.run.old.widget2d;

import automenta.spacenet.run.ArdorSpacetime;
import automenta.spacenet.space.geom.ProcessBox;
import automenta.spacenet.space.geom.border.GridRect;
import automenta.spacenet.space.widget.PanningDragRect;
import automenta.spacenet.var.physical.Color;

/**
 *
 * @author seh
 */
public class DemoWindow3 extends ProcessBox {

    @Override protected void start() {
        final PanningDragRect backRect = add(new PanningDragRect(1.5));
        {
            backRect.setZoomable(false);
            backRect.scale(1, 1).move(0, 0, -0.1);

//            //URL to Background Image
//            actions.add(new InstantAction<URL, URL>() {
//
//                @Override public String toString(URL i) {
//                    return "As Background Image";
//                }
//
//                @Override protected URL run(URL i) throws Exception {
//                    backRect.add(new BitmapSurface(i));
//                    return i;
//                }
//
//                @Override public double applies(URL i) {
//                    return 1.0;
//                }
//            });
            backRect.color(Color.Black);
            backRect.add(new GridRect(Color.Orange, 8, 8, 0.1));
        }

    }

    public static void main(String[] argV) {
        ArdorSpacetime.newWindow(new DemoWindow3().scale(4,3,3));
    }
}
