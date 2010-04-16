/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.spacenet.run.old.bio;

import automenta.spacenet.run.ArdorSpacetime;
import automenta.spacenet.run.old.graph.neural.DemoBrainz;
import automenta.spacenet.space.Repeat;
import automenta.spacenet.space.control.Zoomable;
import automenta.spacenet.space.geom.ProcessBox;
import automenta.spacenet.space.geom.physics.PhyBox;
import automenta.spacenet.space.geom.physics.PhySpaceBox;
import automenta.spacenet.var.Maths;
import automenta.spacenet.var.physical.Color;
import automenta.spacenet.var.vector.V3;
import com.ardor3d.scenegraph.Spatial;
import com.bulletphysics.dynamics.constraintsolver.Generic6DofConstraint;
import com.bulletphysics.dynamics.constraintsolver.HingeConstraint;
import com.bulletphysics.dynamics.constraintsolver.Point2PointConstraint;
import com.bulletphysics.dynamics.constraintsolver.TranslationalLimitMotor;
import com.bulletphysics.linearmath.Transform;
import javax.vecmath.Vector3f;

public class DemoSnake extends ProcessBox {

    public static class PBox extends PhyBox implements Zoomable {

        private PBox(V3 pos, V3 size, float mass) {
            super(pos, size, mass);
            color(Color.newRandomHSB(0.95, 0.5));
        }


        @Override
        public void onZoomStart() {
        }

        @Override
        public void onZoomStop() {
        }

        @Override
        public boolean isZoomable() {
            return true;
        }

        @Override
        public boolean isTangible() {
            return true;
        }

    }

    /** a physical pipe of capsules */
    public /*static*/ class Snake {

        Vector3f tmp = new Vector3f();

        public Snake(PhySpaceBox b, int numSegments, double segmentLength, double radius) {
            super();

            double density = 1.0;

            PhyBox segment[] = new PhyBox[numSegments];
            for (int s = 0; s < numSegments; s++) {
                double l = segmentLength * Maths.random(0.75, 1.25);

                double w = radius;
                double h = radius;

                float mass = (float)((l * w * h) * density);
                PhyBox pb = new PBox(new V3(), new V3(l, radius, radius), mass);

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

        protected void bind6DoF(PhyBox a, PhyBox b) {
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

            add(new Repeat() {

                float maxStretch = 0.2f;
                Vector3f pivA = new Vector3f();
                Vector3f pivB = new Vector3f();

                @Override protected void update(double t, double dt, Spatial parent) {
                    TranslationalLimitMotor m = joint6DOF.getTranslationalLimitMotor();
                    float xmin = m.lowerLimit.x;
                    xmin += (float)Maths.random(-0.01, 0.01);
                    float xmax = m.upperLimit.x;
                    xmax += (float)Maths.random(-0.01, 0.01);

                    xmin = Math.max(xmin, 0);
                    xmax = Math.min(xmax, maxStretch);
                    xmin = Math.min(xmin, xmax);
                    xmax = Math.max(xmin, xmax);

                    m.lowerLimit.set(xmin, 0, 0);
                    m.upperLimit.set(xmax, 0, 0);
//                    j.getPivotInA(pivA);
//                    j.getPivotInB(pivB);
//                    pivA.x += (float) Maths.random(-0.1, 0.1);
//                    System.out.println(j + " " + pivA + " " + pivB);
//                    j.setPivotA(pivA);
//                    j.setPivotB(pivB);
                }
            });

        }

        protected void bind(PhyBox a, PhyBox b) {
            //bindPoint2Point(a, b);
            //bindHinge(a, b);
            bind6DoF(a, b);
        }

        protected void bindPoint2Point(PhyBox a, PhyBox b) {
            final Point2PointConstraint j;

            Vector3f pa = new Vector3f((float) (-a.getSize().getX() / 2.0) * 1.1f, 0, 0);
            Vector3f pb = new Vector3f((float) (b.getSize().getX() / 2.0) * 1.1f, 0, 0);
            j = new Point2PointConstraint(a.getBody(), b.getBody(), pa, pb);

            //j.setLimit( -0.5f, 0.5f, 0.9f, 0.3f, 1.0f );;


            phy.addConstraint(j, false);

            add(new Repeat(0.01) {

                Vector3f pivA = new Vector3f();
                Vector3f pivB = new Vector3f();

                @Override protected void update(double t, double dt, Spatial parent) {

                    j.getPivotInA(pivA);
                    j.getPivotInB(pivB);
                    pivA.x += (float) Maths.random(-0.1, 0.1);
                    System.out.println(j + " " + pivA + " " + pivB);
                    j.setPivotA(pivA);
                    j.setPivotB(pivB);
                }
            });
        }

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
    protected PhySpaceBox phy;

    @Override protected void start() {
        phy = add(new PhySpaceBox());

        phy.physics.getTimeScale().set(1.0);
        phy.physics.getGravity().set(0, -9.5, 0);
        //add(phy.add(new PhyBox(new V3(1, 2, 0.5), new V3(1, 1, 1), 1.0f)));
        //add(phy.add(new PhyBox(new V3(-2, 2, -0.5), new V3(1, 0.5, 1), 1.0f)));
        //add(phy.add(new PhyBox(new V3(2, -2, 0.5), new V3(0.5, 1, 1), 1.0f)));

        PhyBox ground = phy.add(new PhyBox(new V3(0, -2, 0), new V3(15, 0.5, 15), 0f));
        ground.rotate(0.1, 0.1, 0.1);

        new Snake(phy, 14, 0.35, 0.25);

        add(new DemoBrainz()).scale(2).move(0,0,-0.5);

    }

    public static void main(String[] args) {
        ArdorSpacetime.newWindow(new DemoSnake());
    }
}
