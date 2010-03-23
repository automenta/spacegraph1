/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.spacenet.plugin.rdf;

import automenta.spacenet.var.graph.MemGraph;
import com.google.common.base.Predicate;
import info.aduna.iteration.CloseableIteration;
import java.net.URL;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.vocabulary.OWL;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.config.RepositoryConfigException;
import org.openrdf.repository.object.ObjectConnection;
import org.openrdf.repository.object.ObjectRepository;
import org.openrdf.repository.object.config.ObjectRepositoryConfig;
import org.openrdf.repository.object.config.ObjectRepositoryFactory;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.repository.sail.SailRepositoryConnection;
import org.openrdf.rio.RDFFormat;
import org.openrdf.sail.Sail;
import org.openrdf.sail.SailConnection;
import org.openrdf.sail.SailException;
import org.openrdf.sail.memory.MemoryStore;

/**
 *
 * @author seh
 */
public class RDFGrapher {

    private final MemGraph graph;
    private final SailRepository rep;
    ObjectRepository objRep;

    public void print() {
        try {
            System.out.println(this);
            visitStatements(new Predicate<Statement>() {

                @Override
                public boolean apply(Statement s) {
                    System.out.println(" " + s);
                    return true;
                }
            });
        } catch (RepositoryException ex) {
            Logger.getLogger(RDFGrapher.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected void visitStatements(Predicate<Statement> visitor) throws RepositoryException {
        SailRepositoryConnection con = rep.getConnection();

        RepositoryResult<Statement> is = con.getStatements(null, null, null, true);
        while (is.hasNext()) {
            Statement s = is.next();
            if (!visitor.apply(s)) {
                break;
            }
        }
        is.close();

        con.close();
    }

    public static class URIInstance {

        public final URI uri;

        public URIInstance(URI uri) {
            super();
            this.uri = uri;
        }

        @Override public String toString() {
            return uri.toString();
        }
    }

    public RDFGrapher(SailRepository sail, MemGraph graph) {
        super();

        this.graph = graph;
        this.rep = sail;



        ObjectRepositoryConfig config = new ObjectRepositoryConfig();
        try {
            sail.initialize();
            objRep = new ObjectRepositoryFactory().createRepository(config, sail);
        } catch (RepositoryConfigException ex) {
            Logger.getLogger(RDFGrapher.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RepositoryException ex) {
            Logger.getLogger(RDFGrapher.class.getName()).log(Level.SEVERE, null, ex);
        }


        updateRDF();
    }

    public ObjectConnection newObjectConnection() throws RepositoryException {
        return objRep.getConnection();
    }

    public RDFGrapher(MemGraph graph) {
        this(new SailRepository(new MemoryStore()), graph);
    }

    public RDFGrapher(URL rdfData, RDFFormat format) {
        super();
        this.graph = new MemGraph();
        this.rep = new SailRepository(new MemoryStore());

        try {
            rep.initialize();
            SailRepositoryConnection con = rep.getConnection();
            con.add(rdfData, rdfData.toString(), format);
            con.close();

        } catch (Exception ex) {
            Logger.getLogger(RDFGrapher.class.getName()).log(Level.SEVERE, null, ex);
        }

        updateRDF();
    }

    public MemGraph getGraph() {
        return graph;
    }

    public SailRepository getRep() {
        return rep;
    }

    public Sail getSail() {
        return rep.getSail();
    }

    public void updateRDF() {
        try {
            getGraph().clear();

            List<Statement> statements = new LinkedList();


            SailConnection con = getSail().getConnection();
            CloseableIteration<? extends Statement, SailException> ist = con.getStatements(null, null, null, true);
            while (ist.hasNext()) {
                statements.add(ist.next());
            }

            for (Statement s : statements) {
                Resource subject = s.getSubject();
                URI predicate = s.getPredicate();
                Value object = s.getObject();
                //TODO context

                if (!excludes(subject)) {
                    if (!excludes(object)) {
                        if (!excludes(predicate)) {
                            getGraph().addNode(subject);
                            getGraph().addNode(object);
                            getGraph().addEdge(new URIInstance(predicate), subject, object);

                        }
                    }
                }

            }

            ist.close();


            con.close();
        } catch (SailException ex) {
            Logger.getLogger(RDFGrapher.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean excludes(Value u) {
        if (u.equals(OWL.CLASS)) {
            return true;
        }
        return false;
    }

    public Set<String> getInstanceTypes() {
        Set<String> t = new HashSet();

        SailRepositoryConnection con;
        Value classURI;
        try {
            con = getRep().getConnection();
            RepositoryResult<Statement> st = con.getStatements(null, RDF.TYPE, null, true);
            while (st.hasNext()) {
                Statement s = st.next();
                classURI = s.getObject();
                t.add(classURI.stringValue());
            }
            st.close();

            con.close();
        } catch (RepositoryException ex) {
            Logger.getLogger(RDFGrapher.class.getName()).log(Level.SEVERE, null, ex);
        }

        return t;
    }
//    public RDFGrapher(URL rdfData) {
//        this()
//    }
}
