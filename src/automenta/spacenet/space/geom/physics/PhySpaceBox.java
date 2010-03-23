/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.spacenet.space.geom.physics;

import automenta.spacenet.space.Repeat;
import automenta.spacenet.space.Space;
import automenta.spacenet.space.geom.Box;
import automenta.spacenet.space.geom.Box.BoxShape;
import automenta.spacenet.var.vector.V3;
import com.ardor3d.math.Ray3;
import com.ardor3d.scenegraph.Spatial;
import com.bulletphysics.BulletStats;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.dispatch.CollisionWorld;
import com.bulletphysics.collision.dispatch.CollisionWorld.ClosestRayResultCallback;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.constraintsolver.Point2PointConstraint;
import com.bulletphysics.dynamics.constraintsolver.TypedConstraint;
import com.bulletphysics.linearmath.Transform;
import javax.vecmath.Vector3f;

/**
 * wraps a PhySpace as a Box that can be added in space
 */
public class PhySpaceBox extends Box {

    public final PhySpace physics;

    public class PhyPointer extends Repeat {

        private Point2PointConstraint pickConstraint;
        private final PhySpace phySpace;
        private final DynamicsWorld dynamicsWorld;
        //       private final PhyBox b2;
        private Vector3f pickPos;
        float pickDist = 370.0f; //for some reason, the larger the more accurate the pick response
        private Space pickedSpace;
        private ClosestRayResultCallback rayCallback;
        private RigidBody draggedBody;

        public PhyPointer(PhySpace sp) {
            super();
            this.phySpace = sp;
            this.dynamicsWorld = sp.dynamicsWorld;

//            b2 = PhySpaceBox.this.add(new PhyBox(new V3(0, 0, 0), new V3(0.1, 0.1, 0.1), 1f));
        }
        RigidBody pickedBody;
        Vector3f cameraPosition = new Vector3f();
        Vector3f rayTo = new Vector3f();
        Vector3f rayEnd = new Vector3f();
        Vector3f dir = new Vector3f();
        int lastState = -1;

        public void mouseFunc(int button, int state) {
            //printf("button %i, state %i, x=%i,y=%i\n",button,state,x,y);
            //button 0, state 0 means left mouse down

            /*if (lastState != state)*/ {
                switch (button) {
//                case 2: {
//                    if (state == 0) {
//                        shootBox(rayTo);
//                    }
//                    break;
//                }
//                    case 1: {
//                        if (state == 0) {
//                            // apply an impulse
//                            if (dynamicsWorld != null) {
//                                CollisionWorld.ClosestRayResultCallback rayCallback = new CollisionWorld.ClosestRayResultCallback(cameraPosition, rayTo);
//                                dynamicsWorld.rayTest(cameraPosition, rayTo, rayCallback);
//                                if (rayCallback.hasHit()) {
//                                    RigidBody body = RigidBody.upcast(rayCallback.collisionObject);
//                                    if (body != null) {
//                                        body.setActivationState(CollisionObject.ACTIVE_TAG);
//                                        Vector3f impulse = new Vector3f(rayTo);
//                                        impulse.normalize();
//                                        float impulseStrength = 10f;
//                                        impulse.scale(impulseStrength);
//                                        Vector3f relPos = new Vector3f();
//                                        relPos.sub(rayCallback.hitPointWorld, body.getCenterOfMassPosition(new Vector3f()));
//                                        body.applyImpulse(impulse, relPos);
//                                    }
//                                }
//                            }
//                        } else {
//                        }
//                        break;
//                    }
                    case 0: {
                        if (state == 0) {
                            if ((pickedBody != null) && (draggedBody == null)) {
                                draggedBody = pickedBody;

                                draggedBody.setActivationState(CollisionObject.DISABLE_DEACTIVATION);

                                pickPos.set(rayCallback.hitPointWorld);

                                Transform tmpTrans = pickedBody.getCenterOfMassTransform(new Transform());
                                tmpTrans.inverse();
                                Vector3f localPivot = new Vector3f(pickPos);
                                tmpTrans.transform(localPivot);

                                Point2PointConstraint p2p = new Point2PointConstraint(draggedBody, localPivot);
                                dynamicsWorld.addConstraint(p2p);
                                pickConstraint = p2p;
                                // save mouse position for dragging
                                BulletStats.gOldPickingPos.set(rayTo);
                                Vector3f eyePos = new Vector3f(cameraPosition);
                                Vector3f tmp = new Vector3f();
                                tmp.sub(pickPos, eyePos);
                                BulletStats.gOldPickingDist = tmp.length();
                                // very weak constraint for picking
                                p2p.setting.tau = 0.1f;
                            }


                        } else {

                            if (pickConstraint != null && dynamicsWorld != null && draggedBody != null) {
                                dynamicsWorld.removeConstraint(pickConstraint);
                                // delete m_pickConstraint;
                                //printf("removed constraint %i",gPickingConstraintId);
                                pickConstraint = null;
                                draggedBody.forceActivationState(CollisionObject.ACTIVE_TAG);
                                draggedBody.setDeactivationTime(0f);
                                //System.out.println("detach " + draggedBody);
                                draggedBody = null;
                            }
                        }
                        break;
                    }
                }
                lastState = state;
            }

            if (pickConstraint != null) {
                // move the constraint pivot
                Point2PointConstraint p2p = (Point2PointConstraint) pickConstraint;
                if (p2p != null) {
                    // keep it at the same picking distance

                    dir.set(rayTo);
                    dir.sub(cameraPosition);
                    dir.normalize();
                    dir.scale(BulletStats.gOldPickingDist);

                    Vector3f newPos = new Vector3f(cameraPosition);
                    newPos.add(newPos, dir);
                    p2p.setPivotB(newPos);
                }

            }

        }

