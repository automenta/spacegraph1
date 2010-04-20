/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.spacenet.run.old.os;

import automenta.spacenet.run.ArdorSpacetime;
import automenta.spacenet.run.old.DefaultGraphBuilder;
import automenta.spacenet.var.graph.tree.TreeGraph;
import automenta.spacenet.var.graph.tree.TreeNode;
import automenta.spacenet.run.control.DemoZooming;
import automenta.spacenet.space.widget.button.ButtonAction;
import automenta.spacenet.run.control.ZoomableRect;
import automenta.spacenet.run.geom.DemoBox;
import automenta.spacenet.run.geom.DemoRect;
import automenta.spacenet.run.widget.DemoButton;
import automenta.spacenet.space.control.camera.FacesCameraBox;
import automenta.spacenet.space.geom.Box;
import automenta.spacenet.space.geom.ProcessBox;
import automenta.spacenet.space.geom.Rect;
import automenta.spacenet.space.geom.graph.GraphBox;
import automenta.spacenet.space.geom.graph.arrange.forcedirect.ForceDirecting;
import automenta.spacenet.space.geom.graph.arrange.forcedirect.ForceDirecting.ForceDirectedParameters;
import automenta.spacenet.space.geom.graph.build.BoxLineBuilder;
import automenta.spacenet.space.geom.text3d.Font3D;
import automenta.spacenet.space.geom.text3d.Text3D;
import automenta.spacenet.space.widget.button.Button;
import automenta.spacenet.var.graph.MemGraph;
import com.ardor3d.math.Vector3;
import java.awt.Font;

/**
 *
 * @author Nader
 */
public class DemoMenu extends ProcessBox {


    @Override
    protected void start() {
        MemGraph g = new TreeGraph(new TreeNode("Demos",
            new TreeNode(DemoButton.class),
            new TreeNode(DemoBox.class),
            new TreeNode(DemoRect.class),
            new TreeNode(DemoZooming.class)));

        System.out.println(g);
        
        ForceDirectedParameters params = new ForceDirectedParameters(new Vector3(50, 50, 50), 0.025, 0.03, 1.0);
        ForceDirecting fd = new ForceDirecting(params, 0.05, 6, 0.5);



        add(new GraphBox(g, new DefaultGraphBuilder(), fd));

    }

    public static void main(String[] argV) {
        ArdorSpacetime.newWindow(new DemoMenu());
    }
}
