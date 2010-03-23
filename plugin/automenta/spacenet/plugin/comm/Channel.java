/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.spacenet.plugin.comm;

/**
 * IRC channel or Twitter HashTag
 */
public class Channel {

    public final String name;

    public Channel(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Channel) {
            Channel c = (Channel) obj;
            if (c.name.equals(name)) {
                return true;
            }
            return false;
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
