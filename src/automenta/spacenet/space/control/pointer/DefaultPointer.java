/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.spacenet.space.control.pointer;

import automenta.spacenet.run.ArdorSpacetime;
import automenta.spacenet.space.Repeat;
import automenta.spacenet.space.Spacetime;
import automenta.spacenet.space.control.Draggable;
import automenta.spacenet.space.control.Pressable;
import automenta.spacenet.space.control.Tangible;
import automenta.spacenet.space.control.Touchable;
import automenta.spacenet.space.control.Zoomable;
import automenta.spacenet.space.control.camera.ArdorCamera;
import com.ardor3d.framework.Canvas;
import com.ardor3d.framework.NativeCanvas;
import com.ardor3d.input.MouseButton;
import com.ardor3d.input.MouseManager;
import com.ardor3d.input.logical.InputTrigger;
import com.ardor3d.input.logical.LogicalLayer;
import com.ardor3d.input.logical.MouseButtonPressedCondition;
import com.ardor3d.input.logical.MouseButtonReleasedCondition;
import com.ardor3d.input.logical.MouseMovedCondition;
import com.ardor3d.input.logical.TriggerAction;
import com.ardor3d.input.logical.TwoInputStates;
import com.ardor3d.intersection.PickData;
import com.ardor3d.intersection.PickResults;
import com.ardor3d.intersection.PickingUtil;
import com.ardor3d.intersection.PrimitivePickResults;
import com.ardor3d.math.Matrix3;
import com.ardor3d.math.Ray3;
import com.ardor3d.math.Vector2;
import com.ardor3d.math.Vector3;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.Spatial;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 *
 *
 * @author seh
 */
public class DefaultPointer extends Repeat implements Pointer {

    private double firstPersonRotateSpeed = 64.0;
    private double zoomOutSpeed = 2.0;
    private final Matrix3 tempMatrix = new Matrix3();
    private final Vector3 tempVectorA = new Vector3();
    private PrimitivePickResults pickResults;
    private Touchable currentTouch;
    private Tangible currentTangible;
    private Pressable currentPress;
    private PickData tangiblePick;
    private final Spacetime spacetime;
    private Draggable currentDraggable, beingDragged;
    Vector2 pixelPos = new Vector2();
    final Ray3 pickRay = new Ray3();
    Vector2 pixelPosDragStart = new Vector2();
    final Ray3 rayDrag = new Ray3();
    final Ray3 rayDragStart = new Ray3();
    final Ray3 rayDragStop = new Ray3();
    Vector2 pixelPosDragStop = new Vector2();
    private Mesh pickedMesh;
    private boolean middlePressed = false;
    Vector2 midPressStartPixelPos;
    List<PickData> pdList = new LinkedList();
    Vector3 rcUp = new Vector3();
    Vector3 rcLeft = new Vector3();
    Vector3 rcDir = new Vector3();
    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private Mesh pickedMeshWhenRightPressed;
    private double rightZoomTime;
    private double autoZoomPressTimeThreshold = 0.2; //in seconds
    

    protected void setRay(Vector2 pixelPos, Ray3 targetRay) {
        getCanvas().getCanvasRenderer().getCamera().getPickRay(pixelPos, false, targetRay);
    }

    public Ray3 getPickRay() {
        return pickRay;
    }

    
    protected void updatePointer(double dt) {
        setRay(pixelPos, pickRay);

        updatePick(pickRay);

        if (beingDragged != null) {
            dragged();
        } else if (currentDraggable != null) {
            //System.out.println(" drag dist: " + pixelPosDragStart.distance(pixelPos) + " @ " + pixelPos + " <- " + pixelPosDragStart);
            double dist = pixelPosDragStart.distance(pixelPos);
            if (dist > getDragThreshold()) {
                dragStart();
            } else {
                pixelPosDragStart.set(pixelPos);
            }
        } else {
            pixelPosDragStart.set(pixelPos);
        }

        if (middlePressed) {
            if (midPressStartPixelPos == null) {
                midPressStartPixelPos = new Vector2(pixelPos);
            }

            double dx = pixelPos.getX() - midPressStartPixelPos.getX();
            double dy = pixelPos.getY() - midPressStartPixelPos.getY();

            int pixelInnerRadius = Math.min(getSpacetime().getVideo().getCanvasRenderer().getCamera().getWidth(), getSpacetime().getVideo().getCanvasRenderer().getCamera().getHeight());

            dx /= (double) pixelInnerRadius;
            dy /= (double) pixelInnerRadius;

            //midPressStartPixelPos.set(pixelPos);

            rotateCam(dx, dy, dt);
        } else {
            midPressStartPixelPos = null;
        }


        if (rightPressed) {
            rightZoomTime+=dt;
            if (middlePressed) {
                if (pickedMeshWhenRightPressed == null) {
                    getSpacetime().getCamera().zoomForward(-zoomOutSpeed * dt);
                } else {
                    getSpacetime().getCamera().zoomForward(zoomOutSpeed * dt);
                }
            } else {
                getSpacetime().getCamera().zoomForward(-zoomOutSpeed * dt);
            }
        } else {
            rightZoomTime = 0;
        }

    }

