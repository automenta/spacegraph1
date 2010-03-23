package automenta.spacenet.space.widget;

import automenta.spacenet.space.control.Draggable;
import automenta.spacenet.space.geom.Rect;
import automenta.spacenet.var.vector.V3;
import com.ardor3d.math.Ray3;
import com.ardor3d.math.Vector3;

abstract public class DragRect extends Rect implements Draggable {

    private Ray3 dragStart;
    private V3 startIntersect;
    private Vector3 lastIntersect;

    public DragRect() {
        super(RectShape.Rect);
    }

    @Override
    public void onDragStart(Ray3 rayDragStart) {
        dragStart = rayDragStart;
        startIntersect = getIntersectWorld(dragStart, null);
        onDragStart(startIntersect);
        lastIntersect = startIntersect;
    }

    @Override
    public void onDragging(Ray3 rayDrag) {
        Vector3 currentIntersect = getIntersectWorld(rayDrag, null);
        if (currentIntersect!=null) {
            onDragging(currentIntersect);
            lastIntersect = currentIntersect;
        }
    }




    @Override
    public void onDragStop(Ray3 rayDragStop) {
        Vector3 currentIntersect = getIntersectWorld(rayDragStop, null);
        if (currentIntersect==null)
            currentIntersect = lastIntersect;
        onDragging(currentIntersect);
    }

    @Override
    public boolean isTangible() {
        return true;
    }

    abstract protected void onDragStart(Vector3 currentIntersect);
    abstract protected void onDragging(Vector3 currentIntersect);
    
}
