package automenta.spacenet.space.widget3d;

import automenta.spacenet.space.control.Pressable;
import automenta.spacenet.space.geom.text3d.Font3D;
import automenta.spacenet.space.geom.text3d.Text3D;
import automenta.spacenet.var.physical.Color;
import com.ardor3d.intersection.PickData;

public class Button3D extends Panel3D implements Pressable {

    public Button3D() {
        super();
    }

    public Button3D(Font3D font, String text) {
        this();
        Text3D tb = addFront(new Text3D(font, text), -0.4, -0.4, 0.4, 0.4, 0.1);
        tb.color(Color.Black);
    }

    @Override
    public void onPressStart(PickData pick) {
    }

    @Override
    public void onPressStop(PickData pick) {
    }
}
