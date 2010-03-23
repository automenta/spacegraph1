/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.spacenet.space.geom.graph;

import automenta.spacenet.space.Repeat;
import automenta.spacenet.space.geom.layout.ColRect;
import automenta.spacenet.space.geom.layout.RowRect;
import automenta.spacenet.space.geom.text3d.Font3D;
import automenta.spacenet.space.widget.button.Button;
import automenta.spacenet.space.widget.button.ButtonAction;
import automenta.spacenet.space.widget.panel.Panel;
import automenta.spacenet.space.widget.slider.Slider;
import automenta.spacenet.space.widget.text.TextPanel;
import automenta.spacenet.space.widget.window.Window;
import automenta.spacenet.var.graph.MemGraph;
import automenta.spacenet.var.graph.map.AttentionThresholdGraph;
import automenta.spacenet.var.graph.map.ScalarGraphMap;
import automenta.spacenet.var.scalar.DoubleRange;
import automenta.spacenet.var.string.StringVar;
import com.ardor3d.scenegraph.Spatial;

/**
 *
 * @author seh
 */
public class ScalarGraphMapControlWindow extends Window {

    double updatePeriod = 0.5;

    public ScalarGraphMapControlWindow(final ScalarGraphMap map, final AttentionThresholdGraph visGraph, Font3D font) {
        super();

        final MemGraph graph = map.getGraph();

        Button blurButton = new Button(font, "Blur");
        blurButton.add(new ButtonAction() {

            @Override public void onButtonClicked(Button b) {
                map.blur(0.15);
            }
        });
        Button sharpenButton = new Button(font, "Spike");
        sharpenButton.add(new ButtonAction() {

            @Override public void onButtonClicked(Button b) {
                map.addRandom(0, 0.2);
            }
        });
        Button fadeButton = new Button(font, "Fade");
        fadeButton.add(new ButtonAction() {

            @Override public void onButtonClicked(Button b) {
                map.mult(0.9);
            }
        });

        Panel statPanel = new Panel();
        final StringVar statString = new StringVar();
        statPanel.add(new TextPanel(font, statString, 30));

        Panel threshPanel = new Panel();
        DoubleRange range = new DoubleRange(visGraph.getThresh(), 0, 1.0);
        threshPanel.add(new Slider(Slider.SliderDirection.Horizontal, font, range, 0.01, 0.1));


        add(new ColRect(0.1,
            statPanel,
            threshPanel,
            new RowRect(0.1,
            blurButton, sharpenButton, fadeButton) //            new RowRect(0.1, new Text3D(font, "Repulsion", Color.Black), new Spinner(DemoButton.font, repulseRange, 0.01)),
            //            new RowRect(0.1, new Text3D(font, "Length", Color.Black), new Spinner(DemoButton.font, lengthRange, 0.1)))
            )).moveDZ(0.05);


        add(new Repeat(updatePeriod) {

            @Override protected void update(double t, double dt, Spatial parent) {
                statString.set("all[" + graph.getNodeCount() + "|" + graph.getEdgeCount() +
                    "]\nvis[" + visGraph.getNodeCount() + "|" + visGraph.getEdgeCount() + "]");
            }
        });

    }
}
