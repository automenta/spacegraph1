/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package automenta.spacenet.var;

public class Maths {

    /** @see http://en.wikipedia.org/wiki/Golden_ratio */
    public static final double PHI = 1.61803399;

    public static double random(double min, double max) {
        return Math.random() * (max - min) + min;
    }

}
