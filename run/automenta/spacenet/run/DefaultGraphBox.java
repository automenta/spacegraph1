/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.spacenet.run;

import automenta.spacenet.space.geom.graph.arrange.forcedirect.ForceDirectedParametersEditWindow;
import automenta.spacenet.space.geom.ProcessBox;
import automenta.spacenet.space.geom.graph.GraphBoxModel;
import automenta.spacenet.space.geom.graph.GraphBox;
import automenta.spacenet.space.geom.graph.GraphBoxBuilder;
import automenta.spacenet.space.geom.graph.arrange.forcedirect.ForceDirecting;
import automenta.spacenet.space.geom.graph.arrange.forcedirect.ForceDirecting.ForceDirectedParameters;
import automenta.spacenet.var.vector.V3;
import automenta.spacenet.var.graph.MemGraph;
import automenta.spacenet.var.graph.patterns.MeshGraph;

/**
 *
 * @author seh
 */
public class DefaultGraphBox extends ProcessBox {

    static double size = 24.0;
    private ForceDirectedParameters params;

    @Override protected void start() {
        add(new GraphBox(getGraph(), getGraphBuilder(), getGraphArranger()));
        add(new ForceDirectedParametersEditWindow(params, DemoDefaults.font)).move(-1, 0, 0);

    }

    public MemGraph getGraph() {
        return new MeshGraph(4, 4, false);
    }

    public GraphBoxBuilder getGraphBuilder() {
        return new DefaultGraphBuilder();
    }

    public GraphBoxModel getGraphArranger() {
        V3 boundsMax = new V3(size, size, size);
        params = new ForceDirectedParameters(boundsMax, 0.03, 0.07, 1.0);
        double updatePeriod = 0.08;
        double interpSpeed = 0.3;
        int substeps = 4;

        return new ForceDirecting(params, updatePeriod, substeps, interpSpeed);

    }

//    public static void main(String[] args) {
//        ArdorSpacetime.newWindow(new DefaultGraphBox());
//    }
}
