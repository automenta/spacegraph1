/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.spacenet.run.old.story;

import automenta.spacenet.plugin.comm.twitter.TwitterGrapher;
import automenta.spacenet.plugin.file.FileGrapher;
import automenta.spacenet.plugin.rdf.RDFGrapher;
import automenta.spacenet.run.old.graph.rdf.DemoRDF;
import automenta.spacenet.var.action.Actions;
import automenta.spacenet.var.graph.MemGraph;
import automenta.spacenet.var.graph.map.ScalarGraphMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;
import org.openrdf.rio.RDFFormat;

/**
 * visualizes & emulates a "short term memory" by repeatedly REMINDING about a concern/event by adding to a graph with energy that fades.
 */
public class DemoNowPanel {

    static {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            Logger.getLogger(DemoNowPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private final MemGraph graph;
    private final ScalarGraphMap att;

    public DemoNowPanel() {
        //graph = new MemGraph();
        
        graph = new RDFGrapher(DemoRDF.dataURL, RDFFormat.RDFXML).getGraph();

        att = new ScalarGraphMap(graph);

        //new SwingWindow(new AttentionList(att, new DefaultPanelizer()), 400, 400);
        new NowWindow(att, new Actions());

        final TwitterGrapher tg = new TwitterGrapher(graph);
        tg.addPublicTimeline();
        tg.addProfile("kurzweilainews");

        FileGrapher fg = new FileGrapher(graph, "/", 1);
        
    }

    public static void main(String[] args) {
        new DemoNowPanel();
    }
}
