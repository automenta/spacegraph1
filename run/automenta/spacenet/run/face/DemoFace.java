/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.spacenet.run.face;

import automenta.spacenet.run.ArdorSpacetime;
import automenta.spacenet.space.Space;
import automenta.spacenet.space.geom.ProcessBox;
import com.ardor3d.bounding.OrientedBoundingBox;
import com.ardor3d.light.DirectionalLight;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.Vector3;
import com.ardor3d.renderer.queue.RenderBucketType;
import com.ardor3d.renderer.state.LightState;
import com.ardor3d.renderer.state.ZBufferState;
import com.ardor3d.scenegraph.shape.Quad;

/**
 *
 * @author seh
 */
public class DemoFace extends ProcessBox {

    public static class FaceLayer extends Space {

        public FaceLayer() {
            super();

//            final LightState ls = new LightState();
//            ls.setEnabled(true);
//            final DirectionalLight dLight = new DirectionalLight();
//            dLight.setEnabled(true);
//            dLight.setDiffuse(new ColorRGBA(1, 1, 1, 1));
//            dLight.setDirection(new Vector3(-1, -1, -1));
//            ls.attach(dLight);
//            final DirectionalLight dLight2 = new DirectionalLight();
//            dLight2.setEnabled(true);
//            dLight2.setDiffuse(new ColorRGBA(1, 1, 1, 1));
//            dLight2.setDirection(new Vector3(1, 1, 1));
//            ls.attach(dLight2);
//            ls.setTwoSidedLighting(false);
//            //setRenderState(ls);
//
//            final ZBufferState zstate = new ZBufferState();
//            zstate.setWritable(false);
//            zstate.setEnabled(false);
//            //setRenderState(zstate);
//
//            //getSceneHints().setRenderBucketType(RenderBucketType.Ortho);
//            //getSceneHints().setLightCombineMode(LightCombineMode.Off);
//            //getSceneHints().setCullHint(CullHint.Never);
//
//            //add(new Box(BoxShape.Spheroid).scale(3, 3, 3).move(1, 1, 0));
//            //add(new Text3D(DemoButton.font, "a"));
//
//            Quad q = add(new Quad("", 1, 1));
//            q.setDefaultColor(ColorRGBA.ORANGE);
//            q.setModelBound(new OrientedBoundingBox());
//            //q.getSceneHints().setRenderBucketType(RenderBucketType.Ortho);
        }
    }

    @Override
    protected void start() {

        add(new FaceLayer());
    }

    public static void main(String[] args) {
        ArdorSpacetime.newWindow(new DemoFace());
    }
}
