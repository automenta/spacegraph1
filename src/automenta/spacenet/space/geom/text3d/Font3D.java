/*
 * Copyright (c) 2003-2009 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software 
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package automenta.spacenet.space.geom.text3d;

import automenta.spacenet.space.geom.text3d.math.ClosedPolygon;
import com.ardor3d.math.Vector3;
import com.ardor3d.renderer.state.BlendState;
import com.ardor3d.renderer.state.MaterialState;
import com.ardor3d.renderer.state.ZBufferState;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.util.scenegraph.CompileOptions;
import java.awt.Font;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.FlatteningPathIterator;
import java.awt.geom.PathIterator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Logger;

/**
 * This class represents a font ready to be used for 3D.
 *
 * Known bugs:
 * 
 * - When glyphs are constructed from other glyphs, the shape returned by
 *   gv.getGlyphOutline(0); has them all cluddered up. This might be a bug in the
 *   VM, and I have no time to fix it, that is why the loading of each glyph has a
 *   try-catch-all statement around it.
 * 
 * @author emanuel
 */
public class Font3D {

    private static final Logger logger = Logger.getLogger(Font3D.class.getName());
    private static Hashtable<String, Font3D> loadedFonts = new Hashtable<String, Font3D>();
    // This Node is only used for rendering
    Node renderNode = new Node();
    // The glyphs created from the font.
    //Glyph3D glyph3Ds[] = new Glyph3D[256];
    private Map<Character, Glyph3D> glyphs3Ds = new HashMap();
    // Settings
    Font font;
    private double flatness;
    private boolean drawSides;
    private boolean drawFront;
    private boolean drawBack;
    private static BlendState general_alphastate = null;
    private static MaterialState general_diffuse_material = null;
    boolean has_alpha_blending = false;
    boolean has_diffuse_material = false;
    private final CompileOptions options;

    protected Glyph3D updateGlyph(char g) {
        

        try {
            // if(g != 'H') // TEST
            // continue;
            // logger.info("Glyph: "+g+":"+(char)g);

            // GlyphVector gv = font.createGlyphVector(new
            // FontRenderContext(null, true, true), new char[] { (char)g });
            GlyphVector gv = font.layoutGlyphVector(new FontRenderContext(
                null, true, true), new char[]{(char) g}, 0, 1, 0);
            gv.performDefaultLayout();
            ClosedPolygon closedPolygon = null;
            Glyph3D fontGlyph = new Glyph3D((char) g);

            // Get the shape
            Shape s = gv.getGlyphOutline(0);
            // GlyphMetrics metrics = gv.getGlyphMetrics(0);
            PathIterator pi = new FlatteningPathIterator(s.getPathIterator(new AffineTransform()), flatness);
            // logger.info("\n\n\n\nWIND IS BLOWING:
            // "+(pi.getWindingRule() == PathIterator.WIND_EVEN_ODD ?
            // "WIND_EVEN_ODD" : "WIND_NON_ZERO"));
            float[] coords = new float[6];
            while (!pi.isDone()) {
                int seg = pi.currentSegment(coords);
                switch (seg) {
                    case PathIterator.SEG_MOVETO:
                        closedPolygon = new ClosedPolygon();
                        closedPolygon.addPoint(new Vector3(coords[0],
                            -coords[1], 0));
                        break;
                    case PathIterator.SEG_LINETO:
                        closedPolygon.addPoint(new Vector3(coords[0],
                            -coords[1], 0));
                        break;
                    case PathIterator.SEG_CLOSE:
                        closedPolygon.close();
                        fontGlyph.addPolygon(closedPolygon);
                        closedPolygon = null;
                        break;
                    default:
                        throw new IllegalArgumentException(
                            "unknown segment type " + seg);
                }
                pi.next();
            }


            // If we added something then we have a valid glyph !
            fontGlyph.setBounds(gv.getGlyphLogicalBounds(0).getBounds2D());
            if (!fontGlyph.isEmpty()) {
                // Time to triangulate the surface of the glyph
                fontGlyph.triangulate();

                // And create the actual geometry.
                fontGlyph.generateMesh(drawSides, drawFront, drawBack);
                if (fontGlyph.getMesh() != null) {

                    fontGlyph.setChildIndex(renderNode.getNumberOfChildren());
                    renderNode.attachChild(fontGlyph.getMesh());
                }


                //TODO use DisplayList's for rendering optimization - see Ardor3D's DisplayListExample.java
                //SceneCompiler.compile(fontGlyph.getMesh(), _canvas.getCanvasRenderer().getRenderer(), options);
                
                //RenderDelegate delegate = fontGlyph.getMesh().getRenderDelegate(ContextManager.getCurrentContext().getGlContextRep());

            }
            glyphs3Ds.put(g, fontGlyph);
            return fontGlyph;
        } catch (Exception e) {
            //logger.log(Level.INFO, e + " - error in char: (" + g + ":" + (char) g + "), the following is most likely due to glyphs constructed " + "from other glyphs.... that does not work.", e);
        }
        return null;
    }

