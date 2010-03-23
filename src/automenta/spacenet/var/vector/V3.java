/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.spacenet.var.vector;

import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyVector3;
import java.util.LinkedList;
import java.util.List;

public class V3 extends Vector3 {

    //TODO lazy instantiate list with ensureChangesAllocated
    private List<IfV3Changes> ifChanges = new LinkedList();
    private boolean notifying = true;

    public V3() {
        this(0, 0, 0);
    }

    public V3(double s) {
        this(s, s, s);
    }

    public V3(double x, double y, double z) {
        super(x, y, z);
    }

    public V3 interpolate(V3 t, double speed) {
        speed = Math.min(speed, 1.0);
        speed = Math.max(speed, 0);
        double nx = getX() * (1.0 - speed) + t.getX() * speed;
        double ny = getY() * (1.0 - speed) + t.getY() * speed;
        double nz = getZ() * (1.0 - speed) + t.getZ() * speed;
        return set(nx, ny, nz);
    }

    public double getVolume() {
        return Math.abs(getX() * getY() * getZ());
    }

    public abstract static class IfV3Changes {

        abstract public void onV3Changed(V3 v);
    }

    public IfV3Changes add(IfV3Changes i) {
        ifChanges.add(i);
        return i;
    }

    public IfV3Changes remove(IfV3Changes i) {
        ifChanges.remove(i);
        return i;
    }

    @Override
    public void setX(double x) {
        super.setX(x);
        notifyChanges();
    }

    @Override
    public void setY(double y) {
        super.setY(y);
        notifyChanges();
    }

    @Override
    public Vector3 set(ReadOnlyVector3 source) {
        return set(source.getX(), source.getY(), source.getZ());
    }

    @Override public V3 set(double x, double y, double z) {
        if ((getX() == x) && (getY() == y) && (getZ() == z)) {
            return this;
        }

        notifying = false;

        Vector3 v = super.set(x, y, z);

        notifying = true;

        notifyChanges();

        return this;
    }

    protected void notifyChanges() {
        if (notifying) {
            for (IfV3Changes i : ifChanges) {
                i.onV3Changed(this);
            }
        }
    }

    @Override
    protected void finalize() throws Throwable {
        if (ifChanges != null) {
            ifChanges.clear();
            ifChanges = null;
        }

        super.finalize();
    }

    public double getMinComponent() {
        double ax = Math.abs(getX());
        double ay = Math.abs(getY());
        double az = Math.abs(getZ());
        
        return Math.min( ax, Math.min( ay, az ) );
    }
    public double getMaxComponent() {
        double ax = Math.abs(getX());
        double ay = Math.abs(getY());
        double az = Math.abs(getZ());

        return Math.max( ax, Math.max( ay, az ) );
    }

}
