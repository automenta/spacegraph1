package automenta.spacenet.space.geom.graph;

import automenta.spacenet.space.Space;
import automenta.spacenet.space.geom.Box;


public interface GraphBoxModel<V,E>  {

    public void addedNode(V v, Box b);
    public void removedNode(V v);

    public void addedEdge(E e, Space s, Box from, Box to);
    public void removedEdge(E e);

    public void start(GraphBox<V,E> graphBox);
    public void stop();

}
