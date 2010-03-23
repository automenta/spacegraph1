/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package automenta.spacenet.space.widget;

import automenta.spacenet.space.geom.Rect;
import automenta.spacenet.space.geom.Rect.RectShape;
import automenta.spacenet.space.surface.BitmapSurface;
import automenta.spacenet.var.physical.Color;
import java.net.URL;

/**
 * displays a bitmap in a rectangle, with correct aspect ratio and on a 100% white surface (to avoid tinting).
 */
public class BitmapRect extends Rect {
    private final BitmapSurface bmpSurface;

    public BitmapRect(URL u) {
        super(RectShape.Rect);

        color(Color.White);
        
        bmpSurface = add(new BitmapSurface(u));
        aspect(bmpSurface.getPixelHeight() / bmpSurface.getPixelWidth());
    }
}