    protected void dragStart() {
        setRay(pixelPos, rayDragStart);
        beingDragged = currentDraggable;

        beingDragged.onDragStart(rayDragStart);
    }

    protected void dragged() {
        setRay(pixelPos, rayDrag);
        beingDragged.onDragging(rayDrag);
    }

    protected void dragStop() {
        pixelPosDragStop.set(pixelPos);
        setRay(pixelPosDragStop, rayDragStop);

        beingDragged.onDragStop(rayDragStop);
    }

    protected double getDragThreshold() {
        return 1.01;
    }

    protected void leftPressed() {
        leftPressed = true;
//        if (getMouseManager().isSetGrabbedSupported()) {
//            getMouseManager().setGrabbed(GrabbedState.GRABBED);
//        }

        if (currentTangible instanceof Pressable) {
            currentPress = (Pressable) currentTangible;
            ((Pressable) currentTangible).onPressStart(tangiblePick);
        }

        if (currentTangible instanceof Draggable) {
            if (currentDraggable != currentTangible) {
                currentDraggable = (Draggable) currentTangible;
                //System.out.println("start drag " + pixelPos);
                pixelPosDragStart.set(pixelPos);
            }
        }

    }

    protected void leftReleased() {
        leftPressed = false;
//        if (getMouseManager().isSetGrabbedSupported()) {
//            getMouseManager().setGrabbed(GrabbedState.NOT_GRABBED);
//        }

        if (currentPress != null) {
            currentPress.onPressStop(tangiblePick);
            currentPress = null;
        }

        currentDraggable = null;

        if (beingDragged != null) {
            dragStop();
        }
        beingDragged = null;


    }

    public Spacetime getSpacetime() {
        return spacetime;
    }

    protected void middlePressed() {
        this.middlePressed = true;
    }

    protected void middleReleased() {
        this.middlePressed = false;
    }

    protected void rightPressed() {
        this.rightPressed = true;
        this.pickedMeshWhenRightPressed = pickedMesh;
    }

    protected void rightReleased() {
        this.rightPressed = false;
        this.pickedMeshWhenRightPressed = null;

        if (rightZoomTime < autoZoomPressTimeThreshold) {
            if (!middlePressed) {
                if (currentTangible != null) {
                    Zoomable z = getZoomable(currentTangible);
                    if (z != null) {
                        getSpacetime().getCamera().zoomTo(z, currentTangible, pickedMesh);
                    }
                }
            }
        }

    }

