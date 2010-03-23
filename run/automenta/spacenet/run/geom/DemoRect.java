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
import automenta.spacenet.var.vector.Quat;
import automenta.spacenet.var.vector.V2;
import automenta.spacenet.var.vector.V3;
import com.ardor3d.scenegraph.Spatial;

/**
 *
 * @author seh
 */
public class DemoRect extends ProcessBox {

    
    public static void main(String[] args) {
        ArdorSpacetime.newWindow(new DemoRect());
    }

    @Override protected void start() {
        final V3 pos = new V3(0,0,0);
        final V2 scale = new V2(1,1);
        final Quat orientation = Quat.fromAngles(0, 0, 0);
        
        final Rect r = add(new Rect(pos, scale, orientation, RectShape.Rect));
        final ColorSurface cs = r.add(new ColorSurface(0.8f, 0.5f, 0.3f));

        r.add(new Repeat() {
            @Override protected void update(double t, double dt, Spatial s) {
                r.move(Math.cos(t), Math.sin(t), 0);
                
                double sw = 1.0 + Math.cos(t*10.0)/5.0;
                double sh = 1.0 + Math.sin(t*10.0)/5.0;
                r.scale(sw, sh);
                
                float cr = (float)((1.0 + Math.cos(t)) * 0.5);
                float cg = 0.5f;
                float cb = 0.2f;
                cs.color(cr, cg, cb);

                if (Math.cos(t*4.0) < 0)
                    r.setShape(RectShape.Ellipse);
                else
                    r.setShape(RectShape.Rect);

                r.rotate(t/4.0, t/2.0, t);
            }
        });
    }
}
