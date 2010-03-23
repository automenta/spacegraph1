/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package automenta.spacenet.run.graph.ubigraph;

import automenta.spacenet.run.ArdorSpacetime;
import automenta.spacenet.run.DefaultGraphBox;
import automenta.spacenet.run.DefaultGraphBuilder;
import automenta.spacenet.space.geom.ProcessBox;
import automenta.spacenet.space.geom.graph.GraphBox;
import automenta.spacenet.var.graph.MemGraph;

/**
 * emulates UbiGraph functionality
 * 
 * @author seh
 * @see http://ubietylab.net/ubigraph/content/Docs/index.html
 */
public class UbigraphClient extends ProcessBox {

    private MemGraph graph = new MemGraph();
    
    private int nextVertexID = 0;
    private int nextEdgeID = 0;

    public static class NodeProperties {
        //visible (boolean)
        //color
        //shape: none, cone, cube, dodecahedron, icosahedron, octahedron, sphere, octahedron, torus
        //shapedetail:  the level of detail with which the shape should be rendered (only relevant for some shapes)
        //size (1.0=normal)
        //label
        //labelColor
        //labelFont
        //labelSize (1.0 = normal)        
        //left click callback
        //right click callback
    }

    public static class EdgeProperties {
        //visible (boolean)
        //color
        //strength (How much the edge will pull its vertices together.)
        //stroke  (one of "solid", "dashed", "dotted", or "none".)
        //width (1.0 = normal)
        //label
        //labelColor
        //labelfont
        //labelSize
        //orientation (V3 of attempted alignment direction, or null)
        //spline (bool)
        //strainDrawn
        //arrowDrawn (true/false)
        //arrowPosition (On an edge (x,y), if arrow_position=1.0 then the arrowhead is drawn so that the tip is touching y. If arrow_position=0.0 the beginning of the arrowhead is touching x. If arrow_position=0.5 the arrowhead is midway between the two vertices.)
        //arrowRadius
        //arrowLength
        //arrowReverse (bool)

    }


    @Override protected void start() {
        

        //add(new GraphBox(getGraph(), new DemoGraphBuilder(), getGraphArranger()));
    }

    public MemGraph getGraph() {
        return graph;
    }

    public int newVertex() {
        int i = nextVertexID++;
        getGraph().addNode(i);
        return i;
    }

    void newEdge(int a, int b) {
        int i = nextEdgeID++;
        getGraph().addEdge(i, a, b);
    }

    void newWindow() {
        ArdorSpacetime.newWindow(this);
    }

}
