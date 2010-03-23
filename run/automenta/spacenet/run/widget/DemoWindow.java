/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.spacenet.run.widget;

import automenta.spacenet.run.ArdorSpacetime;
import automenta.spacenet.space.geom.ProcessBox;
import automenta.spacenet.space.widget.window.Window;

/**
 * window attached to a superwindow
 * @author seh
 */
public class DemoWindow extends ProcessBox {

    @Override
    protected void start() {
        Window superWindow = add(new Window());
        superWindow.scale(2);

        Window subWindow = superWindow.add(new Window());
        subWindow.move(1.25,1.25).scale(0.25);
    }

    public static void main(String[] args) {
        ArdorSpacetime.newWindow(new DemoWindow());
    }
}
