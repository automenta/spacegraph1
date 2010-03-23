package automenta.spacenet.run.story;

import automenta.spacenet.run.DefaultActions;
import automenta.spacenet.var.action.Actions;
import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class DemoSwingPossible {

    Actions actions = new Actions();
    
    public static class StringPossiblePanel extends JPanel {
        private final JTextField inputField;
        private final JPanel outputField;

        public StringPossiblePanel() {
            super();
            setLayout(new BorderLayout());
            
            inputField = new JTextField();
            add(inputField, BorderLayout.NORTH);

            outputField = new JPanel();
            add(outputField, BorderLayout.CENTER);

            inputField.addKeyListener(new KeyListener() {

                @Override
                public void keyTyped(KeyEvent e) {
                }

                @Override
                public void keyPressed(KeyEvent e) {
                }

                @Override
                public void keyReleased(KeyEvent e) {
                    update();
                }

            });

        }

        protected void update() {

            String t = inputField.getText();
            outputField.removeAll();

            outputField.add(new JLabel("(Possilibities for: " + t));

            updateUI();
        }
    }

    public DemoSwingPossible() {
        super();
        
        DefaultActions.addActions(actions);
        
        new SwingWindow(new StringPossiblePanel(), 300, 200);
    }

    public static void main(String[] args) {
        new DemoSwingPossible();
    }
}
