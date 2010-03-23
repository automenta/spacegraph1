/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.spacenet.var.graph;

import edu.uci.ics.jung.graph.util.EdgeType;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author seh
 */
public interface BufferedGraph<V, H> extends Graph<V, H> {

    public Collection<H> getEdges();
    public Collection<H> getEdges(EdgeType edge_type);
    
    public Collection<V> getNodes();

    public Collection<V> getNeighbors(V node);

    public Collection<H> getIncidentEdges(V node);
    
    public Collection<H> getInEdges(V vertex);
	public Collection<H> getOutEdges(V vertex);
   	
    public Collection<V> getPredecessors(V vertex);
    public Collection<V> getSuccessors(V vertex);


    public List<V> getIncidentVertices(H edge);
}
