/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.spacenet.run.old.bio;

import automenta.spacenet.plugin.neural.brainz.AbstractNeuron;
import automenta.spacenet.plugin.neural.brainz.Brain;
import automenta.spacenet.plugin.neural.brainz.BrainBuilder;
import automenta.spacenet.plugin.neural.brainz.BrainGraph;
import automenta.spacenet.plugin.neural.brainz.InterNeuron;
import automenta.spacenet.plugin.neural.brainz.SenseNeuron;
import automenta.spacenet.run.ArdorSpacetime;
import automenta.spacenet.run.old.DefaultGraphBuilder;
import automenta.spacenet.space.Repeat;
import automenta.spacenet.space.Space;
import automenta.spacenet.space.control.Zoomable;
import automenta.spacenet.space.geom.Box;
import automenta.spacenet.space.geom.Box.BoxShape;
import automenta.spacenet.space.geom.ProcessBox;
import automenta.spacenet.space.geom.Rect;
import automenta.spacenet.space.geom.Rect.RectShape;
import automenta.spacenet.space.geom.graph.GraphBox;
import automenta.spacenet.space.geom.graph.GraphBoxBuilder;
import automenta.spacenet.space.geom.graph.arrange.forcedirect.ForceDirecting;
import automenta.spacenet.space.geom.graph.arrange.forcedirect.ForceDirecting.ForceDirectedParameters;
import automenta.spacenet.space.geom.physics.PhyBox;
import automenta.spacenet.space.geom.physics.PhySpace;
import automenta.spacenet.space.geom.physics.PhySpaceBox;
import automenta.spacenet.var.Maths;
import automenta.spacenet.var.physical.Color;
import automenta.spacenet.var.vector.V3;
import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyVector3;
import com.ardor3d.renderer.state.BlendState;
import com.ardor3d.renderer.state.BlendState.DestinationFunction;
import com.ardor3d.renderer.state.BlendState.SourceFunction;
import com.ardor3d.scenegraph.Spatial;
import com.bulletphysics.collision.dispatch.CollisionWorld;
import com.bulletphysics.collision.dispatch.CollisionWorld.ClosestRayResultCallback;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.constraintsolver.Generic6DofConstraint;
import com.bulletphysics.dynamics.constraintsolver.HingeConstraint;
import com.bulletphysics.dynamics.constraintsolver.Point2PointConstraint;
import com.bulletphysics.dynamics.constraintsolver.TranslationalLimitMotor;
import com.bulletphysics.linearmath.Transform;
import java.util.LinkedList;
import java.util.List;
import javax.vecmath.Vector3f;

/**
 *
 * @author seh
 */
public class DemoCell extends ProcessBox {

    //TODO fix inter-cell mapping, its broken and one-directional i think
    double neuralDensity = 620.0; //neurons per cubic volume unit
    float maxStretch = 0.25f;
    float motorStep = 0.1f;
    double motorPeriod = 0.0;
    int cellPositionInputs = 6;
    int cellMotorNeurons = 3;
    int intercellNeurons = 4;
    //int cellInternalNeurons = 32;
    int cellRandomInputs = 1;
    double randomInputMomentum = 0.95; //closer to zero is faster, closer to 1 is slower
    float motorRestitution = 0.8f; //how quickly the motor returns to zero length (1.0 = never, so use < 1.0)
    double randomPeriod = 1.0;
    protected PhySpaceBox phy;
    double neuronBoxUpdatePeriod = 0.1;
    double neuralPeriod = 0.0;

    public class Retina {

        private final V3 pos;
        private final V3 normal;
        private Box box;

        public Retina(V3 pos, V3 normal) {
            this.pos = pos;
            this.normal = normal;
        }
        Vector3f retinaCenter = new Vector3f();
        Vector3f rayTo = new Vector3f();
        float pickDist = 200.0f;

        public Space seenObject(ReadOnlyVector3 absolutePos, ReadOnlyVector3 direction, PhySpace physics) {


            rayTo.set(direction.getXf(), direction.getYf(), direction.getZf());
            rayTo.scale(pickDist);

            retinaCenter.set((float) absolutePos.getX(), (float) absolutePos.getY(), (float) absolutePos.getZ());

            ClosestRayResultCallback rayCallback = new CollisionWorld.ClosestRayResultCallback(retinaCenter, rayTo);
            physics.dynamicsWorld.rayTest(retinaCenter, rayTo, rayCallback);
            RigidBody pickedBody = null;
            Space pickedSpace = null;
            if (rayCallback.hasHit()) {
                RigidBody body = RigidBody.upcast(rayCallback.collisionObject);
                if (body != null) {
                    // other exclusions?
                    //if (!(body.isStaticObject() || body.isKinematicObject())) {

                    pickedBody = body;
                    pickedSpace = physics.getSpace(pickedBody);
                    //}
                }
            }
            return pickedSpace;
        }

