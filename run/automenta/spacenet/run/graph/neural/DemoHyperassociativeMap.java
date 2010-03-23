/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.spacenet.run.graph.neural;

import automenta.spacenet.run.ArdorSpacetime;
import automenta.spacenet.space.Repeat;
import automenta.spacenet.space.geom.Box;
import automenta.spacenet.space.geom.ProcessBox;
import automenta.spacenet.space.widget.MetaBox;
import com.ardor3d.scenegraph.Spatial;
import com.syncleus.dann.graph.drawing.hyperassociativemap.AbstractHyperassociativeMap;
import com.syncleus.dann.graph.drawing.hyperassociativemap.HyperassociativeNode;
import com.syncleus.dann.graph.drawing.hyperassociativemap.NeighborNotFoundException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author seh
 */
public class DemoHyperassociativeMap extends ProcessBox {

    public class LayeredHyperassociativeMap extends AbstractHyperassociativeMap {

        private HyperassociativeNode layeredNodes[][];
        private static final int NODES_PER_LAYER = 16;

        LayeredHyperassociativeMap(int layers) {
            super(3);

            this.layeredNodes = new HyperassociativeNode[layers][NODES_PER_LAYER];

            //create the nodes
            for (int layerIndex = 0; layerIndex < layers; layerIndex++) {
                for (int nodeIndex = 0; nodeIndex < NODES_PER_LAYER; nodeIndex++) {
                    this.layeredNodes[layerIndex][nodeIndex] = new HyperassociativeNode(this, HyperassociativeNode.randomCoordinates(3), 0.02d);
                    this.nodes.add(this.layeredNodes[layerIndex][nodeIndex]);
                }
            }


            //connect the nodes

            for (int layerIndex = 0; layerIndex < layers; layerIndex++) {
                for (int nodeIndex = 0; nodeIndex < NODES_PER_LAYER; nodeIndex++) {
                    HyperassociativeNode currentNode = this.layeredNodes[layerIndex][nodeIndex];
                    for (int toNodeIndex = 0; toNodeIndex < NODES_PER_LAYER; toNodeIndex++) {
                        if (layerIndex < (layers - 1)) {
                            currentNode.associate(this.layeredNodes[layerIndex + 1][toNodeIndex], 1.0);
                            this.layeredNodes[layerIndex + 1][toNodeIndex].associate(currentNode, 1.0);
                        } else {
                            currentNode.associate(this.layeredNodes[0][toNodeIndex], 1.0);
                            this.layeredNodes[0][toNodeIndex].associate(currentNode, 1.0);
                        }
                    }
                }
            }
        }
    }

    public class HyperassociativeBox extends Box {
        private final HyperassociativeNode node;

        double scale = 0.25;
        
        public HyperassociativeBox(HyperassociativeNode n) {
            super(BoxShape.Spheroid);
            this.node = n;
        }

        protected void update() {
            com.syncleus.dann.math.Vector v = node.getLocation();
            if (v.getDimensions() == 3) {
                move(v.getCoordinate(1)*scale, v.getCoordinate(2)*scale, v.getCoordinate(3)*scale);
            }

            double s = getNeuronScale()*scale;
            scale(s);
        }

        protected double getNeuronScale() {
            double totalConnectivity = 0;
            for (HyperassociativeNode n : node.getNeighbors()) {
                try {
                    totalConnectivity += node.getNeighborsWeight(n);
                } catch (NeighborNotFoundException ex) {
                }
            }
            totalConnectivity /= ((double)node.getNeighbors().size());
            totalConnectivity /= 2.0;
            return totalConnectivity;
        }
    }


    @Override protected void start() {
        final Map<HyperassociativeNode, HyperassociativeBox> haBoxes = new HashMap();
        final LayeredHyperassociativeMap m = new LayeredHyperassociativeMap(8);
        double mapUpdatePeriod = 0.1;

        Box vis = new Box(BoxShape.Empty);
        add(new MetaBox(vis));

        for (HyperassociativeNode n : m.getNodes()) {
            HyperassociativeBox nb = new HyperassociativeBox(n);
            vis.add(nb);
            haBoxes.put(n, nb);
        }

        

        add(new Repeat(mapUpdatePeriod) {
            @Override protected void update(double t, double dt, Spatial s) {
                m.align();
                for (HyperassociativeNode n : m.getNodes()) {
                    HyperassociativeBox nb = haBoxes.get(n);
                    nb.update();
                }
            }
        });

    }

    public DemoHyperassociativeMap() {
        super();
    }

    public static void main(String[] args) {
        ArdorSpacetime.newWindow(new DemoHyperassociativeMap());
    }
}