    public Font3D(Font font, double flatness) {
        this(font, flatness, true, false, false);
    }

    // Create the
    public Font3D(Font font, double flatness, boolean drawFront, boolean drawSides, boolean drawBack) {
        if (font.getSize() != 1) {
            font = font.deriveFont(1.0f);
        }
        // Save for later
        this.font = font;
        this.flatness = flatness;
        this.drawSides = drawSides;
        this.drawFront = drawFront;
        this.drawBack = drawBack;

        options = new CompileOptions();
        options.setDisplayList(true);

        // Clear our "parent node"
        renderNode.detachAllChildren();

        updateRange(0, 256);

        // Apply a Z-state
        ZBufferState zstate = new ZBufferState();
        zstate.setFunction(ZBufferState.TestFunction.LessThan);
        zstate.setWritable(true);
        zstate.setEnabled(true);
        renderNode.setRenderState(zstate);


        // Finally create display-lists for each mesh
        //renderNode.lockMeshes();
    }

    /**
     * This method is used when text wants to render, much like the shared
     * 
     * @return
     */
    public Node getRenderNode() {
        return renderNode;
    }

    /**
     * Method for creating the text from the font. TODO: react on the flags
     * parameter.
     * 
     * @param text
     * @param size
     * @param flags
     * @return
     */
    public Text3D createText(String text, int flags) {

        Text3D text_obj = new Text3D(this, text);

        return text_obj;
    }

    /**
     * This method loads and caches a font, call this before calls to
     * {@link #createText(String, int)}.
     * 
     * @param fontname
     * @param font
     */
    public static void loadFont3D(String fontname, Font font, double flatness,
        boolean drawSides, boolean drawFront, boolean drawBack) {
        logger.info("FontSize:  " + font.getSize());
        logger.info("FontSize2D:" + font.getSize2D());
        Font3D f = new Font3D(font, flatness, drawFront, drawSides, drawBack);
        loadedFonts.put(fontname, f);
    }

    /**
     * Removes a cached Font3D.
     * 
     * @param fontname
     */
    public static void unloadFont(String fontname) {
        loadedFonts.remove(fontname);
    }

    /**
     * This method will create a peace of 3d text from this font.
     * 
     * @param fontname
     * @param text
     * @param size
     * @return
     */
    public static Text3D createText(String fontname, String text, int flags) {
        // Find the cached font and create a text instance.
        Font3D cachedf = loadedFonts.get(fontname);

        return cachedf.createText(text, flags);
    }

    public Glyph3D getGlyph(char c) {
        Glyph3D g = glyphs3Ds.get(c);
        if (g == null) {
            g = updateGlyph(c);
        }
        return g;
    }

    public Font getFont() {
        return font;
    }

    public double getFlatness() {
        return flatness;
    }

    public boolean drawSides() {
        return drawSides;
    }

    public boolean drawFront() {
        return drawFront;
    }

    public boolean drawBack() {
        return drawBack;
    }

    public boolean isMeshLocked() {
        //return (renderNode.getLocks() & Spatial.LOCKED_MESH_DATA) != 0;
        return false;
    }

    public void unlockMesh() {
        //renderNode.unlockMeshes();
    }

    public void lockMesh() {
        //renderNode.lockMeshes();
    }

