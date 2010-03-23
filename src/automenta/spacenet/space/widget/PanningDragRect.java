/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package automenta.spacenet.space.widget;

import automenta.spacenet.space.control.Zoomable;
import automenta.spacenet.var.scalar.DoubleVar;
import com.ardor3d.math.Vector3;

/** rect that when dragged, pulls the camera to look towards the drag point */

public class PanningDragRect extends DragRect implements Zoomable {
    private Vector3 dragStartPoint;
    private Vector3 camTargetStart;
    private Vector3 camPosStart;
    private final DoubleVar speed;
    private boolean zoomable = true;
    

    public PanningDragRect() {
        this(1.0);
    }


    public PanningDragRect(double speed) {
        super();
        this.speed = new DoubleVar(speed);
    }


    @Override
    protected void onDragStart(Vector3 currentIntersect) {
        this.dragStartPoint = new Vector3(currentIntersect);
        camTargetStart = new Vector3(getSpacetime().getCamera().getTargetTarget());
        camPosStart = new Vector3(getSpacetime().getCamera().getTargetPosition());
    }

    public DoubleVar getSpeed() {
        return speed;
    }
    
    @Override protected void onDragging(Vector3 c) {
        Vector3 delta = new Vector3(c).subtractLocal(dragStartPoint).multiplyLocal(getSpeed().d());
        getSpacetime().getCamera().getTargetPosition().set(camPosStart);
        getSpacetime().getCamera().getTargetPosition().subtractLocal(delta);
        getSpacetime().getCamera().getTargetTarget().set(camTargetStart);
        getSpacetime().getCamera().getTargetTarget().subtractLocal(delta);
    }

    public void setZoomable(boolean zoomable) {
        this.zoomable = zoomable;
    }

    
    @Override
    public void onZoomStart() {
    }

    @Override
    public void onZoomStop() {
    }

    @Override
    public boolean isZoomable() {
        return zoomable;
    }

}
