/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.spacenet.run.old.story;

import automenta.spacenet.var.action.Actions;
import automenta.spacenet.var.graph.MemGraph;
import automenta.spacenet.var.list.ListVar;
import automenta.spacenet.var.map.MapVar;
import automenta.spacenet.var.scalar.BoolVar;
import automenta.spacenet.var.scalar.DoubleVar;
import automenta.spacenet.var.scalar.IntVar;
import automenta.spacenet.var.string.StringVar;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 *
 * @author seh
 */
class DefaultObjectPanel extends JPanel {

    private final Object object;
    private final Actions actions;
    private final MemGraph graph;
    Font headerFont = new Font("Arial", 1, 16);
    Font header2Font = new Font("Arial", 1, 14);
    Font normalFont = new Font("Arial", 1, 12);
    private final JPanel bottomPanel;
    private final JPanel topPanel;

    public DefaultObjectPanel(Object o, MemGraph graph, Actions a) {
        super(new GridBagLayout());

        this.object = o;
        this.graph = graph;
        this.actions = a;

        Color c = WideIconPanelizer.getColor(o, 0.1);
        Color headerColor = WideIconPanelizer.getColor(o, 0.4);
        setBackground(c);

        GridBagConstraints gc = new GridBagConstraints();
        gc.anchor = gc.NORTHWEST;

        topPanel = new JPanel(new BorderLayout());
        gc.gridy = 0;
        gc.fill = gc.HORIZONTAL;
        gc.weightx = 1.0;
        gc.weighty = 0.0;
        //topPanel.setOpaque(false);
        topPanel.setBackground(headerColor);
        add(topPanel, gc);

        bottomPanel = new JPanel();
        gc.gridy++;
        gc.fill = gc.HORIZONTAL;
        gc.weightx = 1.0;
        gc.weighty = 1.0;
        gc.insets = new Insets(5, 15, 5, 5);
        bottomPanel.setOpaque(false);
        add(bottomPanel, gc);

        addHeader();

        //add action menu
        addActionMenu();

        //add list of editables mixed with list of links (expandable to a new edit object view recursively)
        addProperties();

        updateUI();


    }

    public static Collection<Method> getVariableGetMethods(Object o) {
        List<Method> l = new LinkedList();

        for (Method m : o.getClass().getMethods()) {
            if (m.getReturnType().isAssignableFrom(BoolVar.class)) {
                l.add(m);
                continue;
            }
            if (m.getReturnType().isAssignableFrom(IntVar.class)) {
                l.add(m);
                continue;
            }
            if (m.getReturnType().isAssignableFrom(DoubleVar.class)) {
                l.add(m);
                continue;
            }
            if (m.getReturnType().isAssignableFrom(StringVar.class)) {
                l.add(m);
                continue;
            }
            if (m.getReturnType().isAssignableFrom(ListVar.class)) {
                l.add(m);
                continue;
            }
            if (m.getReturnType().isAssignableFrom(MapVar.class)) {
                l.add(m);
                continue;
            }
        }
        return l;
    }

    private Collection<Method> getInvokableMethods(Object o) {
        List<String> excludedMethodNames = Arrays.asList(new String[]{"wait", "hashCode", "toString", "notify", "notifyAll"});
        List<Method> l = new LinkedList();
        for (Method m : o.getClass().getMethods()) {

            //TODO support methods with > 0 methods
            if (m.getParameterTypes().length == 0) {
                if (!m.getName().startsWith("get")) {
                    if (!excludedMethodNames.contains(m.getName())) {
                        l.add(m);
                    }
                }
            }
        }
        return l;

    }

    public class HeaderText extends JPanel {

        int maxTitleLength = 24;

        public HeaderText(String title, String subtitle) {
            super(new BorderLayout());

            JLabel titleLabel = new JLabel(title.length() > maxTitleLength ? title.substring(maxTitleLength) : title);
            JLabel subLabel = new JLabel(subtitle);

            titleLabel.setFont(headerFont);
            subLabel.setFont(normalFont);

            add(titleLabel, BorderLayout.CENTER);
            add(subLabel, BorderLayout.SOUTH);
        }
    }

    private void addHeader() {

        //icon + text + subtext
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setOpaque(false);

        JPanel label = new HeaderText(object.toString(), object.getClass().getSimpleName());
        label.setOpaque(false);
        headerPanel.add(label);


        topPanel.add(headerPanel, BorderLayout.CENTER);
    }

