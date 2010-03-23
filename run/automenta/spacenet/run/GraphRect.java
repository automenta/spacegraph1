/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.spacenet.run;

import automenta.spacenet.var.graph.map.ScalarGraphMap;
import automenta.spacenet.space.geom.graph.arrange.forcedirect.ForceDirectedParametersEditWindow;
import automenta.spacenet.space.Space;
import automenta.spacenet.space.geom.Box;
import automenta.spacenet.space.geom.Line3D;
import automenta.spacenet.space.geom.Rect;
import automenta.spacenet.space.geom.Rect.RectShape;
import automenta.spacenet.space.geom.graph.GraphBox;
import automenta.spacenet.space.geom.graph.GraphBoxBuilder;
import automenta.spacenet.space.geom.graph.arrange.forcedirect.ForceDirecting;
import automenta.spacenet.space.geom.graph.arrange.forcedirect.ForceDirecting.ForceDirectedParameters;
import automenta.spacenet.space.widget.PanningDragRect;
import automenta.spacenet.var.vector.V3;
import automenta.spacenet.var.graph.MemGraph;
import com.ardor3d.math.Vector3;

/**
 * displays a 2D graph layout in a panel with controls
 */
public class GraphRect extends Rect {

    private final ScalarGraphMap nodeAttention;
    private final ForceDirectedParameters params;

    public GraphRect(final MemGraph graph, double bias, double size) {
        super(RectShape.Empty);

        V3 boundsMax = new V3(size, size, 0.05);
        params = new ForceDirectedParameters(boundsMax, 0.03, 0.001, 0.25);
        double updatePeriod = 0.15;
        double interpPeriod = 0.0;
        double interpSpeed = 0.25;

        int substeps = 8;
        
        nodeAttention = new ScalarGraphMap(graph, bias);

        GraphBoxBuilder builder = new DefaultGraphBuilder();
        ForceDirecting arranger = new ForceDirecting(params, updatePeriod, substeps, interpSpeed) {

            @Override
            protected void updateEdge(Object e, Space s) {
                super.updateEdge(e, s);


                Object fromNode = graph.getIncidentVertices(e).get(0);
                Object toNode = graph.getIncidentVertices(e).get(1);

                double rad = (nodeAttention.value(fromNode) + nodeAttention.value(toNode)) / 2.0;
                rad *= 0.1;

                Line3D l = (Line3D) s;
                l.getRadius().set(rad);
            }

            @Override
            protected void updateNode(Object n, Box nBox, Vector3 nextSize) {
                super.updateNode(n, nBox, nextSize);

                double x = nodeAttention.value(n);
                double y = x;
                double z = x;
                nextSize.set(x, y, z);
            }
        };

        GraphBox gb = add(new GraphBox(graph, builder, arranger));
        gb.moveDZ(0.1);

        add(new PanningDragRect().scale(size, size).moveDZ(-0.1));
        
        add(new ForceDirectedParametersEditWindow(params, DemoDefaults.font)).move(-1, 0, 0);

    }

    public ScalarGraphMap getNodeAttention() {
        return nodeAttention;
    }
}
