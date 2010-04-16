/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.spacenet.run.old.physics;

import automenta.spacenet.space.geom.ConvexHull;
import automenta.spacenet.run.ArdorSpacetime;
import automenta.spacenet.space.Repeat;
import automenta.spacenet.space.geom.ProcessBox;
import automenta.spacenet.var.physical.Color;
import automenta.spacenet.var.Maths;
import com.ardor3d.scenegraph.Spatial;
import java.util.LinkedList;
import java.util.List;
import javax.vecmath.Vector3f;

/**
 * generate and display a dynamically animated convex hull
 */
public class DemoAnimatedConvexHullBlob extends ProcessBox {

    final static int numPoints = 128;

    @Override
    protected void start() {

        final List<Vector3f> points = new LinkedList();
        for (int i = 0; i < numPoints; i++) {
            double x = Maths.random(-0.5, 0.5);
            double y = Maths.random(-0.2, 0.2);
            double z = Maths.random(-0.2, 0.2);
            points.add(new Vector3f((float) x, (float) y, (float) z));
        }

        final ConvexHull hull = add(new ConvexHull(points));
        hull.color(Color.White);

        add(new Repeat(0.05) {

            @Override protected void update(double t, double dt, Spatial s) {
                double d = 0.01;
                for (Vector3f v : points) {
                    v.x += Maths.random(-d, d);
                    v.y += Maths.random(-d, d);
                    v.z += Maths.random(-d, d);
                }

                //change points
                hull.setPoints(points);
            }
        });
    }

    public static void main(String[] args) {
        ArdorSpacetime.newWindow(new DemoAnimatedConvexHullBlob());
    }
}
