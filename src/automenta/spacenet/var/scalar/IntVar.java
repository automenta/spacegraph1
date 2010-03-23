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
public class IntVar {

    private int value;

    public static interface IfIntChanges {
        public void onIntChange(IntVar d);
    }

    private List<IfIntChanges> ifChanges = new LinkedList();

    public IntVar(int i) {
        super();
        this.value = i;
    }

    public void add(int d) {
        set(i() + d);
    }
    
    public void set(int newValue) {
        if (value == newValue)
            return;

        this.value = newValue;
        
        notifyChanges();
    }

    protected void notifyChanges() {
        for (IfIntChanges d : ifChanges) {
            d.onIntChange(this);
        }
    }

    public int i() { return value; }
    public double d() { return (double)value; }
    public float f() { return (float)value; }

    public IfIntChanges add(IfIntChanges d) {
        ifChanges.add(d);
        return d;
    }
    public IfIntChanges remove(IfIntChanges d) {
        ifChanges.remove(d);
        return d;
    }

}
