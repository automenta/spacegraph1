/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.spacenet.run.geom;

import automenta.spacenet.run.ArdorSpacetime;
import automenta.spacenet.space.*;
import automenta.spacenet.space.geom.Box;
import automenta.spacenet.space.geom.Box.BoxShape;
import automenta.spacenet.space.surface.ColorSurface;
import automenta.spacenet.space.geom.ProcessBox;
import automenta.spacenet.var.vector.Quat;
import automenta.spacenet.var.vector.V3;
import com.ardor3d.renderer.state.BlendState;
import com.ardor3d.scenegraph.Spatial;

/**
 *
 * @author seh
 */
public class DemoBox extends ProcessBox {

    public static void main(String[] args) {
        //Multiple windows can be created by calling newWindow repeatedly
        ArdorSpacetime.newWindow(new DemoBox());
    }

    @Override protected void start() {
        final V3 pos = new V3(0, 0, 0);
        final V3 scale = new V3(1, 1, 1);
        final Quat orientation = Quat.fromAngles(0, 0, 0);

        final Box b = add(new Box(pos, scale, orientation, BoxShape.Cubic));
        final ColorSurface cs = b.add(new ColorSurface(0.8f, 0.5f, 0.3f));

        b.add(new Repeat() {

            @Override protected void update(double t, double dt, Spatial s) {
                b.move(Math.cos(t), Math.sin(t), 0);

                double sc = 1.0 + Math.cos(t * 10.0) / 5.0;
                b.scale(sc, sc, sc);

                float cr = (float) ((1.0 + Math.cos(t)) * 0.5);
                float cg = 0.5f;
                float cb = 0.2f;
                cs.color(cr, cg, cb);

                if (Math.cos(t * 2.0) < 0) {
                    b.setShape(BoxShape.Cubic);
                } else {
                    b.setShape(BoxShape.Spheroid);
                }

                b.rotate(t, t / 2, t / 4);
            }
        });


    }
}
