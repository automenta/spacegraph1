/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package automenta.spacenet.var.scalar;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author seh
 */
public class DoubleVar {

    private double value;

    public static interface IfDoubleChanges {
        public void onDoubleChange(DoubleVar d);
    }

    private List<IfDoubleChanges> ifChanges = new LinkedList();

    public DoubleVar(double d) {
        super();
        this.value = d;
    }

    public void add(double d) {
        set(d() + d);
    }
    
    public void set(double newValue) {
        if (value == newValue)
            return;

        this.value = newValue;
        
        notifyChanges();
    }

    protected void notifyChanges() {
        for (IfDoubleChanges d : ifChanges) {
            d.onDoubleChange(this);
        }
    }

    public double d() { return value; }
    public float f() { return (float)value; }

    public IfDoubleChanges add(IfDoubleChanges d) {
        ifChanges.add(d);
        return d;
    }
    public IfDoubleChanges remove(IfDoubleChanges d) {
        ifChanges.remove(d);
        return d;
    }

}
