package automenta.spacenet.var.graph.tree;

public class TreeNode {

    public final Object value;
    public final Object[] children;

    public TreeNode(Object value, Object... children) {
        super();
        this.value = value;
        this.children = children;
    }
}
