/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.spacenet.plugin.comm.twitter;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import twitter4j.Status;
import twitter4j.Trends;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;

/**
 *
 * @author seh
 */
abstract public class TwitterAgent {

    protected final Twitter t;

    public TwitterAgent() {
        super();

        t = new Twitter();
        t.setClientVersion("SpaceNet NeurOSelf 001");
        
    }

    public void addProfile(String userID) {
        try {
            List<Status> pt = t.getUserTimeline(userID);
            onStatus(pt);

        } catch (TwitterException ex) {
            Logger.getLogger(TwitterAgent.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void addPublicTimeline() {
        List<Status> pt;
        try {
            pt = t.getPublicTimeline();
            onStatus(pt);
        } catch (TwitterException ex) {
            ex.printStackTrace();
        }

    }

    void stop() {
    }

    public void onStatus(List<Status> pt) {
        for (Status s : pt) {
            onStatus(s);
        }
    }

    abstract protected void onStatus(Status s);

    public User getUser(String id) {
        try {
            return t.showUser(id);
        } catch (TwitterException ex) {
            return null;
        }
    }

    public Trends getTrends() {
        try {
            return t.getTrends();
        }
        catch (TwitterException ex) {
            return null;
        }
    }
}
