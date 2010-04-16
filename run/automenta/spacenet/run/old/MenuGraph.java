/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.spacenet.run.old;

import automenta.spacenet.plugin.comm.Next;
import automenta.spacenet.run.control.DemoDraggable;
import automenta.spacenet.run.control.DemoTouchClick;
import automenta.spacenet.run.control.DemoZooming;
import automenta.spacenet.run.geom.DemoBox;
import automenta.spacenet.run.geom.DemoRect;
import automenta.spacenet.var.graph.MemGraph;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author seh
 */
public class MenuGraph extends MemGraph {

    public static List<Class> getClasses(String pckgname) throws ClassNotFoundException {
        ArrayList<Class> classes = new ArrayList<Class>();
        // Get a File object for the package
        File directory = null;
        try {
            ClassLoader cld = Thread.currentThread().getContextClassLoader();
            if (cld == null) {
                throw new ClassNotFoundException("Can't get class loader.");
            }
            String path = '/' + pckgname.replace('.', '/');
            URL resource = cld.getResource("." + path);
            if (resource == null) {
                throw new ClassNotFoundException("No resource for " + path + " : " + resource);
            }
            System.out.println(" resource for " + path + " : " + resource );
            directory = new File(resource.getFile());
        } catch (NullPointerException x) {
            throw new ClassNotFoundException(pckgname + " (" + directory + ") does not appear to be a valid package");
        }
        if (directory.exists()) {
            // Get the list of the files contained in the package
            String[] files = directory.list();
            for (int i = 0; i < files.length; i++) {
                // we are only interested in .class files
                if (files[i].endsWith(".class")) {
                    // removes the .class extension
                    classes.add(Class.forName(pckgname + '.' + files[i].substring(0, files[i].length() - 6)));
                }
            }
        } else {
            throw new ClassNotFoundException(pckgname + " does not appear to be a valid package");
        }
        return classes;
    }

    int i = 0;
    
    public void addClasses(String pckgname, Object parentPackage) throws ClassNotFoundException {
        // Get a File object for the package
        File directory = null;
        try {
            ClassLoader cld = Thread.currentThread().getContextClassLoader();
            if (cld == null) {
                throw new ClassNotFoundException("Can't get class loader.");
            }
            String path = '/' + pckgname.replace('.', '/');
            URL resource = cld.getResource("." + path);
            if (resource == null) {
                throw new ClassNotFoundException("No resource for " + path + " : " + resource);
            }
            directory = new File(resource.getFile());
        } catch (NullPointerException x) {
            throw new ClassNotFoundException(pckgname + " (" + directory + ") does not appear to be a valid package");
        }

        Object pkg = addNode(pckgname);
        if (parentPackage!=null) {
            addEdge("tree." + Integer.toString(i++), parentPackage, pkg);
        }

        if (directory.exists()) {
            // Get the list of the files contained in the package
            String[] files = directory.list();
            for (int i = 0; i < files.length; i++) {
                
                if (new File(directory.getAbsolutePath() + "/" + files[i]).isDirectory()) {
                    try {
                        addClasses(pckgname + "." + files[i], pkg);
                    }
                    catch (Exception e) {
                        Logger.getLogger(MenuGraph.class.toString()).severe("could not recurse " + files[i]);
                    }
                }
                else if (files[i].endsWith(".class") && !files[i].contains("$")) {
                    // removes the .class extension
                    Object cl = addNode(Class.forName(pckgname + '.' + files[i].substring(0, files[i].length() - 6)));

                    System.out.println("adding edge between " + pkg + " and " + cl);
                    
                    addEdge("tree." + Integer.toString(i++), pkg, cl);

                }
            }
        } else {
            throw new ClassNotFoundException(pckgname + " does not appear to be a valid package");
        }
    }

    public MenuGraph() {
        super();
        try {
            addClasses("automenta.spacenet.run", null);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MenuGraph.class.getName()).log(Level.SEVERE, null, ex);
        }


//        addEdge("Demos",
//            addNode(DemoBox.class),
//            addNode(DemoZooming.class),
//            addNode(DemoDraggable.class),
//            addNode(DemoTouchClick.class),
//            addNode(DemoRect.class));

    }

//    public static void main(String[] args) {
//        System.out.println(new MenuGraph().getEdges());
//    }
}
