/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.spacenet.run.old.graph.neural;

import automenta.spacenet.plugin.neural.brainz.AbstractNeuron;
import automenta.spacenet.plugin.neural.brainz.Brain;
import automenta.spacenet.plugin.neural.brainz.BrainBuilder;
import automenta.spacenet.plugin.neural.brainz.BrainGraph;
import automenta.spacenet.plugin.neural.brainz.InterNeuron;
import automenta.spacenet.plugin.neural.brainz.MotorNeuron;
import automenta.spacenet.plugin.neural.brainz.SenseNeuron;
import automenta.spacenet.run.ArdorSpacetime;
import automenta.spacenet.run.old.DefaultGraphBuilder;
import automenta.spacenet.space.Repeat;
import automenta.spacenet.space.geom.Box;
import automenta.spacenet.space.geom.Box.BoxShape;
import automenta.spacenet.space.geom.ProcessBox;
import automenta.spacenet.space.geom.Rect;
import automenta.spacenet.space.geom.Rect.RectShape;
import automenta.spacenet.space.geom.graph.GraphBox;
import automenta.spacenet.space.geom.graph.GraphBoxBuilder;
import automenta.spacenet.space.geom.graph.GraphBoxModel;
import automenta.spacenet.space.geom.graph.arrange.GridListing;
import automenta.spacenet.space.geom.layout.ColRect;
import automenta.spacenet.var.Maths;
import automenta.spacenet.var.graph.MemGraph;
import automenta.spacenet.var.physical.Color;
import com.ardor3d.scenegraph.Spatial;

/**
 *
 * @author seh
 */
public class DemoBrainz extends ProcessBox {
    final Brain b = new BrainBuilder(16, 8).newBrain(100, 4, 32);

    double neuronBoxUpdatePeriod = 0.05;
    double neuronUpdatePeriod = 0.05;
    private Rect outputRect;
    private double[] nextOutputs;

    public MemGraph getGraph() {
        return new BrainGraph(b);
    }

    double neuronScale = 0.7;
    
    public GraphBoxBuilder getGraphBuilder() {
        return new DefaultGraphBuilder() {
            @Override public Box newNodeSpace(Object node) {
                Box b = new Box(BoxShape.Empty);
                if (node instanceof AbstractNeuron) {
                    final AbstractNeuron i = (AbstractNeuron) node;
                    final Rect r = b.add(new Rect(node instanceof InterNeuron ? RectShape.Rect : RectShape.Ellipse));
                    b.add(new Repeat(neuronBoxUpdatePeriod) {
                        @Override protected void update(double t, double dt, Spatial parent) {
                            double o = i.getOutput();
                            double p = o;
                            if (i instanceof InterNeuron)
                                p = ((InterNeuron)i).getPotential();

                            r.color(getNeuronColor(o, p));
                            r.scale( 0.25 + (Math.abs( o ) + Math.abs(p)) * neuronScale);
                        }
                    });
                }
                return b;
            }         
        };
    }



    @Override protected void start() {
        double size = 20.0;
        //V3 boundsMax = new V3(size, size, size);

//        ForceDirectedParameters par = new ForceDirectedParameters(boundsMax, 0.01, 0.001, 1.0);
//        double updatePeriod = 0.05;
//        double interpSpeed = 0.3;
//        int substeps = 12;
        
        //ForceDirecting arr = new ForceDirecting(par, updatePeriod, substeps, interpSpeed);

        GraphBoxModel arr = new GridListing(-0.5, -0.5, 0.5, 0.5, null);

        add(new GraphBox(getGraph(), getGraphBuilder(), arr));
        //add(new ForceDirectedParametersEditWindow(par, DemoDefaults.font)).move(-1, 0, 0);

        System.out.println(b.getTotalNeurons() + " * " + b.getTotalSynapses());
        System.out.println("senses: " + b.getSense());
        System.out.println("neurons: " + b.getNeuron());
        System.out.println("motors: " + b.getMotor());

//        final Map<SenseNeuron, Rect> senseRect = new HashMap();
//        final Map<InterNeuron, Rect> neuronRect = new HashMap();
//        double x = 0;
//        double s = 0.1;
//        for (InterNeuron i : b.getNeuron()) {
//            Rect r = add(new Rect(RectShape.Rect));
//            r.scale(0.1, 1);
//            r.move(x, 0, 0);
//            neuronRect.put(i, r);
//            x += s;
//        }
//
//        x = 0;
//        s = 0.1;
//        for (SenseNeuron i : b.getSense()) {
//            Rect r = add(new Rect(RectShape.Rect));
//            r.scale(0.1, 1);
//            r.move(x, 1, 0);
//            senseRect.put(i, r);
//            x += s;
//        }

        outputRect = add(new Rect(RectShape.Empty)).move(0.8,0,0).scale(0.5);

        add(new Repeat(3.0) {
            @Override protected void update(double t, double dt, Spatial parent) {
                randomizeInputs();
            }
        });

        add(new Repeat(neuronUpdatePeriod) {
            @Override protected void update(double t, double dt, Spatial parent) {
                updateInputs();
                b.forward();
                updateOutputs();
            }
        });

    }

    protected void updateOutputs() {
        outputRect.removeAll();


        Rect[] o = new Rect[b.getMotor().size()];

        int j = 0;
        for (MotorNeuron m : b.getMotor()) {
            double bo = m.getOutput();
            Color c;
            if (bo < 0.5)
                c = Color.Blue;
            else
                c = Color.Red;
            Rect re = new Rect(RectShape.Ellipse);
            re.color(c);
            o[j++] = re;
        }

        outputRect.add(new ColRect(0.01, o));
    }

    public void randomizeInputs() {
        if (nextOutputs == null) {
            nextOutputs = new double[b.getSense().size()];
        }
        for (int i = 0; i < b.getSense().size(); i++) {
            nextOutputs[i] = Maths.random(-1, 1);
        }

    }

    double inputMomentum = 0.98;
    public void updateInputs() {
        int i = 0;

        for (SenseNeuron s : b.getSense()) {
            s.senseInput = (inputMomentum * s.senseInput) + ((1.0 - inputMomentum) * nextOutputs[i++]);
        }

    }

    public Color getNeuronColor(double a, double b) {
        a = (a + 1.0) * 0.5;
        b = (b + 1.0) * 0.5;
        return new Color(a, 0, b);
    }

    public static void main(String[] args) {
        ArdorSpacetime.newWindow(new DemoBrainz());

    }
}
