/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package automenta.spacenet.space.control;

import com.ardor3d.intersection.PickData;

/**
 *
 * @author seh
 */
public interface Touchable extends Tangible {

    public void onTouchStart(PickData pick);
    public void onTouching(PickData pick);
    public void onTouchStop();

}
