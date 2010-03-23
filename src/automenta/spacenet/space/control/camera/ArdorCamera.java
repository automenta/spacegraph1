/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.spacenet.space.control.camera;

import automenta.spacenet.run.ArdorSpacetime;
import automenta.spacenet.space.Repeat;
import automenta.spacenet.space.control.Tangible;
import automenta.spacenet.space.control.Zoomable;
import automenta.spacenet.var.scalar.DoubleVar;
import automenta.spacenet.var.vector.V3;
import com.ardor3d.bounding.OrientedBoundingBox;
import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyVector3;
import com.ardor3d.renderer.Camera;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.scenegraph.Spatial;

/**
 *
 * @author seh
 */
public class ArdorCamera extends Repeat {

    private final ArdorSpacetime spacetime;
    private Camera cam;
    private V3 targetPosition = new V3();
    private V3 targetTarget = new V3();
    private V3 targetUp = new V3();
    private final V3 currentPosition;
    private final V3 currentTarget;
    private final V3 currentUp;
    private Vector3 currentDir = new Vector3();
    private Vector3 currentLeft = new Vector3();
    private Zoomable currentZoomed;
    private DoubleVar viewAngle = new DoubleVar(45.0 * 2 * Math.PI / 360.0);
    
    double zoomBoundsFactor = 1.2;
    
    double positionSpeed = 4.0; //in units per second

    private final DoubleVar near, far;
    private Vector3 targetDir = new Vector3();
    private Vector3 targetLeft = new Vector3();

//    double targetSpeed = 2.0;
    public ArdorCamera(ArdorSpacetime spacetime, V3 currentPosition, V3 currentTarget, V3 currentUp) {
        super();

        this.near = new DoubleVar(1);
        this.far = new DoubleVar(1000);

        this.spacetime = spacetime;
        //this.cam = spacetime.getVideo().getCanvasRenderer().getCamera();

        this.currentPosition = currentPosition;
        this.currentTarget = currentTarget;
        this.currentUp = currentUp;

        targetUp.set(currentUp);
        targetPosition.set(currentPosition);
        targetTarget.set(currentTarget);
    }

    @Override
    protected void update(double t, double dt, Spatial s) {

        currentPosition.interpolate(targetPosition, dt * getPositionSpeed());
        currentTarget.interpolate(targetTarget, dt * getPositionSpeed());
        currentUp.interpolate(targetUp, dt * getPositionSpeed());
        currentUp.normalizeLocal();

        if (this.cam == null)
            this.cam = spacetime.getVideo().getCanvasRenderer().getCamera();
        
//        cam.setDepthRangeFar(getFar().d());
//        cam.setDepthRangeNear(getNear().d());
        cam.setFrustumNear(getNear().d());
        cam.setFrustumFar(getFar().d());

        cam.setLocation(currentPosition);
        cam.lookAt(currentTarget, currentUp);

        cam.normalize();
        cam.update();
    }

    public DoubleVar getNear() {
        return near;
    }

    public DoubleVar getFar() {
        return far;
    }

//    public double getTargetSpeed() {
//        return targetSpeed;
//    }
    public double getPositionSpeed() {
        return positionSpeed;
    }

