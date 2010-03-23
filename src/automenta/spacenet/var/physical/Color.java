/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.spacenet.var.physical;

import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.type.ReadOnlyColorRGBA;
import java.util.LinkedList;
import java.util.List;

public class Color extends ColorRGBA {

    public static final Color White = new Color(1.0f, 1.0f, 1.0f, 1.0f);
    public static final Color Black = new Color(0.0f, 0.0f, 0.0f, 1.0f);
    public static final Color Invisible = new Color(0.0f, 0.0f, 0.0f, 0.0f);
    public static final Color Gray = new Color(0.5f, 0.5f, 0.5f, 1.0f);
    public static final Color GrayPlus = new Color(0.6f, 0.6f, 0.6f, 1.0f);
    public static final Color GrayPlusPlus = new Color(0.7f, 0.7f, 0.7f, 1.0f);
    public static final Color GrayPlusPlusPlus = new Color(0.8f, 0.8f, 0.8f, 1.0f);
    public static final Color GrayMinus = new Color(0.4f, 0.4f, 0.4f, 1.0f);
    public static final Color GrayMinusMinus = new Color(0.3f, 0.3f, 0.3f, 1.0f);
    public static final Color GrayMinusMinusMinus = new Color(0.2f, 0.2f, 0.2f, 1.0f);
    public static final Color GrayTransparent = new Color(0.5f, 0.5f, 0.5f, 0.5f);
    public static final Color Red = new Color(1.0f, 0.0f, 0.0f, 1.0f);
    public static final Color Orange = new Color(1.0f, 0.75f, 0.0f, 1.0f);
    public static final Color Yellow = new Color(1.0f, 1.0f, 0.0f, 1.0f);
    public static final Color Green = new Color(0.0f, 1.0f, 0.0f, 1.0f);
    public static final Color Blue = new Color(0.0f, 0.0f, 1.0f, 1.0f);
    public static final Color Purple = new Color(0.75f, 0.0f, 1.0f, 1.0f);
    public static final Color GrayMinusMinusMinusMinus = new Color(0.1, 0.1, 0.1, 1.0);

    public Color(double r, double g, double b, double a) {
        super((float) r, (float) g, (float) b, (float) a);
    }

    public static Color newRandom() {
        return new Color(Math.random(), Math.random(), Math.random(), Math.random());
    }

    public static Color newRandom(double a) {
        return new Color(Math.random(), Math.random(), Math.random(), a);
    }

    public static Color newRandomHSB(double saturation, double brightness) {
        //TODO remove dependency on AWT for HSB function
        float hue = (float) Math.random();
        java.awt.Color c = java.awt.Color.getHSBColor(hue, (float) saturation, (float) brightness);
        return fromAWTColor(c);
    }

    public static Color fromAWTColor(java.awt.Color c) {
        float f[] = new float[4];
        c.getComponents(f);
        return new Color(f[0], f[1], f[2], f[3]);
    }

    public Color(ReadOnlyColorRGBA c) {
        this(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
    }

    public Color(double r, double g, double b) {
        this(r, g, b, 1.0);
    }


    public java.awt.Color toAWTColor() {
        return new java.awt.Color((float) getRed(), (float) getGreen(), (float) getBlue(), (float) getAlpha());
    }

    public static Color hsb(double hue, double saturation, double brightness) {
        //TODO remove dependency on AWT for HSB function
        java.awt.Color c = java.awt.Color.getHSBColor((float) hue, (float) saturation, (float) brightness);
        return fromAWTColor(c);
    }

    public Color alpha(double alpha) {
        return new Color(getRed(), getGreen(), getBlue(), alpha);
    }

    public static interface IfColorChanges {
        public void onColorChanged(Color c);
    }

    @Override public ColorRGBA set(float r, float g, float b, float a) {
        super.set(r, g, b, a);
        //TODO detect if in-fact has changed (r!=r, g!=g, etc...)
        notifyChanged();
        return this;
    }


    private List<IfColorChanges> ifChanges = new LinkedList();

    public IfColorChanges add(IfColorChanges c) {
        ifChanges.add(c);
        return c;
    }
    public IfColorChanges remove(IfColorChanges c) {
        ifChanges.remove(c);
        return c;
    }

    protected void notifyChanged() {
        for (IfColorChanges c : ifChanges)
            c.onColorChanged(this);
    }
    
}
