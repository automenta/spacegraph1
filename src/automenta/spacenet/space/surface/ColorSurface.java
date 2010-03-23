/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package automenta.spacenet.space.surface;

import automenta.spacenet.space.SpaceState;
import automenta.spacenet.var.physical.Color;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.renderer.state.MaterialState;
import com.ardor3d.scenegraph.Spatial;

/**
 *
 * @author seh
 */
public class ColorSurface extends MaterialState implements SpaceState  {
    //private final List<Spatial> spatials = new LinkedList();
    private final ColorRGBA color;

    public ColorSurface(ColorRGBA color) {
        super();
        setMaterialFace(MaterialState.MaterialFace.FrontAndBack);
        this.color = color;        
    }

    public ColorSurface(float r, float g, float b) {
        this(new ColorRGBA(r, g, b, 1.0f));
    }

    public ColorSurface() {
        this(0.5f, 0.5f, 0.5f);
    }
    
    public ColorRGBA getColor() {
        return color;
    }

    @Override public void apply(Spatial s) {
        setDiffuse(color);
        setShininess(0.2f);
        s.setRenderState(this);
        //spatials.add(s);
    }

    @Override public void unapply(Spatial s) {    }
    


//    protected void applyAll() {
//        for (Spatial s : spatials) {
//            applyTo(s);
//        }
//    }


    public void color(float r, float g, float b) {
        color.set(r, g, b, 1.0f);
        setDiffuse(color);
    }

    public void color(double r, double g, double b) {
        color((float)r, (float)g, (float)b);
    }

    public void color(Color c) {
        color.set(c);
        setDiffuse(color);
    }

}
