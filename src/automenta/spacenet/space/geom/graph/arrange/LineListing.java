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
public class LineListing<N,E> extends CurveListing<N,E> {

    private final Vector3 start;
    private final Vector3 stop;

    public LineListing(double sx, double sy, double tx, double ty, Comparator sort) {
        this(new Vector3(sx, sy, 0), new Vector3(tx, ty, 0), sort);
    }

    public LineListing(Vector3 start, Vector3 stop, Comparator sort) {
        super(true, false);
        this.start = start;
        this.stop = stop;
    }

    @Override protected void refresh() {
        //TODO sort

        if (getObjects().size() == 0)
            return;

        //TODO less hackish way
        for (Space s : getBox().getEdgeSpaces()) {
            s.visible(false);
        }

        Vector3 p = new Vector3(start);
        Vector3 d = new Vector3(stop).subtractLocal(start).normalizeLocal().multiplyLocal(1.0 / getObjects().size());
        for (Object o : getObjects()) {
            Box b = getBox().getNodeBox(o);
            if (b!=null) {
                b.move(p);
                b.scale(d.length(), d.length(), d.length());
                p.addLocal(d);
            }
        }
    }

    @Override
    public void stop() {
        //TODO less hackish way
        for (Space s : getBox().getEdgeSpaces()) {
            s.visible(true);
        }
        super.stop();
    }

    public Vector3 getStart() {
        return start;
    }

    public Vector3 getStop() {
        return stop;
    }

    



}
