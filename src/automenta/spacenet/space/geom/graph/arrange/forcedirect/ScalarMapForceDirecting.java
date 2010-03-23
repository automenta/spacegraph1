package automenta.spacenet.space.geom.graph.arrange.forcedirect;

import automenta.spacenet.space.Space;
import automenta.spacenet.space.geom.Box;
import automenta.spacenet.space.geom.Line3D;
import automenta.spacenet.space.geom.graph.arrange.forcedirect.ForceDirecting.ForceDirectedParameters;
import automenta.spacenet.var.graph.map.ScalarGraphMap;
import com.ardor3d.math.Vector3;

public class ScalarMapForceDirecting<N, E> extends ForceDirecting<N, E> {

    public final ScalarGraphMap<N, E> map;

    public ScalarMapForceDirecting(ScalarGraphMap<N, E> sm, ForceDirectedParameters params, double updatePeriod, int substeps, double interpSpeed) {
        super(params, updatePeriod, substeps, interpSpeed);
        this.map = sm;
    }

    public ScalarGraphMap<N, E> getMap() {
        return map;
    }
    
    @Override
    protected void updateEdge(E e, Space s) {
        super.updateEdge(e, s);
        //((Line3D) s).getRadius().set(0.01);
    }

    @Override
    protected void updateNode(N n, Box nBox, Vector3 nextSize) {
        //double nodeSize = 0.05;
        double nodeSize = getNodeSize(n, map.value(n));
        nextSize.set(nodeSize, nodeSize, nodeSize);
        return;
    }

    public double getNodeSize(N node, double a) {
        return a;
    }
}
