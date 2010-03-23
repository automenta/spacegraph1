/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package automenta.spacenet.var.action;

import java.util.concurrent.Callable;

/** asynchronous potential action  */
public interface Action<I,O> {

    /** returns a String description of the action as applicable to i */
    public String toString(I i);

    /** how action can be applied to input i.  returns salience or relevancy (=0: not relevant, >0: relevant strength)*/
    public double applies(I i);

    /** returns a Callable for the given input */
    public Callable<O> get(I i);
    
}
