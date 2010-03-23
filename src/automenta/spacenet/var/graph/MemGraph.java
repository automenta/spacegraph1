/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package automenta.spacenet.var.graph;

import edu.uci.ics.jung.graph.Hypergraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author seh
 */
public class MemGraph<V,H> implements BufferedGraph<V, H>, Hypergraph<V,H>, edu.uci.ics.jung.graph.Graph<V,H> {

    protected final Map<V, Set<H>> nodes; // Map of vertices to incident hyperedge sets
    protected final Map<H, List<V>> edges;    // Map of hyperedges to incident vertex sets
    protected final Map<H, EdgeType> edgeTypes;

    private final List<IfGraphChanges<V,H>> ifChanges = new LinkedList();
    
    /** creates empty graph   */
    public MemGraph()  {
    	super();
        nodes = new ConcurrentHashMap();
        edges = new ConcurrentHashMap();
        edgeTypes = new ConcurrentHashMap();
    }

    /** creates graph from a JUNG hypergraph   */
    public MemGraph(Hypergraph<V,H> jungGraph) {
        this();
        for (V v : jungGraph.getVertices()) {
            addNode(v);
        }
        for (H h : jungGraph.getEdges()) {
            EdgeType type = jungGraph.getEdgeType(h);
            Collection<V> eNodes = jungGraph.getIncidentVertices(h);
            addEdge(h, type, eNodes);
        }
    }

	public void clear() {
        ArrayList<V> nodeList = new ArrayList<V>(getNodes());
        ArrayList<H> edgeList = new ArrayList<H>(getEdges());

        for (H h : edgeList) {
            removeEdge(h);
        }
        for (V v : nodeList) {
            removeNode(v);
        }
        
//		nodes.clear();
//		edges.clear();
        edgeTypes.clear();
	}


    public H addEdge(H h, List<V> v) {
    	return addEdge(h, EdgeType.DIRECTED, v);
    }


    /**
     * Adds <code>hyperedge</code> to this graph and connects them to the vertex collection <code>to_attach</code>.
     * Any vertices in <code>to_attach</code> that appear more than once will only appear once in the
     * incident vertex collection for <code>hyperedge</code>, that is, duplicates will be ignored.
     *
     * @see Hypergraph#addEdge(Object, Collection)
     */
    public H addEdge(H hyperedge, EdgeType e, Collection<V> vList) {
        if (hyperedge == null)
            throw new IllegalArgumentException("input hyperedge may not be null");

//        Set<V> new_endpoints = new HashSet<V>(to_attach);

        //TODO check for duplicate edge
        if (edges.containsKey(hyperedge))
        {
            List<V> attached = edges.get(hyperedge);
            if (!attached.equals(vList)) {
                throw new IllegalArgumentException("Edge " + hyperedge +
                        " exists in this graph with endpoints " + attached);
            }
            else
                return null;
        }

        edges.put(hyperedge, new ArrayList( vList ));

        for (V v : vList) {
            // add v if it's not already in the graph
            addNode(v);

            // associate v with hyperedge
            this.nodes.get(v).add(hyperedge);
        }

        edgeTypes.put(hyperedge, e);

        for (IfGraphChanges ic : ifChanges) {
            ic.edgeAdded(this, hyperedge);
        }
        return hyperedge;
    }


    /**
     * @see Hypergraph#getEdgeType(Object)
     */
    @Override public EdgeType getEdgeType(H edge)    {
        if (containsEdge(edge))
            return edgeTypes.get(edge);
        else
            return null;
    }

    public boolean containsNode(V node) {
    	return nodes.keySet().contains(node);
    }

    public boolean containsEdge(H edge) {
    	return edges.keySet().contains(edge);
    }

    @Override public Collection<H> getEdges() {
        return edges.keySet();
    }

    @Override public Collection<V> getNodes()  {
        return nodes.keySet();
    }

    public int getEdgeCount()
    {
        return edges.size();
    }

    public int getNodeCount()
    {
        return nodes.size();
    }

