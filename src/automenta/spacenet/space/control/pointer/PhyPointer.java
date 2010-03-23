/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package automenta.spacenet.space.control.pointer;

/**
controller for dragging the currently touched object with JBullet
 */
public class PhyPointer {

//    	public void mouseFunc(int button, int state, int x, int y) {
//		//printf("button %i, state %i, x=%i,y=%i\n",button,state,x,y);
//		//button 0, state 0 means left mouse down
//
//		Vector3f rayTo = new Vector3f(getRayTo(x, y));
//
//		switch (button) {
//			case 2: {
//				if (state == 0) {
//					shootBox(rayTo);
//				}
//				break;
//			}
//			case 1: {
//				if (state == 0) {
//					// apply an impulse
//					if (dynamicsWorld != null) {
//						CollisionWorld.ClosestRayResultCallback rayCallback = new CollisionWorld.ClosestRayResultCallback(cameraPosition, rayTo);
//						dynamicsWorld.rayTest(cameraPosition, rayTo, rayCallback);
//						if (rayCallback.hasHit()) {
//							RigidBody body = RigidBody.upcast(rayCallback.collisionObject);
//							if (body != null) {
//								body.setActivationState(CollisionObject.ACTIVE_TAG);
//								Vector3f impulse = new Vector3f(rayTo);
//								impulse.normalize();
//								float impulseStrength = 10f;
//								impulse.scale(impulseStrength);
//								Vector3f relPos = new Vector3f();
//								relPos.sub(rayCallback.hitPointWorld, body.getCenterOfMassPosition(new Vector3f()));
//								body.applyImpulse(impulse, relPos);
//							}
//						}
//					}
//				}
//				else {
//				}
//				break;
//			}
//			case 0: {
//				if (state == 0) {
//					// add a point to point constraint for picking
//					if (dynamicsWorld != null) {
//						CollisionWorld.ClosestRayResultCallback rayCallback = new CollisionWorld.ClosestRayResultCallback(cameraPosition, rayTo);
//						dynamicsWorld.rayTest(cameraPosition, rayTo, rayCallback);
//						if (rayCallback.hasHit()) {
//							RigidBody body = RigidBody.upcast(rayCallback.collisionObject);
//							if (body != null) {
//								// other exclusions?
//								if (!(body.isStaticObject() || body.isKinematicObject())) {
//									pickedBody = body;
//									pickedBody.setActivationState(CollisionObject.DISABLE_DEACTIVATION);
//
//									Vector3f pickPos = new Vector3f(rayCallback.hitPointWorld);
//
//									Transform tmpTrans = body.getCenterOfMassTransform(new Transform());
//									tmpTrans.inverse();
//									Vector3f localPivot = new Vector3f(pickPos);
//									tmpTrans.transform(localPivot);
//
//									Point2PointConstraint p2p = new Point2PointConstraint(body, localPivot);
//									dynamicsWorld.addConstraint(p2p);
//									pickConstraint = p2p;
//									// save mouse position for dragging
//									BulletStats.gOldPickingPos.set(rayTo);
//									Vector3f eyePos = new Vector3f(cameraPosition);
//									Vector3f tmp = new Vector3f();
//									tmp.sub(pickPos, eyePos);
//									BulletStats.gOldPickingDist = tmp.length();
//									// very weak constraint for picking
//									p2p.setting.tau = 0.1f;
//								}
//							}
//						}
//					}
//
//				}
//				else {
//
//					if (pickConstraint != null && dynamicsWorld != null) {
//						dynamicsWorld.removeConstraint(pickConstraint);
//						// delete m_pickConstraint;
//						//printf("removed constraint %i",gPickingConstraintId);
//						pickConstraint = null;
//						pickedBody.forceActivationState(CollisionObject.ACTIVE_TAG);
//						pickedBody.setDeactivationTime(0f);
//						pickedBody = null;
//					}
//				}
//				break;
//			}
//			default: {
//			}
//		}
//	}
//
//	public void mouseMotionFunc(int x, int y) {
//		if (pickConstraint != null) {
//			// move the constraint pivot
//			Point2PointConstraint p2p = (Point2PointConstraint) pickConstraint;
//			if (p2p != null) {
//				// keep it at the same picking distance
//
//				Vector3f newRayTo = new Vector3f(getRayTo(x, y));
//				Vector3f eyePos = new Vector3f(cameraPosition);
//				Vector3f dir = new Vector3f();
//				dir.sub(newRayTo, eyePos);
//				dir.normalize();
//				dir.scale(BulletStats.gOldPickingDist);
//
//				Vector3f newPos = new Vector3f();
//				newPos.add(eyePos, dir);
//				p2p.setPivotB(newPos);
//			}
//		}
//	}

}
