/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package automenta.spacenet.run.old.graph.xml;

import automenta.spacenet.plugin.xml.HTMLGrapher;
import automenta.spacenet.var.graph.MemGraph;

/**
 *
 * @author seh
 */
public class DemoHTML {

    public static void main(String[] args) {
        MemGraph graph = new MemGraph();
        new HTMLGrapher(graph, "http://automenta.com");

        System.out.println();
        System.out.println(graph);
        System.out.println("edges: " + graph.getEdgeCount() + ", nodes: " + graph.getNodeCount() );

    }

}
