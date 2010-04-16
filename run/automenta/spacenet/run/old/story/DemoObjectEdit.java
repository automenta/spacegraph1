/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package automenta.spacenet.run.old.story;

import automenta.spacenet.var.action.Actions;
import automenta.spacenet.var.graph.MemGraph;
import automenta.spacenet.var.scalar.BoolVar;
import automenta.spacenet.var.scalar.DoubleVar;
import automenta.spacenet.var.scalar.IntVar;
import automenta.spacenet.var.string.StringVar;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

/**
 *
 * @author seh
 */
public class DemoObjectEdit {

    public static class TestObject {
        private IntVar integer = new IntVar(0);
        private BoolVar bool = new BoolVar(false);
        private StringVar str = new StringVar("xyz");
        private DoubleVar doublevar = new DoubleVar(0);

        public BoolVar getBoolean() {
            return bool;
        }

        public IntVar getInt() {
            return integer;
        }
        
        public DoubleVar getDouble() {
            return doublevar;
        }

        public StringVar getStr() {
            return str;
        }

        public void invokable() {
            JOptionPane.showMessageDialog(null, "Invoked.");
        }

    }

    public static void main(String[] args) {
        Actions actions = new Actions();
        MemGraph graph = new MemGraph();

        Object s = graph.addNode(new StringVar("Abc"));
        Object i = graph.addNode(new DoubleVar(0));
        
        Object t = graph.addNode( new TestObject() );

        graph.addEdge("relates", t, s);
        graph.addEdge("relates2", t, i);
        
        new SwingWindow(new JScrollPane(new DefaultObjectPanel(t, graph, actions)), 400, 600, true);
        
    }
}
