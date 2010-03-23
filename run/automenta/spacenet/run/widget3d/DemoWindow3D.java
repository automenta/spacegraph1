/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.spacenet.run.widget3d;

import automenta.spacenet.space.widget3d.Dialog3D;
import automenta.spacenet.run.ArdorSpacetime;
import automenta.spacenet.run.DemoDefaults;
import automenta.spacenet.space.geom.ProcessBox;
import automenta.spacenet.space.geom.text3d.Font3D;


/**
 *
 */
public class DemoWindow3D extends ProcessBox {

    Font3D font = DemoDefaults.font;

    @Override protected void start() {
        add(new Dialog3D(font, "Information", 3, 2, 0.1));
    }

    public static void main(String[] args) {
        ArdorSpacetime.newWindow(new DemoWindow3D());
    }
    
}
