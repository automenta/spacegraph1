/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.spacenet.run.geom;

import automenta.spacenet.run.ArdorWindow;
import automenta.spacenet.space.*;
import automenta.spacenet.space.geom.Box;
import automenta.spacenet.space.geom.Box.BoxShape;
import com.ardor3d.scenegraph.Spatial;

/**
 *
 * @author seh
 */
public class DemoBox {

    public static void main(String[] args) {
        
        final Box b = new Box(BoxShape.Cubic);

        b.add(new Repeat() {

            @Override protected void update(double t, double dt, Spatial s) {
                b.move(Math.cos(t), Math.sin(t), 0);

                double sc = 1.0 + Math.cos(t * 10.0) / 5.0;
                b.scale(sc, sc, sc);

                b.rotate(t, t / 2, t / 4);

                b.color((float) ((1.0 + Math.cos(t)) * 0.5), 0.5f, 0.2f);

                if (Math.cos(t * 2.0) < 0) {
                    b.setShape(BoxShape.Cubic);
                } else {
                    b.setShape(BoxShape.Spheroid);
                }

            }
        });

        new ArdorWindow().withVolume(b);
    }

}
