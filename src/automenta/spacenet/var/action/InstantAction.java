/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package automenta.spacenet.var.action;

import java.util.concurrent.Callable;

abstract public class InstantAction<I,O> implements Action<I,O> {

    @Override public Callable<O> get(final I i) {
        return new Callable<O>() {
            @Override public O call() throws Exception {
                return InstantAction.this.run(i);
            }
        };
    }

    abstract protected O run(I i) throws Exception;

}
