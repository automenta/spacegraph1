package automenta.spacenet.run.graph.twitter;

import automenta.spacenet.plugin.comm.twitter.TwitterGrapher;
import automenta.spacenet.run.ArdorSpacetime;
import automenta.spacenet.run.DefaultGraphBuilder;
import automenta.spacenet.space.geom.ProcessBox;
import automenta.spacenet.space.geom.graph.GraphBoxModel;
import automenta.spacenet.space.geom.graph.GraphBox;
import automenta.spacenet.space.geom.graph.GraphBoxBuilder;
import automenta.spacenet.space.geom.graph.arrange.forcedirect.ForceDirecting;
import automenta.spacenet.space.geom.graph.arrange.forcedirect.ForceDirecting.ForceDirectedParameters;
import automenta.spacenet.space.geom.graph.build.BoxLineBuilder;
import automenta.spacenet.var.vector.V3;
import automenta.spacenet.var.graph.MemGraph;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author seh
 */
public class TwitterSpace extends ProcessBox {

    public TwitterSpace() {
        super();
    }

    
    public GraphBoxBuilder newGraphBuilder() {
        return new DefaultGraphBuilder();
    }

    public GraphBoxModel newArranger() {
        //return new ScatterArranger();

        V3 boundsMax = new V3(15, 15, 15);
        ForceDirectedParameters params = new ForceDirectedParameters(boundsMax, 0.03, 0.04, 2.0);
        double updatePeriod = 0.08;
        int substeps = 8;
        double interpSpeed = 0.3;

        return new ForceDirecting(params, updatePeriod, substeps, interpSpeed);
    }

    protected void start() {
        MemGraph g = new MemGraph();
        TwitterGrapher tg = new TwitterGrapher(g);


        add(new GraphBox(g, newGraphBuilder(), newArranger()));

        tg.addPublicTimeline();
        tg.addProfile("sseehh");
    }

    public static void main(String[] args) {
        ArdorSpacetime.newWindow(new TwitterSpace());
    }
}
