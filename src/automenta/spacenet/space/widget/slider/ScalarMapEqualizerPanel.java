/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package automenta.spacenet.space.widget.slider;

import automenta.spacenet.space.geom.Rect;
import automenta.spacenet.space.geom.layout.ColRect;
import automenta.spacenet.space.geom.layout.RowRect;
import automenta.spacenet.space.geom.text3d.Font3D;
import automenta.spacenet.space.geom.text3d.Text3D;
import automenta.spacenet.space.widget.panel.Panel;
import automenta.spacenet.space.widget.slider.Slider.SliderDirection;
import automenta.spacenet.var.physical.Color;
import automenta.spacenet.var.scalar.DoubleRange;
import automenta.spacenet.var.scalar.DoubleVar;
import com.ardor3d.scenegraph.Spatial;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author seh
 */
public class ScalarMapEqualizerPanel<X> extends Panel {
    private final Map<X, DoubleVar> map;
    private RowRect sliderRows;
    private final Font3D font;
    private final double min;
    private final double max;
    
    //TODO pass MapVar
    public ScalarMapEqualizerPanel(Map<X, DoubleVar> map, Font3D font, double min, double max) {
        super();
        this.map = map;
        this.font = font;
        this.min = min;
        this.max = max;

        updateEqualizer();
        
    }

    protected void updateEqualizer() {
        if (sliderRows!=null) {
            remove(sliderRows);
            sliderRows = null;
        }

        List<Spatial> subPanels = new LinkedList();
        for (X x : map.keySet()) {

            DoubleRange range = new DoubleRange(map.get(x), min, max);
            DoubleVar knobWidth = new DoubleVar(0.1);
            DoubleVar spinIncrement = new DoubleVar((max - min) / 50.0);
            
            Slider slider = new Slider(SliderDirection.Horizontal, font, range, spinIncrement, knobWidth);
            Text3D label = new Text3D(font, getLabel(x));
            label.color(Color.White);

            Rect r = new RowRect(0.05, label, slider);
            subPanels.add(r);
        }

        sliderRows = add(new ColRect(0.05, subPanels.toArray(new Spatial[subPanels.size()])));
        sliderRows.moveDZ(0.05);
    }

    protected String getLabel(X x) {
        return x.toString();
    }

}
