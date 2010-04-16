/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.spacenet.run.old.physics;

import automenta.spacenet.run.ArdorSpacetime;
import automenta.spacenet.space.control.pointer.PhyPointer;
import automenta.spacenet.space.geom.ProcessBox;
import automenta.spacenet.space.geom.physics.PhyBox;
import automenta.spacenet.space.geom.physics.PhySpaceBox;
import automenta.spacenet.space.geom.physics.PhySphere;
import automenta.spacenet.var.physical.Color;
import automenta.spacenet.var.Maths;
import automenta.spacenet.var.vector.V3;
import com.ardor3d.framework.Canvas;
import com.ardor3d.input.Key;
import com.ardor3d.input.logical.InputTrigger;
import com.ardor3d.input.logical.KeyPressedCondition;
import com.ardor3d.input.logical.TriggerAction;
import com.ardor3d.input.logical.TwoInputStates;

/**
 *
 * @author seh
 */
public class DemoFallingObjects extends ProcessBox {

    public static void main(String[] args) {
        //Multiple windows can be created by calling newWindow repeatedly
        ArdorSpacetime.newWindow(new DemoFallingObjects());
    }
    protected PhySpaceBox phy;


    @Override protected void start() {
        phy = add(new PhySpaceBox());

        phy.physics.getTimeScale().set(1.0);
        phy.physics.getGravity().set(0, -0.0, 0);
        //add(phy.add(new PhyBox(new V3(1, 2, 0.5), new V3(1, 1, 1), 1.0f)));
        //add(phy.add(new PhyBox(new V3(-2, 2, -0.5), new V3(1, 0.5, 1), 1.0f)));
        //add(phy.add(new PhyBox(new V3(2, -2, 0.5), new V3(0.5, 1, 1), 1.0f)));

        PhyBox ground = phy.add(new PhyBox(new V3(0, -2, 0), new V3(15, 0.5, 15), 0f));
        ground.rotate(0.1, 0.1, 0.1);


        for (int i = 0; i < 25; i++) {
            fire();
        }

        getSpacetime().addCondition(new InputTrigger(new KeyPressedCondition(Key.SPACE), new TriggerAction() {

            public void perform(final Canvas source, final TwoInputStates inputState, final double tpf) {
                fire();
            }
        }));

    }

    public void fire() {
        double x = Maths.random(-3, 3);
        double y = Maths.random(-3, 3);
        double w = Maths.random(0.2, 0.5);
        double h = w; //Maths.random(0.2, 0.5);
        double d = w; //Maths.random(0.2, 0.5);
        double r1 = Maths.random(0, Math.PI);
        double r2 = Maths.random(0, Math.PI);
        double r3 = Maths.random(0, Math.PI);

        PhyBox box;
        if (Maths.random(0, 1.0) < 0.5) {
            box = phy.add(new PhyBox(new V3(x, y, 3.5), new V3(w, h, d), 0.1f));
            box.color(Color.Blue);
        } else {
            box = phy.add(new PhySphere(new V3(x, y, 3.5), new V3(w, h, d), 0.1f));
            box.color(Color.Purple);
        }
        box.rotate(r1, r2, r3);
    }
}
