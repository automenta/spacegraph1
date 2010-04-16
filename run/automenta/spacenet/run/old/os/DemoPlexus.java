/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.spacenet.run.old.os;

import automenta.spacenet.plugin.comm.twitter.TwitterGrapher;
import automenta.spacenet.plugin.file.FileGrapher;
import automenta.spacenet.run.ArdorSpacetime;
import automenta.spacenet.run.old.DefaultGraphBuilder;
import automenta.spacenet.run.old.DefaultObjectWindow3D;
import automenta.spacenet.run.DemoDefaults;
import automenta.spacenet.space.geom.Box;
import automenta.spacenet.space.geom.ProcessBox;
import automenta.spacenet.space.geom.border.GridRect;
import automenta.spacenet.space.geom.graph.GraphBox;
import automenta.spacenet.space.geom.graph.GraphBoxBuilder;
import automenta.spacenet.space.geom.graph.GraphBoxModel;
import automenta.spacenet.space.geom.graph.arrange.JungModel;
import automenta.spacenet.space.geom.graph.arrange.forcedirect.ForceDirectedParametersEditWindow;
import automenta.spacenet.space.geom.graph.arrange.forcedirect.ForceDirecting.ForceDirectedParameters;
import automenta.spacenet.space.geom.graph.arrange.forcedirect.ScalarMapForceDirecting;
import automenta.spacenet.space.geom.text3d.Font3D;
import automenta.spacenet.space.widget.PanningDragRect;
import automenta.spacenet.space.widget.button.Button;
import automenta.spacenet.space.widget.button.ButtonAction;
import automenta.spacenet.var.graph.MemGraph;
import automenta.spacenet.var.graph.map.AttentionThresholdGraph;
import automenta.spacenet.var.graph.map.ScalarGraphMap;
import automenta.spacenet.var.graph.patterns.MeshGraph;
import automenta.spacenet.var.physical.Color;
import automenta.spacenet.var.vector.V3;
import edu.uci.ics.jung.algorithms.layout.SpringLayout;

/**
 *
 */
public class DemoPlexus extends ProcessBox {

    double attentionUpdatePeriod = 0.1;
    
    double w = 4;
    double h = 4;
    double d = 0.05;
    
    public static final Font3D font = DemoDefaults.font;

    /** plexus is associated with a graph (a subgraph of a larger graph) and an attention map to the elements of the graph */
    public static class Plexus extends GraphBox {

        private final ScalarGraphMap att;
        private final MemGraph g;

        //getArranger
        public Plexus(MemGraph graph, ScalarGraphMap att, GraphBoxModel initialModel, final GraphBoxBuilder builder) {
            super(graph, builder, initialModel);
            this.g = graph;
            this.att = att;

        }

        @Override protected void addNode(final Object vertex, Box b) {
            super.addNode(vertex, b);

            Button minusAttentionButton = new Button(font, "-");
            minusAttentionButton.add(new ButtonAction() {

                @Override public void onButtonClicked(Button b) {
                    att.add(vertex, -0.05);
                }
            });
            Button plusAttentionButton = new Button(font, "+");
            plusAttentionButton.add(new ButtonAction() {

                @Override public void onButtonClicked(Button b) {
                    att.add(vertex, +0.05);
                }
            });
            Button meltAttentionButton = new Button(font, "~");
            meltAttentionButton.add(new ButtonAction() {

                @Override public void onButtonClicked(Button b) {
                    att.blur(vertex, 0.1);
                }
            });
            Button focusButton = new Button(font, "%");
            focusButton.add(new ButtonAction() {
                @Override public void onButtonClicked(Button b) {
                    att.focus(0.05);
                }
            });
            Button spikeButton = new Button(font, "!");
            spikeButton.add(new ButtonAction() {
                @Override public void onButtonClicked(Button b) {
                    att.addRandom(0, 0.2);
                }
            });

            Button prevLayoutButton = new Button(font, "<");
            prevLayoutButton.add(new ButtonAction() {
                @Override public void onButtonClicked(Button b) {
                    Plexus.this.setModel(new JungModel(new SpringLayout(g)));
                }
            });

            b.add(plusAttentionButton).span(0.45, 0.45, 0.5, 0.5).moveDZ(0.6);
            b.add(minusAttentionButton).span(0.4, 0.45, 0.45, 0.5).moveDZ(0.6);
            b.add(meltAttentionButton).span(0.35, 0.45, 0.4, 0.5).moveDZ(0.6);
            b.add(focusButton).span(0.3, 0.45, 0.35, 0.5).moveDZ(0.6);
            b.add(spikeButton).span(0.25, 0.45, 0.3, 0.5).moveDZ(0.6);

            b.add(prevLayoutButton).span(0.45, -0.45, 0.5, -0.5).moveDZ(0.6);
            //b.add(nextLayoutButton).span(0.4, -0.45, 0.45, -0.5).moveDZ(0.6);

            add(b);
        }
    }

    /** a window around each represented object */
    public static class PlexWindow extends Box {

        /** controls:
        1. change Plexus's graph arranger for the
        3. adjust node attention
        2. change node depiction

         */
        public PlexWindow(Plexus p, Object node) {
            super(BoxShape.Empty);
        }
    }

    @Override
    protected void start() {

        MemGraph g = new MemGraph();
        final ScalarGraphMap att = new ScalarGraphMap(g);

        g.addGraph(new MeshGraph(4, 4, false));

        final TwitterGrapher tg = new TwitterGrapher(g);
        tg.addPublicTimeline();

        att.randomize(0, 0.2);

        FileGrapher fg = new FileGrapher(g, "/", 1);
        att.set(fg.getRoot(), 0.9);


        //att.randomize(0.0, 0.5);

        //GraphBoxModel initialModel = new Scattering();  //new JungModel(new CircleLayout(g))

        double m = 0.1;

        V3 boundsMax = new V3(w, h, d);
        final ForceDirectedParameters params = new ForceDirectedParameters(boundsMax, 0.01, 0.002, 1.0);
        double updatePeriod = 0.05;
        double interpSpeed = 0.25;
        int substeps = 8;

        GraphBoxModel initialModel = new ScalarMapForceDirecting(att, params, updatePeriod, substeps, interpSpeed) {

            private ForceDirectedParametersEditWindow fdcp;

            @Override
            public void start(GraphBox graphBox) {
                super.start(graphBox);

                fdcp = getGraphBox().add(new ForceDirectedParametersEditWindow(params, font));
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
                return att.value(node);
            }
        };

        final PanningDragRect backRect = add(new PanningDragRect(1.5));
        {
            backRect.setZoomable(false);
            backRect.scale(w*2, h*2).move(0, 0, -2);

            //URL to Background Image
            backRect.color(Color.Black);
            backRect.add(new GridRect(Color.Orange, 8, 8, 0.1));
        }

        AttentionThresholdGraph visGraph = new AttentionThresholdGraph(g, att);
        add(visGraph.newUpdating(attentionUpdatePeriod));

        add(new Plexus(visGraph, att, initialModel, new DefaultGraphBuilder() {           
            @Override public Box newNodeSpace(final Object node) {
                final Box b = new DefaultObjectWindow3D(node);
                return b;
            }
        }));

    }

    public static void main(String[] args) {
        ArdorSpacetime.newWindow(new DemoPlexus());
    }
}
