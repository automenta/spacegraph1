/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.spacenet.run.graph.neural;

import automenta.spacenet.run.DefaultGraphBuilder;
import automenta.spacenet.plugin.neural.NeuralGraph;
import automenta.spacenet.run.ArdorSpacetime;
import automenta.spacenet.space.Repeat;
import automenta.spacenet.space.Space;
import automenta.spacenet.space.geom.Box;
import automenta.spacenet.space.geom.Line3D;
import automenta.spacenet.space.geom.ProcessBox;
import automenta.spacenet.space.geom.graph.GraphBox;
import automenta.spacenet.space.geom.graph.arrange.forcedirect.ForceDirecting;
import automenta.spacenet.space.geom.graph.arrange.forcedirect.ForceDirecting.ForceDirectedParameters;
import com.ardor3d.framework.Canvas;
import com.ardor3d.input.Key;
import com.ardor3d.input.logical.InputTrigger;
import com.ardor3d.input.logical.KeyPressedCondition;
import com.ardor3d.input.logical.TriggerAction;
import com.ardor3d.input.logical.TwoInputStates;
import com.ardor3d.math.Vector3;
import com.ardor3d.scenegraph.Spatial;
import org.neuroph.core.Connection;
import org.neuroph.core.Neuron;
import org.neuroph.core.Weight;
import org.neuroph.contrib.neat.gen.Evolver;
import org.neuroph.contrib.neat.gen.NeuronGene;
import org.neuroph.contrib.neat.gen.NeuronType;
import org.neuroph.contrib.neat.gen.Organism;
import org.neuroph.contrib.neat.experiment.xor.XorFitnessFunction;

import org.neuroph.contrib.neat.gen.NeatParameters;
import org.neuroph.contrib.neat.gen.impl.SimpleNeatParameters;
import org.neuroph.contrib.neat.gen.operations.selector.NaturalSelectionOrganismSelector;
import org.neuroph.contrib.neat.gen.operations.speciator.DynamicThresholdSpeciator;
import org.neuroph.contrib.neat.gen.persistence.PersistenceException;
import org.neuroph.core.NeuralNetwork;
import java.util.Observable;
import java.util.Observer;
import java.util.ArrayList;
import java.util.List;

import edu.uwa.aidan.robot.*;

/** visualizes a Neuroph NEAT */
// we should add NEAT XOR simulation here and then display winner after each generation
// with method like setNeatGenerationWinner
public class DemoNeatMaze extends ProcessBox implements Observer {

    final public static double redrawUpdatePeriod = 4.0;
    final public static double testUpdatePeriod = 0.25;
    final public static double weightUpdatePeriod = 0.25;
    int maxGenerations = 500;
    private NeuralGraph netGraph;
    private SimpleNeatParameters neatParams;
    private Evolver evolver;
    NeuralNetwork currentNetwork = null;
    private NeuralNetwork shownNetwork;
    private GraphBox gb;
    double rotSpeed = 2.0;
    double rotHor = 0;
    double rotVert = 0;
    private ForceDirecting fd;

    protected void showNetwork(NeuralNetwork n) {
        if (shownNetwork != n) {
            shownNetwork = n;
            netGraph.setNetwork(n);
            //fd.forward(5.0, 15);
        }
    }

    @Override
    public void start() {

        netGraph = new NeuralGraph();

        ForceDirectedParameters params = new ForceDirectedParameters(new Vector3(50, 50, 50), 0.003, 0.05, 3.0);
        fd = new ForceDirecting(params, 0.1, 6, 0.25) {

            protected void updateNode(Object n, Box nBox, Vector3 nextSize) {
                if (n instanceof Neuron) {
                    double v = getNeuronScale((Neuron) n);
                    nextSize.set(v, v, v);
                    //nBox.color(getNeuronColor((Neuron) n));
                }
            }

            @Override
            protected void updateEdge(Object e, Space s) {
                super.updateEdge(e, s);
                if (e instanceof Connection) {
                    Connection c = (Connection) e;
                    ((Line3D) s).getRadius().set(getConnectionRadius(c.getWeight()));
                }
            }
        };

        final DefaultGraphBuilder sp = new DefaultGraphBuilder();

        gb = add(new GraphBox(netGraph, sp, fd));

        add(new Repeat(redrawUpdatePeriod) {

            @Override public void update(double t, double dt, Spatial s) {
                NeuralNetwork nn = currentNetwork;
                if (nn != null) {
                    showNetwork(nn);
                }
            }
        });

        startEvolution();

        initControls();

    }

    public void randomInputs(NeuralNetwork ann) {
        System.out.println("randomizing inputs");
        for (Neuron n : ann.getInputNeurons()) {
            n.setInput((-0.5 + Math.random()) * 2.0);
        }
        ann.calculate();
    }

//    public Color getNeuronColor(Neuron n) {
//        if (!n.hasInputConnections()) {
//            return Color.Purple;
//        }
//        else if (n.getOutConnections().size() == 0) {
//            return Color.White;
//        }
//        else {
//            return Color.Orange;
//        }
////        float v = (float) ((n.getOutput()));
////        return Color.hsb(v, 0.5, 0.5).alpha(0.1);
//    }
    public double getNeuronScale(Neuron n) {
        float v = (float) (0.5f * (n.getOutput() + 1.0));

        //return Math.pow(v / 1.3, 2.0);
        return v;
    }

