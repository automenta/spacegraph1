/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package automenta.spacenet.space.geom.graph;

import automenta.spacenet.space.Space;
import automenta.spacenet.space.geom.Box;



public interface GraphBoxBuilder<V, E> {
    
    public Space newEdgeSpace(E edge, Box pa, Box pb);
    public Box newNodeSpace(V vertex);
    
}
