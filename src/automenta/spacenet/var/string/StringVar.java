/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.spacenet.var.string;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author seh
 */
public class StringVar {

    private String s;

    public interface IfStringChanges {

        public void onStringChange(StringVar s);
    }
    private List<IfStringChanges> stringChanges = new LinkedList(); //TODO lazy-instantiate this

    public StringVar() { this(""); }

    public StringVar(String s) {
        super();
        this.s = s;
    }

    public void set(String s) {
        boolean valueChanged = true;

        if (this.s == s) {
            return;
        }

        if (this.s.equals(s)) {
            valueChanged = false;
        }

        this.s = s;

        if (valueChanged) {
            notifyChanges();
        }
    }

    public String s() {
        return s;
    }

    protected void notifyChanges() {
        for (IfStringChanges i : stringChanges) {
            i.onStringChange(this);
        }
    }

    public IfStringChanges add(IfStringChanges i) {
        stringChanges.add(i);
        return i;
    }

    public IfStringChanges remove(IfStringChanges i) {
        stringChanges.remove(i);
        return i;
    }

    public static String padToLength(String l, int maxLineLength) {
        if (l.length() < maxLineLength) {
            int padLength = maxLineLength - l.length();
            char[] ch = new char[padLength];
            Arrays.fill(ch, ' ');
            return l.concat(new String(ch));
        } else {
            return l;
        }
    }
	public void append(char character) {
		set(s().concat(new Character(character).toString()));
	}
	public int length() {
		return s().length();
	}

	public void insert(int pos, char character) {
		String current = s();
		String pre = current.substring(0, pos);
		String next= pre + character;

		if (pos < current.length())
			next += current.substring(pos, current.length());

		set( next );
	}

	public void set(StringVar t) {
		set(t.s());
	}
	public void append(String string) {
		set(s().concat(string));
	}

	public void set(double d, int decimalPlaces) {
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(decimalPlaces);

		set( nf.format(d) );
	}


}
