/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package automenta.spacenet.space.widget.text;

import automenta.spacenet.space.control.Pressable;
import automenta.spacenet.space.geom.text3d.Font3D;
import automenta.spacenet.space.widget.panel.DefaultPanelModel;
import automenta.spacenet.var.physical.Color;
import automenta.spacenet.var.string.StringVar;
import com.ardor3d.intersection.PickData;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 *
 * @author seh
 */
public class TextEditPanel extends TextPanel implements Pressable {

    public static class StringVarEditPanel extends JPanel implements ActionListener {
        private final StringVar text;
        private final JTextArea textArea;

        private StringVarEditPanel(StringVar text) {
            super(new BorderLayout());
            this.text = text;

            textArea = new JTextArea(text.s());
            textArea.setWrapStyleWord(true);
            textArea.setLineWrap(true);
            add(textArea, BorderLayout.CENTER);

            JButton updateButton = new JButton("Update");
            updateButton.addActionListener(this);
            add(updateButton, BorderLayout.SOUTH);           
        }

        @Override public void actionPerformed(ActionEvent e) {
            String newText = textArea.getText();
            text.set(newText);
        }

    }

    public TextEditPanel(Font3D font, StringVar text) {
        //TODO HACK use a class DefaulTextEditPanelModel
        super(new DefaultPanelModel(Color.White, Color.White), font, text);
    }
    

    public TextEditPanel(Font3D font, String text) {
        this(font, new StringVar(text));
    }

    @Override
    public void onPressStart(PickData pick) {
    }

    @Override
    public void onPressStop(PickData pick) {
        //TODO hack this opens a swing window to edit it...
        JFrame frame = new JFrame("Edit: " + getText());
        
        frame.getContentPane().add(new StringVarEditPanel(getText()));

        frame.setSize(300, 300);
        frame.setVisible(true);
    }


    

    


}
