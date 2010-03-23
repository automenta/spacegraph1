/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.spacenet.space.video.util;

import com.ardor3d.bounding.BoundingSphere;
import com.ardor3d.bounding.BoundingVolume;
import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyTransform;
import com.ardor3d.math.type.ReadOnlyVector3;

/**
 *
 * @author seh
 */
public class BoundingSphereEx extends BoundingSphere {

    static final private double radiusEpsilon = 1 + 0.00001;
    final Vector3 unitScale = new Vector3(1, 1, 1);

    public BoundingSphereEx() {
        this(1, new Vector3(0,0,0));
    }



    public BoundingSphereEx(double radius, ReadOnlyVector3 center) {
        super(radius, center);
    }


    protected double maxAxis(final ReadOnlyVector3 scale) {
        return Math.max(Math.abs(scale.getX()), Math.max(Math.abs(scale.getY()), Math.abs(scale.getZ())));
    }

    @Override public BoundingVolume transform(final ReadOnlyTransform transform, final BoundingVolume store) {
        BoundingSphereEx sphere;
        if (store == null || store.getType() != BoundingVolume.Type.Sphere) {
            sphere = new BoundingSphereEx();
        } else {
            sphere = (BoundingSphereEx) store;
        }

        transform.applyForward(_center, (Vector3) sphere.getCenter());

        if (!transform.isRotationMatrix()) {
            transform.applyForwardVector(unitScale);
            sphere.setRadius(Math.abs(maxAxis(unitScale) * getRadius()) + radiusEpsilon - 1);
        } else {
            final ReadOnlyVector3 scale = transform.getScale();
            sphere.setRadius(Math.abs(maxAxis(scale) * getRadius()) + radiusEpsilon - 1);
        }

        return sphere;
    }
}
