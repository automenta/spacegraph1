/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.spacenet.run.widget;

import automenta.spacenet.run.ArdorSpacetime;
import automenta.spacenet.run.DemoDefaults;
import automenta.spacenet.space.geom.ProcessBox;
import automenta.spacenet.space.geom.border.GridRect;
import automenta.spacenet.space.widget.PanningDragRect;
import automenta.spacenet.space.widget.button.Button;
import automenta.spacenet.space.widget.window.Window;
import automenta.spacenet.var.physical.Color;

/**
 * a plane with resizable windows.  also shows how to limit camera (translation and orientation)
 */
public class DemoDesktop extends ProcessBox {

    double w = 1;
    double h = 1;
    
    @Override protected void start() {
        PanningDragRect back = add(new PanningDragRect(1.5));
        back.moveDZ(-0.1);
        back.scale(w, h);
        back.color(Color.Black);
        back.add(new GridRect(Color.Orange, 4, 4, 0.1));

        Window win = add(new Window());
        win.scale(0.2, 0.1);
        
        win.add(new Button(DemoDefaults.font, "Click").scale(0.5, 0.25).moveDZ(0.1));
    }

    public static void main(String[] args) {
        ArdorSpacetime.newWindow(new DemoDesktop());
    }
}
