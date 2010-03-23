/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package automenta.spacenet.run.geom.text;

import automenta.spacenet.run.ArdorSpacetime;
import automenta.spacenet.run.DemoDefaults;
import automenta.spacenet.space.geom.ProcessBox;
import automenta.spacenet.space.geom.text3d.Char3D;

/**
 *
 * @author seh
 */
public class DemoChar3D extends ProcessBox {

    @Override
    protected void start() {
        addChar('a');
    }
    
    public void addChar(char c) {
        add(new Char3D(DemoDefaults.font, c));
    }



    public static void main(String[] args) {
        ArdorSpacetime.newWindow(new DemoChar3D());
    }

}
