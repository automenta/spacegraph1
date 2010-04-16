/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.spacenet.run.old.story;

import automenta.spacenet.var.graph.map.ScalarGraphMap;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author seh
 */
class WideIconPanelizer implements GraphNodePanelizer {

    public Font getFont(double a) {
        int s = 9 + (int)(a * 10.0);
        Font f= new Font("Arial", Font.PLAIN, s);
        return f;
    }

    @Override public JPanel newPanel(final Object node, ScalarGraphMap attention, final ActionListener actionListener) {
        double a = attention.valueNormalized(node);
        JPanel j = new JPanel(new BorderLayout()) {

            @Override public boolean equals(final Object obj) {
                return obj.equals(node);
            }

            @Override public int hashCode() {
                return node.hashCode();
            }
        };
        //,,
//        JTextArea jta = new JTextArea(node.toString());
//        jta.setWrapStyleWord(true);
//        jta.setLineWrap(true);
//        jta.setEditable(false);
        JLabel jta = new JLabel("<html>" + node.toString() + "</html>");
        jta.setFont(getFont(a));
        j.add(jta, BorderLayout.CENTER);
        jta.setOpaque(false);
        Color backColor = getColor(node, a);
        j.setBackground(backColor);

//        JButton b = new JButton("*");
//        b.setOpaque(false);
//        b.setBackground(backColor);
//        j.add(b, BorderLayout.WEST);
//        b.addActionListener(actionListener);
        j.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                actionListener.actionPerformed(null);
            }
        });

        int bo = 2;
        j.setBorder(BorderFactory.createEmptyBorder(bo, bo, bo, bo));
        return j;
    }

    public static Color getColor(Object o, double a) {
        float h = ((float) o.getClass().hashCode() / Integer.MAX_VALUE);
        float b = 0.9f;
        float s = (float) (0.4 + 0.6 * a);
        return Color.getHSBColor(h, s, b);
    }
}
