/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.spacenet.space.control.camera;

import automenta.spacenet.space.Repeat;
import automenta.spacenet.space.geom.Box;
import com.ardor3d.math.Vector3;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.Spatial;

/**
 *
 * @author seh
 */
public class FacesCameraBox extends Box {

    double updatePeriod = 0.05;
    final Vector3 delta = new Vector3();
    final Vector3 up = new Vector3(0, 1, 0);

    public FacesCameraBox() {
        super(BoxShape.Empty);
    }

    public FacesCameraBox(Node n) {
        this();
        add(n);
    }

    @Override
    protected void afterAttached(Spatial parent) {
        super.afterAttached(parent);


        add(new Repeat(updatePeriod) {

            @Override protected void update(double t, double dt, Spatial s) {
                final ArdorCamera cam = getSpacetime().getCamera();
                delta.set(cam.getCurrentPosition()).subtractLocal(getWorldTranslation());
                getOrientation().lookAt(delta, up);
            }
        });
    }
}
