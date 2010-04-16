/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.spacenet.run.old.story;

import automenta.spacenet.plugin.rdf.RDFGrapher;
import automenta.spacenet.var.graph.MemGraph;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.object.ObjectConnection;

/**
 * @see http://www.openrdf.org/doc/alibaba/2.0-alpha2/alibaba-repository-object/index.html
 */
public class Memory {

    public Memory() {
        super();

//        RDFGrapher g = new RDFGrapher(new MemGraph());
//        ObjectConnection con;
//        try {
//            con = g.newObjectConnection();
//
//            ValueFactory vf = con.getValueFactory();
//            URI id = vf.createURI("http://meta.leighnet.ca/data/2009/getting-started");
//
//            Document doc = new Document();
//            doc.setTitle("Getting Started");
//
//            con.addDesignations(doc, id);
//            //con.addObject(doc);
//
////Document doc = con.getObject(Document.class, id);
//
//            con.commit();
//
//            con.close();
//        } catch (RepositoryException ex) {
//            Logger.getLogger(Memory.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//        g.print();
//
//        g.updateRDF();
//
//        System.out.println(g.getGraph().getNodes());
//        System.out.println(g.getGraph().getEdges());

    }
}
