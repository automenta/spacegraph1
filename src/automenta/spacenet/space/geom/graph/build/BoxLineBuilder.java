package automenta.spacenet.space.geom.graph.build;

import automenta.spacenet.space.Space;
import automenta.spacenet.space.geom.Box;
import automenta.spacenet.space.geom.Box.BoxShape;
import automenta.spacenet.space.geom.Line3D;
import automenta.spacenet.space.geom.graph.GraphBoxBuilder;


public class BoxLineBuilder implements GraphBoxBuilder {

    public BoxLineBuilder() {
        super();
    }

    @Override
    public Space newEdgeSpace(Object edge, Box pa, Box pb) {
        Space s = new Line3D(pa.getPosition(), pb.getPosition(), 5, 0.1);
        //s.add(new ColorSurface(1f, 1, 0));
        return s;
    }

    @Override
    public Box newNodeSpace(Object vertex) {
        Box b = new Box(BoxShape.Spheroid);

        //Color c = Color.newRandomHSB(0.5, 0.5);
        //w.add(new TextRect(vertex.toString(), c).scale(0.9).move(0,0,0.1));
        
        return b;
    }
}
