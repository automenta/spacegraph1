/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.spacenet.plugin.comm.twitter;

import automenta.spacenet.plugin.comm.Agent;
import automenta.spacenet.plugin.comm.Channel;
import automenta.spacenet.plugin.comm.Creates;
import automenta.spacenet.plugin.comm.Mentions;
import automenta.spacenet.plugin.comm.Message;
import automenta.spacenet.plugin.comm.Next;
import automenta.spacenet.plugin.comm.Retweets;
import automenta.spacenet.var.graph.MemGraph;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import twitter4j.Status;
import twitter4j.Trend;
import twitter4j.Trends;
import twitter4j.User;

public class TwitterGrapher extends TwitterAgent {

    private final MemGraph graph;
    private Map<String, Agent> idAgent = new HashMap();
    private Message previousMessage = null;

    public TwitterGrapher(MemGraph graph) {
        super();

        this.graph = graph;
    }

    @Override protected void onStatus(Status s) {
        Agent a = getAgent(s.getUser());
        Message m = new Message(s.getText(), s.getUser().getProfileImageURL());

        getGraph().addNode(a);
        getGraph().addNode(m);
        getGraph().addEdge(new Creates(), a, m);

        if (previousMessage != null) {
            getGraph().addEdge(new Next(), previousMessage, m);
        }

        if (s.getRetweetDetails()!=null) {
            User retweeingUser = s.getRetweetDetails().getRetweetingUser();
            if (retweeingUser != null) {
                Agent retweetingAgent = getAgent(retweeingUser);
                getGraph().addNode(retweetingAgent);
                getGraph().addEdge(new Retweets(), m, retweetingAgent);
            }
        }

        updateMentions(s, m);

        previousMessage = m;
    }

    public Agent getAgent(User u) {
        Agent a = idAgent.get(u.getName());
        if (a == null) {
            a = new Agent(u.getName(), u.getProfileImageURL());
            idAgent.put(u.getName(), a);
        }
        return a;
    }

    public MemGraph getGraph() {
        return graph;
    }

    protected void updateMentions(Status s, final Message m) {
        final List<String> users = new LinkedList();
        List<String> tags = new LinkedList();

        String t = s.getText();
        StringTokenizer st = new StringTokenizer(t, " ");
        while (st.hasMoreTokens()) {
            String x = st.nextToken();
            if (x.startsWith("@")) {
                users.add(x);
            } else if (x.startsWith("#")) {
                tags.add(x);
            }
        }


        for (String x : tags) {
            Channel c = new Channel(x);
            getGraph().addNode(c);
            getGraph().addEdge(new Mentions(), m, c);
        }

        if (users.size() > 0) {
            new Thread(new Runnable() {
                @Override public void run() {
                    for (String us : users) {
                        User u = getUser(us);
                        if (u != null) {
                            Agent a = getAgent(u);
                            getGraph().addNode(a);
                            getGraph().addEdge(new Mentions(), m, a);
                        }
                    }
                }
            }).start();
        }

    }


    public void addTrends() {
        Trends trends = getTrends();
        Channel prevTrend = null;
        for (Trend t : trends.getTrends()) {
            Channel tc = new Channel(t.getName());
            getGraph().addNode(tc);
            if (prevTrend!=null) {
                getGraph().addEdge(new Next(), prevTrend, tc);
            }
            prevTrend = tc;
        }
    }

}
