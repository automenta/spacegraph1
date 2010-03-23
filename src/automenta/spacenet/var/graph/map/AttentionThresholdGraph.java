/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.spacenet.var.graph.map;

import automenta.spacenet.space.Repeat;
import automenta.spacenet.var.graph.IfGraphChanges;
import automenta.spacenet.var.graph.MemGraph;
import automenta.spacenet.var.map.MapVar;
import automenta.spacenet.var.map.MapVar.IfMapChanges;
import automenta.spacenet.var.scalar.DoubleVar;
import com.ardor3d.scenegraph.Spatial;
import java.util.ArrayList;
import java.util.List;

/**
 * contains the contents of a super-graph if the attention values (via a ScalarGraphMap) are above certain adjustable threshold
 */
public class AttentionThresholdGraph<N, E> extends MemGraph<N, E> implements IfGraphChanges<N,E>, IfMapChanges<N, Double> {

    private final MemGraph<N, E> superGraph;
    private final ScalarGraphMap<N, E> map;
    private final DoubleVar thresh = new DoubleVar(0.1);
    private boolean superChanged;
    private boolean mapChanged;

    public AttentionThresholdGraph(MemGraph<N, E> graph, ScalarGraphMap<N, E> attention) {
        super();

        this.superGraph = graph;
        superGraph.add(this);

        this.map = attention;
        map.add(this);

        superChanged = true;
        mapChanged = true;
    }

    public void stop() {
        superGraph.remove(this);
    }

    public synchronized void updateAttention() {
        if ((!superChanged) && (!mapChanged)) {
            return;
        }
        superChanged = false;
        mapChanged = false;

        //1. for existing nodes, remove if now in-visible
        for (N n : getNodes()) {
            if ((!contains(n)) || (!superGraph.containsNode(n))) {
                //TODO should this removeEdge be part of MemGraph.removeNode(n) ?
                List<E> incidentedges = new ArrayList(getIncidentEdges(n));
                for (E e : incidentedges) {
                    removeEdge(e);
                }

                removeNode(n);

            }
        }

        //2. for non-existing nodes, add if now visible
        for (N n : superGraph.getNodes()) {
            if (contains(n)) {
                addNode(n);
            }
        }

        //3. remove invisible edges
        for (E e : getEdges()) {
            if (!superGraph.containsEdge(e))
                removeEdge(e);
        }

        //3. add any edges that involve already added nodes
        for (E e : superGraph.getEdges()) {
            if (!containsEdge(e)) {
                List<N> iv = superGraph.getIncidentVertices(e);
                boolean allNodesContained = true;
                for (N n : iv) {
                    if (!containsNode(n)) {
                        allNodesContained = false;
                        break;
                    }
                }
                if (allNodesContained) {
                    addEdge(e, superGraph.getEdgeType(e), iv);
                }
            }
        }

    }

    public boolean contains(N n) {
        double v = map.value(n);
        if (v < getThresh().d()) {
            return false;
        }
        return true;
    }

    public DoubleVar getThresh() {
        return thresh;
    }

    public Repeat newUpdating(double updatePeriod) {
        return new Repeat(updatePeriod) {

            @Override protected void update(double t, double dt, Spatial parent) {
                updateAttention();
            }
        };
    }

    @Override
    public void nodeAdded(MemGraph<N, E> graph, N vertex) {
        superChanged = true;
    }

    @Override
    public void nodeRemoved(MemGraph<N, E> graph, N vertex) {
        superChanged = true;
    }

    @Override
    public void edgeAdded(MemGraph<N, E> graph, E edge) {
        superChanged = true;
    }

    @Override
    public void edgeRemoved(MemGraph<N, E> graph, E edge) {
        superChanged = true;
    }

    @Override
    public void onMapChanged(MapVar<N, Double> map) {
        mapChanged = true;
    }

}
