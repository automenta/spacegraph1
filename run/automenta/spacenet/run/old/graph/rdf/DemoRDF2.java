/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.spacenet.run.old.graph.rdf;

import automenta.spacenet.plugin.comm.twitter.TwitterGrapher;
import automenta.spacenet.run.ArdorSpacetime;
import automenta.spacenet.run.old.GraphRect;
import automenta.spacenet.space.Repeat;
import automenta.spacenet.space.geom.ProcessBox;
import automenta.spacenet.var.graph.MemGraph;
import com.ardor3d.scenegraph.Spatial;

/**
 *
 * @author seh
 */
public class DemoRDF2 extends ProcessBox {

    public DemoRDF2() {

        super();



    }

    public MemGraph getGraph() {
//        URL url = getClass().getResource("./data/rdfdata.rdf");
//        return new RDFGrapher(url, RDFFormat.RDFXML).getGraph();

        MemGraph g = new MemGraph();
        TwitterGrapher tg = new TwitterGrapher(g);
        tg.addPublicTimeline();
        tg.addProfile("sseehh");
        return g;

//        return new MeshGraph(3, 3, false);
    }

    @Override protected void start() {
        final GraphRect gp = add(new GraphRect(getGraph(), 0.01, 16));

        
        gp.getNodeAttention().randomize(0.05, 0.1);
        add(new Repeat(0.7) {
            @Override protected void update(double t, double dt, Spatial s) {
                gp.getNodeAttention().addRandom(0.1, 0.2);
            }
        });
        add(new Repeat(0.1) {
            @Override protected void update(double t, double dt, Spatial s) {
                gp.getNodeAttention().blur(0.1);
                    gp.getNodeAttention().mult(0.96, 0.1);
            }
        });

    }

    public static void main(String[] args) {
        ArdorSpacetime.newWindow(new DemoRDF2());
    }
}
