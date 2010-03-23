package automenta.spacenet.plugin.neural;

import automenta.spacenet.var.graph.MemGraph;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import org.neuroph.core.Connection;
import org.neuroph.core.Layer;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.Neuron;

//TODO rename to NeuralGraph
//TODO implements (Neuroph) observer - changes in neural network reflects in the graph
public class NeuralGraph extends MemGraph<Neuron, Connection> implements Observer {

    private NeuralNetwork net;

    public NeuralGraph() {
        super();
    }

    public NeuralGraph(NeuralNetwork net) {
        this();
        setNetwork(net);
    }

    public NeuralNetwork getNet() {
        return net;
    }

    public void setNetwork(NeuralNetwork net) {
        this.net = net;
        
        clear();
        Iterator<Layer> li = net.getLayersIterator();
        while (li.hasNext()) {
            Layer l = li.next();
            Iterator<Neuron> ni = l.getNeuronsIterator();
            while (ni.hasNext()) {
                Neuron neuron = ni.next();
                addNode(neuron);
                Iterator<Connection> ci = neuron.getInputConnections().iterator();
                while (ci.hasNext()) {
                    Connection connection = ci.next();
                    Neuron fromNeuron = connection.getConnectedNeuron();
                    addEdge(connection, fromNeuron, neuron);
                }
            }
        }
    }

    public void update(Observable o, Object arg) {
        // we should distinct from network state change when its calculated
        // and when the network structure is changed (like neuron added or removed)
        // we should rebuild graph only when network structure has changed, ands we can use arg parameter to indicate that
        setNetwork((NeuralNetwork)o);
    }
}
