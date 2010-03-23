/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.spacenet.run;

import automenta.spacenet.plugin.comm.Agent;
import automenta.spacenet.plugin.comm.Channel;
import automenta.spacenet.plugin.comm.Message;
import automenta.spacenet.plugin.comm.twitter.TwitterGrapher;
import automenta.spacenet.plugin.file.FileGrapher;
import automenta.spacenet.space.Space;
import automenta.spacenet.space.geom.graph.arrange.forcedirect.ScalarMapForceDirecting;
import automenta.spacenet.space.geom.Box;
import automenta.spacenet.space.geom.graph.arrange.forcedirect.ForceDirectedParametersEditWindow;
import automenta.spacenet.space.geom.ProcessBox;
import automenta.spacenet.space.geom.border.GridRect;
import automenta.spacenet.space.geom.graph.GraphBoxModel;
import automenta.spacenet.space.geom.graph.GraphBox;
import automenta.spacenet.space.geom.graph.GraphBoxBuilder;
import automenta.spacenet.space.geom.graph.arrange.forcedirect.ForceDirecting.ForceDirectedParameters;
import automenta.spacenet.space.geom.graph.ScalarGraphMapControlWindow;
import automenta.spacenet.space.geom.graph.arrange.GridListing;
import automenta.spacenet.space.geom.graph.arrange.JungModel;
import automenta.spacenet.space.geom.graph.arrange.LineListing;
import automenta.spacenet.space.geom.graph.arrange.Scattering;
import automenta.spacenet.space.geom.text3d.Font3D;
import automenta.spacenet.space.surface.BitmapSurface;
import automenta.spacenet.space.widget.PanningDragRect;
import automenta.spacenet.space.widget.slider.ScalarMapEqualizerPanel;
import automenta.spacenet.space.widget.window.Window;
import automenta.spacenet.space.widget3d.Window3D;
import automenta.spacenet.var.action.Actions;
import automenta.spacenet.var.action.InstantAction;
import automenta.spacenet.var.vector.V3;
import automenta.spacenet.var.graph.MemGraph;
import automenta.spacenet.var.graph.map.AttentionThresholdGraph;
import automenta.spacenet.var.graph.map.ScalarGraphMap;
import automenta.spacenet.var.physical.Color;
import automenta.spacenet.var.scalar.DoubleVar;
import automenta.spacenet.var.string.StringVar;
import com.ardor3d.framework.Canvas;
import com.ardor3d.input.Key;
import com.ardor3d.input.logical.InputTrigger;
import com.ardor3d.input.logical.KeyPressedCondition;
import com.ardor3d.input.logical.TriggerAction;
import com.ardor3d.input.logical.TwoInputStates;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout;
import java.awt.Dimension;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.neuroph.core.Weight;

/**
 * empty space Operating System environment
 */
public class DemoOS2d extends ProcessBox {

    Font3D font3d = DemoDefaults.font;
    double w = 16;
    double h = 12;
    private Actions actions = new Actions();
    private ForceDirectedParameters params;
    private MemGraph graph;
    private GraphBox graphBox;
    private ScalarGraphMap attention;
    private AttentionThresholdGraph visGraph;
    double attentionUpdatePeriod = 0.03;
    private ScalarGraphMapControlWindow attentionControlWin;
    private Map<Class, DoubleVar> typeValues = new HashMap();
    private ScalarMapEqualizerPanel<Class> typeEqualizer;
    private Window typeEqualizerWin;

    public class DemoGraphBox extends Box {

