/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package automenta.spacenet.var.vector;

import com.ardor3d.math.Quaternion;
import com.ardor3d.math.type.ReadOnlyQuaternion;
import com.ardor3d.math.type.ReadOnlyVector3;
import java.util.LinkedList;
import java.util.List;


public class Quat extends Quaternion {
    private boolean notifying = true;




    public static interface IfQuatChanges {
        void onQuatChanged(Quat q);
    }

    //TODO lazy instantiate
    private List<IfQuatChanges> ifChanges = new LinkedList();

    public Quat(ReadOnlyQuaternion source) {
        super(source);
    }

    public void set(double heading, double attitude, double bank) {
        set(new Quaternion().fromEulerAngles(heading, attitude, bank));
    }

    public static Quat fromAngles(double heading, double attitude, double bank) {
        Quaternion q = new Quaternion().fromEulerAngles(heading, attitude, bank);
        return new Quat(q);
    }

    public Quat() {
        super();
    }

    @Override
    public Quaternion set(ReadOnlyQuaternion source) {
        return set(source.getX(), source.getY(), source.getZ(), source.getW());
    }

    @Override
    public void setX(double x) {
        super.setX(x);
        notifyChanged();
    }

    @Override
    public void setY(double y) {
        super.setY(y);
        notifyChanged();
    }

    @Override
    public void setZ(double z) {
        super.setZ(z);
        notifyChanged();
    }

    @Override
    public void setW(double w) {
        super.setW(w);
        notifyChanged();
    }





    @Override
    public Quaternion set(double x, double y, double z, double w) {
        if ((getX()==x) && (getY() == y) && (getZ() == z) && (getW() == w))
            return this;

        notifying = false;

        Quaternion q = super.set(x, y, z, w);

        notifying = true;

        notifyChanged();

        return q;
    }

    @Override
    public Quaternion addLocal(ReadOnlyQuaternion quat) {
        super.addLocal(quat);
        notifyChanged();
        return this;
    }



    private void notifyChanged() {
        if (notifying) {
            for (IfQuatChanges i : ifChanges) {
                i.onQuatChanged(this);
            }
        }
    }
    
   public IfQuatChanges add(IfQuatChanges q) {
       ifChanges.add(q);
       return q;
   }
   public IfQuatChanges remove(IfQuatChanges q) {
       ifChanges.remove(q);
       return q;
   }

    @Override
    public void lookAt(ReadOnlyVector3 direction, ReadOnlyVector3 up) {
        super.lookAt(direction, up);
        notifyChanged();
    }




}
