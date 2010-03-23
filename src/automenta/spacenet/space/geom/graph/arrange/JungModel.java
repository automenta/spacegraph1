/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package automenta.spacenet.space.geom.graph.arrange;

import automenta.spacenet.space.Repeat;
import automenta.spacenet.space.Space;
import automenta.spacenet.space.geom.Box;
import automenta.spacenet.space.geom.graph.GraphBox;
import automenta.spacenet.space.geom.graph.GraphBoxModel;

import com.ardor3d.scenegraph.Spatial;
import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.algorithms.util.IterativeContext;
import edu.uci.ics.jung.graph.Graph;
import java.awt.Dimension;

/**
 *
 * @author seh
 */
public class JungModel<N,E> extends Repeat implements GraphBoxModel<N,E> {
    
    private GraphBox<N, E> graphBox;
    private final AbstractLayout<N,E> layout;
    double upscale = 800.0;
    final static double updatePeriod = 0.05;
    
    public JungModel(AbstractLayout<N,E> jungLayout) {
        super(updatePeriod);
        this.layout = jungLayout;
    }


    @Override
    public void addedNode(N v, Box b) {
        updatePositions();
    }


    @Override
    public void removedNode(N v) {
        updatePositions();
    }

    @Override
    public void addedEdge(E e, Space s, Box from, Box to) {
        updatePositions();
    }

    @Override
    public void removedEdge(E e) {
        updatePositions();
    }

    @Override
    public void start(GraphBox<N, E> graphBox) {
        this.graphBox = graphBox;

        layout.setGraph((Graph<N,E>)graphBox.getGraph());
        layout.setSize(new Dimension((int)upscale, (int)upscale));
        layout.reset();

        updatePositions();
    }

    protected void updatePositions() {
        for (N n : graphBox.getGraph().getNodes()) {
            double x = (layout.getX(n) / upscale)-0.5;
            double y = (layout.getY(n) / upscale)-0.5;
            Box b = graphBox.getNodeBox(n);
            if (b!=null) {
                //System.out.println(n + " @ " +x + " , " + y);
                b.getPosition().set(x, y, 0);
            }
        }

    }

    @Override
    public void stop() {
    }

    @Override
    protected void update(double t, double dt, Spatial parent) {
        if (layout instanceof IterativeContext) {
            IterativeContext ic = (IterativeContext) layout;
            if (!ic.done()) {
                ic.step();
                updatePositions();
            }
        }
    }

}
