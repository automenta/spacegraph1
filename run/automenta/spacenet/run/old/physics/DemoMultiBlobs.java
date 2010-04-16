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
public class DemoMultiBlobs extends ProcessBox {

    int numCells = 7;
    final static int pointsPerCell = 32;
    final static int pointsOverlap = 4;
    final List<Vector3f> cellCenters = new LinkedList();
    final List<ConvexHull> cellHulls = new LinkedList();
    final List<List<Vector3f>> cellPoints = new LinkedList();

    @Override
    protected void start() {


        for (int c = 0; c < numCells; c++) {
            double cx = Maths.random(-4, 4);
            double cy = Maths.random(-4, 4);
            double cz = Maths.random(-4, 4);
            cellCenters.add(new Vector3f((float) cx, (float) cy, (float) cz));

            List<Vector3f> points = new LinkedList();
            if (c > 0) {
                List<Vector3f> prevPoints = cellPoints.get(c-1);
                for (int i = 0; i < pointsOverlap; i++) {
                    points.add(prevPoints.get(prevPoints.size()-1-pointsOverlap+i));
                }
            }

            for (int i = (c == 0 ? 0 : pointsOverlap); i < pointsPerCell; i++) {
                double px = Maths.random(-0.5, 0.5)+cx;
                double py = Maths.random(-0.5, 0.5)+cy;
                double pz = Maths.random(-0.5, 0.5)+cz;
                points.add(new Vector3f((float) px, (float) py, (float) pz));
            }
            cellPoints.add(points);

            final ConvexHull hull = add(new ConvexHull(points));
            //hull.color(Color.newRandomHSB(0.25, 1.0));
            cellHulls.add(hull);

        }



//        add(new Repeat(0.05) {
//
//            @Override protected void update(double t, double dt, Spatial s) {
//                double d = 0.01;
//                for (Vector3f v : points) {
//                    v.x += Maths.random(-d, d);
//                    v.y += Maths.random(-d, d);
//                    v.z += Maths.random(-d, d);
//                }
//
//                //change points
//                hull.setPoints(points);
//            }
//        });
    }

    public static void main(String[] args) {
        ArdorSpacetime.newWindow(new DemoMultiBlobs());
    }
}
