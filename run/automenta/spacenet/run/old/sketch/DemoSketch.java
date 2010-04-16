/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package automenta.spacenet.run.old.sketch;

import automenta.spacenet.run.ArdorSpacetime;
import automenta.spacenet.space.geom.ProcessBox;

/**
 *
 * @author seh
 */
public class DemoSketch extends ProcessBox {

    @Override protected void start() {
        
    }


    public static void main(String[] args) {
        ArdorSpacetime.newWindow(new DemoSketch());
    }

}
