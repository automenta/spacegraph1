/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.spacenet.space.geom;

import automenta.spacenet.space.HasPosition3;
import automenta.spacenet.space.HasScale2;
import automenta.spacenet.space.Space;
import automenta.spacenet.space.WrapsMesh;
import automenta.spacenet.var.physical.Color;
import automenta.spacenet.var.scalar.DoubleVar;
import automenta.spacenet.var.scalar.DoubleVar.IfDoubleChanges;
import automenta.spacenet.var.vector.Quat;
import automenta.spacenet.var.vector.Quat.IfQuatChanges;
import automenta.spacenet.var.vector.V2;
import automenta.spacenet.var.vector.V2.IfV2Changes;
import automenta.spacenet.var.vector.V3;
import automenta.spacenet.var.vector.V3.IfV3Changes;
import com.ardor3d.bounding.OrientedBoundingBox;
import com.ardor3d.math.Ray3;
import com.ardor3d.math.Vector3;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.scenegraph.Spatial;
import com.ardor3d.scenegraph.shape.Disk;
import com.ardor3d.scenegraph.shape.Quad;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author seh
 */
public class Rect extends Space implements HasPosition3, HasScale2, WrapsMesh {

    Mesh shapeSpatial;
    private final V3 position;
    private final V2 scale;
    private final Quat ori;
    private IfV3Changes positionChange;
    private IfV2Changes scaleChange;
    private RectShape currentShape;
    private IfQuatChanges oriChange;
    private final DoubleVar aspect = new DoubleVar(0);
    private final Vector3 vA = new Vector3(); //temporary
    private IfDoubleChanges aspectChange;

    public Rect(Color fillColor) {
        this(RectShape.Rect);
        color(fillColor);
    }

    /** aspect ratio (y/x)
     *  @return if aspect=0, aspect ratio is not specified   */
    public DoubleVar getAspect() {
        return aspect;
    }

    @Override
    public Mesh getWrappedMesh() {
        return shapeSpatial;
    }

    public static enum RectShape {

        Empty, Rect, Ellipse
    }

    public Rect(RectShape shape) {
        this(new V3(0), new V2(1), new Quat(), shape);
    }

    public Rect(V3 position, V2 scale, Quat orientation, RectShape shape) {
        super();

        this.position = position;
        this.scale = scale;
        this.ori = orientation;

        setShape(shape);
    }

    protected void afterAttached(Spatial newParent) {
        positionChange = position.add(new V3.IfV3Changes() {

            @Override public void onV3Changed(V3 v) {
                positionChanged();
            }
        });
        scaleChange = scale.add(new IfV2Changes() {

            @Override public void onV2Changed(V2 v) {
                sizeChanged();
            }
        });
        oriChange = ori.add(new IfQuatChanges() {

            @Override public void onQuatChanged(Quat q) {
                oriChanged();
            }
        });
        aspectChange = aspect.add(new DoubleVar.IfDoubleChanges() {
            @Override public void onDoubleChange(DoubleVar d) {
                aspectChanged();
            }
        });

        positionChanged();
        sizeChanged();
        oriChanged();

    }

    protected void beforeDetached(Spatial parent) {
        //System.out.println(this + " detached from " + parent);
        position.remove(positionChange);
        scale.remove(scaleChange);
        ori.remove(oriChange);
        aspect.remove(aspectChange);
    }

    public Rect move(double x, double y) {
        return move(x, y, 0);
    }

    public Rect span(double ulX, double ulY, double brX, double brY) {
        double w = Math.abs(brX - ulX);
        double h = Math.abs(brY - ulY);
        double cx = 0.5 * (ulX + brX);
        double cy = 0.5 * (ulY + brY);

        move(cx, cy);
        scale(w, h);

        return this;
    }

    protected void positionChanged() {
        setTranslation(position);
    }

    protected void sizeChanged() {
        setScale(scale.getX(), scale.getY(), getZScale());
    }

    protected void oriChanged() {
        setRotation(ori);
    }
    
    protected void aspectChanged() {
        sizeChanged();
    }

