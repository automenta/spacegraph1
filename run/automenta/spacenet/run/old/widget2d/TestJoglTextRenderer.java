/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package automenta.spacenet.run.old.widget2d;

import com.ardor3d.renderer.Renderer;
import com.ardor3d.scenegraph.Spatial;
import com.sun.opengl.util.j2d.TextRenderer;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

/**
 *
 * @author seh
 */
public class TestJoglTextRenderer extends Spatial {
    private TextRenderer textRenderer;

    @Override
    public void draw(Renderer rndr) {
        GL gl = GLU.getCurrentGL();
        System.out.println(gl);
//        JoglRenderer r = (JoglRenderer)rndr;
//
//        if (textRenderer==null)
//            textRenderer = new TextRenderer(new Font("SansSerif", Font.PLAIN, 72));
//
//    GL2 gl = get
//    gl.glEnable(GL2.GL_DEPTH_TEST);
//
//    // Compute the scale factor of the largest string which will make
//    // them all fit on the faces of the cube
//    Rectangle2D bounds = renderer.getBounds("Bottom");
//    float w = (float) bounds.getWidth();
//    float h = (float) bounds.getHeight();
//    textScaleFactor = 1.0f / (w * 1.1f);

    }

    @Override
    public void updateWorldBound(boolean bln) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
