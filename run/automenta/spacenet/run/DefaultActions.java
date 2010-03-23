/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package automenta.spacenet.run;

import automenta.spacenet.var.action.Actions;
import automenta.spacenet.var.action.InstantAction;
import java.net.MalformedURLException;
import java.net.URL;

/**
 *
 * @author seh
 */
public class DefaultActions {

    public static void addActions(Actions a) {
        //String to URL
        a.add(new InstantAction<String,URL>() {
            @Override protected URL run(String i) throws Exception {
               return new URL(i);
            }

            @Override public double applies(String i) {
                try {
                    URL u = new URL(i);
                } catch (MalformedURLException ex) {
                    return 0.0;
                }
                return 1.0;
            }

            @Override public String toString(String i) {
                return "To URL";
            }
        });
    }
}
