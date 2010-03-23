/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.spacenet.run;

import automenta.spacenet.space.geom.ProcessBox;
import automenta.spacenet.space.geom.graph.GraphBox;
import automenta.spacenet.space.geom.graph.arrange.forcedirect.ForceDirecting;
import automenta.spacenet.space.geom.graph.arrange.forcedirect.ForceDirecting.ForceDirectedParameters;
import automenta.spacenet.space.geom.text3d.Font3D;
import automenta.spacenet.var.graph.MemGraph;
import com.ardor3d.math.Vector3;
import java.awt.Font;

/**
 *
 * @author Nader
 */
public class DemoMenu2 extends ProcessBox {

    Font3D font3d = new Font3D(new Font("Arial", Font.PLAIN, 12), 0.1, true, false, false);

    @Override
    protected void start() {

        MemGraph g = new MenuGraph();

        ForceDirectedParameters params = new ForceDirectedParameters(new Vector3(50, 50, 50), 0.025, 0.03, 1.0);
        ForceDirecting fd = new ForceDirecting(params, 0.05, 5, 0.5);

        add(new GraphBox(g, new DefaultGraphBuilder(), fd));

    }


    public static void main(String[] argV) {
        ArdorSpacetime.newWindow(new DemoMenu2());
    }
}
