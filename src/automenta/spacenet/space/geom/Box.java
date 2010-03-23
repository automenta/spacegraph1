/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.spacenet.space.geom;

import automenta.spacenet.space.*;
import automenta.spacenet.var.scalar.DoubleVar;
import automenta.spacenet.var.scalar.DoubleVar.IfDoubleChanges;
import automenta.spacenet.var.vector.Quat;
import automenta.spacenet.var.vector.Quat.IfQuatChanges;
import automenta.spacenet.var.vector.V3;
import automenta.spacenet.var.vector.V3.IfV3Changes;
import com.ardor3d.bounding.BoundingSphere;
import com.ardor3d.bounding.OrientedBoundingBox;
import com.ardor3d.math.Vector3;
import com.ardor3d.renderer.Renderer;
import com.ardor3d.renderer.state.CullState;
import com.ardor3d.scenegraph.Spatial;
import com.ardor3d.scenegraph.shape.Sphere;
import java.util.LinkedList;
import java.util.List;

/**
 * TODO rename to BoxSpace?
 * @author seh
 */
public class Box extends Space implements HasPosition3, HasScale3 {

    Spatial shapeSpatial;
    private final V3 position;
    private final V3 scale;
    private final Quat ori;
    private IfV3Changes positionChange;
    private IfV3Changes scaleChange;
    private IfQuatChanges oriChange;
    private BoxShape currentShape;
    private DoubleVar aspectXY = new DoubleVar(0.0);
    private IfDoubleChanges aspectXYChange;
    private final Vector3 vA = new Vector3(); //for temporary calculations

    public Box scale(double d) {
        return scale(d, d, d);
    }

    public void move(Vector3 c) {
        move(c.getX(), c.getY(), c.getZ());
    }

    public static enum BoxShape {

        Empty, Cubic, Spheroid
    }

    public Box(BoxShape b) {
        this(new V3(), new V3(1, 1, 1), new Quat(), b);
    }

    public Box(V3 position, V3 scale, Quat orientation, BoxShape shape) {
        super();

        this.position = position;
        this.scale = scale;
        this.ori = orientation;


        setShape(shape);
    }

    @Override protected void afterAttached(Spatial newParent) {
        positionChange = position.add(new V3.IfV3Changes() {

            @Override public void onV3Changed(V3 v) {
                positionChanged();
            }
        });
        scaleChange = scale.add(new V3.IfV3Changes() {

            @Override public void onV3Changed(V3 v) {
                sizeChanged();
            }
        });
        oriChange = ori.add(new IfQuatChanges() {

            @Override public void onQuatChanged(Quat q) {
                oriChanged();
            }
        });
        aspectXYChange = getAspectXY().add(new DoubleVar.IfDoubleChanges() {
            @Override public void onDoubleChange(DoubleVar d) {
                aspectChanged();
            }
        });

        positionChanged();
        sizeChanged();
        oriChanged();

    }

    @Override protected void beforeDetached(Spatial parent) {
        position.remove(positionChange);
        scale.remove(scaleChange);
        ori.remove(oriChange);
        aspectXY.remove(aspectXYChange);
    }

    protected void positionChanged() {
        setTranslation(position);
    }

    protected void sizeChanged() {
        setScale(scale);
    }

    protected void oriChanged() {
        setRotation(ori);
    }

    protected void aspectChanged() {
        sizeChanged();
    }

    public void setShape(BoxShape shape) {
        if (this.currentShape == shape) {
            return;
        }

        if (shapeSpatial != null) {
            detachChild(shapeSpatial);
        }

        switch (shape) {
            case Empty:
//                //com.ardor3d.scenegraph.shape.Quad x = new com.ardor3d.scenegraph.shape.Quad("", 1.0, 1.0);
//                com.ardor3d.scenegraph.shape.Box x = new com.ardor3d.scenegraph.shape.Box();
//                CullState cs = new CullState();
//                cs.setCullFace(CullState.Face.FrontAndBack);
//                x.setRenderState(cs);
//                x.setModelBound(new OrientedBoundingBox());
//                shapeSpatial = x;
                break;
            case Cubic:
                com.ardor3d.scenegraph.shape.Box b = new com.ardor3d.scenegraph.shape.Box();
                b.setModelBound(new OrientedBoundingBox());
                shapeSpatial = b;
                break;
            case Spheroid:
                Sphere s = new Sphere("", 12, 12, 0.5);
                s.setModelBound(new BoundingSphere());
                shapeSpatial = s;
                break;
        }

        this.currentShape = shape;

        CullState cs = new CullState();
        cs.setCullFace(CullState.Face.None);
        setRenderState(cs);


        if (shapeSpatial != null) {
            attachChild(shapeSpatial);
        }
    }

    public Box move(double px, double py, double pz) {
        position.set(px, py, pz);
        return this;
    }

    public Box scale(double sx, double sy, double sz) {
        scale.set(sx, sy, sz);
        return this;
    }

    public Box span(double ulX, double ulY, double brX, double brY) {
        double w = Math.abs(brX - ulX);
        double h = Math.abs(brY - ulY);
        double cx = 0.5 * (ulX + brX);
        double cy = 0.5 * (ulY + brY);

        move(cx, cy, 0);
        scale(w, h, getSize().getZ());

        return this;
    }

    public Box moveDZ(double dz) {
        return move(getPosition().getX(), getPosition().getY(), getPosition().getZ() + dz);
    }

    public Box rotate(double heading, double attitude, double bank) {
        ori.set(heading, attitude, bank);
        return this;
    }

    @Override public V3 getPosition() {
        return position;
    }

    @Override public V3 getScale() {
        return scale;
    }

    @Override
    @Deprecated public V3 getSize() {
        return scale;
    }

    public Quat getOrientation() {
        return ori;
    }

    @Override public void removeAll() {
        List<Spatial> c = new LinkedList(getChildren());
        c.remove(shapeSpatial);
        for (Spatial s : c) {
            remove(s);
        }
    }

    public Box spanZ(double zStart, double zStop) {
        scale(getSize().getX(), getSize().getY(), Math.abs(zStop - zStart));
        move(getPosition().getX(), getPosition().getY(), 0.5 * (zStop + zStart));
        return this;
    }

    /** makes the box face a vector */
    public Box face(double x, double y, double z) {
        Vector3 up = new Vector3(0, 1, 0);
        getOrientation().lookAt(new Vector3(x, y, z), up);
        return this;
    }

    public Box aspectXY(double newAspectXY) {
        getAspectXY().set(newAspectXY);
        return this;
    }

    public DoubleVar getAspectXY() {
        return aspectXY;
    }

    @Override public void updateWorldTransform(boolean recurse) {
        super.updateWorldTransform(recurse);

        //TODO alX and alY
        applyAspectXY(((Vector3) getWorldScale()), ((Vector3) getWorldTranslation()), getWorldRotation(), getAspectXY().d(), 0, 0, vA);
    }


}
