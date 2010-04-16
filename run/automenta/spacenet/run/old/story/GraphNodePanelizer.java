package automenta.spacenet.run.old.story;

import automenta.spacenet.var.graph.map.ScalarGraphMap;
import java.awt.event.ActionListener;
import javax.swing.JPanel;

public interface GraphNodePanelizer {

    public JPanel newPanel(Object node, ScalarGraphMap attention, ActionListener actionListener);
}
