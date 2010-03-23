/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package automenta.spacenet.run;

import automenta.spacenet.plugin.comm.twitter.TwitterGrapher;
import automenta.spacenet.plugin.file.FileGrapher;
import automenta.spacenet.plugin.xml.HTMLGrapher;
import automenta.spacenet.space.geom.layout.ColRect;
import automenta.spacenet.space.geom.layout.RowRect;
import automenta.spacenet.space.geom.text3d.Font3D;
import automenta.spacenet.space.widget.button.Button;
import automenta.spacenet.space.widget.button.ButtonAction;
import automenta.spacenet.space.widget.window.Window;
import automenta.spacenet.var.graph.MemGraph;
import javax.swing.JOptionPane;

/**
 *
 * @author seh
 */
public class OSRootMenu extends Window {

    Font3D font = DemoDefaults.font;
    
    public OSRootMenu(final TwitterGrapher tg) {
        super();

        final MemGraph graph = tg.getGraph();

        Button trendsButton = new Button(font, "Trends");
        trendsButton.add(new ButtonAction() {
            @Override public void onButtonClicked(Button b) {
                tg.addTrends();
            }
        });

        
        Button publicButton = new Button(font, "Public");
        publicButton.add(new ButtonAction() {
            @Override public void onButtonClicked(Button b) {
                tg.addPublicTimeline();
            }
        });

        Button filesButton = new Button(font, "Files");
        filesButton.add(new ButtonAction() {
           @Override public void onButtonClicked(Button b) {
               String path = getString("File Path, or URL", "/");
               new FileGrapher(graph,"/", 1);
           }
        });

        Button htmlButton = new Button(font, "Web Site");
        htmlButton.add(new ButtonAction() {
           @Override public void onButtonClicked(Button b) {
               String url = getString("URL", "http://");
               new HTMLGrapher(graph, url);           
           }
        });


        Button agentButton = new Button(font, "Agent...");

        Button imagesButton = new Button(font, "Images...");

        Button clearButton = new Button(font, "Clear");
        clearButton.add(new ButtonAction() {
            @Override public void onButtonClicked(Button b) {
                tg.getGraph().clear();
            }
        });

        
        add(new RowRect(0.01,
            new ColRect(0.01, trendsButton, publicButton, filesButton),
            new ColRect(0.01, htmlButton, agentButton, imagesButton),
            clearButton)).moveDZ(0.05).scale(0.9);
    }

    public String getString(String message, String defaultValue) {
        String input = JOptionPane.showInputDialog(message, defaultValue);
        return input;
    }
}
