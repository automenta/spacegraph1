/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package automenta.spacenet.var.physical;

import automenta.spacenet.var.map.MapVar;
import automenta.spacenet.var.scalar.DoubleVar;

/**
 * interpolates across a set of 4-space points arranged in a parametric curve from t=0 to t=1.0
 */
public class ColorGradient extends MapVar<DoubleVar, Color> {

    public void put(double t, Color c) {
        put(new DoubleVar(t), c);
    }
    
    public void removeBetween(double tStart, double tEnd) {
        //..
    }

    public Color get(double t) {
        double r = 0;
        double g = 0;
        double b = 0;
        double a = 0;
        return new Color(r, g, b, a);
    }

}
