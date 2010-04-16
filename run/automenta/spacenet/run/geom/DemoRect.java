/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package automenta.spacenet.run.geom;

import automenta.spacenet.run.*;
import automenta.spacenet.space.*;
import automenta.spacenet.space.geom.Rect;
import automenta.spacenet.space.geom.Rect.RectShape;
import automenta.spacenet.space.surface.ColorSurface;
import automenta.spacenet.space.geom.ProcessBox;
import com.ardor3d.scenegraph.Spatial;

/**
 *
 * @author seh
 */
public class DemoRect  {
    
    public static void main(String[] args) {
        final Rect r = new Rect(RectShape.Rect);        

        r.add(new Repeat() {
            @Override protected void update(double t, double dt, Spatial s) {
                r.move(Math.cos(t), Math.sin(t), 0);

                r.scale(1.0 + Math.cos(t*10.0)/5.0, 1.0 + Math.sin(t*10.0)/5.0);

                r.rotate(t/4.0, t/2.0, t);

                r.color((float)((1.0 + Math.cos(t)) * 0.5), 0.5f, 0.2f);

                if (Math.cos(t*4.0) < 0)
                    r.setShape(RectShape.Ellipse);
                else
                    r.setShape(RectShape.Rect);
            }
        });

        new ArdorWindow().withVolume(r);
    }

}
