/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.spacenet.space.widget.slider;

import automenta.spacenet.space.geom.text3d.Font3D;
import automenta.spacenet.space.widget.spinner.Spinner;
import automenta.spacenet.var.physical.Color;
import automenta.spacenet.var.scalar.DoubleRange;
import automenta.spacenet.var.scalar.DoubleVar;

/**
 *
 * @author seh
 */
public class Slider extends Spinner {

    private final DoubleVar knobWidth;
    private final SliderDirection direction;

    public static enum SliderDirection {
        Horizontal, Vertical
    }
    public Slider(SliderDirection direction, Font3D font, DoubleRange range, double spinIncrement, double knobWidth) {
        this(direction, font, range, new DoubleVar(spinIncrement), new DoubleVar(knobWidth));
    }

    public Slider(SliderDirection direction, Font3D font, DoubleRange range, DoubleVar spinIncrement, DoubleVar knobWidth) {
        super(font, range, spinIncrement);

        this.direction = direction;
        this.knobWidth = knobWidth;

        valueChanged();
    }

    public DoubleVar getKnobWidth() {
        return knobWidth;
    }

    @Override protected void arrange() {
        //super.arrange();

        if (direction == SliderDirection.Horizontal) {
            upButton.span(0.5, -0.5, 0.4, 0.5).moveDZ(0.05);
            downButton.span(-0.5, -0.5, -0.4, 0.5).moveDZ(0.05);
            numberButton.span(-0.1, 0.5, 0.1, -0.5).moveDZ(0.05);
            numberButton.color(Color.White);
        }
    }

    @Override protected void valueChanged() {
        super.valueChanged();

        arrange();
    }
}
