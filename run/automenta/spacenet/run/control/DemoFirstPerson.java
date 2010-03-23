/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package automenta.spacenet.run.control;

import automenta.spacenet.run.ArdorSpacetime;
import automenta.spacenet.space.geom.ProcessBox;
import automenta.spacenet.space.geom.Rect;
import automenta.spacenet.var.physical.Color;
import automenta.spacenet.var.Maths;

/**
 * space for testing first-person controls - a scene that includes 3d objects surrounding the camera
 */
public class DemoFirstPerson extends ProcessBox {

    int numObjects = 128;
    double radius = 10.0;
    double minSize = 0.5;
    double maxSize = 2.5;

    @Override
    protected void start() {

        for (int i = 0; i < numObjects; i++) {
            double x = Maths.random(-radius, radius);
            double y = Maths.random(-radius, radius);
            double z = Maths.random(-radius, radius);
            double w = Maths.random(minSize, maxSize);
            double h = Maths.random(minSize, maxSize);

            double r1 = Maths.random(-Math.PI, Math.PI);
            double r2 = Maths.random(-Math.PI, Math.PI);
            double r3 = Maths.random(-Math.PI, Math.PI);

            Rect r = add(new ZoomableRect());
            r.move(x, y, z).scale(w, h).rotate(r1, r2, r3);

            r.color(Color.newRandomHSB(1.0, 1.0));
        }
    }

    public static void main(String[] args) {
        ArdorSpacetime.newWindow(new DemoFirstPerson());
    }


}
