package automenta.spacenet.var.graph.map;

import automenta.spacenet.var.Maths;
import automenta.spacenet.var.graph.MemGraph;
import automenta.spacenet.var.map.MapVar;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ScalarGraphMap<N, E> extends MapVar<N, Double> {

    //TODO the way this functions it is actually a ScalarNodeMap - how to specify scalars for edges?
    private final double bias;
    public final MemGraph<N, E> graph;
    double maxAllowedValue = 1.0;
    double minAllowedValue = 0.0;
    private double currentMaxValue;
    private double currentMinValue;

    public ScalarGraphMap(MemGraph<N, E> g, double bias) {
        super();
        this.graph = g;
        this.bias = bias;
        clear();
    }

    public ScalarGraphMap(MemGraph graph) {
        this(graph, 0.0);
    }

    //public void sharpen(double transferProportion, ...)
    public void addRandom(double min, double max) {
        List<N> l = new LinkedList(keySet());
        if (l.size() > 0) {
            N n = l.get((int) Math.floor(Math.random() * l.size()));
            add(n, Maths.random(min, max));
        }
    }

    public void blur(N node, double transferProportion) {
        Map<N, Double> nextAtt = new HashMap();
        double prevA = value(node);
        double average = prevA;
        Collection<N> neighbors = graph.getNeighbors(node);
        for (N ne : neighbors) {
            average += value(ne);
        }
        average /= (neighbors.size() + 1);
        double nextA = (average * transferProportion) + (prevA * (1.0 - transferProportion));

        nextAtt.put(node, nextA);
        for (N ne : neighbors) {
            double prevB = value(ne);
            double nextB = (average * transferProportion) + (prevB * (1.0 - transferProportion));
            nextAtt.put(ne, nextB);
        }

        for (N n : nextAtt.keySet()) {
            set(n, nextAtt.get(n));
        }

    }

    public void blur(double transferProportion) {
        Map<N, Double> nextAtt = new HashMap();
        for (N n : graph.getNodes()) {
            double prevA = value(n);
            double average = prevA;
            Collection<N> neighbors = graph.getNeighbors(n);
            for (N ne : neighbors) {
                average += value(ne);
            }
            average /= (neighbors.size() + 1);
            double nextA = (average * transferProportion) + (prevA * (1.0 - transferProportion));
            nextAtt.put(n, nextA);
        }
        for (N n : nextAtt.keySet()) {
            set(n, nextAtt.get(n));
        }
    }

//        //blur = diffuse
//        public void blur(double transferProportion, Predicate<E> traverseEdge) {
//
//        }
    public void randomize(double min, double max) {
        List<N> l = new LinkedList(graph.getNodes());
        for (N n : l) {
            set(n, Maths.random(min, max));
        }
    }

    public void set(N n, double a) {
        a = Math.min(a, maxAllowedValue);
        a = Math.max(a, minAllowedValue);
        put(n, a);
        if (a > currentMaxValue) {
            currentMaxValue = a;
        }
        if (a < currentMinValue) {
            currentMinValue = a;
        }
    }

    public void add(N n, double dA) {
        set(n, d(n) + dA);
    }

    public double d(N n) {
        Double d = get(n);
        if (d == null) {
            d = getDefaultValue();
            put(n, d);
        }
        return d;
    }

    public double value(N n) {
        return d(n) + getBias();
    }

    public double getBias() {
        return bias;
    }

    public double getDefaultValue() {
        return 0.0;
    }

    public void mult(double d) {
        for (N n : graph.getNodes()) {
            set(n, value(n) * d);
        }
    }

    public void mult(double d, double minSize) {
        for (N n : graph.getNodes()) {
            set(n, Math.max(minSize, get(n) * d));
        }
    }

    public MemGraph<N, E> getGraph() {
        return graph;
    }

    public List<N> getNodesSortedNow() {
        List<N> l = new ArrayList(graph.getNodes());
        Collections.sort(l, new Comparator<N>() {

            @Override public int compare(N a, N b) {
                double va = value(a);
                double vb = value(b);
                if (va == vb) {
                    return 0;
                }
                if (va < vb) {
                    return 1;
                }
                return -1;
            }
        });
        return l;
    }

    public double getMin() {
        if (size() == 0) {
            return 0;
        }
        return currentMinValue;
//        //TODO optimize
//        double min = Double.POSITIVE_INFINITY;
//        for (Double d : values()) {
//            if (d < min)
//                min = d;
//        }
//        return min;
    }

    public double getMax() {
        if (size() == 0) {
            return 0;
        }
        return currentMaxValue;
//        //TODO optimize
//        double max = Double.NEGATIVE_INFINITY;
//        for (Double d : values()) {
//            if (d > max)
//                max = d;
//        }
//        return max;
    }

    public double valueNormalized(N node) {
        double v = value(node);
        double min = getMin();
        double max = getMax();
        return (v - min) / (max - min);
    }

    public void focus(double s) {
        //apply sigmoid function to all nodes
        List<N> l = new LinkedList(graph.getNodes());
        for (N n : l) {
            double v = value(n);
            if (v < 0.5 * (getMin() + getMax())) {
                 v  = v * (1.0 - s);
            }
            else {
                 v  = v * (1.0 + s);
            }
            set(n, v);
        }
        
    }
}
