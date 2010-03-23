/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package automenta.spacenet.test;

import automenta.spacenet.var.graph.MemGraph;
import automenta.spacenet.var.*;
import edu.uci.ics.jung.graph.util.EdgeType;
import junit.awtui.TestRunner;
import junit.framework.TestCase;

/**
 *
 * @author seh
 */
public class TestGraph extends TestCase {

    public void testGraphEdit() {

        MemGraph g = new MemGraph();
        g.addNode("x");
        g.addNode("y");
        g.addEdge("xy", EdgeType.DIRECTED, "x", "y");

        assertEquals(2, g.getNodeCount());
        assertEquals(1, g.getEdgeCount());
        assertEquals(EdgeType.DIRECTED, g.getEdgeType("xy"));
    }

//    public void testMultiEdge() {
//        MemGraph g = new MemGraph();
//        g.addNode("x");
//        g.addNode("y");
//        g.addNode("z");
//        String e = "e";
//        g.addEdge(e, "x", "y");
//        g.addEdge(e, "x", "z");
//        System.out.println(g);
//    }

    public static void main(String[] args) {
        new TestRunner().run(TestGraph.class);
    }
}
