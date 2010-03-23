/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.spacenet.run.control;

import automenta.spacenet.run.ArdorSpacetime;
import automenta.spacenet.run.DemoDefaults;
import automenta.spacenet.space.geom.Rect;
import automenta.spacenet.space.geom.ProcessBox;
import automenta.spacenet.space.geom.text2d.BmpFont;
import automenta.spacenet.space.geom.text3d.Char3D;
import automenta.spacenet.var.Maths;
import java.awt.Font;

/**
 *
 * @author seh
 */
public class DemoZooming extends ProcessBox {

    Font awtFont = new Font("Arial", Font.PLAIN, 64);
    BmpFont font = new BmpFont(awtFont);

    @Override
    protected void start() {
        int n = 2;
        Rect zr;
        for (int x = -n; x < n; x++) {
            for (int y = -n; y < n; y++) {
                double r1 = Maths.random(-0.5, 0.5);
                double r2 = Maths.random(-0.5, 0.5);
                double r3 = Maths.random(-0.5, 0.5);
                
                zr = add(new ZoomableRect().move(x * 2, y * 2, (double) (x ^ y)).rotate(r1, r2, r3));
                zr.add(new Char3D(DemoDefaults.font, 'x').moveDZ(0.5));
            }
        }
    }

    public static void main(String[] args) {
        ArdorSpacetime.newWindow(new DemoZooming());
    }
}
