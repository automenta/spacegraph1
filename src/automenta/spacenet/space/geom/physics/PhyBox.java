/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.spacenet.space.geom.physics;

import automenta.spacenet.space.geom.Box;

import automenta.spacenet.var.vector.Quat;
import automenta.spacenet.var.vector.V3;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

/**
 *
 * @author seh
 */
public class PhyBox extends Box {
    //TODO implement world bounds limiting

    private RigidBody body;
    private final Transform t = new Transform();
    private final Vector3f v = new Vector3f();
    private final Quat4f q = new Quat4f();
    private float mass;
    private CollisionShape collShape;

    public PhyBox(double w, double h, double d, float mass) {
        this(new V3(), new V3(w, h, d), mass);
    }

    protected CollisionShape newCollisionShape() {
        return new com.bulletphysics.collision.shapes.BoxShape(new Vector3f(1, 1, 1));
    }

    public class PhyBoxMotionState extends DefaultMotionState {

        public PhyBoxMotionState(Transform startTransform) {
            super(startTransform);
        }

        @Override
        public void setWorldTransform(Transform centerOfMassWorldTrans) {
            super.setWorldTransform(centerOfMassWorldTrans);
            bulletToArdor();
        }
    }

    protected void bulletToArdor() {
        //if (!isPositionStatic()) {
        //Position
        getPosition().set(t.origin.x, t.origin.y, t.origin.z);
        //}

        //Orientation
        body.getWorldTransform(t);
        t.getRotation(q);
        PhyBox.this.getOrientation().set(q.x, q.y, q.z, q.w);


        //Size?

    }

    public void setVelocity(double vx, double vy, double vz) {
        body.setLinearVelocity(new Vector3f((float) vx, (float) vy, (float) vz));
    }

    public RigidBody getBody() {
        return body;
    }

    public PhyBox(V3 position, V3 scale, float mass) {
        this(position, scale, mass, BoxShape.Cubic);
    }

    public PhyBox(Box b, float mass) {
        super(b.getPosition(), b.getSize(), b.getOrientation(), BoxShape.Empty);
        init(mass);
    }

    protected void init(float mass) {
        this.mass = mass;

        collShape = newCollisionShape();
        Vector3f localInertia = new Vector3f(0, 0, 0);
        if (mass != 0) {
            collShape.calculateLocalInertia(mass, localInertia);
        }


        Transform startTransform = new Transform();
        startTransform.setIdentity();

        RigidBodyConstructionInfo rbInfo = new RigidBodyConstructionInfo(mass, new PhyBoxMotionState(startTransform), collShape, localInertia);

        body = new RigidBody(rbInfo);


        if (mass != 0) {
            body.setActivationState(RigidBody.DISABLE_DEACTIVATION);
            //body.setSleepingThresholds(0.1f, 0.1f);

        }


        positionChanged();
        sizeChanged();
        oriChanged();

        setPhysicalOrientation();

        rotate(0, 0, 0);

    }

    /**
     *
     * @param position
     * @param scale
     * @param mass set mass=0 for static (frozen, non-dynamic) object
     */
    public PhyBox(V3 position, V3 scale, float mass, BoxShape shape) {
        super(position, scale, new Quat(), shape);

        init(mass);
    }

    @Override
    protected void sizeChanged() {
        super.sizeChanged();

        //Scale
        v.set(getSize().getXf() / 2.0f, getSize().getYf() / 2.0f, getSize().getZf() / 2.0f);

        body.getCollisionShape().setLocalScaling(v);
    }

    @Override
    public Box rotate(double heading, double attitude, double bank) {
        Box b = super.rotate(heading, attitude, bank);
        setPhysicalOrientation();
        return b;
    }

    protected void setPhysicalOrientation() {
        body.getWorldTransform(t);
        t.getRotation(q);
        q.set(getOrientation().getXf(), getOrientation().getYf(), getOrientation().getZf(), getOrientation().getWf());
        System.out.println("node -> phy: " + q);
        t.setRotation(q);
        body.setWorldTransform(t);
    }

    @Override
    protected void oriChanged() {
        super.oriChanged();
    }

    @Override
    protected void positionChanged() {
        super.positionChanged();

        body.getWorldTransform(t);

        t.origin.set(getPosition().getXf(), getPosition().getYf(), getPosition().getZf());

        body.setWorldTransform(t);

    }

    public boolean isPositionStatic() {
        return mass == 0;
    }
}
