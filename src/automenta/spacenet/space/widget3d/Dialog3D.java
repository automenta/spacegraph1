package automenta.spacenet.space.widget3d;

import automenta.spacenet.space.geom.text3d.Font3D;
import automenta.spacenet.space.geom.text3d.Text3D;
import automenta.spacenet.var.physical.Color;

public class Dialog3D extends Window3D {

    public Dialog3D(Font3D font, String title) {
        super();

        double windowMargin = 0.02;

        Text3D titleText = new Text3D(font, title, Color.Black);
        //titleText.getAspectXY().set(1.0);
        addFront(titleText, -0.5 + windowMargin, 0.5 + windowMargin, 0, 0.4, 0.2);

        Button3D closeButton = new Button3D(font, "x");
        closeButton.color(Color.GrayPlusPlus);
        addFront(closeButton, 0.4, 0.4, 0.5 - windowMargin, 0.5 - windowMargin, 0.2);

        Panel3D contentPanel = new Panel3D();
        addFront(contentPanel, -0.5 + windowMargin, 0.38, 0.5 - windowMargin, -0.5 + windowMargin, 0.1);
    }

    public Dialog3D(Font3D font, String title, double w, double h, double d) {
        this(font, title);
        scale(w, h, d);
    }
}
