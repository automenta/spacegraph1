/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package automenta.spacenet.space.geom.graph.arrange;

import automenta.spacenet.space.Space;
import automenta.spacenet.space.geom.Box;
import com.ardor3d.math.Vector3;
import java.util.Comparator;

/**
 *
 * @author seh
 */
public class GridListing<N,E> extends LineListing<N,E> {


    public GridListing(double sx, double sy, double tx, double ty, Comparator sort) {
        this(new Vector3(sx, sy, 0), new Vector3(tx, ty, 0), sort);
    }

    public GridListing(Vector3 start, Vector3 stop, Comparator sort) {
        super(start, stop, sort);
    }

    @Override protected void refresh() {
        //TODO sort

        int num = getObjects().size();
        if (num == 0)
            return;

        //TODO less hackish way
        for (Space s : getBox().getEdgeSpaces()) {
            s.visible(false);
        }

        double width = (int)Math.ceil(Math.sqrt(num))+1;
        double height = num/width + 1; //TODO is +1 necessary?

        double x = 0, y = 0;
        for (Object o : getObjects()) {
            Box b = getBox().getNodeBox(o);
            if (b!=null) {
                double px = getStop().getX() * (x/width) + getStart().getX() * (1.0 - x/width);
                double py = getStop().getY() * (y/height) + getStart().getY() * (1.0 - y/height);
                double pz = 0;
                double sx = Math.min(1.0 / (width), 1.0 / (height));
                double sy = sx;
                
                b.move(px, py, pz);
                b.scale(sx, sy, 1.0);

                x++;
                if (x == width) {
                    x = 0;
                    y++;
                }
            }
        }
    }

 
}
