package automenta.spacenet.plugin.groovy;


public class GroovyScope  {
//	private static final Logger logger = Logger.getLogger(GroovyScope.class);
//
//	private GroovyShell shell;
//	private Binding groovyBinding;
//	private WhenMapEntriesChange<Object, Object> whenBindingChanges;
//
//
//	public GroovyScope() {
//		super();
//	}
//
//
//	@Override
//	public void stop() {
//
//	}
//
//
//	@Override public void start(Scope superNode) {
//
//		updateBinding();
//
//		shell = new GroovyShell(groovyBinding);
//	}
//
//	private void updateBinding() {
//		groovyBinding = new Binding();
//
//		whenBindingChanges = new WhenMapEntriesChange<Object, Object>(this.getObjects()) {
//
//			@Override public void afterMapPut(Entry<Object, Object>... entry) {
//				for (Entry<Object,Object> e : entry) {
//					String name;
//					if (e.getKey() instanceof String) {
//						name = e.getKey().toString();
//					}
//					else {
//						name = e.getValue().getClass().getSimpleName() + "-" + e.getValue().hashCode();
//					}
//
//					groovyBinding.setVariable(name, e.getValue());
//				}
//			}
//
//			@Override public void beforeMapRemoves(Entry<Object, Object>... entry) {
//				for (Entry<Object,Object> e : entry) {
//					logger.warn("unable to remove binding: " + e.getKey() + "->" + e.getValue());
//				}
//			}
//
//		};
//
//	}
//
//
//	public Object evaluate(Object value) {
//		String expression = null;
//		if (value instanceof StringVar) {
//			StringVar text = (StringVar)value;
//			expression = text.get();
//
//		}
//		else if (value instanceof String) {
//			expression = (String)value;
//		}
//
//		if (expression!=null) {
//			try {
//				Object result = shell.evaluate(expression);
//				return result;
//			}
//			catch (Exception e) {
//				e.printStackTrace();
//				return e;// + "\n" + Arrays.asList(e.getStackTrace());
//			}
//		}
//		return value;
//	}
//
//
//	public void bind(String name, Object object) {
//		groovyBinding.setVariable(name, object);
//
//	}
//


}
