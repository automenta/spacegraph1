package automenta.spacenet.space.geom.text2d;

import automenta.spacenet.space.geom.Rect;
import com.ardor3d.bounding.OrientedBoundingBox;
import java.util.LinkedList;

import com.ardor3d.math.ColorRGBA;
import com.ardor3d.renderer.state.BlendState;
import com.ardor3d.renderer.state.BlendState.DestinationFunction;
import com.ardor3d.renderer.state.BlendState.SourceFunction;
import com.ardor3d.renderer.state.MaterialState;
import com.ardor3d.renderer.state.ZBufferState;
import com.ardor3d.scenegraph.shape.Quad;

/**
 * 
 * Adapted from code by:
 *      @author Victor Porof, blue_veek@yahoo.com
 */
public class BmpTextLineRect extends Rect {

    private static final long serialVersionUID = 1L;
    public BmpFont gFont;
    private String text;
    private ColorRGBA fill;
    private LinkedList<Quad> charQuads = new LinkedList<Quad>();
    private float kerneling;
    
    private float spacing;

    public BmpTextLineRect(String text, BmpFont gFont, ColorRGBA fill, float kerneling) {
        super(RectShape.Empty);

        this.fill = fill;
        this.gFont = gFont;
        this.kerneling = kerneling;

        BlendState bs = new BlendState();
        bs.setBlendEnabled(true);
        bs.setSourceFunction(SourceFunction.SourceAlpha);
        bs.setDestinationFunction(DestinationFunction.OneMinusSourceAlpha);
        bs.setEnabled(true);
        setRenderState(bs);


        ZBufferState zb = new ZBufferState();
        zb.setWritable(false);
        zb.setEnabled(true);
        setRenderState(zb);

        setText(text);
    }

    protected void updateBmpText() {        
        spacing = 0;

        removeAll(charQuads);
        charQuads.clear();

        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);

            Quad quad = new Quad(String.valueOf(ch), 1f, 1f);

            double cWidth = (double)gFont.getMetricsWidths()[ch];
            double cHeight = (double)gFont.getMetricsHeights();
          
            //double sx = cWidth / cHeight;
            double sx = 1.0;
            double sy = cHeight / cWidth;

            double px = spacing; // + width;
            double py = -sy/2.0f; //gFont.getTextDescent();

            quad.setTranslation(px, py, 0);
            quad.setScale(sx, sy, 1);
            quad.setRenderState(gFont.getChar(ch));
            quad.setModelBound(new OrientedBoundingBox());

            System.out.println("char " + i + " (" + ch + ") p:" + px + ", " + py + "  s:" + sx + ", " + sy + " - c:" + sx +" , " + sy);
            

            if (fill != null) {
                quad.setSolidColor(fill);                
            }
            
            attachChild(quad);
            charQuads.add(quad);

            spacing += cWidth; //gFont.getMetricsWidths()[text.charAt(i)] + kerneling;
        }

        //float scale = 1.0f / gFont.getMetricsHeights();
        double width = 1.0f; //spacing * scale;
        double height = 1.0f;

        setTranslation(-0.5f, -0.25f, 0);
        setScale(width, height, 1.0f);

        MaterialState ms = new MaterialState();
        ms.setDiffuse(fill);
        setRenderState(ms);
        
        updateGeometricState(0.0);
    }

    public void setText(Object text) {
        this.text = String.valueOf(text);
        updateBmpText();
    }

    public String getText() {
        return text;
    }

    public void setFill(ColorRGBA fill) {
        this.fill = fill;
        updateBmpText();
    }

    public ColorRGBA getFill() {
        return fill;
    }

//    public float getWidth() {
//        return width;
//    }
//
//    public float getHeight() {
//        return height;
//    }

    
}
