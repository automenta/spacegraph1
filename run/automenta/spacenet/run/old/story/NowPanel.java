/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.spacenet.run.old.story;

import automenta.spacenet.var.action.Actions;
import automenta.spacenet.var.graph.MemGraph;
import automenta.spacenet.var.graph.map.ScalarGraphMap;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class NowPanel extends JPanel {

    private final ScalarGraphMap memory;
    double focusSpike = 0.2;
    private final StatusPanel statusPanel;
    JPanel contentPanel = new JPanel(new BorderLayout());
    ViewMenu viewMenu = new ViewMenu();
    private BrowserPanel browserPanel;
    private BrowserContentPanel browserContentPanel;
    private final Actions actions;

    public class InputPanel extends JPanel {

        private final JTextArea inputArea;
        //TODO add drag-and-drop support

        public InputPanel() {
            super(new BorderLayout());

            inputArea = new JTextArea();
            inputArea.setFont(inputArea.getFont().deriveFont((float) (inputArea.getFont().getSize() * 3)));
            add(inputArea, BorderLayout.CENTER);

        }
    }

    public class NewMenu extends JPopupMenu {

        public NewMenu() {
            super();
            add(new JMenuItem("Text"));
            add(new JMenuItem("URL"));
            add(new JMenuItem("Agent"));
        }
    }

    public static class TextSummaryViewer implements ObjectPanelizer {

        @Override public JComponent newPanel(Object node, ScalarGraphMap graph, Actions a) {
            return new JScrollPane(new DefaultObjectPanel(node, graph.graph, a));
        }
    }

    public class JUNGViewer implements ObjectPanelizer {

        public MemGraph getNeighborhood(MemGraph g, Object node, Actions a) {
            Collection edges = g.getIncidentEdges(node);
            Collection nodes = g.getNeighbors(node);

            MemGraph s = new MemGraph();
            for (Object x : nodes)
                s.addNode(x);
            for (Object e : edges) {
                s.addEdge(e, g.getIncidentVertices(e));
            }

            return s;
        }

        @Override public JComponent newPanel(Object node, ScalarGraphMap graph, Actions a) {
            JPanel j = new JPanel(new BorderLayout());
            Layout<Integer, String> layout = new CircleLayout(getNeighborhood(graph.graph, node, actions));
            //layout.setSize(new Dimension(300, 300));
            VisualizationViewer<Integer, String> vv = new VisualizationViewer<Integer, String>(layout);
            //vv.setPreferredSize(new Dimension(350, 350));

            // Show vertex and edge labels
            vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
            vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller());

            // Create a graph mouse and add it to the visualization component
            DefaultModalGraphMouse gm = new DefaultModalGraphMouse();
            gm.setMode(ModalGraphMouse.Mode.TRANSFORMING);
            vv.setGraphMouse(gm);
            j.add(vv, BorderLayout.CENTER);
            return j;
        }
    }

    public class ViewMenu extends JPopupMenu {

        public ViewMenu() {
            super();
        }

        protected ObjectPanelizer update(Object o) {
            removeAll();

            ObjectPanelizer defaultView = new TextSummaryViewer();
            addView(o, "Text", defaultView);
            addView(o, "Graph", new JUNGViewer());

            return defaultView;
        }

        protected void addView(final Object o, String label, final ObjectPanelizer view) {
            JMenuItem jm = new JMenuItem(label);
            jm.addActionListener(new ActionListener() {

                @Override public void actionPerformed(ActionEvent e) {
                    setView(o, view);
                }
            });
            add(jm);
        }
    }

    public class PopupButton extends JButton implements ActionListener {

        private final JPopupMenu menu;

        private PopupButton(String label, JPopupMenu menu) {
            super(label);
            this.menu = menu;
            addActionListener(this);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (menu != null) {
                menu.show(this, getWidth() / 2, getHeight() / 2);
            }
        }
    }

    public class FocusMenu extends JPopupMenu {

        public FocusMenu() {
            super();
            add(new JMenuItem("All"));
            add(new JSeparator());
            add(new JMenuItem("In"));
            add(new JMenuItem("Frequent"));
            add(new JMenuItem("Recent"));

            JMenu typeMenu = new JMenu("What");
            typeMenu.add(new JMenuItem("Agents"));
            typeMenu.add(new JMenuItem("Documents"));
            typeMenu.add(new JMenuItem("Images"));
            typeMenu.add(new JMenuItem("Videos"));
            typeMenu.add(new JMenuItem("Options"));
            add(typeMenu); //Types

            add(new JMenuItem("Who")); //by agents
            add(new JMenuItem("Where")); //locations
            add(new JMenuItem("Why")); //categories = tags. the tag itself answers 'why' something was tagged / classified, or its reason for existence
            add(new JMenuItem("When"));//time frames (tomorrow, 5 mins ago, 10 mins ago, yesterday, etc..)
        }
    }

    public class ListViewMenu extends JPopupMenu {

        public ListViewMenu() {
            super();
            add(new JMenuItem("Attention Spectrum"));
            add(new JMenuItem("Tree"));
            add(new JMenuItem("Graph 2D"));
        }
    }

    public class ThingTreePanel extends JPanel implements Runnable {

        long periodMS = 200;
        private final AttentionList attList;

        public ThingTreePanel() {
            super(new BorderLayout());

            JPanel topPanel = new JPanel();
            topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.LINE_AXIS));

            final JButton newButton = new PopupButton("+", new NewMenu());
            final JButton modeSelect = new PopupButton("Focus", new FocusMenu());
            final JButton viewSelect = new PopupButton("View", new ListViewMenu());

            topPanel.add(newButton);
            topPanel.add(modeSelect);
            topPanel.add(viewSelect);

            add(topPanel, BorderLayout.NORTH);

            attList = new AttentionList(memory, new WideIconPanelizer()) {

                @Override public void onFocused(Object o) {
                    focus(o);
                }
            };

            add(new JScrollPane(attList), BorderLayout.CENTER);

            JPanel bottomPanel = new JPanel();
            bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.LINE_AXIS));


            JButton forgetButton = new JButton("Forget");
            forgetButton.addActionListener(new ActionListener() {

                @Override public void actionPerformed(ActionEvent e) {
                    memory.mult(0.9);
                    attList.update();
                }
            });

            JButton randomButton = new JButton("Random");
            randomButton.addActionListener(new ActionListener() {

                @Override public void actionPerformed(ActionEvent e) {
                    memory.addRandom(0.1, 0.4);
                    attList.update();
                }
            });

            JButton blurButton = new JButton("Blur");
            blurButton.addActionListener(new ActionListener() {

                @Override public void actionPerformed(ActionEvent e) {
                    memory.blur(0.02);
                    attList.update();
                }
            });

            JButton sharpenButton = new JButton("Sharpen");
            sharpenButton.addActionListener(new ActionListener() {

                @Override public void actionPerformed(ActionEvent e) {
                    //memory.sharpen(0.02);
                    attList.update();
                }
            });

            bottomPanel.add(forgetButton);
            bottomPanel.add(randomButton);
            bottomPanel.add(blurButton);
            bottomPanel.add(sharpenButton);

            add(bottomPanel, BorderLayout.SOUTH);

