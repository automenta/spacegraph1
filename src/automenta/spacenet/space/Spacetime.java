/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package automenta.spacenet.space;

import automenta.spacenet.space.control.camera.ArdorCamera;
import automenta.spacenet.space.control.pointer.DefaultPointer;
import automenta.spacenet.var.physical.Color;
import com.ardor3d.framework.NativeCanvas;
import com.ardor3d.input.PhysicalLayer;
import com.ardor3d.input.logical.InputTrigger;
import com.ardor3d.input.logical.LogicalLayer;

/**
 *
 * @author seh
 */
public interface Spacetime {

    //TODO use Camera super-interface
    public ArdorCamera getCamera();

    //TODO use Pointer super-interface
    public DefaultPointer getPointer();

    public Color getBackgroundColor();

    public NativeCanvas getVideo();
    public LogicalLayer getInputLogic();
    public PhysicalLayer getInputPhy();

    public void addCondition(InputTrigger t);
    public void removeCondition(InputTrigger t);

    public Space getFace();
    public Space getSky();

}