    public DefaultPointer(Spacetime spacetime) {
        super();

        this.spacetime = spacetime;

        // Set up a reusable pick results
        pickResults = new PrimitivePickResults();
        pickResults.setCheckDistance(true);


        //        _logicalLayer.registerTrigger(new InputTrigger(new MouseButtonClickedCondition(MouseButton.RIGHT),
//            new TriggerAction() {
//
//                public void perform(final Canvas source, final TwoInputStates inputStates, final double tpf) {
//
//                    final Vector2 pos = Vector2.fetchTempInstance().set(
//                        inputStates.getCurrent().getMouseState().getX(),
//                        inputStates.getCurrent().getMouseState().getY());
//                    final Ray3 pickRay = new Ray3();
//                    _canvas.getCanvasRenderer().getCamera().getPickRay(pos, false, pickRay);
//                    Vector2.releaseTempInstance(pos);
//                    doPick(pickRay);
//                }
//            }));
//
//        final Predicate<TwoInputStates> clickLeftOrRight = Predicates.or(new MouseButtonClickedCondition(
//            MouseButton.LEFT), new MouseButtonClickedCondition(MouseButton.RIGHT));
//
//        _logicalLayer.registerTrigger(new InputTrigger(clickLeftOrRight, new TriggerAction() {
//
//            public void perform(final Canvas source, final TwoInputStates inputStates, final double tpf) {
//                System.err.println("clicked: " + inputStates.getCurrent().getMouseState().getClickCounts());
//            }
//        }));

        spacetime.addCondition(new InputTrigger(new MouseButtonPressedCondition(MouseButton.LEFT),
            new TriggerAction() {

                public void perform(final Canvas source, final TwoInputStates inputState, final double tpf) {
                    leftPressed();
                }
            }));

        spacetime.addCondition(new InputTrigger(new MouseButtonReleasedCondition(MouseButton.LEFT),
            new TriggerAction() {

                public void perform(final Canvas source, final TwoInputStates inputState, final double tpf) {
                    leftReleased();
                }
            }));
        spacetime.addCondition(new InputTrigger(new MouseButtonPressedCondition(MouseButton.RIGHT),
            new TriggerAction() {

                public void perform(final Canvas source, final TwoInputStates inputState, final double tpf) {
                    rightPressed();
                }
            }));
        spacetime.addCondition(new InputTrigger(new MouseButtonPressedCondition(MouseButton.MIDDLE),
            new TriggerAction() {

                public void perform(final Canvas source, final TwoInputStates inputState, final double tpf) {
                    middlePressed();
                }
            }));
        spacetime.addCondition(new InputTrigger(new MouseButtonReleasedCondition(MouseButton.MIDDLE),
            new TriggerAction() {

                public void perform(final Canvas source, final TwoInputStates inputState, final double tpf) {
                    middleReleased();
                }
            }));

        spacetime.addCondition(new InputTrigger(new MouseButtonReleasedCondition(MouseButton.RIGHT),
            new TriggerAction() {

                public void perform(final Canvas source, final TwoInputStates inputState, final double tpf) {
                    rightReleased();
                }
            }));

        spacetime.addCondition(new InputTrigger(new MouseMovedCondition(), new TriggerAction() {

            public void perform(final Canvas source, final TwoInputStates inputStates, final double tpf) {
                pixelPos.set(inputStates.getCurrent().getMouseState().getX(), inputStates.getCurrent().getMouseState().getY());

                //moved();
            }
        }));

    }

    @Override
    protected void update(double t, double dt, Spatial s) {
        updatePointer(dt);
    }

    public Node getRoot() {
        return spacetime.getRoot();
    }

    public NativeCanvas getCanvas() {
        return spacetime.getVideo();
    }

    public LogicalLayer getLogicalLayer() {
        return spacetime.getInputLogic();
    }

    public MouseManager getMouseManager() {
        return spacetime.getMouseManager();
    }

    private void print(List<PickData> pr) {

        System.out.println("pick results: " + pr.size());
        for (PickData pd : pr) {
            System.out.println(" " + pd.getClosestDistance() + " : " + pd.getTargetMesh() + " " + isTangible(pd.getTargetMesh()));
        }
    }

    protected void processPicks(final PrimitivePickResults pickResults) {
        pdList.clear();
        for (int i = 0; i < pickResults.getNumber(); i++) {
            pdList.add(pickResults.getPickData(i));
        }

        Collections.sort(pdList, new Comparator<PickData>() {

            @Override public int compare(PickData a, PickData b) {
                double ad = a.getClosestDistance();
                double bd = b.getClosestDistance();
                if (ad == bd) {
                    return 0;
                }
                if (ad < bd) {
                    return -1;
                }
                return 1;
            }
        });

        //print(pdList);

        setTangible(null, null);
        pickedMesh = null;

        boolean touched = false;
        for (PickData pd : pdList) {
            if (isTangible(pd.getTargetMesh())) {

                final PickData pick = pd;

                pickedMesh = pd.getTargetMesh();

                final Tangible topLevel = getTangible(pick.getTargetMesh());
                setTangible(topLevel, pick);

                if (topLevel instanceof Touchable) {
                    setPicked((Touchable) topLevel, pick);
                    touched = true;
                    return;
                } 
            }
        }

        if (!touched)
            setPicked(null, null);

    }

    private void setTangible(Tangible t, PickData pick) {
        this.currentTangible = t;
        this.tangiblePick = pick;
    }

    private void setPicked(Touchable p, PickData pick) {

       // System.out.println("picked currentTouch=" + currentTouch + " , touchable=" + p);
        if (this.currentTouch == p) {
            if (p != null) {
                currentTouch.onTouching(pick);
            }
        } else {

            if (this.currentTouch != null) {
                currentTouch.onTouchStop();
            }

            this.currentTouch = p;

            if (currentTouch != null) {
                p.onTouchStart(pick);
            }
        }
    }