    @Override public Collection<V> getNeighbors(V node)    {
        if (!containsNode(node))
            return null;

        Set<V> neighbors = new HashSet<V>();
        for (H hyperedge : nodes.get(node)) {
        	neighbors.addAll(edges.get(hyperedge));
        }
        return neighbors;
    }

    @Override public Collection<H> getIncidentEdges(V node)    {
        return nodes.get(node);
    }

    @Override public List<V> getIncidentVertices(H edge) {
        return edges.get(edge);
    }

    public H findEdge(V v1, V v2)  {
        if (!containsNode(v1) || !containsNode(v2))
            return null;

        for (H h : getIncidentEdges(v1))
        {
            if (isIncident(v2, h))
                return h;
        }
        return null;
    }

    public Collection<H> findEdgeSet(V v1, V v2)    {
        if (!containsNode(v1) || !containsNode(v2))
            return null;

        Collection<H> edges = new ArrayList<H>();
        for (H h : getIncidentEdges(v1))
        {
            if (isIncident(v2, h))
                edges.add(h);
        }
        return Collections.unmodifiableCollection(edges);
    }

    @Override public V addNode(V node)    {
    	if(node == null)
    	    throw new IllegalArgumentException("cannot add a null vertex");
        if (containsNode(node))
            return null;
        nodes.put(node, new HashSet<H>());

        for (IfGraphChanges ic : ifChanges) {
            ic.nodeAdded(this, node);
        }

        return node;
    }

    @Override public boolean removeNode(V node)    {
        if (!containsNode(node))
            return false;
        for (H hyperedge : nodes.get(node)) {
            edges.get(hyperedge).remove(node);
        }
        nodes.remove(node);

        for (IfGraphChanges ic : ifChanges) {
            ic.nodeRemoved(this, node);
        }

        return true;
    }

    @Override public boolean removeEdge(H hyperedge)    {
        if (!containsEdge(hyperedge))
            return false;
        for (V vertex : edges.get(hyperedge))
        {
            nodes.get(vertex).remove(hyperedge);
        }
        edges.remove(hyperedge);
        edgeTypes.remove(hyperedge);

        for (IfGraphChanges ic : ifChanges) {
            ic.edgeRemoved(this, hyperedge);
        }

        return true;
    }

    public boolean isNeighbor(V v1, V v2)   {
        if (!containsNode(v1) || !containsNode(v2))
            return false;

        if (nodes.get(v2).isEmpty())
            return false;
        for (H hyperedge : nodes.get(v1))
        {
            if (edges.get(hyperedge).contains(v2))
                return true;
        }
        return false;
    }

    public boolean isIncident(V vertex, H edge)    {
        if (!containsNode(vertex) || !containsEdge(edge))
            return false;

        return nodes.get(vertex).contains(edge);
    }

    public int degree(V vertex)    {
        if (!containsNode(vertex))
            return 0;

        return nodes.get(vertex).size();
    }

    public int getNeighborCount(V vertex)    {
        if (!containsNode(vertex))
            return 0;

        return getNeighbors(vertex).size();
    }

    public int getIncidentCount(H edge)    {
        if (!containsEdge(edge))
            return 0;

        return edges.get(edge).size();
    }

    public int getEdgeCount(EdgeType edge_type)    {
        if (edge_type == EdgeType.UNDIRECTED)
            return edges.size();
        return 0;
    }

    @Override public Collection<H> getEdges(EdgeType edge_type)    {
        if (edge_type == EdgeType.UNDIRECTED)
            return edges.keySet();
        return null;
    }

	public EdgeType getDefaultEdgeType()	{
		return EdgeType.UNDIRECTED;
	}

	@Override public Collection<H> getInEdges(V vertex)   {
		return getIncidentEdges(vertex);
	}

	public Collection<H> getOutEdges(V vertex) {
		return getIncidentEdges(vertex);
	}

	public int inDegree(V vertex)
	{
		return degree(vertex);
	}

