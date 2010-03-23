/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.spacenet.var.vector;

import com.ardor3d.math.Vector2;
import com.ardor3d.math.type.ReadOnlyVector2;
import java.util.LinkedList;
import java.util.List;

public class V2 extends Vector2 {

    //TODO lazy instantiate list with ensureChangesAllocated
    private List<IfV2Changes> ifChanges = new LinkedList();
    private boolean notifying = true;

    public V2(double x, double y) {
        super(x, y);
    }

    public V2(double s) {
        this(s, s);
    }

    public abstract static class IfV2Changes {

        abstract public void onV2Changed(V2 v);
    }

    public IfV2Changes add(IfV2Changes i) {
        ifChanges.add(i);
        return i;
    }

    public IfV2Changes remove(IfV2Changes i) {
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



    @Override public Vector2 set(double x, double y) {
        if ((getX() == x) && (getY() == y)) {
            return this;
        }

        notifying = false;
        
        Vector2 v = super.set(x, y);

        notifying = true;
        
        notifyChanges();

        return v;
    }

    @Override
    public Vector2 set(ReadOnlyVector2 source) {
        return set(source.getX(), source.getY());
    }



    private void notifyChanges() {
        if (notifying) {
            for (IfV2Changes i : ifChanges) {
                i.onV2Changed(this);
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
}
