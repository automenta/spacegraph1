/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.spacenet.space.widget.spinner;

import automenta.spacenet.space.Repeat;
import automenta.spacenet.space.geom.text3d.Font3D;
import automenta.spacenet.space.geom.text3d.Text3D;
import automenta.spacenet.space.widget.button.Button;
import automenta.spacenet.space.widget.panel.Panel;
import automenta.spacenet.var.physical.Color;
import automenta.spacenet.var.scalar.DoubleRange;
import automenta.spacenet.var.scalar.DoubleVar;
import automenta.spacenet.var.scalar.DoubleVar.IfDoubleChanges;
import com.ardor3d.intersection.PickData;
import com.ardor3d.scenegraph.Spatial;
import java.text.DecimalFormat;

/**
 * TODO spin up & down according to how long the buttons are pressed (not per click)
 * @author seh
 */
public class Spinner extends Panel implements IfDoubleChanges {

    protected final Button upButton;
    protected final Button downButton;
    protected final Text3D numberButton;
    public final DoubleRange range;
    public final DoubleVar increment;
    private Color textColor = Color.Black;
    private Repeat accel;
    private DoubleVar spinPeriod = new DoubleVar(0.25);
    DecimalFormat dec = new DecimalFormat("###.##");



    public class SpinButton extends Button {

        private final int direction;

        public SpinButton(Font3D font, String label, int direction) {
            super(font, label);
            this.direction = direction;

        }

        @Override public void onPressStart(PickData pick) {
            startAccel(direction);
        }

        @Override public void onPressStop(PickData pick) {
            stopAccel();
        }
    }

    public Spinner(Font3D font, DoubleRange range, double increment) {
        this(font, range, new DoubleVar(increment));
    }

    public Spinner(Font3D font, DoubleRange range, DoubleVar increment) {
        super();

        this.range = range;
        this.increment = increment;

        //TODO trigger change on range.getValue() changes

        //Rect r = add(new Rect(RectShape.Empty));
        //r.scale(0.85);

        upButton = add(new SpinButton(font, "^", 1));
//        upButton.add(new ButtonAction() {
//
//            @Override public void onButtonClicked(Button b) {
//                spin(+1);
//            }
//        });

        downButton = add(new SpinButton(font, "v", -1));
//        downButton.add(new ButtonAction() {
//
//            @Override public void onButtonClicked(Button b) {
//                spin(-1);
//            }
//        });

        numberButton = add(new Text3D(font, "0.0"));

        arrange();

    }

    @Override public void onDoubleChange(DoubleVar d) {
        valueChanged();
    }

    protected void arrange() {
        upButton.span(0.25, 0, 0.5, 0.5).moveDZ(0.1);
        downButton.span(0.25, 0, 0.5, -0.5).moveDZ(0.1);
        numberButton.span(-0.5, 0.5, 0.25, -0.5).moveDZ(0.1);
        numberButton.color(textColor);
    }

    protected void startAccel(final int direction) {
        if (accel != null) {
            stopAccel();
        }

        accel = add(new Repeat(getSpinPeriod().d()) {

            @Override protected void update(double t, double dt, Spatial s) {
                spin(direction);
            }
        });
    }

    @Override
    protected void afterAttached(Spatial newParent) {
        super.afterAttached(newParent);

        getValue().add((IfDoubleChanges)this);
        valueChanged();
    }



    @Override
    protected void beforeDetached(Spatial parent) {
        stopAccel();
        getValue().remove((IfDoubleChanges)this);

        super.beforeDetached(parent);
    }




    public DoubleVar getSpinPeriod() {
        return spinPeriod;
    }

    protected void stopAccel() {
        remove(accel);
        accel = null;
    }

    protected void spin(int direction) {
        double change = getIncrement().d() * ((double) direction);
        double nextValue = getValue().d() + change;

        nextValue = Math.min(nextValue, getMax().d());
        nextValue = Math.max(nextValue, getMin().d());

        getValue().set(nextValue);
    }

    protected void valueChanged() {
        String ds = dec.format(getValue().d()); //Double.toString(getValue().d());
        numberButton.setText(ds);
    }

    public DoubleVar getMin() {
        return range.getMin();
    }

    public DoubleVar getMax() {
        return range.getMax();
    }

    public DoubleVar getValue() {
        return range.getValue();
    }

    public DoubleVar getIncrement() {
        return increment;
    }
}