        public Color getColor(ReadOnlyVector3 worldTranslation, ReadOnlyVector3 normal, PhySpace p) {
            Color c;

            Space pickedSpace = seenObject(worldTranslation, normal, p);
            if (pickedSpace != null) {
                c = Color.White.alpha(0.5);
            } else {
                c = Color.Black.alpha(0.5);
            }
            box.color(c);

            return c;
        }

        private void setBox(Box rb) {
            this.box = rb;
        }
    }

    public class BoxCell extends PhyBox implements Zoomable {

        private double[] nextOutputs;
        private final Brain b;
        private final BrainGraph bGraph;
        private final List<Retina> retinas;

        private BoxCell(V3 pos, V3 size, float mass, double neuralDensity, List<Retina> retinas) {
            super(pos, size, mass);
            color(Color.White.alpha(0.3));

            if (retinas == null) {
                retinas = new LinkedList(); //empty
            }

            this.retinas = retinas;

            for (Retina r : retinas) {
                Box rb = new Box(BoxShape.Spheroid);
                rb.move(r.pos);
                r.setBox(rb);
                add(rb);
            }

            int cellInternalNeurons = (int) (neuralDensity * size.getVolume());
            int cellInternalMinSynapses = cellInternalNeurons / 6;
            int cellInternalMaxSynapses = cellInternalNeurons / 3;

            /**
             * inputs = (intercell input, random, position, retina cells[r,g,b])
             *
             */
            b = new BrainBuilder(cellRandomInputs + intercellNeurons + cellPositionInputs + retinas.size() * 3, intercellNeurons + cellMotorNeurons).newBrain(cellInternalNeurons, cellInternalMinSynapses, cellInternalMaxSynapses);
            bGraph = new BrainGraph(b);

            V3 boundsMax = new V3(2, 2, 2);

            ForceDirectedParameters par = new ForceDirectedParameters(boundsMax, 0.01, 0.001, 1.0);
            double updatePeriod = 0.1;
            double interpSpeed = 0.3;
            int substeps = 4;

            ForceDirecting arr = new ForceDirecting(par, updatePeriod, substeps, interpSpeed);

            add(new GraphBox(bGraph, getGraphBuilder(), arr));


            add(new Repeat(randomPeriod) {

                @Override protected void update(double t, double dt, Spatial parent) {
                    updateRandomInputs();
                }
            });

            add(new Repeat(neuralPeriod, true) {

                @Override protected void update(double t, double dt, Spatial parent) {
                    updateInputs();
                    b.forward();
                    updateOutputs();
                }
            });

        }

        protected void updateOutputs() {
//            outputRect.removeAll();
//
//
//            Rect[] o = new Rect[b.getMotor().size()];
//
//            int j = 0;
//            for (MotorNeuron m : b.getMotor()) {
//                double bo = m.getOutput();
//                Color c;
//                if (bo < 0.5) {
//                    c = Color.Blue;
//                } else {
//                    c = Color.Red;
//                }
//                Rect re = new Rect(RectShape.Ellipse);
//                re.color(c);
//                o[j++] = re;
//            }
//
//            outputRect.add(new ColRect(0.01, o));
        }

        public void updateRandomInputs() {
            if (nextOutputs == null) {
                nextOutputs = new double[cellRandomInputs];
            }
            for (int i = 0; i < cellRandomInputs; i++) {
                nextOutputs[i] = Maths.random(-1, 1);
            }

        }

        public void updateInputs() {
            int randomOffset = intercellNeurons;
            for (int i = 0; i < cellRandomInputs; i++) {
                SenseNeuron s = b.getSense().get(i + randomOffset);
                s.senseInput = (randomInputMomentum * s.senseInput) + ((1.0 - randomInputMomentum) * nextOutputs[i++]);
            }

            int positionOffset = intercellNeurons + cellRandomInputs;
            for (int i = 0; i < cellPositionInputs; i++) {
                SenseNeuron s = b.getSense().get(i + positionOffset);
                if (i % 3 == 0) {
                    //cos(X)
                    s.senseInput = Math.cos(getPosition().getXf());
                } else if (i % 3 == 1) {
                    //cos(Y)
                    s.senseInput = Math.cos(getPosition().getYf());
                } else {
                    //cos(Z)
                    s.senseInput = Math.cos(getPosition().getZf());
                }

            }

            int retinaOffset = intercellNeurons + cellRandomInputs + cellPositionInputs;
            int r = 0;
            for (int i = 0; i < retinas.size(); i++) {
                Retina x = retinas.get(i);
                Vector3[] axes = new Vector3[3];
                getOrientation().toAxes(axes);
                Color c = x.getColor(getWorldTranslation(), axes[0], phy.physics);
                b.getSense().get(retinaOffset + (r++)).senseInput = c.getRed();
                b.getSense().get(retinaOffset + (r++)).senseInput = c.getGreen();
                b.getSense().get(retinaOffset + (r++)).senseInput = c.getBlue();
            }

        }

