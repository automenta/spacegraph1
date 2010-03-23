/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.spacenet.space.control.pointer;

import automenta.spacenet.run.ArdorSpacetime;
import automenta.spacenet.space.Repeat;
import automenta.spacenet.space.SpaceGraphSwingWindow;
import automenta.spacenet.space.control.camera.ArdorCamera;
import com.ardor3d.bounding.OrientedBoundingBox;
import com.ardor3d.framework.Canvas;
import com.ardor3d.input.Key;
import com.ardor3d.input.KeyboardState;
import com.ardor3d.input.logical.InputTrigger;
import com.ardor3d.input.logical.KeyPressedCondition;
import com.ardor3d.input.logical.TriggerAction;
import com.ardor3d.input.logical.TwoInputStates;
import com.ardor3d.math.Vector3;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.Spatial;
import com.google.common.base.Predicate;

/**
 *
 * @author seh
 */
public class DefaultKeyboard extends Repeat {

    private double strafeSpeed = 2.0;
    private final ArdorSpacetime spacetime;

    public DefaultKeyboard(final ArdorSpacetime spacetime, final DefaultPointer pointer) {
        super();
        this.spacetime = spacetime;

        // WASD control
        final Predicate<TwoInputStates> keysHeld = new Predicate<TwoInputStates>() {

            Key[] keys = new Key[]{Key.LMENU, Key.LCONTROL, Key.LEFT, Key.RIGHT, Key.UP, Key.DOWN};

            public boolean apply(final TwoInputStates states) {
                for (final Key k : keys) {
                    if (states.getCurrent() != null && states.getCurrent().getKeyboardState().isDown(k)) {
                        return true;
                    }
                }
                return false;
            }
        };

        final TriggerAction moveAction = new TriggerAction() {

            public void perform(final Canvas source, final TwoInputStates inputStates, final double tpf) {
                moveCam(inputStates.getCurrent().getKeyboardState(), tpf);
            }
        };
        spacetime.addCondition(new InputTrigger(keysHeld, moveAction));

        spacetime.addCondition(new InputTrigger(new KeyPressedCondition(Key.ESCAPE), new TriggerAction() {

            public void perform(final Canvas source, final TwoInputStates inputState, final double tpf) {
                spacetime.stop();
            }
        }));

        spacetime.addCondition(new InputTrigger(new KeyPressedCondition(Key.SCROLL), new TriggerAction() {

            public void perform(final Canvas source, final TwoInputStates inputState, final double tpf) {
                Node currentTouch = pointer.getCurrentTouched();
                newSwingTree(currentTouch != null ? currentTouch : spacetime);
            }
        }));

        spacetime.addCondition(new InputTrigger(new KeyPressedCondition(Key.SLASH), new TriggerAction() {

            public void perform(final Canvas source, final TwoInputStates inputState, final double tpf) {
                printTree();
            }
        }));

    }

    public void newSwingTree(Node node) {
        new SpaceGraphSwingWindow(node);
    }

    public void printTree() {
        System.out.println(this);
        for (Spatial s : spacetime.getChildren()) {
            printChild(s, 1);
        }
    }

    protected void printChild(Spatial s, int indentation) {
        for (int i = 0; i < indentation; i++) {
            System.out.print(" ");
        }

        if (s.getWorldBound() instanceof OrientedBoundingBox) {
            OrientedBoundingBox obb = (OrientedBoundingBox) s.getWorldBound();
            System.out.println(s + " " + obb.getCenter() + " " + obb.getExtent());
        } else {
            System.out.println(s);
        }

        if (s instanceof Node) {
            for (Spatial c : ((Node) s).getChildren()) {
                printChild(c, indentation + 1);
            }
        }
    }

    @Override protected void update(double t, double dt, Spatial s) {
    }

    protected void moveCam(final KeyboardState kb, final double tpf) {
        ArdorCamera camera = spacetime.getCamera();

        // MOVEMENT
        int moveFB = 0, strafeLR = 0, strafeUD = 0;
        if (kb.isDown(Key.LCONTROL)) {
            moveFB += 1;
        }
        if (kb.isDown(Key.LMENU)) {
            moveFB -= 1;
        }
        if (kb.isDown(Key.LEFT)) {
            strafeLR += 1;
        }
        if (kb.isDown(Key.RIGHT)) {
            strafeLR -= 1;
        }
        if (kb.isDown(Key.UP)) {
            strafeUD = 1;
        }
        if (kb.isDown(Key.DOWN)) {
            strafeUD = -1;
        }

        if (moveFB != 0 || strafeLR != 0 || strafeUD != 0) {
            final Vector3 loc = new Vector3(); //_workerStoreA.zero();

            if (moveFB == 1) {
                loc.addLocal(camera.getCurrentDirection());
            } else if (moveFB == -1) {
                loc.subtractLocal(camera.getCurrentDirection());
            }
            if (strafeLR == 1) {
                loc.addLocal(camera.getCurrentLeft());
            } else if (strafeLR == -1) {
                loc.subtractLocal(camera.getCurrentLeft());
            }
            if (strafeUD == 1) {
                loc.addLocal(camera.getCurrentUp());
            } else if (strafeUD == -1) {
                loc.subtractLocal(camera.getCurrentUp());
            }

            //loc.normalizeLocal().multiplyLocal(_moveSpeed * tpf).addLocal(camera.getCurrentPosition());

            loc.multiplyLocal(strafeSpeed * tpf);
            spacetime.getCamera().getTargetPosition().addLocal(loc);
            spacetime.getCamera().getTargetTarget().addLocal(loc);
        }

//        // ROTATION
//        int rotX = 0, rotY = 0;
//        if (kb.isDown(Key.UP)) {
//            rotY -= 1;
//        }
//        if (kb.isDown(Key.DOWN)) {
//            rotY += 1;
//        }
//        if (kb.isDown(Key.LEFT)) {
//            rotX += 1;
//        }
//        if (kb.isDown(Key.RIGHT)) {
//            rotX -= 1;
//        }
//        if (rotX != 0 || rotY != 0) {
//            //rotate(camera, rotX * (_keyRotateSpeed / _mouseRotateSpeed) * tpf, rotY * (_keyRotateSpeed / _mouseRotateSpeed) * tpf);
//        }
    }
}
