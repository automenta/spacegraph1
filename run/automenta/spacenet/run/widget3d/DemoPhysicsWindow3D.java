/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package automenta.spacenet.run.widget3d;

import automenta.spacenet.run.ArdorSpacetime;
import automenta.spacenet.run.DemoDefaults;
import automenta.spacenet.space.geom.ProcessBox;
import automenta.spacenet.space.geom.physics.PhyBox;
import automenta.spacenet.space.geom.physics.PhySpaceBox;
import automenta.spacenet.space.geom.text3d.Font3D;
import automenta.spacenet.space.widget3d.Dialog3D;

/**
 *
 * @author seh
 */
public class DemoPhysicsWindow3D extends ProcessBox {

    Font3D font = DemoDefaults.font;

    @Override protected void start() {
        PhySpaceBox pb = add(new PhySpaceBox(5.0, 5.0, 5.0));
        for (int i = 0; i < 8; i++) {
            pb.add(new PhyBox(0.5,0.25,0.1,1.0f)).add(new Dialog3D(font, "Label"));
        }
        pb.physics.getGravity().set(0,-3.5, 0);
    }

    public static void main(String[] args) {
        ArdorSpacetime.newWindow(new DemoPhysicsWindow3D());
    }


}
