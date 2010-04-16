/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.spacenet.run.geom.text;

import automenta.spacenet.run.ArdorSpacetime;
import automenta.spacenet.space.geom.Box;
import automenta.spacenet.space.geom.ProcessBox;
import automenta.spacenet.var.physical.Color;
import com.ardor3d.renderer.Renderer;
import com.ardor3d.scenegraph.Spatial;
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

    public static void main(String[] args) {
        ArdorSpacetime.newWindow(new DemoChar2DJogl());
    }

    protected Box newText(final String text, int pointSize) {

        Box r = new Box(BoxShape.Cubic) {

            private TextRenderer renderer;
            private float textScaleFactor;

           
            @Override
            public void draw(Renderer rndr) {
                super.draw(rndr);

                GL gl = GLU.getCurrentGL();
                gl.glFlush();
//
                if (renderer == null) {
                    renderer = new TextRenderer(new Font("SansSerif", Font.PLAIN, 72));
                    renderer.setSmoothing(true);
                    renderer.setUseVertexArrays(false);

                    // Compute the scale factor of the largest string which will make
                    // them all fit on the faces of the cube
                    Rectangle2D bounds = renderer.getBounds("Bottom");
                    float w = (float) bounds.getWidth();
                    float h = (float) bounds.getHeight();
                    textScaleFactor = 1.0f / (w * 1.1f);
                }

                float halfFaceSize = 0.5f;
                renderer.begin3DRendering();
                gl.glEnable(GL.GL_DEPTH_TEST);
                gl.glEnable(GL.GL_CULL_FACE);
//
//                // Note that the defaults for glCullFace and glFrontFace are
//                // GL_BACK and GL_CCW, which match the TextRenderer's definition
//                // of front-facing text.
                Rectangle2D bounds = renderer.getBounds(text);
                float w = (float) bounds.getWidth();
                float h = (float) bounds.getHeight();
                renderer.draw3D(text,
                    w / -2.0f * textScaleFactor,
                    h / -2.0f * textScaleFactor,
                    halfFaceSize,
                    textScaleFactor);
              renderer.end3DRendering();

            }
        };
        return r;
    }


    @Override
    protected void start() {
        add(newText("Abc", 16).scale(1, 1, 1)).color(Color.Orange);


    }
}
