/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package automenta.spacenet.run.old.graph.rdf;

import automenta.spacenet.plugin.rdf.RDFGrapher;
import automenta.spacenet.run.ArdorSpacetime;
import automenta.spacenet.run.old.GraphRect;
import automenta.spacenet.run.old.graph.rdf.swing.RDFEqualizerPanel;
import automenta.spacenet.space.Repeat;
import automenta.spacenet.space.geom.ProcessBox;
import automenta.spacenet.var.graph.MemGraph;
import com.ardor3d.scenegraph.Spatial;
import java.net.URL;
import javax.swing.JFrame;
import org.openrdf.rio.RDFFormat;

/**
 *
 * @author seh
 */
public class DemoRDFEqualizer  extends ProcessBox {
    private RDFGrapher rdf;

    public DemoRDFEqualizer() {
        super();

    }

    public MemGraph getGraph() {
        URL url = getClass().getResource("./data/foaf.rdf");
        this.rdf = new RDFGrapher(url, RDFFormat.RDFXML);
        return rdf.getGraph();
   }

    @Override protected void start() {
        final GraphRect gp = add(new GraphRect(getGraph(), 0.01, 16));
        gp.scale(4);

        JFrame eqWin = new JFrame();
        eqWin.setSize(200, 500);
        eqWin.setVisible(true);
        eqWin.getContentPane().add(new RDFEqualizerPanel(rdf, null));

        gp.getNodeAttention().randomize(0.05, 0.1);
        add(new Repeat(0.7) {
            @Override protected void update(double t, double dt, Spatial s) {
                gp.getNodeAttention().addRandom(0.1, 0.2);
            }
        });
        add(new Repeat(0.1) {
            @Override protected void update(double t, double dt, Spatial s) {
                gp.getNodeAttention().blur(0.2);
                gp.getNodeAttention().mult(0.96);
            }
        });

    }

    public static void main(String[] args) {
        ArdorSpacetime.newWindow(new DemoRDFEqualizer());
    }
}
