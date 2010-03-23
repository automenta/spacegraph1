package automenta.spacenet.space;

import com.ardor3d.scenegraph.Spatial;

public interface SpaceState<S extends Spatial> {

    public void apply(S s);
    public void unapply(S s);
    
}
