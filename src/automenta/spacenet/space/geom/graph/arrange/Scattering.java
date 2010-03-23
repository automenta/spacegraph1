package automenta.spacenet.space.geom.graph.arrange;

import automenta.spacenet.space.Space;
import automenta.spacenet.space.geom.Box;
import automenta.spacenet.space.geom.graph.GraphBoxModel;
import automenta.spacenet.space.geom.graph.GraphBox;
import automenta.spacenet.var.Maths;


public class Scattering implements GraphBoxModel {

    double r = 0.5;

    @Override
    public void addedNode(Object v, Box b) {
        double px = Maths.random(-r, r);
        double py = Maths.random(-r, r);
        double pz = Maths.random(-r, r);
        b.move(px, py, pz);
    }

    @Override
    public void removedNode(Object v) {
    }

    @Override
    public void addedEdge(Object e, Space s, Box from, Box to) {
    }

    @Override
    public void removedEdge(Object e) {
    }

    @Override
    public void start(GraphBox graphBox) {
    }

    @Override
    public void stop() {
    }


}
