/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package automenta.spacenet.run.surface;

import automenta.spacenet.run.ArdorSpacetime;
import automenta.spacenet.space.geom.ProcessBox;
import automenta.spacenet.space.geom.Rect;
import automenta.spacenet.space.geom.Rect.RectShape;
import automenta.spacenet.space.surface.BitmapSurface;
import automenta.spacenet.space.widget.BitmapRect;
import java.net.URL;

public class DemoBitmapSurface extends ProcessBox {

    @Override protected void start() {
        URL imgUrl = getClass().getResource("test.png");
        Rect r = add(new Rect(RectShape.Rect)).move(-1, 0, 0);
        r.add(new BitmapSurface(imgUrl));

        add(new BitmapRect(imgUrl)).move(1, 0, 0);
    }

    public static void main(String[] args) {
        ArdorSpacetime.newWindow(new DemoBitmapSurface());
    }


}
