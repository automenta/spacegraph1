/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.spacenet.run.widget3d;

import automenta.spacenet.space.widget3d.Dialog3D;
import automenta.spacenet.run.ArdorSpacetime;
import automenta.spacenet.run.DemoDefaults;
import automenta.spacenet.space.geom.Box;
import automenta.spacenet.space.geom.ProcessBox;
import automenta.spacenet.space.geom.text3d.Font3D;


/**
 *
 */
public class DemoSphereWithWindows extends ProcessBox {

    Font3D font = DemoDefaults.font;


    @Override protected void start() {
        int numWindows = 5;
        Box center = add(new Box(BoxShape.Spheroid));
        double t = 0.0;
        double w = 0.5;
        double h = 0.55;
        double d = 0.05;
        for (int i = 0; i < numWindows; i++) {
            double x = Math.sin(t)/2.0;
            double y = 0;
            double z = Math.cos(t)/2.0;
            double normal = t;
            center.add(new Dialog3D(font, " ", w, h, d)).move(x, y, z).rotate(normal, 0, 0);
            t += Math.PI*2.0 / ((double)numWindows);
        }
    }

    public static void main(String[] args) {
        ArdorSpacetime.newWindow(new DemoSphereWithWindows());
    }
    
}
