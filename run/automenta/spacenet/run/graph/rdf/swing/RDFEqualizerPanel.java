/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package automenta.spacenet.run.graph.rdf.swing;

import automenta.spacenet.plugin.rdf.RDFGrapher;
import automenta.spacenet.var.graph.map.ScalarGraphMap;
import java.awt.BorderLayout;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

/**
 *
 * @author seh
 */
public class RDFEqualizerPanel extends JPanel {
    private final JButton loadButton;
    private final JPanel typeSliders;
    private final RDFGrapher rdf;

    public RDFEqualizerPanel(RDFGrapher rdf, ScalarGraphMap attention) {
        super(new BorderLayout());

        this.rdf = rdf;

        loadButton = new JButton("Load...");
        add(loadButton, BorderLayout.NORTH);

        typeSliders = new JPanel();
        typeSliders.setLayout(new BoxLayout(typeSliders, BoxLayout.PAGE_AXIS));
        add(typeSliders, BorderLayout.CENTER);

        updateEqualizer();
    }

    protected void updateEqualizer() {

        for (String typeURI : rdf.getInstanceTypes()) {
            JPanel s = new JPanel(new BorderLayout());
            s.add(new JLabel(typeURI), BorderLayout.NORTH);

            JSlider sli = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
            s.add(sli, BorderLayout.CENTER);
            
            typeSliders.add(s);
        }

        updateUI();
    }

}
