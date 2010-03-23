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
public class PhySphere extends PhyBox {


    /**
     *
     * @param position
     * @param scale
     * @param mass set mass=0 for static (frozen, non-dynamic) object
     */
    public PhySphere(V3 position, V3 scale, float mass) {
        super(position, scale, mass, BoxShape.Spheroid);
    }    

    
    @Override protected CollisionShape newCollisionShape() {
        return new com.bulletphysics.collision.shapes.SphereShape(1.0f);
    }
    
}
