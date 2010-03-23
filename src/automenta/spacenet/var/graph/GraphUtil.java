/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package automenta.spacenet.var.graph;

import java.util.Collection;
import java.util.Map;

/**
 *
 * @author seh
 */
public class GraphUtil {

    public static <O> MemGraph<O, String> fromList(Collection<O> l, String edgePrefix, boolean loop) {
        MemGraph<O,String> g = new MemGraph();

        O prev = null;
        O first = null;
        int i = 0;
        for (O o : l) {
            if (first == null)
                first = o;

            g.addNode(o);
            if (prev!=null) {
                g.addEdge(edgePrefix + "." + Integer.toString(i++), prev, o);
            }
            prev = o;
        }
        if (loop) {
            if ( first != prev)
                g.addEdge(edgePrefix + ".top", prev, first);
        }

        return g;
    }

    public static <N,V> MemGraph<N, V> fromMap(Map<V,N> l, String edgePrefix, boolean loop) {
        return null;
    }

}
