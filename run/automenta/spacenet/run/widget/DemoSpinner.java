/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package automenta.spacenet.run.widget;

import automenta.spacenet.run.ArdorSpacetime;
import automenta.spacenet.run.DemoDefaults;
import automenta.spacenet.space.geom.ProcessBox;
import automenta.spacenet.space.widget.slider.Slider;
import automenta.spacenet.space.widget.slider.Slider.SliderDirection;
import automenta.spacenet.space.widget.spinner.Spinner;
import automenta.spacenet.var.scalar.DoubleRange;
import automenta.spacenet.var.scalar.DoubleVar;

/**
 *
 * @author seh
 */
public class DemoSpinner extends ProcessBox {

    @Override
    protected void start() {
        DoubleRange range = new DoubleRange(5, 0, 10);
        DoubleVar inc = new DoubleVar(0.25);        
        add(new Spinner(DemoDefaults.font, range, inc).move(0,1,0));
        add(new Slider(SliderDirection.Horizontal, DemoDefaults.font, range, inc, new DoubleVar(0.2)).scale(3,1).move(0,-1,0));
    }

    public static void main(String[] args) {
        ArdorSpacetime.newWindow(new DemoSpinner());
    }

}
