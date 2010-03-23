package automenta.spacenet.space.widget.window;

import automenta.spacenet.space.control.Draggable;
import automenta.spacenet.space.geom.Rect;
import automenta.spacenet.space.surface.ColorSurface;
import com.ardor3d.math.Plane;
import com.ardor3d.math.Ray3;
import com.ardor3d.math.Vector3;

@Deprecated class WindowRect extends Rect implements Draggable {

    private final ColorSurface cs;
    Vector3 a = new Vector3();
    Vector3 b = new Vector3();
    Vector3 c = new Vector3();
    Plane p = new Plane();
    Vector3 i = new Vector3();
    Vector3 d = new Vector3();

    public WindowRect() {
        super(RectShape.Rect);
        cs = add(new ColorSurface());
    }

    @Override
    public boolean isTangible() {
        return true;
    }

    @Override
    public void onDragStart(Ray3 rayDrag) {
        updateIntersect(rayDrag);
        d.set(i);
        i.subtractLocal(getWorldTranslation());
        System.out.println("drag start d=" + d);
        cs.color(0, 1, 0);
    }

    protected void updateIntersect(Ray3 r) {
        double x = getWorldTranslation().getX();
        double y = getWorldTranslation().getY();
        double z = getWorldTranslation().getZ();
        a.set(x, y, z);
        b.set(x + 1, y, z);
        c.set(x, y + 1, z);
        p.setPlanePoints(a, b, c);
        r.intersects(p, i);
    }

    @Override
    public void onDragging(final Ray3 rayDrag) {
        cs.color(0, 1, 0);
        updateIntersect(rayDrag);
        //TODO use World -> Local
        //Vector3 o = getWorldTransform().applyInverseVector(i);
        getPosition().set(i);
    }

    @Override
    public void onDragStop(Ray3 rayDragStop) {
        cs.color(0.5, 0.5, 0.5);
    }
}
