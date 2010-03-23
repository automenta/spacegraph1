/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package automenta.spacenet.plugin.xml;

import automenta.spacenet.plugin.comm.Contains;
import automenta.spacenet.var.graph.MemGraph;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.Remark;
import org.htmlparser.Tag;
import org.htmlparser.Text;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

/**
 *
 * @author seh
 */
public class HTMLGrapher {
    private final MemGraph graph;

    public HTMLGrapher(MemGraph graph, String url) {
        super();
        this.graph = graph;

        addURL(url);
    }

    public void addURL(String url) {
        Parser p;
        try {
            p = new Parser(url);
            p.setFeedback(null);
            NodeList nodes = p.parse(null);
            for (Node n : nodes.toNodeArray()) {
                addNode(url, null, n);
            }
        } catch (ParserException ex) {
            Logger.getLogger(HTMLGrapher.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    public void addNode(String url, Object parent, Node n) {
        Object node = n;

        if (n instanceof Text) {
            Text t = (Text)n;
            String text = t.getText();
            text = text.trim();
            if (text.equals(""))
                return;
            node = text;

            //...
        }
        else if (n instanceof Tag) {
            Tag t = (Tag)n;
            //TODO use HTMLTag class
            node = "htmlTag{" + t.getTagName() + "}";
            //TODO recurse tag attributes
        }
        else if (n instanceof Remark) {
            Remark r = (Remark)n;
            //TODO use HTMLComment class
            node = "htmlComment{" + r.getText() + "}";
            //...
        }

        System.out.println("ADDING NODE: \"" + node + "\"");
        
        graph.addNode(node);
        if (parent!=null) {
            graph.addEdge(new Contains(), parent, node);
        }
        else {
            try {
                URL u = new URL(url);
                graph.addNode(u);
                graph.addEdge(new Contains(), u, node);
            } catch (MalformedURLException ex) {
                Logger.getLogger(HTMLGrapher.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if (n.getChildren()!=null) {
            for (Node c : n.getChildren().toNodeArray()) {
                addNode(url, node, c);
            }
        }
    }

}
