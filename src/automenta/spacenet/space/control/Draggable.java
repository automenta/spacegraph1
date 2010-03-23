/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package automenta.spacenet.space.control;

import com.ardor3d.math.Ray3;

/**
 *
 * @author seh
 */
public interface Draggable extends Tangible {

    public void onDragStart(Ray3 rayDragStart);
    public void onDragging(Ray3 rayDrag);
    public void onDragStop(Ray3 rayDragStop);

}
