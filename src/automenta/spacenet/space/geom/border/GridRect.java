/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.spacenet.space.geom.border;

import automenta.spacenet.space.SpaceState;
import automenta.spacenet.space.geom.Rect;
import automenta.spacenet.space.surface.ColorSurface;
import automenta.spacenet.var.physical.Color;
import automenta.spacenet.var.scalar.DoubleVar;
import automenta.spacenet.var.vector.V2;

/**
 *
 * @author seh
 */
public class GridRect extends Rect {

    protected V2 gridFrequency;
    protected DoubleVar thick;
    protected SpaceState gridSurface;
    double dz = 0.05;

    public GridRect(Color fore, double freqX, double freqY, double thick) {
        this(null, new ColorSurface(fore), new V2(freqX, freqY), new DoubleVar(thick));
    }

    public GridRect(Color back, Color fore, double freqX, double freqY, double thick) {
        this(new ColorSurface(back), new ColorSurface(fore), new V2(freqX, freqY), new DoubleVar(thick));
    }

    /** grid frequency x & y components */
    public GridRect(SpaceState backSurface, SpaceState foreSurface, V2 gridFrequency, DoubleVar thick) {
        super(RectShape.Rect);
    
        if (backSurface == null)
            setShape(RectShape.Empty);
        else
            add(backSurface);

        this.gridSurface = foreSurface;
        this.gridFrequency = gridFrequency;
        this.thick = thick;


        updateGrid();

        //TODO watch gridFrequency for changes
    }

    protected void updateGrid() {
        removeAll();

        int nx = (int) gridFrequency.getX();
        int ny = (int) gridFrequency.getY();

        double spacingX = 1.0 / (nx);
        double spacingY = 1.0 / (ny);

        double dx = -0.5 + spacingX / 2.0;
        double dy = -0.5 + spacingY / 2.0;

        double thickX = thick.d() / (nx);
        double thickY = thick.d() / (ny);

        for (int x = 0; x < nx; x++) {
            Rect s = new Rect(RectShape.Rect);
            s.add(gridSurface);
            //s.tangible(false);
            s.move(dx, 0, dz);
            s.scale(thickX, 1.0);
            add(s);

            dx += spacingX;
        }

        for (int y = 0; y < ny; y++) {
            Rect r = new Rect(RectShape.Rect);
            r.add(gridSurface);
            //r.tangible(false);
            r.move(0, dy, dz);
            r.scale(1.0, thickY);
            add(r);

            dy += spacingY;
        }
    }
}
