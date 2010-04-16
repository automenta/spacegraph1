/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package automenta.spacenet.run.old.physics;

import automenta.spacenet.run.ArdorSpacetime;
import automenta.spacenet.space.geom.physics.PhyBox;
import automenta.spacenet.space.geom.physics.PhySphere;
import automenta.spacenet.var.physical.Color;
import automenta.spacenet.var.Maths;
import automenta.spacenet.var.vector.V3;

/**
 *
 * @author seh
 */
public class DemoTwitterHunt extends DemoFallingMetaBoxes {

    
    public static void main(String[] args) {
        //Multiple windows can be created by calling newWindow repeatedly
        ArdorSpacetime.newWindow(new DemoTwitterHunt());
    }

    public void fire() {
        double x = getSpacetime().getCamera().getCurrentPosition().getX();
        double y = getSpacetime().getCamera().getCurrentPosition().getY();
        double z = getSpacetime().getCamera().getCurrentPosition().getZ();
        double w = Maths.random(0.2, 0.5);
        double h = w; //Maths.random(0.2, 0.5);
        double d = w; //Maths.random(0.2, 0.5);
        double r1 = Maths.random(0, Math.PI);
        double r2 = Maths.random(0, Math.PI);
        double r3 = Maths.random(0, Math.PI);

        double v = 10.0;
        double vx = getSpacetime().getCamera().getCurrentDirection().getX() * v;
        double vy = getSpacetime().getCamera().getCurrentDirection().getY() * v;
        double vz = getSpacetime().getCamera().getCurrentDirection().getZ() * v;

        PhyBox box;
        if (Maths.random(0,1.0) < 0.5) {
            box = add(phy.add(new PhyBox(new V3(x, y, z), new V3(w, h, d), 0.1f)));
            box.color(Color.Blue);
        }
        else {
            box = add(phy.add(new PhySphere(new V3(x, y, z), new V3(w, h, d), 0.1f)));
            box.color(Color.Purple);
        }
        System.out.println("velocity: " + vx + " " + vy +  " " + vz);

        box.setVelocity(vx, vy, vz);
        box.rotate(r1, r2, r3);
    }
}
