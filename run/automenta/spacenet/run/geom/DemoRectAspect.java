/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package automenta.spacenet.run.geom;

import automenta.spacenet.space.widget.knob.CircleKnob;
import automenta.spacenet.run.ArdorSpacetime;
import automenta.spacenet.run.DemoDefaults;
import automenta.spacenet.run.surface.DemoBitmapSurface;
import automenta.spacenet.space.geom.ProcessBox;
import automenta.spacenet.space.geom.Rect;
import automenta.spacenet.space.geom.Rect.RectShape;
import automenta.spacenet.space.geom.layout.ColRect;
import automenta.spacenet.space.surface.BitmapSurface;
import automenta.spacenet.space.surface.ColorSurface;
import automenta.spacenet.space.widget.spinner.Spinner;
import automenta.spacenet.space.widget.window.Window;
import automenta.spacenet.var.physical.Color;
import automenta.spacenet.var.scalar.DoubleRange;
import automenta.spacenet.var.scalar.DoubleVar;
import java.net.URL;

/**
 *
 * @author seh
 */
public class DemoRectAspect extends ProcessBox {

    @Override protected void start() {
        URL imgUrl = DemoBitmapSurface.class.getResource("test.png");
        final Window bitmapWindow = add(new Window());
        final Rect r = bitmapWindow.add(new Rect(RectShape.Rect)).scale(0.9).moveDZ(0.05);
        r.add(new BitmapSurface(imgUrl));
        r.getAspect().set(1.0);

        DoubleRange range = new DoubleRange(r.getAspect(), 0.05, 2.0);
        Window controlWindow = add(new Window());

        Spinner aspectSpinner = controlWindow.add(new Spinner(DemoDefaults.font, range, 0.05));

        CircleKnob rotate = controlWindow.add(new CircleKnob(new ColorSurface(Color.Blue), new ColorSurface(Color.Green)));
        rotate.getAngle().add(new DoubleVar.IfDoubleChanges() {
            @Override public void onDoubleChange(DoubleVar d) {
                bitmapWindow.rotate(0, d.d(), 0);
            }
        });

        controlWindow.add(new ColRect(0.1, rotate, aspectSpinner).moveDZ(0.05));

        controlWindow.move(-1.5, 0, 0);
        bitmapWindow.move(1.5, 0, 0);

    }

    public static void main(String[] args) {
        ArdorSpacetime.newWindow(new DemoRectAspect());
    }


}
