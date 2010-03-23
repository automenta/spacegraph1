/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.spacenet.space.geom.physics;

import automenta.spacenet.space.Repeat;
import automenta.spacenet.space.Space;
import automenta.spacenet.var.scalar.DoubleVar;
import automenta.spacenet.var.vector.V3;
import com.ardor3d.scenegraph.Spatial;
import com.bulletphysics.collision.broadphase.AxisSweep3;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;
import java.util.Map;
import java.util.WeakHashMap;
import javax.vecmath.Vector3f;

/**
 *
 * @author seh
 */
public class PhySpace extends Repeat {

    private final DoubleVar timeScale = new DoubleVar(1.0);
    public final DynamicsWorld dynamicsWorld;
    private int defaultSubSteps = 1;
    private final V3 gravity;
    private Vector3f va = new Vector3f();
    private final static double updatePeriod = 0.01;
    private Map<RigidBody, Space> bodySpace = new WeakHashMap();
    private final Vector3f worldAabbMax;
    private final Vector3f worldAabbMin;

    @Deprecated public PhySpace() {
        this(25.0);
    }

    public PhySpace(double worldDimension) {
        this(worldDimension, worldDimension, worldDimension);
    }

    //TODO make space dimensions necessary constructor parameter
    public PhySpace(double wx, double wy, double wz) {
        super(updatePeriod);

        float w = (float) wx;
        float h = (float) wy;
        float d = (float) wz;

        // collision configuration contains default setup for memory, collision
        // setup. Advanced users can create their own configuration.

        DefaultCollisionConfiguration collisionConfiguration = new DefaultCollisionConfiguration();

        // use the default collision dispatcher. For parallel processing you
        // can use a diffent dispatcher (see Extras/BulletMultiThreaded)
        CollisionDispatcher dispatcher = new CollisionDispatcher(collisionConfiguration);

        // the maximum size of the collision world. Make sure objects stay
        // within these boundaries
        // Don't make the world AABB size too large, it will harm simulation
        // quality and performance
        worldAabbMin = new Vector3f(-w / 2.0f, -h / 2.0f, -d / 2.0f);
        worldAabbMax = new Vector3f(w / 2.0f, h / 2.0f, d / 2.0f);
        int maxProxies = 1024;
        AxisSweep3 overlappingPairCache = new AxisSweep3(worldAabbMin, worldAabbMax, maxProxies);

        //BroadphaseInterface overlappingPairCache = new SimpleBroadphase(
        //		maxProxies);
        // the default constraint solver. For parallel processing you can use a
        // different solver (see Extras/BulletMultiThreaded)
        SequentialImpulseConstraintSolver solver = new SequentialImpulseConstraintSolver();

        dynamicsWorld = new DiscreteDynamicsWorld(dispatcher, overlappingPairCache, solver, collisionConfiguration);
        //dynamicsWorld = new SimpleDynamicsWorld(dispatcher, overlappingPairCache, solver, collisionConfiguration);

        gravity = new V3(0, 0, 0) {

            @Override protected void notifyChanges() {
                super.notifyChanges();
                updateGravity(this);
            }
        };
        updateGravity(gravity);


//        // create a few basic rigid bodies
//        CollisionShape groundShape = new BoxShape(new Vector3f(50.f, 50.f, 50.f));
//        // keep track of the shapes, we release memory at exit.
//        // make sure to re-use collision shapes among rigid bodies whenever
//        // possible!
//        List<CollisionShape> collisionShapes = new ArrayList<CollisionShape>();
//
//        collisionShapes.add(groundShape);


    }

    protected void updateGravity(V3 g) {
        va.set(g.getXf(), g.getYf(), g.getZf());
        dynamicsWorld.setGravity(va);
    }

    @Override
    protected void update(double t, double dt, Spatial s) {
        double timeStep = getTimeScale().d() * dt;
        int subSteps = defaultSubSteps;

        //dynamicsWorld.stepSimulation((float)timeStep);
        dynamicsWorld.stepSimulation((float) timeStep, subSteps);


        dynamicsWorld.performDiscreteCollisionDetection();

//        // print positions of all objects
//        for (int j = dynamicsWorld.getNumCollisionObjects() - 1; j >= 0; j--) {
//            CollisionObject obj = dynamicsWorld.getCollisionObjectArray().get(j);
//            RigidBody body = RigidBody.upcast(obj);
//            if (body != null && body.getMotionState() != null) {
//                Transform trans = new Transform();
//                body.getMotionState().getWorldTransform(trans);
//                System.out.printf("world pos = %f,%f,%f\n", trans.origin.x,
//                    trans.origin.y, trans.origin.z);
//            }
//        }
    }

    public DoubleVar getTimeScale() {
        return timeScale;
    }

    public V3 getGravity() {
        return gravity;
    }

    protected void addBoundPlane(Vector3f size, Vector3f center) {
        CollisionShape groundShape = new BoxShape(size);
        Transform groundTransform = new Transform();
        groundTransform.setIdentity();
        groundTransform.origin.set(center);

        float mass = 0f;
        Vector3f localInertia = new Vector3f(0, 0, 0);

        // using motionstate is recommended, it provides interpolation capabilities, and only synchronizes 'active' objects
        DefaultMotionState myMotionState = new DefaultMotionState(groundTransform);
        RigidBodyConstructionInfo rbInfo = new RigidBodyConstructionInfo(mass, myMotionState, groundShape, localInertia);
        RigidBody body = new RigidBody(rbInfo);

        
        // add the body to the dynamics world
        dynamicsWorld.addRigidBody(body);

    }


    protected void addBoundPlanes() {
        float minX = worldAabbMin.x;
        float minY = worldAabbMin.y;
        float minZ = worldAabbMin.z;
        float maxX = worldAabbMax.x;
        float maxY = worldAabbMax.y;
        float maxZ = worldAabbMax.z;

        float thick = 0.05f;
        
        //back plane at -z
        addBoundPlane(new Vector3f(maxX-minX, maxY-minY, thick), new Vector3f(0, 0, minZ));
        //front plane at +z
        addBoundPlane(new Vector3f(maxX-minX, maxY-minY, thick), new Vector3f(0, 0, maxZ));
        //left plane at -x
        addBoundPlane(new Vector3f(thick, maxY-minY, maxZ-minZ), new Vector3f(minX, 0, 0));
        //right plane at +x
        addBoundPlane(new Vector3f(thick, maxY-minY, maxZ-minZ), new Vector3f(maxX, 0, 0));
        //bottom plane at -y
        addBoundPlane(new Vector3f(maxX-minX, thick, maxZ-minZ), new Vector3f(0, minY, 0));
        //top plane at +y
        addBoundPlane(new Vector3f(maxX-minX, thick, maxZ-minZ), new Vector3f(0, maxY, 0));
    }

    public PhyBox add(PhyBox pb) {
        dynamicsWorld.addRigidBody(pb.getBody());
        bodySpace.put(pb.getBody(), pb);
        return pb;
    }

    public PhyBox remove(PhyBox pb) {
        //TODO write
        return null;
    }

    public Space getSpace(RigidBody b) {
        return bodySpace.get(b);
    }
}