    public void enableBlendState() {
        if (has_alpha_blending) {
            return;
        }

        if (general_alphastate == null) {
            general_alphastate = new BlendState();
            general_alphastate.setBlendEnabled(true);
            general_alphastate.setSourceFunction(BlendState.SourceFunction.SourceAlpha);
            general_alphastate.setDestinationFunction(BlendState.DestinationFunction.OneMinusSourceAlpha);
            general_alphastate.setTestEnabled(true);
            general_alphastate.setTestFunction(BlendState.TestFunction.Always);
            general_alphastate.setEnabled(true);
        }

        //renderNode.setRenderQueueMode(Renderer.QUEUE_TRANSPARENT);

        renderNode.setRenderState(general_alphastate);
        has_alpha_blending = true;

    }

    public void enableDiffuseMaterial() {
        if (has_diffuse_material) {
            return;
        }

        if (general_diffuse_material == null) {
            general_diffuse_material = new MaterialState();
            general_diffuse_material.setEnabled(true);
            general_diffuse_material.setColorMaterial(MaterialState.ColorMaterial.Diffuse);
        }
        renderNode.setRenderState(general_diffuse_material);

    }

    protected void updateRange(int start, int stop) {
        // Generate the glyphs
        for (int g = start; g < stop; g++) {
            //HACK: wrap updateGlyph because it often throws "unnecessary" geometric errors
            try {
                updateGlyph((char)g);
            } catch (Exception e) {            }
        }

    }
}
//public class MVectorFont extends Font3D {
//	private static final Logger logger = Logger.getLogger(MVectorFont.class);
//
//	private ColorRGBA col;
//
//	final private VectorFont vectorFont;
//
//	protected boolean drawSides = false;
//
//	private final Jme jme;
//	protected final static boolean drawFront = true;
//	protected final static boolean drawBack = false;
//
//	protected static Map<VectorFont, MVectorFont> fonts = new FastMap<VectorFont, MVectorFont>();
//
//	static {
//		java.util.logging.Logger.getLogger(Font3D.class.getName()).setLevel(Level.OFF);
//		java.util.logging.Logger.getLogger(Triangulator.class.getName()).setLevel(Level.OFF);
//		java.util.logging.Logger.getLogger(TriangulationVertex.class.getName()).setLevel(Level.OFF);
//		java.util.logging.Logger.getLogger("com.jmex.font3d.math.Triangulator$SweepLineComparer").setLevel(Level.OFF);
//		java.util.logging.Logger.getLogger("com.jmex.font3d.math.Triangulator$1SweepLineStatus").setLevel(Level.OFF);
//	}
//
//	protected MVectorFont(Jme jme, final VectorFont font) {
//		super(font.getFont(), font.getFlatness(), font.hasSides(), drawFront,  drawBack );
//
//		logger.info(this + " loaded font: " + font + " -> [ " + getRenderNode() + " ]");
//		this.jme = jme;
//		this.vectorFont = font;
//
//
//		setColor(Jme.asJMEColor( font.getDefaultColor() ) );
//	}
//
//	public void setColor(final ColorRGBA c) {
//		this.col = c;
//
//		//				ColorRGBA cStart = new ColorRGBA(1,0,0,1);
//		//				ColorRGBA cStop = new ColorRGBA(0,1,0,0f);
//		//				Font3DGradient gradient = new Font3DGradient(Vector3f.UNIT_X, cStart, cStop);
//		//				gradient.applyEffect(this);
//
//		if (getVectorFont().getBorderColor()!=null) {
//			ColorRGBA innerColor = c;
//			ColorRGBA borderColor = Jme.asJMEColor(getVectorFont().getBorderColor());
//
//			Font3DBorder fontborder = new Font3DBorder(getBorderWidth(), innerColor, borderColor, this);
//			fontborder.applyEffect(this);
//		}
//
//		//this.enableBlendState();
//		//this.enableDiffuseMaterial();
//		//
//		//
//		this.getRenderNode().updateGeometricState(0.0f, true);
//		this.getRenderNode().updateRenderState();
//
//	}
//
//	private float getBorderWidth() {
//		return 0.02f;
//	}
//
//	public VectorFont getVectorFont() {
//		return vectorFont;
//	}
//
//
//	public static MVectorFont getFont(Jme jme, VectorFont font) {
//		MVectorFont f;
//		f = fonts.get(font);
//		if (f == null) {
//			f = new MVectorFont(jme, font);
//			fonts.put(font, f);
//		}
//		return f;
//	}
//}