        public DemoGraphBox() {
            super(BoxShape.Empty);

            attention = new ScalarGraphMap(graph, 0);


            {
                visGraph = new AttentionThresholdGraph(graph, attention);
                add(visGraph.newUpdating(attentionUpdatePeriod));
            }

            {
                graphBox = new GraphBox(visGraph, getGraphBuilder(), newForceDirect3DArranger(Math.min(w, h), 0.1));
                add(graphBox);
            }

            {
                attentionControlWin = add(new ScalarGraphMapControlWindow(attention, visGraph, font3d));
                attentionControlWin.move(0, 1, 0).scale(0.2, 0.15);
            }

            {
                typeValues.put(Channel.class, new DoubleVar(1.0));
                typeValues.put(Message.class, new DoubleVar(1.0));
                typeValues.put(Agent.class, new DoubleVar(1.0));
                typeValues.put(StringVar.class, new DoubleVar(1.0));

                typeEqualizer = new ScalarMapEqualizerPanel<Class>(typeValues, font3d, 0, 1.0) {

                    @Override protected String getLabel(Class x) {
                        return x.getSimpleName();
                    }
                };
                typeEqualizerWin = add(new Window(typeEqualizer, 0.1));
                typeEqualizerWin.move(0, -1, 0).scale(0.15, 0.2);

            }

            final PanningDragRect backRect = add(new PanningDragRect(1.5));
            {
                backRect.setZoomable(false);
                backRect.scale(w, h).move(0, 0, -0.2);

                //URL to Background Image
                actions.add(new InstantAction<URL, URL>() {

                    @Override public String toString(URL i) {
                        return "As Background Image";
                    }

                    @Override protected URL run(URL i) throws Exception {
                        backRect.add(new BitmapSurface(i));
                        return i;
                    }

                    @Override public double applies(URL i) {
                        return 1.0;
                    }
                });
                backRect.color(Color.Black);
                backRect.add(new GridRect(Color.Orange, 8, 8, 0.1));
            }


            new FileGrapher(graph, "/", 1);

            final TwitterGrapher tg = new TwitterGrapher(graph);
            tg.addPublicTimeline();

            add(new OSRootMenu(tg)).move(2, 0, 0);




        }
    }

    //1. graph inside MetaBox
    //2. metabox with controls to:
    //      adjust all aspects graph filtering
    //      adjust all aspects graph presentation (incl. arrangement)
    //      manipulate graph content and processes
    //      toggle 2D/3D mode
    //      find object: keyword search or index browser
    @Override protected void start() {
        DefaultActions.addActions(actions);

        graph = new MemGraph();

        add(new DemoGraphBox()).move(1, 0, 0).scale(2, 2, 2);
        //add(new DemoDesktop()).move(-1, 0, 0).scale(2,2,2);

    //            getSpacetime().addCondition(new InputTrigger(new KeyPressedCondition(Key.INSERT), new TriggerAction() {
//
//                public void perform(final Canvas source, final TwoInputStates inputState, final double tpf) {
//                    graph.addNode(new StringVar("?"));
//                }
//            }));
//            getSpacetime().addCondition(new InputTrigger(new KeyPressedCondition(Key.ONE), new TriggerAction() {
//
//                public void perform(final Canvas source, final TwoInputStates inputState, final double tpf) {
//                    graphBox.setModel(newForceDirect3DArranger(0.1));
//                }
//            }));
//            getSpacetime().addCondition(new InputTrigger(new KeyPressedCondition(Key.TWO), new TriggerAction() {
//
//                public void perform(final Canvas source, final TwoInputStates inputState, final double tpf) {
//                    graphBox.setModel(newForceDirect3DArranger(1.0));
//                }
//            }));
            getSpacetime().addCondition(new InputTrigger(new KeyPressedCondition(Key.THREE), new TriggerAction() {

                public void perform(final Canvas source, final TwoInputStates inputState, final double tpf) {
                    graphBox.setModel(new Scattering());
                }
            }));
            getSpacetime().addCondition(new InputTrigger(new KeyPressedCondition(Key.FOUR), new TriggerAction() {

                public void perform(final Canvas source, final TwoInputStates inputState, final double tpf) {
                    graphBox.setModel(new LineListing(0, -0.5, 0, 0.5, null));
                }
            }));
            getSpacetime().addCondition(new InputTrigger(new KeyPressedCondition(Key.FIVE), new TriggerAction() {

                public void perform(final Canvas source, final TwoInputStates inputState, final double tpf) {
                    graphBox.setModel(new GridListing(-0.5, -0.5, 0.5, 0.5, null));
                }
            }));
            getSpacetime().addCondition(new InputTrigger(new KeyPressedCondition(Key.SIX), new TriggerAction() {

                public void perform(final Canvas source, final TwoInputStates inputState, final double tpf) {
                    graphBox.setModel(new JungModel(new FRLayout(graph, new Dimension(600, 600))));
                }
            }));
            getSpacetime().addCondition(new InputTrigger(new KeyPressedCondition(Key.SEVEN), new TriggerAction() {

                public void perform(final Canvas source, final TwoInputStates inputState, final double tpf) {
                    graphBox.setModel(new JungModel(new SpringLayout(graph)));
                }
            }));
            getSpacetime().addCondition(new InputTrigger(new KeyPressedCondition(Key.EIGHT), new TriggerAction() {

                public void perform(final Canvas source, final TwoInputStates inputState, final double tpf) {
                    graphBox.setModel(new JungModel(new CircleLayout(graph)));
                }
            }));
//            getSpacetime().addCondition(new InputTrigger(new KeyPressedCondition(Key.NINE), new TriggerAction() {
//
//                public void perform(final Canvas source, final TwoInputStates inputState, final double tpf) {
//                    try {
//                        String input = JOptionPane.showInputDialog("Enter Background Image URL");
//                        backRect.add(new BitmapSurface(input));
//                    } catch (MalformedURLException ex) {
//                        Logger.getLogger(DemoOS.class.getName()).log(Level.SEVERE, null, ex);
//                    }
//                }
//            }));

    }

