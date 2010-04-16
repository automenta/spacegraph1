package automenta.spacenet.run.old.story;

import automenta.spacenet.var.action.Actions;
import automenta.spacenet.var.graph.map.ScalarGraphMap;
import javax.swing.JComponent;

public interface ObjectPanelizer {

    public JComponent newPanel(Object node, ScalarGraphMap graph, Actions a);
}
