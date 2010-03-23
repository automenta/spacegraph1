/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package automenta.spacenet.space.geom.physics;

import com.ardor3d.scenegraph.Node;

/**
 *
 * @author seh
 */
abstract public class Body {

    /** produces a new Spatial node that can be attached into a SpaceGraph */
    abstract public Node newNode();

}
