/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package automenta.spacenet.var.scalar;

import automenta.spacenet.var.scalar.IntVar.IfIntChanges;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author seh
 */
public class BoolVar {
    private boolean value;

    public static interface IfBoolChanges {
        public void onBoolChange(BoolVar d);
    }

    private List<IfBoolChanges> ifChanges = new LinkedList();

    public BoolVar(boolean i) {
        super();
        this.value = i;
    }


    public void set(boolean newValue) {
        if (value == newValue)
            return;

        this.value = newValue;

        notifyChanges();
    }

    protected void notifyChanges() {
        for (IfBoolChanges d : ifChanges) {
            d.onBoolChange(this);
        }
    }

    public boolean b() { return value; }

    public IfBoolChanges add(IfBoolChanges d) {
        ifChanges.add(d);
        return d;
    }
    public IfBoolChanges remove(IfBoolChanges d) {
        ifChanges.remove(d);
        return d;
    }

}