        @Override public void onZoomStart() {
        }

        @Override public void onZoomStop() {
        }

        @Override public boolean isZoomable() {
            return true;
        }

        @Override public boolean isTangible() {
            return true;
        }
        double minNeuronScale = 0.01;
        double neuronScale = 0.05;

        public GraphBoxBuilder getGraphBuilder() {
            return new DefaultGraphBuilder() {

                @Override public Box newNodeSpace(Object node) {
                    final Box b = new Box(BoxShape.Empty);
                    if (node instanceof AbstractNeuron) {
                        final AbstractNeuron i = (AbstractNeuron) node;
                        final Rect r = b.add(new Rect(node instanceof InterNeuron ? RectShape.Rect : RectShape.Ellipse));
                        b.add(new Repeat(neuronBoxUpdatePeriod) {

                            @Override protected void update(double t, double dt, Spatial parent) {
                                double o = i.getOutput();
                                double p = o;
                                if (i instanceof InterNeuron) {
                                    p = ((InterNeuron) i).getPotential();
                                } else if (i instanceof SenseNeuron) {
                                    p = ((SenseNeuron) i).senseInput;
                                }

                                r.color(getNeuronColor(o, p));
                                double s = 0.1 + (Math.abs(p)) * 0.2;
                                r.scale(s);
                            }
                        });
                    }
                    b.scale(minNeuronScale);
                    return b;
                }

                @Override
                public Space newEdgeSpace(Object edge, Box pa, Box pb) {
                    return new Box(BoxShape.Empty);
                }
            };
        }

        private double getOutput(int i) {
            return b.getMotor().get(i).getOutput();
        }

        private void setInput(int i, double d) {
            b.getSense().get(i).senseInput = d;
        }
    }

