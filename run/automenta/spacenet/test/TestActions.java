/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package automenta.spacenet.test;

import automenta.spacenet.var.action.Action;
import automenta.spacenet.var.action.Actions;
import automenta.spacenet.var.action.InstantAction;
import java.util.List;
import junit.awtui.TestRunner;
import junit.framework.TestCase;

/**
 *
 * @author seh
 */
public class TestActions extends TestCase {

    public void testActions() {
        Actions actions = new Actions();
        actions.add(new InstantAction<String,String>() {

            @Override public double applies(String i) {
                return 1.0;
            }

            @Override public String toString(String i) {
                return "relevant";
            }

            @Override protected String run(String i) {
                return i;
            }
        });
        actions.add(new InstantAction<String,String>() {

            @Override public double applies(String i) {
                return 0;
            }

            @Override public String toString(String i) {
                return "irrelevant";
            }

            @Override protected String run(String i) {
                return i;
            }
        });
        actions.add(new InstantAction<String,String>() {

            @Override public double applies(String i) {
                return 0.5;
            }

            @Override public String toString(String i) {
                return "semi-relevant";
            }

            @Override protected String run(String i) {
                return i;
            }
        });

        String input = "x";
        List<Action> results = actions.getApplicable(input);
        assertEquals(2, results.size());
        assertEquals(1.0, results.get(0).applies(input));
        assertEquals(0.5, results.get(1).applies(input));
    }

    public static void main(String[] args) {
        new TestRunner().run(TestActions.class);
    }


    
}
