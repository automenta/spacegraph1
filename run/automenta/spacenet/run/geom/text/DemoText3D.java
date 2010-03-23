/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.spacenet.run.geom.text;

import automenta.spacenet.space.geom.border.SolidBoxBorder;
import automenta.spacenet.run.ArdorSpacetime;
import automenta.spacenet.space.geom.ProcessBox;
import automenta.spacenet.space.geom.border.GridRect;
import automenta.spacenet.space.geom.text3d.Font3D;
import automenta.spacenet.space.geom.text3d.Text3D;
import automenta.spacenet.space.widget.PanningDragRect;
import automenta.spacenet.space.widget.window.Window;
import automenta.spacenet.var.physical.Color;
import java.awt.Font;

/**
 *
 * @author seh
 */
public class DemoText3D extends ProcessBox {

    static final Font font = new Font("OCRA", Font.PLAIN, 64);
    static final Font3D f = new Font3D(font, 0.01, true, false, false);

    @Override public void start() {
        double bw = 4;
        double bh = 4;
        PanningDragRect back = add(new PanningDragRect(1.5));
        back.moveDZ(-0.3);
        back.scale(bw, bh);
        back.color(Color.Black);
        back.add(new GridRect(Color.Orange, 4, 4, 0.1));

        Window w = add(new Window());
        w.add(new SolidBoxBorder(1.0, 1.2, 1.0, 1.2).scale(0.9).spanZ(-0.1, 0.1));
        w.add(new Text3D(f, "Abcdefg", Color.Black)).scale(0.9).spanZ(0.1, 0.2);

        Window w2 = add(new Window());
        w2.add(new SolidBoxBorder(1.0, 1.2, 1.0, 1.2).scale(0.9).spanZ(-0.1, 0.1));
        Text3D t2 = new Text3D(f, "Abcdefg", Color.Black);
        w2.add(t2).scale(0.9).spanZ(0.1, 0.2);
        t2.aspectXY(1.0);

    }

//    @Override
//    protected void start() {
////        add(new Rect(RectShape.Ellipse)).color(Color.Orange);
////        add(new Box(BoxShape.Cubic)).color(Color.Orange);
//
//        //Font font = new Font("Arial", Font.PLAIN, 12);
//        Text3D t = add(new Text3D(f, "Abcdef"));
//    }
    public static void main(String[] args) {
        ArdorSpacetime.newWindow(new DemoText3D());
    }
}