    public void setShape(RectShape shape) {
        if (this.currentShape == shape) {
            return;
        }

        if (shapeSpatial != null) {
            detachChild(shapeSpatial);
        }

        switch (shape) {
            case Empty:
                shapeSpatial = null;
                break;
            case Rect:
                Quad s = new com.ardor3d.scenegraph.shape.Quad("", 1, 1);
                s.setModelBound(new OrientedBoundingBox());
                shapeSpatial = s;
                break;
            case Ellipse:
                Disk d = new com.ardor3d.scenegraph.shape.Disk("", 2, 10, 0.5);
                d.setModelBound(new OrientedBoundingBox());
                shapeSpatial = d;
                break;
        }

        this.currentShape = shape;

        if (shapeSpatial != null) {
            attachChild(shapeSpatial);
        }
    }

    public Rect move(double px, double py, double pz) {
        position.set(px, py, pz);
        return this;
    }

    public Rect scale(double sx, double sy) {
        scale.set(sx, sy);
        return this;

    }

    public Rect scale(double d) {
        return scale(d,d);
    }

    public Rect rotate(double heading, double attitude, double bank) {
        ori.set(heading, attitude, bank);
        return this;
    }

//    public ColorSurface add(ColorSurface cs) {
//        //surfaces.add(cs);
//        cs.apply(this);
//        return cs;
//    }
    @Override public V3 getPosition() {
        return position;
    }

    @Override public V2 getSize() {
        return scale;
    }

    protected double getZScale() {
        return 1.0;
    }

    @Override public void removeAll() {
        List<Spatial> c = new LinkedList(getChildren());
        c.remove(shapeSpatial);
        for (Spatial s : c) {
            remove(s);
        }
    }

    public Rect moveDZ(double dz) {
        move(getPosition().getX(), getPosition().getY(), getPosition().getZ() + dz);
        return this;
    }

    public V3 getIntersectWorld(Ray3 ray, V3 result) {
        if (result == null) {
            result = new V3();
        }
        if (!(getWorldBound() instanceof OrientedBoundingBox))
            return result;
        
        OrientedBoundingBox obb = (OrientedBoundingBox) getWorldBound();

        
        double cx = obb.getCenter().getX();
        double cy = obb.getCenter().getY();
        double cz = obb.getCenter().getZ();

        //X basis vector        
        
        double xx = obb.getXAxis().getX() * obb.getExtent().getX();
        double xy = obb.getXAxis().getY() * obb.getExtent().getX();
        double xz = obb.getXAxis().getZ() * obb.getExtent().getX();

        //Y basis vector
        double yx = obb.getYAxis().getX() * obb.getExtent().getY();
        double yy = obb.getYAxis().getY() * obb.getExtent().getY();
        double yz = obb.getYAxis().getZ() * obb.getExtent().getY();


//        Vector3[] corners = new Vector3[4];
//        corners[0] = new Vector3(cx + xx + yx, cy + xy + yy, cz + xz + yz );
//        corners[1] = new Vector3(cx + xx - yx, cy + xy - yy, cz + xz - yz );
//        corners[2] = new Vector3(cx - xx - yx, cy - xy - yy, cz - xz - yz );
//        corners[3] = new Vector3(cx - xx + yx, cy - xy + yy, cz - xz + yz );
        
        Vector3[] corners = new Vector3[3];
        corners[0] = new Vector3(cx + xx + yx, cy + xy + yy, cz + xz + yz );
        corners[1] = new Vector3(cx + xx - yx, cy + xy - yy, cz + xz - yz );
        corners[2] = new Vector3(cx - xx + yx, cy - xy + yy, cz - xz + yz );

        if (ray.intersects(corners[0], corners[1], corners[2], result, false)) {
            return result;
        }
        
        return null;
    }


    @Override public void updateWorldTransform(boolean recurse) {
        super.updateWorldTransform(recurse);

        //TODO alX and alY
        applyAspectXY(((Vector3)getWorldScale()), ((Vector3)getWorldTranslation()), getWorldRotation(), aspect.d(), 0, 0, vA);
	}

    public Rect aspect(double newAspect) {
        getAspect().set(newAspect);
        return this;
    }

}