    public static <C> C getParent(final Spatial target, Class<? extends C> c) {
        if (c.isInstance(target)) {
            return (C) target;
        }
        if (target.getParent() == null) {
            return null;
        } else {
            return getParent(target.getParent(), c);
        }
    }

    public static Tangible getTangible(final Spatial target) {
        Tangible t = getParent(target, Tangible.class);
        if (t == null) {
            return null;
        }
        if (t.isTangible()) {
            return t;
        }
        return null;
    }

    public static Zoomable getZoomable(final Object target) {
        Zoomable z = getParent((Spatial) target, Zoomable.class);
        if (z == null) {
            return null;
        }
        if (z.isZoomable()) {
            return z;
        }
        return null;
    }

    public PickResults updatePick(Ray3 pickRay) {
        pickResults.clear();

        pickResults.setCheckDistance(true);

        PickingUtil.findPick(getRoot(), pickRay, pickResults);

        processPicks(pickResults);

        return pickResults;
    }

    protected void rotateCam(double dx, double dy, double dt) {

        dx = -dx;
        dy = -dy;

        //System.out.println("rotateCam: " + dx + " " + dy);

        ArdorCamera camera = getSpacetime().getCamera();

        rcUp.set(camera.getCurrentUp());
        rcLeft.set(camera.getCurrentLeft());
        rcDir.set(camera.getCurrentDirection());

        tempMatrix.fromAngleNormalAxis(firstPersonRotateSpeed * dx * dt, rcUp);

        tempMatrix.applyPost(rcLeft, tempVectorA);
        rcLeft.set(tempVectorA);

        tempMatrix.applyPost(rcDir, tempVectorA);
        rcDir.set(tempVectorA);

        tempMatrix.applyPost(rcUp, tempVectorA);
        rcUp.set(tempVectorA);


        tempMatrix.fromAngleNormalAxis(firstPersonRotateSpeed * dy * dt, rcLeft);

        tempMatrix.applyPost(rcDir, tempVectorA);
        rcDir.set(tempVectorA);
        rcDir.normalizeLocal();


        camera.getTargetTarget().set(camera.getTargetPosition()).addLocal(rcDir);
        //camera.getTargetUp().set(up);

    }
//    protected void rotateCam(double dx, double dy, double dt) {
//        //TODO make camera.setTargetToCurrent() unnecessary - it produces a jerky movement
//
//        dx = -dx;
//        dy = -dy;
//
//        System.out.println("rotateCam: " + dx + " " + dy);
//
//        ArdorCamera camera = getSpacetime().getCamera();
//
//        if (dx != 0) {
//            tempMatrix.fromAngleNormalAxis(firstPersonRotateSpeed * dx, _upAxis != null ? _upAxis : camera.getCurrentUp());
//            tempMatrix.applyPost(camera.getCurrentLeft(), tempVectorA);
//            //camera.gettar setLeft(_workerStoreA);
//            tempMatrix.applyPost(camera.getCurrentDirection(), tempVectorA);
//            camera.getTargetTarget().set(camera.getTargetPosition()).addLocal(tempVectorA);
//            tempMatrix.applyPost(camera.getCurrentUp(), tempVectorA);
//            camera.getTargetUp().set(tempVectorA);
//            camera.setTargetToCurrent();
//        }
//
//        if (dy != 0) {
//            tempMatrix.fromAngleNormalAxis(firstPersonRotateSpeed * dy, camera.getCurrentLeft());
//            tempMatrix.applyPost(camera.getCurrentLeft(), tempVectorA);
//            //camera.setLeft(_workerStoreA);
//            tempMatrix.applyPost(camera.getCurrentDirection(), tempVectorA);
//            camera.getTargetTarget().set(camera.getTargetPosition()).addLocal(tempVectorA);
//            tempMatrix.applyPost(camera.getCurrentUp(), tempVectorA);
//            camera.getTargetUp().set(tempVectorA);
//            camera.setTargetToCurrent();
//        }
//
//    }

    private boolean isTangible(Mesh targetMesh) {
        return getTangible(targetMesh) != null;
    }

    @Override public String toString() {
        //TODO add more pointer stats
        return ("pickedMesh=" + pickedMesh + ", currentTouch=" + currentTouch);
    }

    public Node getCurrentTouched() {
        if (currentTouch instanceof Node) {
            return ((Node) currentTouch);
        }
        return null;
    }

    public boolean isPressed(int button) {
        //TODO use a list of Button Boolean's
        if (button == 0) {
            return leftPressed;
        }
        return false;
    }

}
