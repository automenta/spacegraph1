/**
 * 
 */
package automenta.spacenet.space.geom.graph.arrange.forcedirect;

import automenta.spacenet.space.Repeat;
import automenta.spacenet.space.Space;
import automenta.spacenet.space.geom.Box;
import automenta.spacenet.space.geom.graph.GraphBoxModel;
import automenta.spacenet.space.geom.graph.GraphBox;
import automenta.spacenet.var.scalar.DoubleVar;
import automenta.spacenet.var.Maths;
import automenta.spacenet.var.vector.Quat;
import automenta.spacenet.var.graph.MemGraph;
import com.ardor3d.math.Vector3;
import com.ardor3d.scenegraph.Spatial;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class ForceDirecting<V, E> extends Repeat implements GraphBoxModel<V, E> {

    private static final Logger logger = Logger.getLogger(ForceDirecting.class.getName());
    private final ForceDirectedParameters params;
    private final int substeps;

    public static class ForceDirectedParameters {
//        private final static double DEFAULT_UPDATE_PERIOD = 0.01;
//        private final static double DEFAULT_INTERPOLATION_PERIOD = 0.005;
//        private final static double DEFAULT_INTERPOLATION_SPEED = 0.2;

        private DoubleVar stiffness;
        private DoubleVar repulsion;
        private DoubleVar lengthFactor;
        private final Vector3 boundsMax;

        public ForceDirectedParameters(Vector3 boundsMax, double initialStiffness, double initialRepulsion, double initialLengthFactor) {
            super();
            this.boundsMax = boundsMax;
            this.stiffness = new DoubleVar(initialStiffness);
            this.repulsion = new DoubleVar(initialRepulsion);
            this.lengthFactor = new DoubleVar(initialLengthFactor);
        }

        public Vector3 getBoundsMax() {
            return boundsMax;
        }

        public DoubleVar getLengthFactor() {
            return lengthFactor;
        }

        public DoubleVar getRepulsion() {
            return repulsion;
        }

        public DoubleVar getStiffness() {
            return stiffness;
        }
    }
    private double updatePeriod;
    protected Map<V, Box> nodeVis = new HashMap();
    protected Map<E, Space> linkVis = new HashMap();
    private final double interpolationSpeed;
    private DoubleVar timeScale = new DoubleVar(1.0);
    private DoubleVar maxSpeed = new DoubleVar(0.1);
    private Map<Box, Vector3> nextPosition = new HashMap();
    private Map<Box, Vector3> nextSize = new HashMap();
    private Repeat calcRepeat;
    private Repeat interpRepeat;
    private GraphBox<V, E> graphBox;

    public ForceDirecting(ForceDirectedParameters params, double updatePeriod, int interpolationSubSteps, double interpolationSpeed) {
        super();
        this.params = params;
        this.updatePeriod = updatePeriod;
        this.substeps = interpolationSubSteps;
        this.interpolationSpeed = interpolationSpeed;
    }

    public ForceDirectedParameters getParams() {
        return params;
    }

    public Box getBox(V v) {
        return nodeVis.get(v);
    }

    public Vector3 getBoundsMax() {
        return getParams().getBoundsMax();
    }

//    public Node getRandomNode() {
//        List<Node> nodes = new LinkedList(nodeVis.keySet());
//        if (nodes.size() > 0) {
//            return nodes.get((int) Maths.random(0, nodes.size()));
//        }
//        return null;
//    }
    @Override
    public void addedNode(V v, Box nb) {
        double wx = getBoundsMax().getX() / 2.0;
        double wy = getBoundsMax().getY() / 2.0;
        double wz = getBoundsMax().getY() / 2.0;

        double sx = 0.01;
        double sy = 0.01;
        double sz = 0.01;

        nb.move(Maths.random(-wx, wx), Maths.random(-wy, wy), Maths.random(-wz, wz));
        nodeVis.put(v, nb);
    }

    @Override
    public void removedNode(V v) {
        Box box = getBox(v);

        Space vis = nodeVis.get(v);
        nodeVis.remove(v);

        if (box != null) {
            nextPosition.remove(box);
            nextSize.remove(box);
        }

    }

    @Override
    public void addedEdge(E e, Space s, Box from, Box to) {
        linkVis.put(e, s);
    }

    @Override
    public void removedEdge(E e) {
        linkVis.remove(e);
    }

    public MemGraph<V, E> getGraph() {
        return graphBox.getGraph();
    }

    public GraphBox<V, E> getGraphBox() {
        return graphBox;
    }

    @Override public void start(GraphBox<V, E> graphBox) {
        this.graphBox = graphBox;

//        calcRepeat = graphBox.add(new Repeat(updatePeriod) {
//            @Override protected void update(double t, double dt, Spatial s) {
//                forward(dt);
//            }
//        });
//        interpRepeat = graphBox.add(new Repeat(interpolationPeriod) {
//            @Override public void update(double t, double dt, Spatial s) {
//                interpolate(dt);
//            }
//        });

    }

    @Override
    public void stop() {
        graphBox.remove(calcRepeat);
        graphBox.remove(interpRepeat);
    }

    public DoubleVar getStiffness() {
        return getParams().getStiffness();
    }

    public DoubleVar getRepulsion() {
        return getParams().getRepulsion();
    }
    Vector3 force = new Vector3();

    protected Vector3 getNextPosition(Box b) {
        Vector3 v = nextPosition.get(b);
        if (v == null) {
            v = new Vector3();
            nextPosition.put(b, v);
        }
        return v;
    }

    public Vector3 getNextSize(Box b) {
        Vector3 v = nextSize.get(b);
        if (v == null) {
            v = new Vector3(1, 1, 1);
            nextSize.put(b, v);
        }
        return v;
    }
    int step = 0;

    @Override protected void update(double t, double dt, Spatial parent) {
        if (step % substeps == 0) {
            forward(dt * substeps);
        }

        interpolate(dt);

        step++;
    }

//    public void forward(double dt, int steps) {
//        for (int i = 0; i < steps; i++) {
//            double ddt = dt / ((double)steps);
//            forward(ddt);
//            interpolate(ddt);
//        }
//    }
    protected synchronized void forward(double dt) {
        double wx = getBoundsMax().getX() / 2.0;
        double wy = getBoundsMax().getY() / 2.0;
        double wz = getBoundsMax().getZ() / 2.0;

        synchronized (nodeVis) {
            synchronized (linkVis) {
                for (V n : nodeVis.keySet()) {
                    Box nBox = getBox(n);
                    updateNode(n, nBox, getNextSize(nBox));
                    updateOrientation(n, nBox, nBox.getOrientation());
                }
                for (E l : linkVis.keySet()) {
                    Space s = linkVis.get(l);
                    updateEdge(l, s);
                }

                for (E l : linkVis.keySet()) {
                    double stiffness = getStiffness(l); //getStiffness().d();

                    //Line3D line = linkVis.get(l);

                    //Pair endPoints = getGraph().getEndpoints(l);
                    List<V> iv = getGraph().getIncidentVertices(l);
                    if (iv == null) {
                        continue;
                    }


                    V a = (V) iv.get(0);
                    V b = (V) iv.get(1);

                    if (a == null) {
                        continue;
                    }
                    if (b == null) {
                        continue;
                    }


                    Box aBox = getBox(a);
                    double aRad = (aBox.getSize().getMaxComponent() + aBox.getSize().getMinComponent()) / 2.0; //TODO use getAvgRadius

                    Box bBox = getBox(b);
                    double bRad = (bBox.getSize().getMaxComponent() + bBox.getSize().getMinComponent()) / 2.0;//TODO use getAvgRadius

                    //line.getRadius().set(getLineRadius(aRad, bRad));

                    double naturalLength = getLengthFactor().d() * (aRad + bRad) / 4.0;

                    double currentLength = aBox.getPosition().distance(bBox.getPosition());

                    double f = stiffness * (currentLength - naturalLength);

                    double sx = f * (bBox.getPosition().getX() - aBox.getPosition().getX());
                    double sy = f * (bBox.getPosition().getY() - aBox.getPosition().getY());
                    double sz = f * (bBox.getPosition().getZ() - aBox.getPosition().getZ());

                    getNextPosition(aBox).addLocal(sx / 2.0, sy / 2.0, sz / 2.0);
                    getNextPosition(bBox).addLocal(-sx / 2.0, -sy / 2.0, -sz / 2.0);
                }



                for (V n : nodeVis.keySet()) {

                    Box nBox = getBox(n);
                    double nMass = getMass(nBox);//nBox.getSize().getMaxRadius();

                    force.set(0, 0, 0);

                    for (V m : nodeVis.keySet()) {
                        if (n == m) {
                            continue;
                        }

                        double repulsion = getRepulsion(n, m); //getRepulsion().d();

                        Box mBox = nodeVis.get(m);
                        double mMass = getMass(mBox); //mBox.getSize().getMaxRadius();
                        double dist = nBox.getPosition().distance(mBox.getPosition());

                        double f = -repulsion * (nMass * mMass) / (dist * dist);

                        double sx = f * (mBox.getPosition().getX() - nBox.getPosition().getX());
                        double sy = f * (mBox.getPosition().getY() - nBox.getPosition().getY());
                        double sz = f * (mBox.getPosition().getZ() - nBox.getPosition().getZ());
                        force.addLocal(sx, sy, sz);
                    }

                    force.multiplyLocal(dt * getTimeScale().d());

                    if (force.length() > getMaxSpeed().d()) {
                        force.normalizeLocal().multiplyLocal(getMaxSpeed().d());
                    }

                    Vector3 p = getNextPosition(nBox); //getPosition();
                    Vector3 s = nBox.getSize();


                    double nx = p.getX() + force.getX();
                    double ny = p.getY() + force.getY();
                    double nz = p.getZ() + force.getZ();

                    nx = Math.min(nx, wx - s.getX() / 2.0);
                    nx = Math.max(nx, -wx + s.getX() / 2.0);

                    ny = Math.min(ny, wy - s.getY() / 2.0);
                    ny = Math.max(ny, -wy + s.getY() / 2.0);

                    nz = Math.min(nz, wz - s.getZ() / 2.0);
                    nz = Math.max(nz, -wz + s.getZ() / 2.0);


                    p.set(nx, ny, nz);
                }

            }
        }

    }

    protected void interpolate(double dt) {
        synchronized (nodeVis) {
            for (V n : nodeVis.keySet()) {
                Box b = getBox(n);

                Vector3 currentPosition = b.getPosition();
                Vector3 currentSize = b.getSize();

                double p = interpolationSpeed;
                double np = 1.0 - p;

                Vector3 nextPosition = getNextPosition(b);
                Vector3 nextSize = getNextSize(b);
                double px = np * currentPosition.getX() + p * nextPosition.getX();
                double py = np * currentPosition.getY() + p * nextPosition.getY();
                double pz = np * currentPosition.getZ() + p * nextPosition.getZ();

                double sx = np * currentSize.getX() + p * nextSize.getX();
                double sy = np * currentSize.getY() + p * nextSize.getY();
                double sz = np * currentSize.getZ() + p * nextSize.getZ();

                currentPosition.set(px, py, pz);

                currentSize.set(sx, sy, sz);
            }

            if (isCentering()) {
                centerNodes();
            }
        }
    }

    public DoubleVar getTimeScale() {
        return timeScale;
    }

    private Double getLineRadius(double rad, double rad2) {
        return ((rad + rad2) / 2.0) / 20.0;
    }

    private DoubleVar getMaxSpeed() {
        return maxSpeed;
    }

    public DoubleVar getLengthFactor() {
        return getParams().getLengthFactor();
    }

    protected void updateNode(V n, Box nBox, Vector3 nextSize) {
        return;
    }

    protected void updateOrientation(V n, Box nBox, Quat orientation) {
        return;
    }

    protected double getStiffness(E l) {
        return getStiffness().d();
    }

    /** TODO rename to getGravity and invert values */
    protected double getRepulsion(V n, V m) {
        return getRepulsion().d();
    }

    protected double getMass(Box nBox) {
        return 1.0;
    }

    protected void updateEdge(E e, Space s) {
    }

    public boolean isCentering() {
        return true;
    }

    protected void centerNodes() {
        Vector3 center = new Vector3();
        int numBoxes = 0;
        for (Box b : nodeVis.values()) {
            center.addLocal(getNextPosition(b));
            numBoxes++;
        }
        center.multiplyLocal(1.0 / ((double) numBoxes));
        for (Box b : nodeVis.values()) {
            Vector3 nextPosition = getNextPosition(b);

            nextPosition.subtractLocal(center);
        }
    }
}
