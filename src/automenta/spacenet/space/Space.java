/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.spacenet.space;

import automenta.spacenet.space.surface.ColorSurface;
import automenta.spacenet.var.physical.Color;
import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyMatrix3;
import com.ardor3d.renderer.Renderer;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.Spatial;
import com.ardor3d.scenegraph.hint.CullHint;
import com.ardor3d.util.scenegraph.RenderDelegate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/** wraps Ardor3D node with some convenient abilities and assumptions */
public class Space extends Node {

    private Map<Class<? extends SpaceState>, SpaceState> properties = new HashMap();
    private Spacetime spacetime;

    @Override
    public String toString() {
        if (getName() == null) {
            return "? " + getClass().getName();
        }
        return super.toString();
    }

    public <S extends Spatial> S add(S s) {
        if (attachChild(s) == -1) {
            return null;
        }
        return s;
    }

    public <S extends Spatial> S remove(S s) {
        if (detachChild(s) == -1) {
            return null;
        }
        return s;
    }

    public Spacetime getSpacetime() {
        if (spacetime == null) {
            updateSpacetime();
        }

        return spacetime;
    }

    protected void updateSpacetime() {
        spacetime = null;
        if (this instanceof Spacetime) {
            spacetime = ((Spacetime) this);
            return;
        } else {
            Node n = getParent();
            while (n != null) {
                if (n instanceof Spacetime) {
                    spacetime = ((Spacetime) n);
                    return;
                }
                n = n.getParent();
            }
        }
    }

    public <R extends Repeat> R add(R r) {
        addController(r);
        return r;
    }

    public <R extends Repeat> R remove(R r) {
        if (!removeController(r)) {
            return null;
        }
        return r;
    }

    public <S extends SpaceState> S add(S spaceState) {
        SpaceState removed = properties.put(spaceState.getClass(), spaceState);
        if (removed != null) {
            removed.unapply(this);
        }
        spaceState.apply(this);
        return spaceState;
    }

    public <S extends SpaceState> S remove(S cs) {
        if (properties.remove(cs.getClass()) != null) {
            cs.unapply(this);
        }
        return cs;
    }

    public ColorSurface color(Color c) {
        ColorSurface oldProperty = (ColorSurface) properties.remove(ColorSurface.class);
        if (oldProperty != null) {
            //cs.unApply(this);
        }

        ColorSurface cs = new ColorSurface(c);
        add(cs);
        return cs;
    }

    @Override
    protected void setParent(Node parent) {
        Spatial previousParent = getParent();

        if (parent == null) {
            beforeDetached(previousParent);
        }

        super.setParent(parent);

        if (parent != null) {
            updateSpacetime();
            afterAttached(parent);
        }
    }

    protected void afterAttached(Spatial parent) {
    }

    protected void beforeDetached(Spatial previousParent) {
    }

    public void removeAll(Collection<? extends Spatial> c) {
        for (Spatial s : c) {
            remove(s);
        }
    }

    public void removeAll() {
        detachAllChildren();
    }

    public static void applyAspectXY(Vector3 worldScale, Vector3 worldTranslation, ReadOnlyMatrix3 worldRotation, double a, double alX, double alY, Vector3 vA) {
        if (a != 0) {

            //		float wx = worldScale.getX();
            //		float wy = worldScale.getY();

            double sx = worldScale.getX();
            double sy = worldScale.getY();
            double sz = worldScale.getZ();

            //original values
            double ox = sx;
            double oy = sy;
            double oz = sz;

            double px = 0, py = 0, pz = 0;

            double ex = 0, ey = 0, ez = 0;
            //constraint sx, sy according to aspect
            double n = sy / sx;
            if (a > n) {
                //visually taller, so shrink width & preserve height
                ex = 1.0 - (n / a);
                sx = (n / a) * sx;
            } else {
                //visually wider, so shrink height & preserve width
                ey = 1.0 - (a / n);
                sy = (a / n) * sy;
            }

            px = alX * (ox - sx) / 2.0;
            py = alY * (oy - sy) / 2.0;


            //rotate the delta vector by the absolute orientation
            vA.set((float) px, (float) py, (float) pz);

            worldRotation.applyPost(vA, vA);
            px = vA.getX();
            py = vA.getY();
            pz = vA.getZ();

            //"correct" the scale
            worldScale.set((float) sx, (float) sy, (float) sz);
            worldTranslation.addLocal((float) px, (float) py, (float) pz);
//            System.out.println(" scale=" + worldScale);
//            System.out.println(" translat=" + worldTranslation);
        }
    }

    public synchronized void visible(boolean b) {
        getSceneHints().setCullHint(b ? CullHint.Inherit : CullHint.Always);
    }
//    private Stack<RenderState> localStack = new Stack<RenderState>();
//
//    private Map<RenderState.StateType, Stack<RenderState>> localStacks = Maps.newHashMap();
//
//    @Override public void propagateStatesFromRoot(final Map<RenderState.StateType, Stack<RenderState>> stateStack) {
//        // traverse to root to allow downward state propagation
//        if (_parent != null) {
//            _parent.propagateStatesFromRoot(stateStack);
//        }
//
//        // push states onto current render state stack
//        Stack<RenderState> stack;
//        for (final RenderState state : _renderStateList.values()) {
//            stack = stateStack.get(state.getType());
//            if (stack == null) {
//                stack = new Stack<RenderState>();
//                stateStack.put(state.getType(), stack);
//            }
//            stack.push(state);
//        }
//    }
//
//    @Override protected void updateWorldRenderStates(final boolean recurse, final Map<RenderState.StateType, Stack<RenderState>> stateStacks) {
//        Map<RenderState.StateType, Stack<RenderState>> stacks = stateStacks;
//
//        final boolean initiator = (stacks == null);
//
//        // first we need to get all the states from parent to us.
//        if (initiator) {
//            // grab all states from root to here.
//
//            localStacks.clear();
//            stacks = localStacks;
//
//            propagateStatesFromRoot(stacks);
//        } else {
//            Stack<RenderState> stack;
//            for (final RenderState state : _renderStateList.values()) {
//                stack = stacks.get(state.getType());
//                if (stack == null) {
//                    //System.out.println("new stack");
//                    localStack.clear();
//                    stack = localStack;
//                    stacks.put(state.getType(), stack);
//                }
//                stack.push(state);
//            }
//        }
//
//        applyWorldRenderStates(recurse, stacks);
//
//        // restore previous if we are not the initiator
//        if (!initiator) {
//            for (final RenderState state : _renderStateList.values()) {
//                stacks.get(state.getType()).pop();
//            }
//        }
//    }
    private boolean drawnReverse = false;

    public void setDrawnReverse(boolean drawnReverse) {
        this.drawnReverse = drawnReverse;
    }

    public boolean isDrawnReverse() {
        return drawnReverse;
    }

    @Override
    public void draw(final Renderer r) {

        final RenderDelegate delegate = getCurrentRenderDelegate();

        if (delegate == null) {                
            if (!isDrawnReverse()) {
                for (int i = getNumberOfChildren() - 1; i >= 0; i--) {
                    Spatial child = _children.get(i);
                    if (child != null) {
                        child.onDraw(r);
                    }
                }
            }
            else {
                for (int i = 0; i < getNumberOfChildren() ; i++) {
                    Spatial child = _children.get(i);
                    if (child != null) {
                        child.onDraw(r);
                    }
                }
            }


        } else {
            // Queue as needed
            if (!r.isProcessingQueue()) {
                if (r.checkAndAdd(this)) {
                    return;
                }
            }

            delegate.render(this, r);
        }
    }
}
