/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.spacenet.run.old.story;

import automenta.spacenet.var.scalar.IntVar;
import automenta.spacenet.var.string.StringVar;
import java.awt.BorderLayout;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author seh
 */
public class VariableEditPanel extends JPanel {

    public VariableEditPanel(Method m, Object obj) {
        super(new BorderLayout());


        addLabel(m);


        Object variable;
        try {
            variable = m.invoke(obj);
            
            setBackground(WideIconPanelizer.getColor(variable, 0.5));

            if (variable instanceof IntVar) {
                initInt((IntVar) variable);
            } else if (variable instanceof StringVar) {
                initString((StringVar) variable);
            } else {
            }
            
        } catch (Exception ex) {
            Logger.getLogger(VariableEditPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void addLabel(Method m) {
        add(new JLabel(m.getName()), BorderLayout.NORTH);
    }

    private void initInt(IntVar intVar) {
        add(new JTextField(), BorderLayout.CENTER);
    }

    private void initString(StringVar stringVar) {
        add(new JTextField(stringVar.s()), BorderLayout.CENTER);
    }
}
