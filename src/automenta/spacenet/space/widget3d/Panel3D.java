package automenta.spacenet.space.widget3d;

import automenta.spacenet.space.control.Zoomable;
import automenta.spacenet.space.geom.Box;
import automenta.spacenet.space.geom.TrapBox;

public class Panel3D extends TrapBox implements Zoomable {

    public Panel3D() {
        super(0.97);
        //SolidBoxBorder border = addFront(new SolidBoxBorder(0.985, 1.0, 0.985, 1.0), -0.5, -0.5, 0.5, 0.5, 0.1);
        //border.color(Color.GrayMinus);
    }

    public <B extends Box> B addFront(B b, double tx, double ty, double bx, double by, double d) {
        b.span(tx, ty, bx, by);
        b.spanZ(0.5, 0.5 + d);
        add(b);
        return b;
    }

//        public <B extends Box> B addLeft(B b, double tx, double ty, double bx, double by, double d) {
//
//            b.span(tx, ty, bx, by);
//            b.spanZ(0.5, 0.5 + d);
//            add(b);
//            return b;
//        }
    @Override
    public void onZoomStart() {
    }

    @Override
    public void onZoomStop() {
    }

    @Override
    public boolean isZoomable() {
        return true;
    }

    @Override
    public boolean isTangible() {
        return true;
    }
}