    private void addProperties() {

        bottomPanel.setLayout(new GridBagLayout());

        GridBagConstraints gc = new GridBagConstraints();

        gc.anchor = gc.NORTHWEST;
        gc.weightx = 1.0;
        gc.fill = gc.NONE;
        gc.gridy = 0;
        gc.insets = new Insets(15, 1, 1, 12);
        gc.ipadx = 5;
        gc.ipady = 5;

        //gets invokable methods
        Collection<Method> invokables = getInvokableMethods(object);
        if (invokables.size() > 0) {
            bottomPanel.add(newInvokablesPanel(object, invokables), gc);
            gc.gridy++;
        }

        Collection<Method> variables = getVariableGetMethods(object);
        for (Method m : variables) {
            bottomPanel.add(newVariablePanel(object, m), gc);
            gc.gridy++;
        }

        if (graph != null) {
            Collection<Object> inEdges = graph.getIncidentEdges(object);
            if (inEdges != null) {
                for (Object e : inEdges) {
                    Object o = graph.getOpposite(object, e);

                    //TODO this might not be able to handle hyperedges, check
                    boolean incoming = (graph.getSource(e) == object);
                    bottomPanel.add(newEdgePanel(e, o, incoming), gc);
                    gc.gridy++;
                }
            }
        }

    }

    protected JPanel newInvokablesPanel(final Object o, Collection<Method> invokables) {
        JPanel x = new JPanel(new BorderLayout());
        x.setOpaque(false);

        final JPanel output = new JPanel(new FlowLayout());
        output.setOpaque(false);

        JPanel j = new JPanel(new FlowLayout());
        j.setOpaque(false);

        for (final Method m : invokables) {
            JButton b = new JButton(m.getName());
            b.addActionListener(new ActionListener() {
                @Override public void actionPerformed(ActionEvent e) {
                    SwingUtilities.invokeLater(new Runnable() {

                        @Override public void run() {
                            try {
                                Object result = m.invoke(o);
                                output.removeAll();
                                output.add(new JLabel(m.getName() + ": " + result.toString()));
                            } catch (Exception ex) {
                                Logger.getLogger(DefaultObjectPanel.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    });
                }
            });
            j.add(b);

        }

        x.add(j, BorderLayout.CENTER);
        x.add(output, BorderLayout.SOUTH);

        return x;
    }

    protected JPanel newVariablePanel(Object node, Method method) {
        return new VariableEditPanel(method, node);
    }

    protected JPanel newEdgePanel(Object edge, final Object o, boolean isIncoming) {
        final JPanel p = new JPanel(new FlowLayout());
        p.setOpaque(true);
        p.setBackground(WideIconPanelizer.getColor(o, 0.2));

        JPanel s = new JPanel(new GridBagLayout());
        s.setOpaque(false);
        GridBagConstraints gc = new GridBagConstraints();

        gc.anchor = gc.NORTHWEST;

        JHyperLink je = new JHyperLink(edge.toString(), "Go to Edge");
        gc.gridy = 0;
        s.add(je, gc);

        final JHyperLink jh = new JHyperLink(o.toString(), "Go to Node");
        jh.setFont(header2Font);
        gc.gridy = 1;
        s.add(jh, gc);

        JButton icon = new JButton(UIManager.getIcon("FileView.fileIcon"));

        final JPanel contentArea = new JPanel();
        contentArea.setOpaque(false);

        final JToggleButton expandButton = new JToggleButton(isIncoming ? ">>" : "<<");
        expandButton.addActionListener(new ActionListener() {

            @Override public void actionPerformed(ActionEvent e) {
                if (expandButton.isSelected()) {
                    //contentArea.add(new JLabel("expanded"));
                    contentArea.add(new DefaultObjectPanel(o, graph, actions));
                    jh.setVisible(false);
                } else {
                    contentArea.removeAll();
                    jh.setVisible(true);
                }
                p.updateUI();
            }
        });

        if (o != object) {
            p.add(expandButton);
        }
        p.add(icon);
        p.add(s);


        JPanel q = new JPanel(new GridBagLayout());
        q.setOpaque(false);
        GridBagConstraints gd = new GridBagConstraints();
        gd.anchor = GridBagConstraints.NORTHWEST;
        gd.fill = gc.NONE;

        gd.gridy = 0;
        gd.weightx = 1.0;
        q.add(p, gd);

        gd.gridy++;
        gd.weightx = 0;
        gd.ipadx = 25;
        gd.insets = new Insets(5, 35, 5, 5);

        q.add(contentArea, gd);

        return q;
    }

    private void addActionMenu() {
        JPanel jb = new JPanel(new FlowLayout(FlowLayout.LEFT));
        jb.setOpaque(false);

        JButton jm = new JButton("Action");
        //jm.add(new JMenuItem("X"));
        jb.add(jm);
        topPanel.add(jb, BorderLayout.SOUTH);
    }
//    public static class RoundedPanel extends JPanel {
//
//        public RoundedPanel() {
//            super();
//        }
//        public RoundedPanel(LayoutManager l) {
//            this();
//            setLayout(l);
//            setOpaque(false);
//        }
//
//
//        @Override public void paint(Graphics g) {
//            super.paint(g);
//
//            Graphics2D graphics2 = (Graphics2D) g;
//            graphics2.setBackground(Color.yellow);
//            graphics2.setColor(Color.red);
//            RoundRectangle2D roundedRectangle = new RoundRectangle2D.Float(getX(), getY(), getWidth(), getHeight(), 10, 10);
//            graphics2.draw(roundedRectangle);
//
//
//        }
//    }
}
