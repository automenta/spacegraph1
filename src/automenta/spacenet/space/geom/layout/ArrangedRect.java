/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package automenta.spacenet.space.geom.layout;

import automenta.spacenet.space.Space;
import automenta.spacenet.space.geom.Rect;
import com.ardor3d.scenegraph.Spatial;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author seh
 */
public abstract class ArrangedRect extends Rect {

    public ArrangedRect() {
        super(RectShape.Empty);
    }

    @Override
    public <S extends Spatial> S add(S s) {
        S a = super.add(s);
        arrange(getSubSpaces(getChildren()));
        return a;
    }

    @Override
    public <S extends Spatial> S remove(S s) {
        S r = super.remove(s);
        arrange(getSubSpaces(getChildren()));
        return r;
    }


    private List<Space> subSpaces = new LinkedList();

    public List<Space> getSubSpaces(List<Spatial> s) {
        subSpaces.clear();
        for (Spatial x : s) {
            if (x instanceof Space)
                subSpaces.add((Space)x);
        }
        return subSpaces;
    }
    
    abstract protected void arrange(List<Space> subSpaces);
    
}
