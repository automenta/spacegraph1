/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package automenta.spacenet.space.geom;

import com.ardor3d.scenegraph.Spatial;

abstract public class ProcessBox extends Box {

    public ProcessBox() {
        super(BoxShape.Empty);
    }



    @Override
    protected void afterAttached(Spatial newParent) {
        super.afterAttached(newParent);

        start();
    }

    @Override
    protected void beforeDetached(Spatial parent) {
        stop();
        
        super.beforeDetached(parent);
    }

    abstract protected void start();
    
    protected void stop() {

    }

}
