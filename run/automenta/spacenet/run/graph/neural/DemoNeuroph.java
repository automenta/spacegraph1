package automenta.spacenet.run.graph.neural;

import automenta.spacenet.space.geom.graph.arrange.forcedirect.ForceDirectedParametersEditWindow;
import automenta.spacenet.run.DefaultGraphBuilder;
import automenta.spacenet.plugin.neural.NeuralGraph;
import automenta.spacenet.run.ArdorSpacetime;
import automenta.spacenet.run.DemoDefaults;
import automenta.spacenet.space.Repeat;
import automenta.spacenet.space.Space;
import automenta.spacenet.space.geom.Box;
import automenta.spacenet.space.geom.ProcessBox;
import automenta.spacenet.space.geom.graph.GraphBox;
import automenta.spacenet.space.geom.graph.arrange.forcedirect.ForceDirecting;
import automenta.spacenet.space.geom.graph.arrange.forcedirect.ForceDirecting.ForceDirectedParameters;
import automenta.spacenet.space.widget.button.Button;
import automenta.spacenet.space.widget.button.ButtonAction;
import automenta.spacenet.space.widget.panel.Panel;
import automenta.spacenet.space.widget.window.Window;
import automenta.spacenet.var.physical.Color;
import com.ardor3d.framework.Canvas;
import com.ardor3d.input.Key;
import com.ardor3d.input.logical.InputTrigger;
import com.ardor3d.input.logical.KeyPressedCondition;
import com.ardor3d.input.logical.TriggerAction;
import com.ardor3d.input.logical.TwoInputStates;
import com.ardor3d.math.Vector3;
import com.ardor3d.scenegraph.Spatial;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.Neuron;
import org.neuroph.core.Weight;
import org.neuroph.nnet.Hopfield;
import org.neuroph.nnet.Kohonen;
import org.neuroph.nnet.MultiLayerPerceptron;

/** visualizes a Neuroph Kohenen neural-network */
public class DemoNeuroph extends ProcessBox {

    final public static double annUpdatePeriod = 0.1;
    final public static double weightUpdatePeriod = 0.25;
    int numInputs = 8;
    int numOutputs = 6;
    private NeuralGraph netGraph;
    private GraphBox gb;

//    public class NeuralNetControlWindow extends Window {
//
//        public NeuralNetControlWindow() {
//            super();
//
//            TextButton kohonenButton = add(new TextButton("Kohonen"));
//            kohonenButton.addButtonAction(new ButtonAction() {
//                @Override public void onButtonPressed(Button b) {
//                    setNetworkKohonen();
//                }
//            });
//
//            TextButton mlButton = add(new TextButton("MultiLayer Perceptron"));
//            mlButton.addButtonAction(new ButtonAction() {
//                @Override public void onButtonPressed(Button b) {
//                    setNetworkMultiLayerPerceptron();
//                }
//            });
//
//            kohonenButton.span(-0.4, -0.4, 0.4, -0.3);
//
//            mlButton.span(-0.4, -0.2, 0.4, -0.1);
//
//        }
//
//
//    }
    protected void setNetwork(NeuralNetwork n) {
        n.randomizeWeights();
        randomInputs(n);
        netGraph.setNetwork(n);
    }

    int currentNetwork = 0;
    protected void setNextNetwork() {
        switch (currentNetwork % 3) {
            case 1:
                setNetwork(new Hopfield(5));
                break;
            case 0:
                setNetwork(new MultiLayerPerceptron(4, 6, 3, 3, 3));
                break;
            case 2:
                setNetwork(new Kohonen(numInputs, numOutputs));
                break;
        }
        currentNetwork++;
    }

    double rotSpeed = 2.0;
    double rotHor = 0;
    double rotVert = 0;

    @Override
    public void start() {

        netGraph = new NeuralGraph();

        setNextNetwork();

        Panel p = new Panel();
        Button nextNetButton = new Button(DemoDefaults.font, "Next");
        nextNetButton.add(new ButtonAction() {
            @Override public void onButtonClicked(Button b) {
                setNextNetwork();
            }
        });
        p.add(nextNetButton).moveDZ(0.05);
        add(new Window(p, 0.1)).move(-1, -1, 0);

        ForceDirectedParameters params = new ForceDirectedParameters(new Vector3(50, 50, 50), 0.01, 0.01, 2.0);
        ForceDirecting fd = new ForceDirecting(params, 0.1, 5, 0.25) {

            protected void updateNode(Object n, Box nBox, Vector3 nextSize) {
                if (n instanceof Neuron) {
                    double v = getNeuronScale((Neuron) n);
                    nextSize.set(v, v, v);
                    //nBox.color(getNeuronColor((Neuron)n));
                }
            }

            @Override
            protected void updateEdge(Object e, Space s) {
                super.updateEdge(e, s);
//                if (e instanceof Connection) {
//                    Connection c = (Connection) e;
//                    ((Line3D)s).getRadius().set(getConnectionRadius(c.getWeight()));
//                }
            }
        };


        add(new ForceDirectedParametersEditWindow(params, DemoDefaults.font)).move(-1, 0, 0);


        final DefaultGraphBuilder sp = new DefaultGraphBuilder();

        gb = add(new GraphBox(netGraph, sp, fd));

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

//        ForceDirectedControlWindow fdControlWin = add(new ForceDirectedControlWindow(fd));
//        fdControlWin.move(-2, 1, 0);

//        NeuralNetControlWindow nnControlWin = add(new NeuralNetControlWindow());
//        nnControlWin.move(-2, -1, 0);


        add(new Repeat(annUpdatePeriod) {

            @Override public void update(double t, double dt, Spatial s) {
                if (Math.random() < 0.1) {
                    netGraph.getNet().randomizeWeights();
                }
                if (Math.random() < 0.1) {
                    randomInputs(netGraph.getNet());
                }
                netGraph.getNet().calculate();
            }
        });
    }

    protected void rotateGraph(double rotHorDelta, double rotVertDelta) {
        rotHor += rotHorDelta;
        rotVert += rotVertDelta;
        gb.rotate(rotHor, rotVert, 0);
    }

    public void randomInputs(NeuralNetwork ann) {
        for (Neuron n : ann.getInputNeurons()) {
            n.setInput((-0.5 + Math.random()) * 2.0);
        }
    }

    public Color getNeuronColor(Neuron n) {
        float v = (float) ((n.getOutput()));
        return Color.hsb(v, 0.5, 0.5).alpha(0.1);
    }

    public double getNeuronScale(Neuron n) {
        float v = (float) (0.5f * (n.getOutput() + 1.0));
        return v;
        //return Math.pow(v / 1.3, 2.0);
    }

    public double getConnectionRadius(Weight weight) {
        double w = weight.getValue();
        float v = (float) (0.5f * (w + 1.0));
        return v / 20.0;
    }

    public static void main(String[] args) {
        ArdorSpacetime.newWindow(new DemoNeuroph());
    }
}
