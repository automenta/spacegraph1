/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package automenta.spacenet.space.geom.layout;

import com.ardor3d.scenegraph.Spatial;

/**
 *
 * @author seh
 */
public class ColRect extends RowRect {

    public ColRect(double marginX, double marginY, Spatial... spatials) {
        super(marginX, marginY, spatials);
    }

    public ColRect(double marginXY, Spatial... spatials) {
        this(marginXY, marginXY, spatials);
    }

    public double getCellWidth(int num) {
        return (1.0-marginX);
    }
    public double getCellHeight(int num) {
        return (1.0-marginY) / num;
    }

    public double getCellX(Spatial s, int i, int num) {
        return 0;
    }
    public double getCellY(Spatial s, int i, int num) {
        return 0.5 - marginY/4.0 - ((double)i / (double)num) - getCellHeight(num)/2.0;
    }

    
}
