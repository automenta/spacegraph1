/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package automenta.spacenet.space.control;

/**
 *
 * @author seh
 */
public interface Zoomable extends Tangible {
    public void onZoomStart();
    public void onZoomStop();
    public boolean isZoomable();
}
