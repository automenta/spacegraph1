package automenta.spacenet.space.geom.graph.arrange.forcedirect;

import automenta.spacenet.run.old.widget.DemoButton;
import automenta.spacenet.space.geom.graph.arrange.forcedirect.ForceDirecting.ForceDirectedParameters;
import automenta.spacenet.space.geom.layout.ColRect;
import automenta.spacenet.space.geom.layout.RowRect;
import automenta.spacenet.space.geom.text3d.Font3D;
import automenta.spacenet.space.geom.text3d.Text3D;
import automenta.spacenet.space.widget.spinner.Spinner;
import automenta.spacenet.space.widget.window.Window;
import automenta.spacenet.var.physical.Color;
import automenta.spacenet.var.scalar.DoubleRange;

public class ForceDirectedParametersEditWindow extends Window {

    private final ForceDirectedParameters params;

    public ForceDirectedParametersEditWindow(ForceDirectedParameters params, Font3D font) {
        super();
        this.params = params;
        
        DoubleRange stiffRange = new DoubleRange(params.getStiffness(), 0, 0.30);
        DoubleRange repulseRange = new DoubleRange(params.getRepulsion(), -0.5, 0.5);
        DoubleRange lengthRange = new DoubleRange(params.getLengthFactor(), 0, 2.0);

        add(new ColRect(0.1, new RowRect(0.1, new Text3D(font, "Tension", Color.Black), new Spinner(font, stiffRange, 0.0050)), new RowRect(0.1, new Text3D(font, "Repulsion", Color.Black), new Spinner(font, repulseRange, 0.0025)), new RowRect(0.1, new Text3D(font, "Length", Color.Black), new Spinner(font, lengthRange, 0.1)))).moveDZ(0.05);
 
    }
}
