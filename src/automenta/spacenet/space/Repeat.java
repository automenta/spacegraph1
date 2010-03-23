/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.spacenet.space;

import com.ardor3d.scenegraph.Spatial;
import com.ardor3d.scenegraph.controller.ComplexSpatialController;
import javolution.context.ConcurrentContext;

/**
 *
 * @author seh
 */
abstract public class Repeat<S extends Spatial> extends ComplexSpatialController<S> {

    double t = 0;
    double period = 0;
    double timeRemaining = 0;
    double accumDT = 0;
    boolean par = false;

    public Repeat() {
        super();
    }

    public Repeat(double updatePeriod) {
        this();
        setPeriod(updatePeriod);
    }

    public Repeat(double updatePeriod, boolean parallel) {
        this(updatePeriod);
        this.par = parallel;
    }

    protected void setPeriod(double newPeriod) {
        this.period = newPeriod;
    }

    @Override public void update(final double dt, final S parent) {
        accumDT += dt;
        if (timeRemaining <= 0) {
            if (isParallel()) {
                ConcurrentContext.execute(new Runnable() {
                    @Override public void run() {
                        update(t, accumDT, parent);
                        timeRemaining = period;
                        accumDT = 0;
                    }
                });
            } else {
                update(t, accumDT, parent);
                timeRemaining = period;
                accumDT = 0;
            }
        } else {
            timeRemaining -= dt;
        }
        t += dt;
    }

    abstract protected void update(double t, double dt, S parent);

    public boolean isParallel() {
        return par;
    }
}
