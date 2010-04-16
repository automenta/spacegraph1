package automenta.spacenet.run.old;

import automenta.spacenet.plugin.comm.BlankEdge;
import automenta.spacenet.space.Repeat;
import automenta.spacenet.space.control.Touchable;
import automenta.spacenet.space.control.Zoomable;
import automenta.spacenet.space.geom.Box;
import automenta.spacenet.space.geom.Line3D;
import automenta.spacenet.space.geom.Rect;
import automenta.spacenet.space.geom.Rect.RectShape;
import com.ardor3d.intersection.PickData;
import com.ardor3d.scenegraph.Spatial;

public class DefaultEdgeLine extends Line3D implements Touchable, /*Pressable,*/ Zoomable {

    protected boolean showingLabel = false;
    //private final Text3D label;
    private Rect rect;
    double sc = 8.0;
    private double updatePeriod = 0.02;
    double relativeScaleToEndBoxes = 0.1;
    private DefaultObjectBox box;
    private final Object obj;

    public DefaultEdgeLine(Object obj, final Box pa, final Box pb) {
        super(pa.getPosition(), pb.getPosition(), 3, 0.02);

        this.obj = obj;

        if (!(obj instanceof BlankEdge)) {
            rect = add(new Rect(RectShape.Rect));
            rect.scale(0.5);
            rect.color(DefaultObjectBox.getColor(obj));
            add(new Repeat(updatePeriod) {

                @Override protected void update(double t, double dt, Spatial s) {
                    //double r = getRadius().d();
                    //double sizeThresh = 0.1;

//                if ((pa.getSize().getMaxComponent() < sizeThresh) || (pb.getSize().getMaxComponent() < sizeThresh)) {
//                    visible(false);
//                }
//                else {
//                    visible(true);
//                }
                    double r = 0.5 * Math.min(pa.getSize().getMaxComponent(), pb.getSize().getMaxComponent()) * relativeScaleToEndBoxes;
                    rect.scale(r * sc, r * sc);
                    if (box != null) {
                        box.scale(r * sc, r * sc, r * sc);
                    }
                    getRadius().set(r / 10.0);
                }
            });

        }

        showLabel(true);

//        label = new Text3D(DefaultObjectBox.font3d, obj.toString());
//        label.scale(0.5);
//        label.moveDZ(0.05);
//        label.color(Color.White);

    }

//    @Override
//    public void onPressStart(PickData pick) {
//        toggleLabel();
//    }
//
//    @Override
//    public void onPressStop(PickData pick) {
//    }

    @Override
    public boolean isTangible() {
        return true;
    }

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


    protected synchronized void showLabel(boolean b) {
        if (rect == null) {
            return;
        }

        if (!b) {
            if (box != null) {
                remove(box);
            }
        } else {
            if (box == null) {
                box = new DefaultObjectBox(obj);
                box.scale(0.75);
                box.moveDZ(0.1);
                add(box);
            }
        }
    }

    @Override
    public void onTouchStart(PickData pick) {
    }

    @Override
    public void onTouching(PickData pick) {
    }

    @Override
    public void onTouchStop() {
    }
}
