/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package automenta.spacenet.space.widget.panel;

import automenta.spacenet.space.control.Touchable;
import automenta.spacenet.space.control.Zoomable;
import automenta.spacenet.space.geom.Rect;
import automenta.spacenet.space.geom.Rect.RectShape;
import com.ardor3d.intersection.PickData;
import com.ardor3d.renderer.state.BlendState;

public class Panel extends Rect implements Touchable, Zoomable {
    private PanelModel model;
    private boolean touched;

    public Panel() {
        this(new DefaultPanelModel());
    }
    
    public Panel(PanelModel m) {
        super(RectShape.Empty);
        setModel(m);

        BlendState bState = new BlendState();
        bState.setBlendEnabled(true);
        bState.setSourceFunction(BlendState.SourceFunction.SourceAlpha);
        bState.setDestinationFunction(BlendState.DestinationFunction.OneMinusSourceAlpha);
        bState.setTestEnabled(true);
        bState.setTestFunction(BlendState.TestFunction.Always);
        bState.setEnabled(true);
        setRenderState(bState);

    }

    public void setModel(PanelModel m) {
        removeAll();
        this.model = m;
        getModel().initPanel(this);
    }

    public PanelModel getModel() {
        return model;
    }

    @Override
    public void onTouchStart(PickData pick) {
        this.touched = true;
        getModel().onTouchStart();
    }

    @Override
    public void onTouching(PickData pick) {
    }

    @Override
    public void onTouchStop() {
        this.touched = false;
        getModel().onNormal();
    }

    @Override
    public boolean isTangible() {
        return true;
    }

    @Override
    public void onZoomStart() {
    }

    @Override
    public void onZoomStop() {
    }

    @Override
    public boolean isZoomable() {
        return true;
    }

    public boolean isTouched() {
        return touched;
    }


    
    
}