    public double getConnectionRadius(Weight weight) {
        double w = Math.abs(weight.getValue());
        return Math.log10(1.0 + w / 20.0);
    }

    protected void finishedEvolving() {
        if (shownNetwork == null) {
            return;
        }
        add(new Repeat(testUpdatePeriod) {

            int cycle = 0;

            @Override public void update(double t, double dt, Spatial s) {
                if (cycle % 8 == 0) {
                    //  randomInputs(shownNetwork);
                } else {
                    //  System.out.println("updating");
                    //  shownNetwork.calculate();
                }
                cycle++;
            }
        });

    }

    protected void startEvolution() {
        neatParams = new SimpleNeatParameters();
        neatParams.setFitnessFunction(new XorFitnessFunction());
        neatParams.setPopulationSize(300);
        neatParams.setMaximumFitness(XorFitnessFunction.MAXIMUM_FITNESS);
        // as NN's only approximate functions there is no chance that we will generate the maximum
        // fitness, so enforce a number of generations limit to stop us running forever.
        neatParams.setMaximumGenerations(maxGenerations);

        NaturalSelectionOrganismSelector selector = new NaturalSelectionOrganismSelector();
        selector.setKillUnproductiveSpecies(true);
        neatParams.setOrganismSelector(selector);

        DynamicThresholdSpeciator speciator = new DynamicThresholdSpeciator();
        speciator.setMaxSpecies(4);
        neatParams.setSpeciator(speciator);

        NeuronGene inputOne = new NeuronGene(NeuronType.INPUT, neatParams);
        NeuronGene inputTwo = new NeuronGene(NeuronType.INPUT, neatParams);
        NeuronGene output = new NeuronGene(NeuronType.OUTPUT, neatParams);

        // create the evolver and run.
//        evolver = Evolver.createNew(neatParams, Arrays.asList(inputOne, inputTwo), Arrays.asList(output));

        NeatParametersBuilder.getTrainParams();

        NeatParameters params = NeatParametersBuilder.neatParameters;

        evolver = Evolver.createNew(params, createInputLayer(params), createOutputLayer(params));
        evolver.addObserver(this);
        Thread evolutionThread = new Thread(new Runnable() {

            @Override public void run() {
                try {
                    //Organism best = evolver.evolve();

                    Organism o = evolver.evolve();
                    finishedEvolving();

                } catch (PersistenceException ex) {
                    ex.printStackTrace();
                }
            }
        });
        evolutionThread.setPriority(Thread.MIN_PRIORITY);
        evolutionThread.start();
    }

    @Override
    public void update(Observable o, Object arg) {
        NeuralNetwork nn = neatParams.getNeuralNetworkBuilder().createNeuralNetwork((Organism) arg);
//        System.out.println("inputs=" + nn.getInputNeurons());
//        System.out.println("outputs=" + nn.getOutputNeurons());
//        System.out.println("layers=" + nn.getLayers());
//        for (Layer l : nn.getLayers()) {
//            System.out.println(" " + l.getNeuronsCount());
//        }
        currentNetwork = nn;
    }

    public static void main(String[] args) {
        ArdorSpacetime.newWindow(new DemoNeatMaze());
    }

    private void initControls() {
        getSpacetime().addCondition(new InputTrigger(new KeyPressedCondition(Key.F), new TriggerAction() {

            public void perform(final Canvas source, final TwoInputStates inputState, final double tpf) {
                rotateGraph(tpf * rotSpeed, 0);
            }
        }));
        getSpacetime().addCondition(new InputTrigger(new KeyPressedCondition(Key.H), new TriggerAction() {

            public void perform(final Canvas source, final TwoInputStates inputState, final double tpf) {
                rotateGraph(-tpf * rotSpeed, 0);
            }
        }));
        getSpacetime().addCondition(new InputTrigger(new KeyPressedCondition(Key.V), new TriggerAction() {

            public void perform(final Canvas source, final TwoInputStates inputState, final double tpf) {
                rotateGraph(0, tpf * rotSpeed);
            }
        }));
        getSpacetime().addCondition(new InputTrigger(new KeyPressedCondition(Key.G), new TriggerAction() {

            public void perform(final Canvas source, final TwoInputStates inputState, final double tpf) {
                rotateGraph(0, -tpf * rotSpeed);
            }
        }));
    }

    protected void rotateGraph(double rotHorDelta, double rotVertDelta) {
        rotHor += rotHorDelta;
        rotVert += rotVertDelta;
        gb.rotate(rotHor, rotVert, 0);
    }

    public List<NeuronGene> createInputLayer(NeatParameters params) {
        List<NeuronGene> inputs = new ArrayList<NeuronGene>();

        for (int i = 0; i < 12; i++) {
            NeuronGene ng = new NeuronGene(NeuronType.INPUT, params);
            inputs.add(ng);
        }

        return inputs;
    }

    public List<NeuronGene> createOutputLayer(NeatParameters params) {
        List<NeuronGene> outputs = new ArrayList<NeuronGene>();

        for (int i = 0; i < 2; i++) {
            NeuronGene ng = new NeuronGene(NeuronType.OUTPUT, params);
            outputs.add(ng);
        }

        return outputs;
    }
}