    public void zoomTo(Zoomable z, Tangible touched, Mesh pickedMesh) {

        if (currentZoomed != z) {
            if (currentZoomed != null) {
                currentZoomed.onZoomStop();
                currentZoomed = null;
            }
        }


        if ((z != null) && (touched != null)) {
            Spatial s = pickedMesh;


            double tx = s.getWorldBound().getCenter().getX();
            double ty = s.getWorldBound().getCenter().getY();
            double tz = s.getWorldBound().getCenter().getZ();

            Vector3 up = new Vector3(0, 1, 0);

            //Vector3 normal = s.getWorldRotation();
            if (s.getWorldBound() instanceof OrientedBoundingBox) {
                OrientedBoundingBox ob = ((OrientedBoundingBox) s.getWorldBound());

                ReadOnlyVector3 normal = ob.getZAxis();

                double r = ob.getExtent().length();

                double va = getViewAngle().d();

                double viewDist = 2 * r * zoomBoundsFactor * Math.sin(Math.PI / 4 - va / 2) / Math.sin(va / 2);

                //limit zooming to no closer than the near clipping plane times a certain factor (ex: 2.0)
                viewDist = Math.max(viewDist, getNear().d() * 2.0);

                double px = tx + normal.getX() * viewDist;
                double py = ty + normal.getY() * viewDist;
                double pz = tz + normal.getZ() * viewDist;

                ReadOnlyVector3 tup = ob.getYAxis();

                targetPosition.set(px, py, pz);

                targetUp.set(tup);
            } else {
            }


            targetTarget.set(tx, ty, tz);

            currentZoomed = z;
            z.onZoomStart();
        }
    }

//	public void zoomTo(Spacetime spaceTime, Space spatial, double zoomBoundsFactor) {
//		if (spatial instanceof HasPosition3) {
//			double x = ((HasPosition3)spatial).getAbsolutePosition().x();
//			double y = ((HasPosition3)spatial).getAbsolutePosition().y();
//			double z = ((HasPosition3)spatial).getAbsolutePosition().z();
//
//			double r;
//			if ((spatial instanceof HasSize3)) {
//				double w = ((HasSize3)spatial).getAbsoluteSize().x();
//				double h = ((HasSize3)spatial).getAbsoluteSize().y();
//				double d = ((HasSize3)spatial).getAbsoluteSize().z();
//
//				//r = Math.max(w, Math.max(h, d));
//				r = Math.max(w, h);
//			}
//			else if (spatial instanceof HasSize2) {
//				double w = ((HasSize2)spatial).getAbsoluteSize().x();
//				double h = ((HasSize2)spatial).getAbsoluteSize().y();
//
//				r = Math.max(w, h);
//			}
//			else {
//				r = 1;
//			}
//
//			if (spatial instanceof HasOrientation) {
//				HasOrientation ho = (HasOrientation) spatial;
//				double tilt = ho.getAbsoluteOrientation().z();
//
//
//				q.fromAngles(ho.getAbsoluteOrientation().x(), ho.getAbsoluteOrientation().y(), ho.getAbsoluteOrientation().z());
//				if (qRot[0] == null) {
//					qRot[0] = new fVector3();
//					qRot[1] = new fVector3();
//					qRot[2] = new fVector3();
//				}
//				q.toAxes(qRot);
//
//				spaceTime.video().getUp().set(qRot[1].getX(), qRot[1].getY(), qRot[1].getZ());
//			}
//
//			double viewAngle = Math.toRadians(spaceTime.video().getFocusAngle().get());
//
//			double viewingDistance = 2 * r * zoomBoundsFactor * Math.sin(Math.PI/4 - viewAngle/2) / Math.sin(viewAngle/2);
//
////			logger.info("Zoom to focused: " + spatial + " pos=" + x + "," + y + ", " + z);
////			logger.info("  r=" + r);
////			logger.info("  dist=" + viewingDistance);
//
//			//nextSightPosition.set(x, y, z + viewingDistance);
//
//			((HasPosition3)spatial).getAbsoluteNormal(vNormal);
//			vNormal.multiply(viewingDistance);
//
//			Vector3 p = new Vector3(x + vNormal.x(), y + vNormal.y(), z + vNormal.z());
//			Vector3 t = new Vector3(x,y,z);
//
//
//			spaceTime.video().getPosition().set(p);
//			spaceTime.video().getTarget().set(t);
//
//		}
//
//	}
    public V3 getTargetPosition() {
        return targetPosition;
    }

    public V3 getCurrentPosition() {
        return currentPosition;
    }

    public V3 getCurrentUp() {
        return currentUp;
    }

    public Vector3 getCurrentDirection() {
        currentDir.set(currentTarget);
        currentDir.subtractLocal(currentPosition);
        currentDir.normalizeLocal();
        return currentDir;
    }
    public Vector3 getTargetDirection() {
        targetDir.set(targetTarget);
        targetDir.subtractLocal(targetPosition);
        targetDir.normalizeLocal();
        return targetDir;
    }

    public Vector3 getCurrentLeft() {
        Vector3 direction = getCurrentDirection();
        Vector3 up = getCurrentUp();
        currentLeft.set(up);
        currentLeft.crossLocal(direction);
        currentLeft.normalizeLocal();
        return currentLeft;
    }
    public Vector3 getTargetLeft() {
        Vector3 direction = getTargetDirection();
        Vector3 up = getTargetUp();
        targetLeft.set(up);
        targetLeft.crossLocal(direction);
        targetLeft.normalizeLocal();
        return targetLeft;
    }

    public Vector3 getTargetTarget() {
        return targetTarget;
    }

    public Vector3 getTargetUp() {
        return targetUp;
    }

    public DoubleVar getViewAngle() {
        return viewAngle;
    }

    public void zoomForward(double l) {
        Vector3 d = getCurrentDirection().clone();
        d.multiplyLocal(l);
        getTargetPosition().addLocal(d);
    }

    public void setTargetToCurrent() {
        getCurrentUp().set(getTargetUp());
        getCurrentPosition().set(getTargetPosition());
        getCurrentTarget().set(getTargetTarget());
    }

    public V3 getCurrentTarget() {
        return currentTarget;
    }

    
}
