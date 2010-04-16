/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.spacenet.run.old.graph.rdf;

import automenta.spacenet.plugin.rdf.RDFGrapher;
import automenta.spacenet.run.ArdorSpacetime;
import automenta.spacenet.run.old.DefaultGraphBox;
import automenta.spacenet.space.geom.graph.arrange.forcedirect.ForceDirectedParametersEditWindow;
import automenta.spacenet.var.graph.MemGraph;
import java.net.URL;
import org.openrdf.rio.RDFFormat;

/**
 *
 * @author seh
 */
public class DemoRDF extends DefaultGraphBox {

    private final MemGraph rdfGraph;
    private final RDFGrapher g;
    public static final URL dataURL = DemoRDF.class.getResource("./data/rdfdata.rdf");

    public DemoRDF() {
        super();


        g = new RDFGrapher(dataURL, RDFFormat.RDFXML);


        this.rdfGraph = g.getGraph();
    }

    @Override
    public MemGraph getGraph() {
        return rdfGraph;
    }

    @Override
    protected void start() {
        super.start();
    }

    public static void main(String[] args) {
        ArdorSpacetime.newWindow(new DemoRDF());
    }
}
