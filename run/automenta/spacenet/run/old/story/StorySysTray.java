/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.spacenet.run.old.story;

import automenta.spacenet.plugin.rdf.RDFGrapher;
import automenta.spacenet.run.old.graph.rdf.DemoRDF;
import automenta.spacenet.run.swing.*;
import automenta.spacenet.var.action.Actions;
import automenta.spacenet.var.graph.MemGraph;
import automenta.spacenet.var.graph.map.ScalarGraphMap;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import javax.swing.JFrame;
import org.openrdf.rio.RDFFormat;

/**
@see http://java.sun.com/developer/technicalArticles/J2SE/Desktop/javase6/systemtray/
@author seh
 */
public class StorySysTray extends SysTrayIcon {

    JFrame window;
    private final MemGraph graph;
    private final ScalarGraphMap att;

    public StorySysTray(Image image, String tooltip, PopupMenu menu) {
        super(image, tooltip, menu);

        //graph = new MemGraph();

        graph = new RDFGrapher(DemoRDF.dataURL, RDFFormat.RDFXML).getGraph();

        att = new ScalarGraphMap(graph);

    }

    @Override
    public void mousePressed(MouseEvent e) {
        super.mousePressed(e);

        if (e.getButton() == MouseEvent.BUTTON1) {
            Point p = e.getLocationOnScreen();
            toggleWindow(p);
        }
    }

    protected void hideWindow() {
        window.setVisible(false);
        window = null;
    }

    public static Dimension getVideoSize() {
        return Toolkit.getDefaultToolkit().getScreenSize();
    }

    private void showNewWindow(Point p) {
        window = newWindow();
        window.setVisible(true);

        //TODO calculate best screen position.  here is the center of the screen
        centerWindow(window);
    }

    protected void toggleWindow(Point p) {
        if (window != null) {
            if (!window.isVisible()) {
                showNewWindow(p);
            } else {
                hideWindow();
            }
        } else {
            showNewWindow(p);
        }
    }

    protected JFrame newWindow() {
        JFrame jf = new SwingWindow(new NowPanel(att, new Actions()), 300, 400);
        return jf;
    }

    public static void main(String[] args) {
        ActionListener exitListener = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                System.out.println("Exiting...");
                System.exit(0);
            }
        };

        PopupMenu popup = new PopupMenu();
        MenuItem defaultItem = new MenuItem("Exit");
        defaultItem.addActionListener(exitListener);
        popup.add(defaultItem);

        new StorySysTray(Toolkit.getDefaultToolkit().getImage("tray.gif"), "Netention", popup);
    }

    public static void centerWindow(JFrame window) {

        {
            window.setSize((int) (getVideoSize().getWidth() / 4), (int) (getVideoSize().getHeight() / 2));
        }


        //TRY TO CENTER IN FIRST SCREEN
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gs = ge.getScreenDevices();
        for (int j = 0; j < gs.length; j++) {
            GraphicsDevice gd = gs[j];
        
            Rectangle s = gd.getDefaultConfiguration().getBounds();
            int x = (int)(s.getWidth() - window.getWidth())/2;
            int y = (int)(s.getHeight() - window.getHeight())/2;
            window.setLocation(x, y);
            
            return;
        }

        //DEFAULT - place relative to entire virtual screen
        {
            window.setLocation((int) (getVideoSize().getWidth() - window.getWidth()), 0);
        }

    }
}
