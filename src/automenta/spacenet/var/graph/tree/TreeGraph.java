package automenta.spacenet.var.graph.tree;

import automenta.spacenet.var.graph.MemGraph;

/**
 * actually a ForestGraph as it can hold multiple tree roots
 */
public class TreeGraph extends MemGraph {

    public static class ChildEdge {

        @Override
        public String toString() {
            return "child";
        }
    }

    public TreeGraph(TreeNode... roots) {
        super();
        for (TreeNode tn : roots) {
            addTreeNode(tn);
        }
    }

    public Object add(Object o) {
        if (o instanceof TreeNode) {
            return addTreeNode((TreeNode) o);
        } else {
            return addNode(o);
        }

    }

    private Object addTreeNode(TreeNode tn) {
        addNode(tn.value);
        for (Object o : tn.children) {
            Object child = add(o);
            addEdge(new ChildEdge(), tn.value, child);
        }
        return tn.value;
    }
}
