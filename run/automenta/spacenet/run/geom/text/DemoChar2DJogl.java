/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.spacenet.run.geom.text;

import automenta.spacenet.run.ArdorWindow;
import automenta.spacenet.space.geom.ProcessBox;
import automenta.spacenet.var.Maths;
import com.ardor3d.math.Vector3;
import com.ardor3d.renderer.Renderer;
import com.sun.opengl.util.j2d.TextRenderer;
import java.awt.Font;
import java.awt.geom.Rectangle2D;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

/**
 *
 * @author seh
 */
public class DemoChar2DJogl extends ProcessBox {

    public static class JoglFont {

        public TextRenderer renderer;

        public void ensureInit() {
            if (renderer == null) {
                renderer = new TextRenderer(new Font("SansSerif", Font.PLAIN, 72));
                renderer.setSmoothing(true);
                renderer.setUseVertexArrays(false);
            }
        }
    }

    //TODO try a more lightweight Ardor shape than Box
    public static class JoglText extends com.ardor3d.scenegraph.shape.Box {

        private JoglFont font;
        private String text;

        public JoglText(JoglFont font, String initialText) {
            super("name", new Vector3(0, 0, 0), 1f, 1f, 1f);
            this.font = font;
            this.text = initialText;
        }
        private float textScaleFactor;

        @Override
        public void draw(Renderer rndr) {
            //super.draw(rndr);

            GL gl = GLU.getCurrentGL();
            gl.glFlush();

            font.ensureInit();

            // Compute the scale factor of the largest string which will make
            // them all fit on the faces of the cube
            Rectangle2D xbounds = font.renderer.getBounds("Bottom");
            float bw = (float) xbounds.getWidth();

            textScaleFactor = 1.0f / (bw * 1.1f);

            float halfFaceSize = 1.5f;

            font.renderer.begin3DRendering();
            gl.glEnable(GL.GL_DEPTH_TEST);
            gl.glEnable(GL.GL_CULL_FACE);

//
//                // Note that the defaults for glCullFace and glFrontFace are
//                // GL_BACK and GL_CCW, which match the TextRenderer's definition
//                // of front-facing text.
            Rectangle2D bounds = font.renderer.getBounds(text);
            float w = (float) bounds.getWidth();
            float h = (float) bounds.getHeight();
            float x = getTranslation().getXf();
            float y = getTranslation().getYf();
            float z = getTranslation().getZf();
            font.renderer.draw3D(text,
                x + w / -2.0f * textScaleFactor,
                y + h / -2.0f * textScaleFactor,
                z + halfFaceSize,
                textScaleFactor);
            font.renderer.end3DRendering();

        }
    };

    public static void main(String[] args) {
        new ArdorWindow().withVolume(new DemoChar2DJogl());
    }

    @Override
    protected void start() {
        JoglFont font = new JoglFont();

        for (int i = 0; i < 500; i++) {
            JoglText jt = add(new JoglText(font, "Abc"));
            jt.addTranslation(Maths.random(-5, 5), Maths.random(-5, 5), Maths.random(-5, 5));
            jt.updateGeometricState(0.0f);
        }
    }
}
