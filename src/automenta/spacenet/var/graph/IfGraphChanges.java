package automenta.spacenet.var.graph;

public interface IfGraphChanges<N, E> {

    public void nodeAdded(MemGraph<N, E> graph, N vertex);

    public void nodeRemoved(MemGraph<N, E> graph, N vertex);

    public void edgeAdded(MemGraph<N, E> graph, E edge);

    public void edgeRemoved(MemGraph<N, E> graph, E edge);
    
}
