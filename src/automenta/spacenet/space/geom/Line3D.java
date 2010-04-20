/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.spacenet.space.geom;

import automenta.spacenet.space.Space;
import automenta.spacenet.space.WrapsMesh;
import automenta.spacenet.var.scalar.DoubleVar;
import automenta.spacenet.var.scalar.DoubleVar.IfDoubleChanges;
import automenta.spacenet.var.vector.V3;
import automenta.spacenet.var.vector.V3.IfV3Changes;
import com.ardor3d.bounding.OrientedBoundingBox;
import com.ardor3d.math.Vector3;
import com.ardor3d.scenegraph.Line;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.scenegraph.Spatial;
import com.ardor3d.scenegraph.shape.Extrusion;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author seh
 */
public class Line3D extends Space implements WrapsMesh {

    private final V3 a;
    private final V3 b;
    private final int radialSamples;
    private final DoubleVar radius;
    private Extrusion extrusion;
    final static Vector3 up = new Vector3(0, 1, 0);
    private List<Vector3> points;
    private List<Vector3> localPoints = new LinkedList();
    private IfV3Changes pointChanges;
    private IfDoubleChanges radChanges;
    private Line shape;
    private V3 center = new V3();
    private boolean extrusionNeedsUpdate;

    public Line3D(V3 a, V3 b, int radialSamples, double radius) {
        super();

        this.a = a;
        this.b = b;
        this.radialSamples = radialSamples;
        this.radius = new DoubleVar(radius);
        
        this.points = Arrays.asList(new Vector3[]{a, b});
    }

    @Override
    protected void afterAttached(Spatial parent) {
        super.afterAttached(parent);

        pointChanges = new IfV3Changes() {

            @Override public void onV3Changed(V3 v) {
                needsToUpdateExtrusion();
            }
        };
        radChanges = getRadius().add(new IfDoubleChanges() {

            @Override public void onDoubleChange(DoubleVar d) {
                updateShape();
                needsToUpdateExtrusion();
            }
        });

        a.add(pointChanges);
        b.add(pointChanges);

        updateExtrusion();
    }

    protected void needsToUpdateExtrusion() {
        extrusionNeedsUpdate = true;
    }

    @Override
    protected void beforeDetached(Spatial previousParent) {
        a.remove(pointChanges);
        b.remove(pointChanges);
        pointChanges = null;

        super.beforeDetached(previousParent);
    }

    private void updateShape() {
        shape = new Line();
        shape.appendCircle(getRadius().d(), 0, 0, radialSamples, false);
    }

    @Override
    public void updateControllers(double time) {
        super.updateControllers(time);

        if (extrusionNeedsUpdate) {
            extrusionNeedsUpdate = false;
            updateExtrusion();
        }
    }

    protected void updateExtrusion() {
        if (shape == null) {
            updateShape();
        }

        center.set(a);
        center.addLocal(b);
        center.multiplyLocal(0.5);

        setTranslation(center);

        localPoints.clear();
        for (Vector3 v : points) {
            Vector3 v2 = new Vector3(v);
            v2.subtractLocal(center);
            localPoints.add(v2);
        }

        if (extrusion == null) {
            extrusion = new Extrusion("", shape, localPoints, up);
            extrusion.setModelBound(new OrientedBoundingBox());

            attachChild(extrusion);
        } else {
            extrusion.updateGeometry(shape, localPoints, up);
            extrusion.updateModelBound();
        }



    }

    public DoubleVar getRadius() {
        return radius;
    }

    public double getLength() {
        return a.distance(b);
    }

    @Override
    public Mesh getWrappedMesh() {
        return extrusion;
    }
}
