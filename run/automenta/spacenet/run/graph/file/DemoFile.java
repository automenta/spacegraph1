/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.spacenet.run.graph.file;

import automenta.spacenet.plugin.file.FileGrapher;
import automenta.spacenet.run.ArdorSpacetime;
import automenta.spacenet.run.DefaultGraphBox;
import automenta.spacenet.var.graph.MemGraph;

/**
 *
 * @author seh
 */
public class DemoFile extends DefaultGraphBox {

    private final MemGraph graph = new MemGraph();

    public DemoFile() {
        super();


        FileGrapher g = new FileGrapher(graph,"/", 1);


    }

    @Override
    public MemGraph getGraph() {
        return graph;
    }

    @Override
    protected void start() {
        super.start();
    }

    public static void main(String[] args) {
        ArdorSpacetime.newWindow(new DemoFile());
    }
}
