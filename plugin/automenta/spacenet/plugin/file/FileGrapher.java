package automenta.spacenet.plugin.file;

import automenta.spacenet.plugin.comm.Contains;
import automenta.spacenet.var.graph.MemGraph;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.VFS;

public class FileGrapher {

    FileSystemManager fsManager;
    private MemGraph graph;
    private URI uri;
    private FileObject root;

    public FileGrapher(MemGraph graph,String uriOrPath, int depth) {
        super();


        this.graph = graph;

        addPath(uriOrPath, depth);

    }

    public static Object newMissingFile(String uriOrPath, Exception ex) {
        return "Missing: " + uriOrPath + " : " + ex.toString();
    }

    public void addPath(String uriOrPath, int depth) {
        try {
            if (fsManager == null) {
                fsManager = VFS.getManager();
            }
        } catch (FileSystemException ex) {
            Logger.getLogger(FileGrapher.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }

        try {
            this.uri = new URI(uriOrPath);
        } catch (URISyntaxException ex) {
            graph.addNode(newMissingFile(uriOrPath, ex));
            return;
        }

        FileObject fileObject;

        if (depth > 0) {
            try {
                fileObject = VFS.getManager().resolveFile(uriOrPath);
                if (fileObject.getChildren() != null) {
                    org.apache.commons.vfs.FileObject[] ch = fileObject.getChildren();
                    graph.addNode(fileObject);
                    this.root = fileObject;
                    for (org.apache.commons.vfs.FileObject f : ch) {
                        try {
                            addFile(fileObject, f);
                        } catch (Exception ex) {
                            addFile(fileObject, graph.addNode(newMissingFile(f.getURL().toString(), ex)));
                        }
                    }
                }
            } catch (FileSystemException ex) {
                Logger.getLogger(FileGrapher.class.getName()).log(Level.SEVERE, null, ex);
                graph.addNode(newMissingFile(uriOrPath, ex));
            }
        }


    }

    private void addFile(Object parent, Object child) {
        graph.addNode(child);
        graph.addEdge(new Contains(), parent, child);
    }

    public FileObject getRoot() {
        return root;
    }

    

}
