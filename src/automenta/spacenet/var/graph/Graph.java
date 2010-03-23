/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package automenta.spacenet.var.graph;


import edu.uci.ics.jung.graph.util.EdgeType;
import java.util.Iterator;

/**
 * combination of directed graph and hypergraph.
 * modified from JUNG graph library
 * @author seh
 */
public interface Graph<V, H> {

    public V addNode(V node);
    public boolean removeNode(V node);
    
    public H addEdge(H edge, EdgeType edgeType, V... vList);

    public boolean removeEdge(H hyperedge);
        
    public EdgeType getEdgeType(H edge);

    public void add(IfGraphChanges ic);
    public void remove(IfGraphChanges<V,H> ic);

    public Iterator<H> iterateEdges();
    public Iterator<V> iterateNodes();

    //TODO add time-aware visitors for nodes and edges

}
