/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package automenta.spacenet.run.geom;

import automenta.spacenet.run.ArdorWindow;
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
        newLine(1,0,0, 7, 0.1);
        newLine(0,1,0, 7, 0.1);
        newLine(0,0,1, 7, 0.1);

        add(new Repeat() {
            @Override protected void update(double t, double dt, Spatial s) {
                rotate(t, t/2.0, t/4.0);
            }
        });

    }

    protected void newLine(double x, double y, double z, int sides, double radius) {
        add(new Line3D(new V3(), new V3(x, y, z), sides, radius)).color((float)x, (float)y, (float)z);
    }

    public static void main(String[] args) {
        new ArdorWindow().withVolume(new DemoLine3D());
    }

}
