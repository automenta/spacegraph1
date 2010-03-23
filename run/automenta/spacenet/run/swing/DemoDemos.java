/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.spacenet.run.swing;

import automenta.spacenet.run.story.SwingWindow;
import automenta.spacenet.run.ArdorPanel;
import automenta.spacenet.run.bio.DemoCell;
import automenta.spacenet.run.control.DemoDragRectSketching;
import automenta.spacenet.run.control.DemoDraggable;
import automenta.spacenet.run.control.DemoFirstPerson;
import automenta.spacenet.run.geom.DemoBox;
import automenta.spacenet.run.geom.DemoLine3D;
import automenta.spacenet.run.geom.DemoRectAspect;
import automenta.spacenet.run.geom.layout.DemoRowCol;
import automenta.spacenet.run.geom.text.DemoChar2D;
import automenta.spacenet.run.geom.text.DemoChar3D;
import automenta.spacenet.run.geom.text.DemoText1;
import automenta.spacenet.run.geom.text.DemoText3D;
import automenta.spacenet.run.graph.neural.DemoBrainz;
import automenta.spacenet.run.graph.neural.DemoHyperassociativeMap;
import automenta.spacenet.run.graph.neural.DemoNeuroph;
import automenta.spacenet.run.physics.DemoAnimatedConvexHullBlob;
import automenta.spacenet.run.physics.DemoFallingObjects;
import automenta.spacenet.run.physics.DemoShootingBoxes;
import automenta.spacenet.run.bio.DemoSnake;
import automenta.spacenet.run.surface.DemoBitmapSurface;
import automenta.spacenet.run.widget.DemoButton;
import automenta.spacenet.run.widget.DemoDesktop;
import automenta.spacenet.run.widget.DemoMetaBox;
import automenta.spacenet.run.widget.DemoPanel;
import automenta.spacenet.run.widget.DemoSpinner;
import automenta.spacenet.run.widget.DemoTextPanel;
import automenta.spacenet.run.widget.DemoWindow;
import automenta.spacenet.space.geom.ProcessBox;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 *
 * @author seh
 */
public class DemoDemos {
    private final JPanel xp;
    private ArdorPanel jp;

    public DemoDemos() {
        super();

        List<Class<? extends ProcessBox>> demoClasses = new LinkedList();
        initDemos(demoClasses);

        xp = new JPanel(new BorderLayout());
        xp.add(newMenuPanel(demoClasses), BorderLayout.WEST);


        new SwingWindow(xp, 800, 600, true);

    }

    public static void main(String[] args) {
        new DemoDemos();
    }

    protected void initDemos(List<Class<? extends ProcessBox>> c) {
        c.add(DemoButton.class);
        c.add(DemoNeuroph.class);
        c.add(DemoFirstPerson.class);
        c.add(DemoDraggable.class);
        c.add(DemoDragRectSketching.class);
        c.add(DemoLine3D.class);
        c.add(DemoBox.class);
        c.add(DemoRectAspect.class);
        c.add(DemoRowCol.class);
        c.add(DemoChar2D.class);
        c.add(DemoChar3D.class);
        c.add(DemoText1.class);
        c.add(DemoText3D.class);
        c.add(DemoBrainz.class);
        c.add(DemoHyperassociativeMap.class);
        c.add(DemoCell.class);
        c.add(DemoSnake.class);
        c.add(DemoShootingBoxes.class);
        c.add(DemoFallingObjects.class);
        c.add(DemoAnimatedConvexHullBlob.class);
        c.add(DemoBitmapSurface.class);
        c.add(DemoDesktop.class);
        c.add(DemoMetaBox.class);
        c.add(DemoSpinner.class);
        c.add(DemoPanel.class);
        c.add(DemoWindow.class);
        c.add(DemoTextPanel.class);


        
    }

    protected JScrollPane newMenuPanel(Collection<Class<? extends ProcessBox>> demoClasses) {
        JPanel m = new JPanel();
        m.setLayout(new BoxLayout(m, BoxLayout.PAGE_AXIS));
        for (Class<? extends ProcessBox> c : demoClasses) {
            addButton(m, c);
        }
        return new JScrollPane(m);
    }

    protected void addButton(JPanel j, final Class<? extends ProcessBox> c) {
        JButton jb = new JButton(c.getSimpleName());
        j.add(jb);
        jb.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                showDemo(c);
            }
        });
    }

    protected void showDemo(Class<? extends ProcessBox> c) {
        try {
            if (jp!=null) {
                xp.remove(jp);
            }

            jp = new ArdorPanel(c.newInstance());
            xp.add(jp, BorderLayout.CENTER);

            xp.updateUI();
        } catch (InstantiationException ex) {
            Logger.getLogger(DemoDemos.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(DemoDemos.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}
