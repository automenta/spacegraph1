/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package automenta.spacenet.run.geom;

import automenta.spacenet.run.ArdorSpacetime;
import automenta.spacenet.space.Repeat;
import automenta.spacenet.space.geom.Line3D;
import automenta.spacenet.space.geom.ProcessBox;
import automenta.spacenet.var.vector.V3;
import com.ardor3d.scenegraph.Spatial;

/**
 *
 * @author seh
 */
public class DemoLine3D extends ProcessBox {

    @Override
    protected void start() {
        newLine(1,0,0);
        newLine(0,1,0);
        newLine(0,0,1);

        scale(4.0, 4.0, 4.0);

        final V3 r = new V3();
        Line3D c = add(new Line3D(new V3(), r, 5, 0.05));
        
        add(new Repeat() {

            @Override
            protected void update(double t, double dt, Spatial s) {
                getOrientation().set(t, t/2.0, t/4.0);

                r.set(Math.cos(t)*2, Math.sin(t)*2, 0);
            }

        });

    }


    protected void newLine(double x, double y, double z) {
        
        add(new Line3D(new V3(), new V3(x, y, z), 3, 0.1));


    }

    public static void main(String[] args) {
        ArdorSpacetime.newWindow(new DemoLine3D());
    }

}
