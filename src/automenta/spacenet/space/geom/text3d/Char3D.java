package automenta.spacenet.space.geom.text3d;

import automenta.spacenet.space.geom.Box;
import com.ardor3d.bounding.OrientedBoundingBox;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.renderer.state.MaterialState;
import com.ardor3d.scenegraph.Mesh;
import java.util.logging.Logger;

/**
 * contains one 3D text character
 * {@link Font3D#createText(String, String, int, boolean, boolean, boolean)}.
 */
public class Char3D extends Box {

    //TODO eliminate 'textNode' and explicitly center (normalize) text meshes - this will eliminate excess bounding volume
    private static final Logger logger = Logger.getLogger(Char3D.class.getName());
    private Font3D font;
    private float height, width;
    private ColorRGBA fontcolor;
    private char c;

    public Char3D(Font3D factory, char c) {
        super(BoxShape.Empty);

        // Save for later
        this.font = factory;
        // Setup a render-container
        // render_mesh = new TriMesh("RenderMesh");
        //render_mesh_bounds = new OrientedBoundingBox();
        // attachChild(render_mesh);

        // And now scale to the correct "size" (all font are size 1)
        //setSize(size);

        // Ready the glyphs
        setChar(c);

    }

    public int getFlags() {
        return 0; // TODO: this should be working
    }

    public char getChar() {
        return c;
    }

    public void setChar(char c) {
        this.c = c;

        // Set width and text to zip
        this.width = 0;
        this.height = 0;
        detachAllChildren();

        Mesh charMesh;

        //textNode = add(new Node());


        double x = 0;
        double y = -0.5;
        double z = -0.5;
        double depth = 1.0;

        Glyph3D glyph = font.getGlyph(c);

        if (glyph == null) {
            logger.severe(this + " could not find glyph for char " + c);
            return;
        }

        double charWidth = glyph.getBounds().getWidth();
        width += charWidth;
        height = (float) Math.max(height, glyph.getBounds().getHeight());


        if (glyph.getChildIndex() != -1) {
            Mesh mesh = new Mesh("" + c);
            Mesh sourceMesh = ((Mesh) font.getRenderNode().getChild(glyph.getChildIndex()));

            mesh.setMeshData(sourceMesh.getMeshData());

//            double depthZ;
//            if (!font.drawBack() && !font.drawSides()) {
//                depthZ = -depth * 1.35;
//            } else {
//                depthZ = 0;
//            }

            mesh.setTranslation(x, y, z);

            mesh.setModelBound(new OrientedBoundingBox());
            attachChild(mesh);

            charMesh = mesh;
        } else {
            charMesh = null;
            return;
        }
        x += charWidth;

        double ns;


        //Normalize: position and scale
        //System.out.println("text: " + text + " width=" + width + " & height=" + height);
        if (width > height) {
            ns = height / width;
        } else {
            ns = width / height;
        }
        //textNode.setScale(ns, ns, 1.0);

        updateGeometricState(0.0f);
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

    public double getHeight() {
        return height;
    }
}
