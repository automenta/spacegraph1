/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.spacenet.space.geom.graph.arrange;

import automenta.spacenet.space.Space;
import automenta.spacenet.space.geom.Box;
import automenta.spacenet.space.geom.graph.GraphBox;
import automenta.spacenet.space.geom.graph.GraphBoxModel;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * arranges a list of objects parametrically (ex: a curve or line)
 */
public abstract class CurveListing<N, E> implements GraphBoxModel<N, E> {

    private GraphBox<N, E> graphBox;
    private List<Object> objects = new LinkedList();
    private final boolean includeNodes;
    private final boolean includeEdges;

    public CurveListing(boolean includeNodes, boolean includeEdges) {
        super();
        this.includeNodes = includeNodes;
        this.includeEdges = includeEdges;
    }

    @Override
    public void start(GraphBox<N, E> graphBox) {
        this.graphBox = graphBox;

    }

    @Override
    public void stop() {
        objects.clear();
    }

    @Override
    public void addedNode(N v, Box b) {
        if (includeNodes) {
            objects.add(v);
            refresh();
        }
    }

    @Override
    public void removedNode(N v) {
        if (includeNodes) {
            objects.remove(v);
            refresh();
        }
    }

    @Override
    public void addedEdge(E e, Space s, Box from, Box to) {
        if (includeEdges) {
            objects.add(e);
            refresh();
        }
    }

    @Override
    public void removedEdge(E e) {
        if (includeEdges) {
            objects.remove(e);
            refresh();
        }
    }

    public List<Object> getObjects() {
        return objects;
    }

    public GraphBox<N, E> getBox() {
        return graphBox;
    } 

    
    abstract protected void refresh();
}
