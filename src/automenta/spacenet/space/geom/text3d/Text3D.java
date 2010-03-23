package automenta.spacenet.space.geom.text3d;

import automenta.spacenet.space.geom.Box;
import automenta.spacenet.var.physical.Color;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.renderer.state.MaterialState;
import java.util.LinkedList;
import java.util.logging.Logger;

/**
 * This class represents a peace of text compiled using the
 * {@link Font3D#createText(String, String, int, boolean, boolean, boolean)}.
 * 
 * @author emanuel from jME (http://jmonkeyengine.com)
 */
public class Text3D extends Box {

    //TODO eliminate 'textNode' and explicitly center (normalize) text meshes - this will eliminate excess bounding volume
    private static final Logger logger = Logger.getLogger(Text3D.class.getName());
    private Font3D font;
    private double height, width;
    private StringBuffer text = new StringBuffer();
    private ColorRGBA fontcolor;
    private LinkedList<Char3D> charBoxes;

    public Text3D(Font3D font, String text) {
        super(BoxShape.Empty);

        // Save for later
        this.font = font;

        // Ready the glyphs
        setText(text);
    }

    public Text3D(Font3D font, String string, Color color) {
        this(font, string);
        color(color);
    }

    public int getFlags() {
        return 0; // TODO: this should be working
    }

    public StringBuffer getText() {
        return text;
    }

    public void setText(String text) {
        // Set width and text to zip
        this.width = 0;
        this.height = 0;
        this.text.setLength(0);
        detachAllChildren();

        double depth = 1.0;

        charBoxes = new LinkedList<Char3D>();

        for (char c : text.toCharArray()) {
            //Glyph3D glyph = font.getGlyph(c);
            Char3D charBox = new Char3D(font, c);
            charBox.updateGeometricState(0.0f);
            charBoxes.add(charBox);

            height = Math.max(height, charBox.getHeight());
            width += charBox.getWidth();
        }

        double visibleWidth = 0, visibleHeight = 0;
        if (width > height) {
            visibleWidth = 1.0;
            visibleHeight = (height / width) * visibleWidth;
        } else {
            visibleHeight = 1.0;
            visibleWidth = (width / height) * visibleHeight;
        }


        //the current offset values CENTER the text line on the box
        double x = -0.5;// + (1.0 - visibleWidth) / 2.0;
        double y = 0; //-0.5 + (1.0 - visibleHeight);
        double z = 0;
        for (Char3D cb : charBoxes) {
            cb.getPosition().set(x, y, z);
            double xs, ys;
            if (width > height) {
                xs = cb.getWidth() / width;
                ys = (cb.getHeight() / cb.getWidth()) * xs;
            } else {
                ys = cb.getHeight() / height;
                xs = (cb.getWidth() / cb.getHeight()) * ys;
            }
            double zs = 1.0 / Math.max(cb.getWidth(), cb.getHeight());
            x += xs;
            cb.getScale().set(xs, ys, zs);
            attachChild(cb);
        }
        //aspectXY(getCharAspect() / ((double) charBoxes.size()) );

        this.text.append(text);

    }

    public ColorRGBA getFontColor() {
        return fontcolor;
    }

    public void setFontColor(ColorRGBA fontcolor) {

        this.fontcolor = fontcolor;
        if (!font.has_diffuse_material) {
            MaterialState ms = new MaterialState();
            ms.setDiffuse(fontcolor);
            ms.setEnabled(true);
            setRenderState(ms);
        } else {
            logger.warning("You cannot set the font-color on " + "Text3D when the Font3D has a font color already.");
        }
    }

    public double getWidth() {
        return width;
    }

//    /**
//     * Just a hack that positions the text with its center in the origo (only on
//     * the X-axis).
//     */
//    public void alignCenter() {
//        getLocalTranslation().x = -width / 2;
//        updateWorldVectors();
//    }
}
