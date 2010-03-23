/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package automenta.spacenet.run.geom.text;

import automenta.spacenet.run.ArdorSpacetime;
import automenta.spacenet.space.geom.ProcessBox;
import automenta.spacenet.space.geom.Rect;
import automenta.spacenet.space.geom.Rect.RectShape;
import automenta.spacenet.space.geom.text2d.BmpFont;
import automenta.spacenet.space.geom.text2d.BmpTextLineRect;
import automenta.spacenet.var.physical.Color;
import com.ardor3d.math.ColorRGBA;
import java.awt.Font;

/**
 *
 * @author seh
 */
public class DemoChar2D extends ProcessBox {

    public static void main(String[] args) {
        ArdorSpacetime.newWindow(new DemoChar2D());
    }
    private Rect textSpatial;


    protected Rect newText(String text, int pointSize) {
        BmpFont font = new BmpFont(new Font("Arial", Font.PLAIN, pointSize));
        ColorRGBA fillColor = new ColorRGBA(ColorRGBA.WHITE);
        float kerneling = 0f;
        BmpTextLineRect tn = new BmpTextLineRect(text, font, fillColor, kerneling);
        return tn;
    }

    protected void updateText(String text, int pointSize) {
        if (textSpatial!=null)
            remove(textSpatial);
        
        textSpatial = add(newText(text, pointSize).move(0,0,0.05));
    }

    @Override
    protected void start() {
        add(new Rect(RectShape.Rect).move(0,0,0).scale(1,1)).color(Color.Orange);
        
        new CharForm() {
            @Override protected void update() {
                String text = getText();
                int pointSize = getPointSize();

                updateText(text, pointSize);
            }
        }.setVisible(true);

        updateText("ABCDEFG", 16);

    }
}
