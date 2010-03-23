/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package automenta.spacenet.plugin.neural.brainz;

import automenta.spacenet.plugin.comm.BlankEdge;
import automenta.spacenet.var.graph.MemGraph;

/**
 *
 * @author seh
 */
public class BrainGraph extends MemGraph {

    private final Brain brain;

    public BrainGraph(Brain b) {
        super();

        this.brain = b;
        
        update();
    }

    protected void update() {
        clear();

        for (SenseNeuron sn : brain.getSense()) {
            addNode(sn);
        }

        for (InterNeuron in : brain.getNeuron()) {
            addNode(in);
            for (Synapse s : in.getSynapses()) {
                AbstractNeuron pred = s.inputNeuron;
                addEdge(new BlankEdge(), in, pred);
            }            
        }
    }


}