	public int outDegree(V vertex)
	{
		return degree(vertex);
	}

	public V getDest(H h)	{
		List<V> vc = edges.get(h);
        return vc.get(vc.size()-1);
	}

	public V getSource(H h)	{
		List<V> vc = edges.get(h);
        return vc.get(0);
	}

	public Collection<V> getPredecessors(V vertex)
	{
		return getNeighbors(vertex);
	}

	public Collection<V> getSuccessors(V vertex)
	{
		return getNeighbors(vertex);
	}

    @Override public H addEdge(H edge, EdgeType edgeType, V... vList) {
        addEdge(edge, edgeType, Arrays.asList(vList));
        return edge;
    }
    
    public void addEdge(H edge, V... vList) {
        addEdge(edge, EdgeType.DIRECTED, Arrays.asList(vList));
    }

    @Override public void add(IfGraphChanges ic) {
        ifChanges.add(ic);
        
        notifyAll(ic);
    }

    @Override public void remove(IfGraphChanges<V,H> ic) {
        ifChanges.remove(ic);
    }

    /** notify existing nodes & edges*/
    private void notifyAll(IfGraphChanges<V,H> ic) {
        for (V v : getNodes()) {
            ic.nodeAdded(this, v);
        }
        for (H h : getEdges()) {
            ic.edgeAdded(this, h);
        }
    }

    @Override
    public Iterator<H> iterateEdges() {
        return getEdges().iterator();
    }

    @Override
    public Iterator<V> iterateNodes() {
        return getNodes().iterator();
    }

    @Override
    public String toString() {
        return "n{" + getNodes() + "}, e{" + getEdges() + "}";
    }

    @Deprecated @Override
    public Collection<V> getVertices() {
        return getNodes();
    }

    @Deprecated @Override
    public boolean containsVertex(V v) {
        return containsNode(v);
    }

    @Deprecated @Override
    public int getVertexCount() {
        return getNodeCount();
    }

    @Deprecated @Override
    public boolean addVertex(V arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Deprecated @Override
    public boolean addEdge(H arg0, Collection<? extends V> arg1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Deprecated @Override
    public boolean addEdge(H arg0, Collection<? extends V> arg1, EdgeType arg2) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean removeVertex(V arg0) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isPredecessor(V b, V a) {
        return getPredecessors(a).contains(b);
    }

    @Override
    public boolean isSuccessor(V b, V a) {
        return getSuccessors(a).contains(b);
    }

    @Override
    public int getPredecessorCount(V a) {
        return getPredecessors(a).size();
    }

    @Override
    public int getSuccessorCount(V a) {
        return getSuccessors(a).size();
    }

    @Override
    public boolean isSource(V v, H h) {
        return getSource(h) == v;
    }

    @Override
    public boolean isDest(V v, H h) {
        return getDest(h) == v;
    }

    @Override
    public boolean addEdge(H h, V a, V b) {
        LinkedList<V> ll = new LinkedList<V>();
        ll.add(a);
        ll.add(b);

        this.addEdge(h, ll);
        return true;
    }

    @Deprecated @Override
    public boolean addEdge(H h, V a, V b, EdgeType t) {
        LinkedList<V> ll = new LinkedList<V>();
        ll.add(a);
        ll.add(b);

        this.addEdge(h, t, ll);
        return true;
    }

    @Override
    public Pair<V> getEndpoints(H h) {
        return new Pair<V>(getSource(h), getDest(h));
    }

    @Override
    public V getOpposite(V v, H h) {
        if (getSource(h) == v)
            return getDest(h);
        else
            return getSource(h);
    }

    public void addGraph(MemGraph<V,H> g) {
        for (V v : g.getNodes()) {
            addNode(v);
        }
        for (H h : g.getEdges()) {
            //TODO this assumes the 'g' is not hypergraph.. remove this assumption/limitation
            addEdge(h, g.getEndpoints(h).getFirst(), g.getEndpoints(h).getSecond());
        }
    }




}