//            this.tree = new JTree((TreeNode)new GraphTreeProjectionModel(memory, modeSelect));
//            tree.setRootVisible(false);
//            add(tree, BorderLayout.CENTER);

            new Thread(this).start();

            //TODO add widgets that show the current size of the memory
        }
        boolean running = true;

        @Override public void run() {
            while (running) {
                try {
                    update();
                    Thread.sleep(periodMS);
                } catch (InterruptedException ex) {
                }
            }

        }

        protected void update() {
            attList.update();

            try {
                statusPanel.setString("Memory: " + memory.getGraph().getNodeCount() + " nodes, " + memory.getGraph().getEdgeCount() + " edges || " + attList.getShown() + " nodes shown");
            }
            catch (Exception e) { } 
        }
    }

    public class BrowserPanel extends JPanel {

        private final JSplitPane split;

        public BrowserPanel() {
            super(new BorderLayout());


            split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
            add(split, BorderLayout.CENTER);

            split.setLeftComponent(new ThingTreePanel());

            browserContentPanel = new BrowserContentPanel();
            split.setRightComponent(browserContentPanel);

        }
    }

    public class BrowserContentPanel extends JPanel {

        public BrowserContentPanel() {
            super(new BorderLayout());

            JPanel toolbar = new JPanel(new GridBagLayout());
            GridBagConstraints gc = new GridBagConstraints();


            JButton backButton = new JButton("<-");
            gc.fill = gc.VERTICAL;
            gc.gridx = 0;
            toolbar.add(backButton, gc); //backward

            gc.gridx++;
            toolbar.add(new JButton("<*"), gc); //incoming

            gc.gridx++;
            gc.fill = gc.BOTH;
            gc.weightx = 1.0;
            JTextField inputPanel = new JTextField();
            toolbar.add(inputPanel, gc);

            gc.weightx = 0;
            gc.fill = gc.VERTICAL;
            gc.gridx++;
            toolbar.add(new JButton("*>"), gc); //outgoing

            gc.gridx++;
            toolbar.add(new JButton("->"), gc); //forward

            gc.gridx++;
            gc.anchor = gc.WEST;
            toolbar.add(new PopupButton("View", viewMenu), gc);

            add(toolbar, BorderLayout.NORTH);

            add(contentPanel, BorderLayout.CENTER);
        }
    }

    public class StatusPanel extends JPanel {

        public StatusPanel() {
            super(new FlowLayout(FlowLayout.LEFT));
            add(new JLabel("(graph/memory & net statistics"));
        }

        public void setString(String s) {
            removeAll();
            add(new JLabel(s));
            updateUI();
        }
    }

    public void setView(Object o, ObjectPanelizer view) {
        JComponent j = view.newPanel(o, memory, actions);
        contentPanel.removeAll();
        contentPanel.add(j, BorderLayout.CENTER);
        contentPanel.updateUI();
    }

    public void focus(Object o) {
        memory.add(o, focusSpike);
        ObjectPanelizer defaultView = viewMenu.update(o);
        setView(o, defaultView);
    }

    public NowPanel(ScalarGraphMap m, Actions a) {
        super(new BorderLayout());
        this.actions = a;
//        split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
//        add(split, BorderLayout.CENTER);
//
        this.memory = m;

        //split.setTopComponent(new InputPanel());
        browserPanel = new BrowserPanel();
        add(browserPanel, BorderLayout.CENTER);

        statusPanel = new StatusPanel();
        add(statusPanel, BorderLayout.SOUTH);
        updateUI();

    }
}
