/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package automenta.spacenet.run.widget;

import automenta.spacenet.space.widget.PanningDragRect;
import automenta.spacenet.run.ArdorSpacetime;
import automenta.spacenet.run.DemoDefaults;
import automenta.spacenet.space.geom.ProcessBox;
import automenta.spacenet.space.widget.button.Button;
import automenta.spacenet.var.physical.Color;
import automenta.spacenet.var.Maths;

/**
 *
 * @author seh
 */
public class DemoButton extends ProcessBox {


    @Override protected void start() {

        add(new PanningDragRect(2.0).scale(12,12).move(0,0,-0.5)).color(Color.Blue);

        for (double x = -4; x < 4; x+=1.5) {
            for (double y = -4; y < 4; y+=1.5) {
                double w = Maths.random(0.2, 1.0);
                double h = Maths.random(0.2, 1.0);
                add(new Button(DemoDefaults.font, "Button").move(x, y, 0).scale(w, h));
            }
        }

        //add(new Text3D(font, "Button"));

    }


    public static void main(String[] args) {
        ArdorSpacetime.newWindow(new DemoButton());
    }
}
