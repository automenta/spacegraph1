/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.spacenet.space.widget.window;

import automenta.spacenet.space.widget.panel.Panel;
import automenta.spacenet.space.control.Draggable;
import com.ardor3d.intersection.PickData;
import com.ardor3d.math.Plane;
import com.ardor3d.math.Ray3;
import com.ardor3d.math.Vector3;
import automenta.spacenet.space.control.Touchable;
import automenta.spacenet.space.geom.Rect;
import automenta.spacenet.space.widget.panel.DefaultPanelModel;
import automenta.spacenet.space.widget.panel.PanelModel;
import automenta.spacenet.var.physical.Color;

/**
 *
 * @author Nader
 */
public class Window extends Panel implements Touchable, Draggable {

    private final Rect reactionRect;
    private boolean touching;

    public static enum DragMode {

        None, Move, SizeN, SizeS, SizeE, SizeW, SizeNW, SizeNE, SizeSE, SizeSW
    }
    DragMode dragMode = DragMode.None;
    DragMode dragModeStart = DragMode.None;
    //TODO account for dragStartOffset
    Vector3 a = new Vector3();
    Vector3 b = new Vector3();
    Vector3 c = new Vector3();
    Plane p = new Plane();
    Vector3 iWorld = new Vector3();
    Vector3 iParent = new Vector3();
    Vector3 iParentStart = new Vector3();
    Vector3 posStart = new Vector3();
    Vector3 iLocal = new Vector3();
    Vector3 iLocalStart = new Vector3();
    private float sr = -1;
    private float sg = -1;
    private float sb = -1;
    private float tr = -1;
    private float tg = -1;
    private float tb = -1;
    float or = 0.1f;
    float mr = 0.2f;
    float og = 0.1f;
    float mg = 0.2f;
    float ob = 0.1f;
    float mb = 0.2f;
    double resizeBorder = 0.1;
    double defaultDZ = 0.05;

    public Window() {
        this(new DefaultPanelModel());
    }

    public Window(PanelModel m) {
        super(m);
        reactionRect = add(new Rect(RectShape.Empty));
    }

    public Window(Panel p, double margins) {
        this();
        add(p).scale(1.0 - (margins)).moveDZ(defaultDZ);
    }

    @Override
    public void onTouchStart(PickData pick) {
        super.onTouchStart(pick);
        touching = true;
    }

    @Override
    public void onTouching(PickData pick) {
        super.onTouching(pick);
        Ray3 r = pick.getRay();
        updateIntersect(r);
        updateTouch();
    }

    protected void updateTouch() {
        double x = iLocal.getX();
        double y = iLocal.getY();

        double rb = resizeBorder / 2.0;

        reactionRect.removeAll();

        if (x > 0.5 - rb) {
            if (y > 0.5 - rb) {
                dragMode = DragMode.SizeNE;
                reactionRect.add(new Rect(Color.Purple).span(0.5 - rb, 0.5 - rb, 0.5 + rb, 0.5 + rb));
                return;
            } else if (y < -0.5 + rb) {
                dragMode = DragMode.SizeSE;
                reactionRect.add(new Rect(Color.Purple).span(0.5 - rb, -0.5 + rb, 0.5 + rb, -0.5 - rb));
                return;
            }
        } else if (x < -0.5 + rb) {
            if (y > 0.5 - rb) {
                dragMode = DragMode.SizeNW;
                reactionRect.add(new Rect(Color.Purple).span(-0.5 + rb, 0.5 - rb, -0.5 - rb, 0.5 + rb));
                return;
            } else if (y < -0.5 + rb) {
                dragMode = DragMode.SizeSW;
                reactionRect.add(new Rect(Color.Purple).span(-0.5 + rb, -0.5 + rb, -0.5 - rb, -0.5 - rb));
                return;
            }
        }

        dragMode = DragMode.Move;


    }

    @Override
    public void onTouchStop() {
        touching = false;

        super.onTouchStop();
        //System.out.println("stop touch");

        dragMode = DragMode.None;
        reactionRect.removeAll();
    }

    @Override
    public void onDragStart(Ray3 rayDrag) {
        updateIntersect(rayDrag);


        dragModeStart = dragMode;
        iLocalStart.set(iLocal);
        iParentStart.set(iParent);
        posStart.set(getPosition());
    }

    protected void updateIntersect(Ray3 r) {
        double x = getWorldTranslation().getX();
        double y = getWorldTranslation().getY();
        double z = getWorldTranslation().getZ();
        a.set(x, y, z);
        b.set(x + 1, y, z);
        c.set(x, y + 1, z);
        p.setPlanePoints(a, b, c);
        r.intersects(p, iWorld);
        getParent().worldToLocal(iWorld, iParent);
        worldToLocal(iWorld, iLocal);
    }

    @Override
    public void onDragging(final Ray3 rayDrag) {

        updateIntersect(rayDrag);

        double px = getPosition().getX();
        double py = getPosition().getY();

        if (dragModeStart == DragMode.Move) {
            px = posStart.getX() + (iParent.getX() - iParentStart.getX());
            py = posStart.getY() + (iParent.getY() - iParentStart.getY());
        } else {

            double nx = getSize().getX();
            double ny = getSize().getY();

            double dx = iLocal.getX() - iLocalStart.getX();
            double dy = iLocal.getY() - iLocalStart.getY();
//            double dx = iParent.getX() - iParentStart.getX();
//            double dy = iParent.getY() - iParentStart.getY();

            if (dragModeStart == DragMode.SizeNE) {
                nx += dx / 2.0;
                ny += dy / 2.0;
            } else if (dragModeStart == DragMode.SizeSE) {
                nx += dx / 2.0;
                ny -= dy / 2.0;
            } else if (dragModeStart == DragMode.SizeNW) {
                nx -= dx / 2.0;
                ny += dy / 2.0;
            } else if (dragModeStart == DragMode.SizeSW) {
                nx -= dx / 2.0;
                ny -= dy / 2.0;
            }


            px += dx / 4.0;
            py += dy / 4.0;

            nx = Math.abs(nx);
            ny = Math.abs(ny);

            scale(nx, ny);
            //setScale(nx, ny, 1.0);
        }

        move(px, py, 0);
        setTranslation(px, py, 0);
    }

    @Override
    public void onDragStop(Ray3 rayDragStop) {
    }

   
}
