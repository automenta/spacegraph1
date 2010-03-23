/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package automenta.spacenet.var.scalar;

import automenta.spacenet.var.scalar.DoubleVar;

/**
 *
 * @author seh
 */
public class DoubleRange {
    private final DoubleVar value;
    private final DoubleVar min;
    private final DoubleVar max;

    public DoubleRange(DoubleVar value, DoubleVar min, DoubleVar max) {
        super();
        this.value = value;
        this.min = min;
        this.max = max;
    }

    public DoubleRange(double val, double min, double max) {
        this(new DoubleVar(val), new DoubleVar(min), new DoubleVar(max));
    }

    public DoubleRange(DoubleVar val, double min, double max) {
        this(val, new DoubleVar(min), new DoubleVar(max));
    }

    public DoubleVar getMax() {
        return max;
    }

    public DoubleVar getMin() {
        return min;
    }

    public DoubleVar getValue() {
        return value;
    }
    
}
