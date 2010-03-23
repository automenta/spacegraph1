package automenta.spacenet.run.control;

import automenta.spacenet.space.control.Zoomable;
import automenta.spacenet.space.geom.Rect;
import automenta.spacenet.space.surface.ColorSurface;
import automenta.spacenet.var.physical.Color;

public class ZoomableRect extends Rect implements Zoomable {

    private final ColorSurface cs;

    public ZoomableRect() {
        super(RectShape.Rect);
        cs = color(Color.newRandomHSB(0.5, 1.0));
    }

//        protected BmpTextLineRect newText(String text) {
//            ColorRGBA fillColor = new ColorRGBA(ColorRGBA.WHITE);
//            float kerneling = 1f;
//            BmpTextLineRect tn = new BmpTextLineRect(text, font, fillColor, kerneling);
//            return tn;
//        }
    @Override
    public void onZoomStart() {
        add(new Rect(RectShape.Ellipse).span(-0.5, -0.5, -0.7, -0.7));
        add(new Rect(RectShape.Ellipse).span(0.5, -0.5, 0.7, -0.7));
        add(new Rect(RectShape.Ellipse).span(-0.5, 0.5, -0.7, 0.7));
        add(new Rect(RectShape.Ellipse).span(0.5, 0.5, 0.7, 0.7));
    }

    @Override
    public void onZoomStop() {
        removeAll();
    }

    @Override
    public boolean isZoomable() {
        return true;
    }

    @Override
    public boolean isTangible() {
        return true;
    }

}
