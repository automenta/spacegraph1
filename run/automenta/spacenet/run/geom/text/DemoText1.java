/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.spacenet.run.geom.text;

import automenta.spacenet.run.ArdorSpacetime;
import automenta.spacenet.space.geom.Box;
import automenta.spacenet.space.geom.text2d.BmpTextLineRect;
import automenta.spacenet.space.geom.text2d.BmpFont;
import automenta.spacenet.space.geom.ProcessBox;
import automenta.spacenet.space.geom.Rect;
import automenta.spacenet.space.geom.text3d.Font3D;
import automenta.spacenet.space.geom.text3d.Text3D;
import com.ardor3d.math.ColorRGBA;
import java.awt.Font;

/**
 *
 * @author seh
 */
public class DemoText1 extends ProcessBox {

    public static void main(String[] args) {
        //Multiple windows can be created by calling newWindow repeatedly
        ArdorSpacetime.newWindow(new DemoText1());
    }
    Font awtFont = new Font("Arial", Font.PLAIN, 64);
    BmpFont font = new BmpFont(awtFont);

    protected Rect newText(String text) {
        ColorRGBA fillColor = new ColorRGBA(ColorRGBA.WHITE);
        float kerneling = 1f;
        BmpTextLineRect tn = new BmpTextLineRect(text, font, fillColor, kerneling);
        return tn;
    }

    Font3D font3d = new Font3D(new Font("Arial", Font.PLAIN, 12), 0.1, true, true, true);
    
    protected Box newText3D(char c) {
        Box b = new Box(BoxShape.Empty);
        System.out.println("making: " + c);
        b.add(new Text3D(font3d, new String("" + c)));
        return b;
    }

    @Override
    protected void start() {
        double x = 0;
        double y = 0;

        for (char i = 'a'; i < 'z'; i++) {

            //add(newText(new String(new char[]{i}))).move(x, y);
            
            add(newText3D(i)).move(x, y, 0);

            x += 1.0;
            if (x > 10.0) {
                y -= 1.0;
                x = 0;
            }
        }

        for (char i = 'A'; i < 'Z'; i++) {

            //add(newText(new String(new char[]{i}))).move(x, y);

            add(newText3D(i)).move(x, y, 0);

            x += 1.0;
            if (x > 10.0) {
                y -= 1.0;
                x = 0;
            }
        }

    }
}
