package automenta.spacenet.run.story;

import automenta.spacenet.var.graph.map.ScalarGraphMap;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * displays a list of items sorted by attention
 */
abstract public class AttentionList extends JPanel implements MouseListener {

    private final ScalarGraphMap att;
    boolean running = true;
    //TODO use float (seconds)
    int shown = 64;
    private final GraphNodePanelizer panelizer;
//    private Map<Object, JPanel> panels = new WeakHashMap();
    //private Map<JPanel, Object> objects = new WeakHashMap();

    public AttentionList(ScalarGraphMap att, GraphNodePanelizer panelizer) {
        super();
        this.att = att;
        this.panelizer = panelizer;
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
    }

    protected void update() {
        removeAll();
        //System.out.println("updating:  " + att.getMin() + " " + att.getMax());
        List<Object> displayed = att.getNodesSortedNow();
        for (int i = 0; i < Math.min(displayed.size(), shown); i++) {
            Object o = displayed.get(i);
            add(getPanel(o));
        }
        updateUI();
    }

    public JPanel getPanel(final Object o) {
//        JPanel p = panels.get(o);
//        if (p == null) {
        JPanel p = panelizer.newPanel(o, att, new ActionListener() {

            @Override public void actionPerformed(ActionEvent e) {
                System.out.println("clicked: " + o);
                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        onFocused(o);
                    }
                });
            }
        });
//            panels.put(o, p);
//            objects.put(p, o);
        return p;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        onFocused(((PresentsObject) ((JPanel) e.getComponent())).getPresented());
    }

//    public Object getObject(JPanel j) {
//        return objects.get(j);
//    }
    abstract public void onFocused(Object o);

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    public int getShown() {
        return shown;
    }
}