        @Override
        protected void update(double t, double dt, Spatial parent) {
            if (dynamicsWorld != null) {
                V3 center = getSpacetime().getCamera().getCurrentPosition();

                Ray3 pickRay = getSpacetime().getPointer().getPickRay();
                cameraPosition.set(center.getXf(), center.getYf(), center.getZf());
                rayTo.set(pickRay.getDirection().getXf(), pickRay.getDirection().getYf(), pickRay.getDirection().getZf());
                //rayEnd.set(rayTo);
                rayTo.scale(pickDist);

//                b2.getPosition().set(getSpacetime().getCamera().getCurrentPosition());
//                b2.getPosition().addLocal(pickRay.getDirection());
//                b2.getPosition().addLocal(pickRay.getDirection());
//                b2.setVelocity(0, 0, 0);
//

                rayCallback = new CollisionWorld.ClosestRayResultCallback(cameraPosition, rayTo);
                dynamicsWorld.rayTest(cameraPosition, rayTo, rayCallback);

                pickedBody = null;
                if (rayCallback.hasHit()) {
                    RigidBody body = RigidBody.upcast(rayCallback.collisionObject);
                    if (body != null) {
                        // other exclusions?
                        if (!(body.isStaticObject() || body.isKinematicObject())) {

                            pickedBody = body;

                            //pickedBody.setActivationState(CollisionObject.DISABLE_DEACTIVATION);
                            pickedSpace =
                                physics.getSpace(pickedBody);

                            pickPos =
                                new Vector3f(rayCallback.hitPointWorld);

                        }

                    }
                }
            }


            mouseFunc(0, getSpacetime().getPointer().isPressed(0) ? 0 : 1);
        }
    }

    public PhySpaceBox(double worldDimension) {
        this(worldDimension, worldDimension, worldDimension);
    }

    public PhySpaceBox(double wx, double wy, double wz) {
        super(BoxShape.Empty);

        this.physics = add(new PhySpace(wx, wy, wz));

        add(new PhyPointer(physics));

    }

    @Deprecated public PhySpaceBox() {
        this(100.0);
    }

    public TypedConstraint addConstraint(TypedConstraint j, boolean collisionsBetweenBodies) {
        physics.dynamicsWorld.addConstraint(j, collisionsBetweenBodies);
        return j;
    }

    public PhyBox add(PhyBox pb) {
        physics.add(pb);
        return super.add(pb);
    }
}
