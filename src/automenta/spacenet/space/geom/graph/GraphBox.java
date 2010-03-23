package automenta.spacenet.space.geom.graph;

import automenta.spacenet.space.Repeat;
import automenta.spacenet.space.Space;
import automenta.spacenet.space.geom.Box;
import automenta.spacenet.var.graph.MemGraph;
import automenta.spacenet.var.graph.IfGraphChanges;
import com.ardor3d.scenegraph.Spatial;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author seh
 */
public class GraphBox<V, E> extends Box implements IfGraphChanges<V, E> {

    private MemGraph<V, E> graph;
    private GraphBoxModel<V, E> model;
    private GraphBoxBuilder<V, E> builder;
    private Map<V, Box> nodeBox = new HashMap();
    private Map<E, Space> edgeSpace = new HashMap();

    public GraphBox(MemGraph<V, E> graph, GraphBoxBuilder<V, E> initialBuilder, GraphBoxModel<V, E> initialModel) {
        super(BoxShape.Empty);
        this.graph = graph;
        this.builder = initialBuilder;

        setModel(initialModel);
    }

    @Override
    protected void afterAttached(Spatial parent) {
        super.afterAttached(parent);

        getGraph().add(this);
    }

    @Override
    protected void beforeDetached(Spatial previousParent) {
        this.model.stop();

        getGraph().remove(this);

        model.stop();
        model = null;

        super.beforeDetached(previousParent);
    }

    public MemGraph<V, E> getGraph() {
        return graph;
    }

    public GraphBoxBuilder<V, E> getBuilder() {
        return builder;
    }

    public GraphBoxModel<V, E> getModel() {
        return model;
    }

    public synchronized void setModel(final GraphBoxModel<V, E> next) {
        GraphBoxModel current = getModel();
        if (current == next) {
            return;
        }
        //synchronized (current) {
        if (current != null) {
            current.stop();
            if (current instanceof Repeat) {
                remove((Repeat) current);
            }
        }

        this.model = next;

        next.start(this);

        for (V n : getGraph().getNodes()) {
            //updateNode(n);
            nodeAdded(getGraph(), n);
            //model.addedNode(n, vertexBox.get(n));
            }
        for (E e : getGraph().getEdges()) {
            edgeAdded(getGraph(), e);
//                List<V> iv = graph.getIncidentVertices(e);
//                Box aBox = getVertexBox(iv.get(0));
//                Box bBox = getVertexBox(iv.get(1));
//                model.addedEdge(e, edgeSpace.get(e), aBox, bBox);
            }

        if (next instanceof Repeat) {
            add((Repeat) next);
        }
        //}
    }

    protected void addNode(V vertex, Box b) {
        add(b);
    }

    @Override public void nodeAdded(MemGraph<V, E> graph, V vertex) {
        Box b = nodeBox.get(vertex);
        if (b == null) {
            b = getBuilder().newNodeSpace(vertex);
            nodeBox.put(vertex, b);
            addNode(vertex, b);
        }

        getModel().addedNode(vertex, b);
    }

    @Override public void nodeRemoved(MemGraph<V, E> graph, V vertex) {
        getModel().removedNode(vertex);
        remove(getVertexBox(vertex));
        nodeBox.remove(vertex);
    }

    @Override public void edgeAdded(MemGraph<V, E> graph, E edge) {
        List<V> iv = graph.getIncidentVertices(edge);
//        if (iv.size() != 2) {
//            System.err.println(graph + " has malformed edge " + edge);
//            return;
//        }

        Box aBox = getVertexBox(iv.get(0));
        Box bBox = getVertexBox(iv.get(1));

        Space s = edgeSpace.get(edge);
        if (s == null) {
            if (aBox != null) {
                if (bBox != null) {
                    s = getBuilder().newEdgeSpace(edge, aBox, bBox);
                    edgeSpace.put(edge, s);
                    add(s);
                }
            }
        }

        getModel().addedEdge(edge, s, aBox, bBox);
    }

    public Space getEdgeSpace(E e) {
        return edgeSpace.get(e);
    }

    public Box getVertexBox(V v) {
        return nodeBox.get(v);
    }

    @Override public void edgeRemoved(MemGraph<V, E> graph, E edge) {
        getModel().removedEdge(edge);
        remove(getEdgeSpace(edge));
        edgeSpace.remove(edge);
    }

    public Box getNodeBox(Object o) {
        return nodeBox.get(o);
    }

    public Iterable<Box> getNodeBoxes() {
        return nodeBox.values();
    }

    public Iterable<Space> getEdgeSpaces() {
        return edgeSpace.values();
    }
}
