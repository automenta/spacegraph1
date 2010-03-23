/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package automenta.spacenet.var.action;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * index or database of Action's = procedural memory
 */
public class Actions {

    /** streaming, continuously growing search of possible actions for a given context */
    abstract public static class Possibility {

        public Possibility(Object x) {
            super();
            
        }

        abstract public void stop();
    }

    private final List<Action> actions = new LinkedList();

    public Actions() {
        super();
    }

    public List<Action> getAll() { return actions; }
    
    /** returns a sorted list of applicable actions for a given input */
    public List<Action> getApplicable(Object input) {
        final Map<Action, Double> resultStrengths = new HashMap();
        
        for (Action a : actions) {
            try {
                double s = a.applies(input);
                if (s > 0) {
                    resultStrengths.put(a, s);
                }
            }
            //because 'input' may not fit the generic type parameter of 'a'
            catch (Exception e) { }
        }

        //Sort results
        List<Action> results = new LinkedList(resultStrengths.keySet());
        
        Collections.sort(results, new Comparator<Action>() {
            @Override public int compare(Action a, Action b) {
                double aS = resultStrengths.get(a);
                double bS = resultStrengths.get(b);
                if (aS == bS) return 0;
                if (aS < bS) return 1;
                return -1;
            }
        });

        return results;
    }

    public void add(Action a) {
        actions.add(a);
    }
}
