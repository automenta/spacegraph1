package automenta.spacenet.space.geom.graph.build;

import automenta.spacenet.space.geom.graph.GraphBoxBuilder;


abstract public class TypeSpatializer implements GraphBoxBuilder {

//    private Map<Class, Transformer<Object, Box>> types = new HashMap();
//
//    public TypeSpatializer() {
//        super();
//    }
//
//    public <C> void add(Class<C> superType, Transformer<Object, Box> s) {
//        types.put(superType, s);
//    }
//
//    @Override
//    public Box newVertexBox(Object vertex) {
//        for (Class c : types.keySet()) {
//            if (c.isInstance(vertex)) {
//                Transformer<Object, Box> t = types.get(c);
//                Box b = t.transform(vertex);
//                return b;
//            }
//        }
//        return newDefaultBox(vertex);
//    }
//
//    @Override
//    public Space newEdgeSpace(Object edge, Box pa, Box pb) {
//        Line3D fl = new Line3D(pa.getPosition(), pb.getPosition(), new DoubleVar(0.02), 2);
//        fl.color(Color.newRandomHSB(0.5, 0.5));
//        return fl;
//    }
//
//    protected Box newDefaultBox(Object vertex) {
//        Window w = new Window();
//
//        Color c = Color.newRandomHSB(0.5, 0.5);
//        w.add(new TextRect(vertex.toString(), c).scale(0.9).move(0, 0, 0.1));
//
//        return w;
//    }
}
