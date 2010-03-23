package automenta.spacenet.plugin.web;

/** syndication feed (RSS, Atom, FOAF, etc..) */
public class Feed  {
//	private static final Logger logger = Logger.getLogger(Feed.class);
//
//	public final static String FeedType = "application/rss+xml";
//
//	private URI uri;
//
//	private String title = new String();
//	private String summary = new String();
//
//	public Feed(URI uri) {
//		super();
//
//		this.uri = uri;
//
//		updateFeed();
//	}
//
//	public UURI getURI() {
//		return uri;
//	}
//
//	/**
//	 * @see https://rome.dev.java.net/
//	 * @see http://wiki.java.net/bin/view/Javawsxml/Rome04TutorialFeedReader
//	 */
//	public void updateFeed() {
//	    try {
//            URL feedUrl = getURI().toURL();
//
//            SyndFeedInput input = new SyndFeedInput();
//            SyndFeed feed = input.build(new XmlReader(feedUrl));
//
//            getTitle().set(feed.getTitle());
//            getSummary().set(feed.getDescription());
//
//            for (Object o : feed.getEntries()) {
//            	SyndEntry s = (SyndEntry) o;
//
//            	final String title = s.getTitle();
//            	final String uri = s.getUri();
//            	final String desc = s.getDescription().getValue();
//
//            	final UURI uuri = new UURI(uri);
//
//            	add(new Found() {
//
//					@Override public String getDescription() {
//						return desc;
//					}
//
//					@Override
//					public String getName() {
//						return title;
//					}
//
//					@Override
//					public Object getObject() {
//						return uuri;
//					}
//
//					@Override
//					public double getStrength() {
//						return 1.0;
//					}
//
//					@Override
//					public String getTags() {
//						return "item";
//					}
//
//					@Override
//					public UURI getUURI() {
//						return uuri;
//					}
//
//            	});
//            }
//
//        }
//        catch (Exception ex) {
//        	logger.error(ex);
//        	ex.printStackTrace();
//        }
//
//	}
//
//	public StringVar getTitle() {
//		return title;
//	}
//	public StringVar getSummary() {
//		return summary ;
//	}
//
}