    /** a physical pipe of capsules */
    public /*static*/ class Snake {

        Vector3f tmp = new Vector3f();

        public Snake(PhySpaceBox b, int numSegments, double segmentLength, double radius, double snakeHeadFactor) {
            super();

            double density = 1.0;

            BoxCell segment[] = new BoxCell[numSegments];
            for (int s = 0; s < numSegments; s++) {
                double l = segmentLength * Maths.random(0.75, 1.25);

                double w = radius;
                double h = radius;

                List<Retina> retinas = null;
                if (s == 0) {
                    l *= snakeHeadFactor;
                    w *= snakeHeadFactor;
                    h *= snakeHeadFactor;

                    retinas = new LinkedList();
                    retinas.add(new Retina(new V3(1, 0, 0), new V3(1, 0, 0)));
                }

                float mass = (float) ((l * w * h) * density);

                BoxCell pb = new BoxCell(new V3(), new V3(l, w, h), mass, neuralDensity, retinas);

                b.add(pb);
                segment[s] = pb;

                pb.getBody().setDamping(0.05f, 0.85f);
                pb.getBody().setDeactivationTime(0.8f);
                pb.getBody().setSleepingThresholds(1.6f, 2.5f);

                if (s > 0) {
                    bind(segment[s - 1], segment[s]);
                }

            }
        }

        protected void bind6DoF(final BoxCell a, final BoxCell b) {
            final Generic6DofConstraint joint6DOF;
            Transform localA = new Transform(), localB = new Transform();
            boolean useLinearReferenceFrameA = false;

            localA.setIdentity();
            localB.setIdentity();

            localA.origin.set((float) (-a.getSize().getX() / 2.0) * 1.1f, 0, 0);
            localB.origin.set((float) (b.getSize().getX() / 2.0) * 1.1f, 0, 0);

            joint6DOF = new Generic6DofConstraint(a.getBody(), b.getBody(), localA, localB, useLinearReferenceFrameA);
            //joint6DOF.setLimit(0, -0.5f, 0.5f);

            tmp.set(-3.10f, -3.1f, -3.1f);
            joint6DOF.setAngularLowerLimit(tmp);

            tmp.set(3.1f, 3.1f, 3.1f);
            joint6DOF.setAngularUpperLimit(tmp);

            phy.addConstraint(joint6DOF, false);

            add(new Repeat(motorPeriod) {

                Vector3f pivA = new Vector3f();
                Vector3f pivB = new Vector3f();
                double icAB[] = new double[intercellNeurons];
                double icBA[] = new double[intercellNeurons];

                @Override protected void update(double t, double dt, Spatial parent) {
                    TranslationalLimitMotor m = joint6DOF.getTranslationalLimitMotor();
                    float length = m.upperLimit.x;

                    double expand = 0;
                    for (int x = 0; x < cellMotorNeurons; x++) {
                        expand += a.getOutput(x);
                    }

                    if (expand > 0) {
                        //m.accumulatedImpulse.x += motorStep;
                        length += motorStep;
                        length = Math.min(length, maxStretch);
                    } else {
                        length = length * motorRestitution;
                    }

                    //System.out.println(expand + " " + length + " " + m.accumulatedImpulse);

                    m.lowerLimit.set(length / 2.0f, 0, 0);
                    m.upperLimit.set(length, 0, 0);


                    //transfer a->b & b->a
                    for (int i = 0; i < intercellNeurons; i++) {
                        icAB[i] = a.getOutput(i + cellMotorNeurons);
                        icBA[i] = b.getOutput(i + cellMotorNeurons);
                        a.setInput(i, icBA[i]);
                        b.setInput(i, icAB[i]);
                        //System.out.print(icAB[i] + "/" + icBA[i]);
                    }
                    //System.out.println();


                }
            });

        }

        protected void bind(BoxCell a, BoxCell b) {
            //bindPoint2Point(a, b);
            //bindHinge(a, b);
            bind6DoF(a, b);
        }

//
        protected void bindHinge(PhyBox a, PhyBox b) {
            HingeConstraint j;
            Transform localA = new Transform(), localB = new Transform();
            boolean useLinearReferenceFrameA = true;

            localA.setIdentity();
            localB.setIdentity();

            localA.origin.set(-0.07f, 0, 0f);
            localB.origin.set(0.07f, 0, 0f);

            j = new HingeConstraint(a.getBody(), b.getBody(), localA, localB);
            j.setLimit(-1.0f, 1.0f);

            //j.setLimit( -0.5f, 0.5f, 0.9f, 0.3f, 1.0f );;

            j.enableAngularMotor(true, 0.3f, 0.5f);


            phy.addConstraint(j, false);
        }
    }

    @Override protected void start() {
        BlendState bs = new BlendState();
        bs.setBlendEnabled(true);
        bs.setSourceFunction(SourceFunction.SourceAlpha);
        bs.setDestinationFunction(DestinationFunction.OneMinusSourceAlpha);
        bs.setEnabled(true);
        setRenderState(bs);

        phy = add(new PhySpaceBox(20));

        phy.physics.getTimeScale().set(1.0);
        phy.physics.getGravity().set(0, -9.5, 0);
 
        PhyBox ground = phy.add(new PhyBox(new V3(0, -2, 0), new V3(15, 0.5, 15), 0f));

        //walls
        double wallHeight = 1.5;
        phy.add(new PhyBox(new V3(-7.5, -wallHeight, 0), new V3(0.5, wallHeight, 15), 0f));
        phy.add(new PhyBox(new V3(7.5, -wallHeight, 0), new V3(0.5, wallHeight, 15), 0f));
        phy.add(new PhyBox(new V3(0, -wallHeight, -7.5), new V3(15, wallHeight, 0.5), 0f));
        phy.add(new PhyBox(new V3(0, -wallHeight, 7.5), new V3(15, wallHeight, 0.5), 0f));
        //ground.rotate(0.1, 0.1, 0.1);

        //Obstacles and Toys
        phy.add(new PhyBox(new V3(), new V3(0.5, 0.5, 0.5), 0.5f, BoxShape.Spheroid)).color(Color.Orange);

        phy.add(new PhyBox(new V3(0, -2.5, 0), new V3(6.5, 2.5, 6.5), 0f, BoxShape.Spheroid)).color(Color.Gray);

        new Snake(phy, 9, 0.45, 0.25, 1.1);
        //new Snake(phy, 12, 0.15, 0.25);

    }

    public static Color getNeuronColor(double a, double b) {
        a = (a + 1.0) * 0.5;
        b = (b + 1.0) * 0.5;
        return new Color(a, 0, b);
    }

    public static void main(String[] args) {
        ArdorSpacetime.newWindow(new DemoCell());

    }
}
