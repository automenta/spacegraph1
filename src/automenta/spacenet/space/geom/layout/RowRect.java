/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package automenta.spacenet.space.geom.layout;

import automenta.spacenet.space.Space;
import automenta.spacenet.space.geom.Box;
import automenta.spacenet.space.geom.Rect;
import com.ardor3d.scenegraph.Spatial;
import java.util.List;

public class RowRect extends ArrangedRect {
    protected final double marginX;
    protected final double marginY;

    public RowRect(Spatial... spatials) {
        this(0, spatials);
    }
    
    public RowRect(double marginX, double marginY, Spatial... spatials) {
        super();
        this.marginX = marginX;
        this.marginY = marginY;
        for (Spatial s : spatials)
            add(s);
    }

    public RowRect(double marginXY, Spatial... spatials) {
        this(marginXY, marginXY, spatials);
    }

    @Override
    protected void arrange(List<Space> spatials) {
        int num = spatials.size();
        if (num == 0)
            return;
        
        double w = getCellWidth(num);
        double h = getCellHeight(num);

        int i = 0;
        for (Spatial s : spatials) {
            double x = getCellX(s, i, num);
            double y = getCellY(s, i, num);

            //TODO use an interface 'Spannable' that has the span(...) method
            if (s instanceof Rect) {
                Rect r = (Rect)s;
                r.move(x, y);
                r.scale(w, h);
            }
            else if (s instanceof Box) {
                Box r = (Box)s;
                r.move(x, y, 0);
                r.scale(w, h, 1.0);
            }

            i++;
        }
    }

    public double getCellWidth(int num) {
        return (1.0-marginX) / num;
    }
    public double getCellHeight(int num) {
        return (1.0-marginY);
    }

    public double getCellX(Spatial s, int i, int num) {
        return -0.5 + marginX/4.0 + ((double)i / (double)num) + getCellWidth(num)/2.0;
    }
    public double getCellY(Spatial s, int i, int num) {
        return 0;
    }

}
