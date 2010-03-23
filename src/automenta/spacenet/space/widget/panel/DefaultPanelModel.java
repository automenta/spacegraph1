/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.spacenet.space.widget.panel;

import automenta.spacenet.space.geom.Rect.RectShape;
import automenta.spacenet.var.physical.Color;

/**
 *
 * @author seh
 */
public class DefaultPanelModel implements PanelModel {

    public static final Color defaultNormalColor = Color.GrayPlusPlus;
    public static final Color defaultTouchedColor = Color.GrayPlusPlusPlus;
    public final Color normalColor;
    public final Color touchedColor;
//    private Color defaultColor = Color.Red;
//    private Color touchedColor = Color.Blue;
    private Panel panel;

    public DefaultPanelModel() {
        this(defaultNormalColor, defaultTouchedColor);
    }

    public DefaultPanelModel(Color normalColor, Color touchedColor) {
        super();
        this.normalColor = normalColor;
        this.touchedColor = touchedColor;
    }

    @Override
    public void initPanel(Panel p) {
        this.panel = p;
        p.setShape(RectShape.Rect);
        onNormal();
    }

    public Panel getPanel() {
        return panel;
    }

    @Override
    public void onTouchStart() {
        panel.color(touchedColor);
    }

    @Override
    public void onNormal() {
        panel.color(normalColor);
    }
}
