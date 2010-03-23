package automenta.spacenet.run;

import automenta.spacenet.space.Space;
import automenta.spacenet.space.control.camera.FacesCameraBox;
import automenta.spacenet.space.geom.Box;
import automenta.spacenet.space.geom.graph.GraphBoxBuilder;
import automenta.spacenet.var.physical.Color;
import org.neuroph.core.Weight;

public class DefaultGraphBuilder implements GraphBoxBuilder {

    public static final double neuronUpdatePeriod = 0.2;
    private final double scale;

    public DefaultGraphBuilder() {
        this(1.0);
    }

    public DefaultGraphBuilder(double scale) {
        super();
        this.scale = scale;
    }

    @Override
    public Space newEdgeSpace(Object edge, Box pa, Box pb) {
        return new DefaultEdgeLine(edge, pa, pb);
    }

    @Override
    public Box newNodeSpace(final Object node) {
        final Box f = new FacesCameraBox();
        final Box b = new DefaultObjectBox(node);
        b.scale(scale);
        //rect.scale(0.9).move(0, 0, 0.1);
        f.add(b);
        return f;
    }

    public Color getWeightColor(Weight w) {
        float v = (float) (0.5F * (w.getValue() + 1.0));
        return Color.hsb(v, 0.2, v).alpha(0.1);
    }
}