    public GraphBoxBuilder getGraphBuilder() {
        return new GraphBoxBuilder() {

            @Override
            public Space newEdgeSpace(Object edge, Box pa, Box pb) {
                return new DefaultEdgeLine(edge, pa, pb);
            }

            @Override
            public Box newNodeSpace(final Object node) {
                System.out.println("new box for " + node);

                final Box b = new DefaultObjectBox(node);
                final Window3D w = new Window3D();
                w.spanZ(-0.05, 0.05);
                w.add(b);
                b.moveDZ(0.6);
                return w;
            }

            public Color getWeightColor(Weight w) {
                float v = (float) (0.5F * (w.getValue() + 1.0));
                return Color.hsb(v, 0.2, v).alpha(0.1);
            }
        };
    }

    public GraphBoxModel newForceDirect3DArranger(double w, double depthFactor) {
        double m = 0.1;

        V3 boundsMax = new V3(w - m, w - m, (w - m) * depthFactor);
        params = new ForceDirectedParameters(boundsMax, 0.3, 0.002, 1.0);
        double updatePeriod = 0.05;
        double interpSpeed = 0.25;
        int substeps = 8;

        return new ScalarMapForceDirecting(attention, params, updatePeriod, substeps, interpSpeed) {

            private ForceDirectedParametersEditWindow fdcp;

            @Override
            public void start(GraphBox graphBox) {
                super.start(graphBox);

                fdcp = getGraphBox().add(new ForceDirectedParametersEditWindow(params, font3d));
                fdcp.move(-1, 0, 0).scale(0.5);
            }

            double getValue(Object node) {
                return 0.1;
            }

            @Override
            public void stop() {
                getGraphBox().remove(fdcp);
                fdcp = null;

                super.stop();
            }

            public double getNodeSize(Object node, double n) {
                for (Class c : typeValues.keySet()) {
                    if (c.isAssignableFrom(node.getClass())) {
                        return n * typeValues.get(c).d();
                    }
                }

                return n;
            }
        };
    }

    public static void main(String[] argV) {
        ArdorSpacetime.newWindow(new DemoOS2d());
    }
}
