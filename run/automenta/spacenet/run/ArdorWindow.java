/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package automenta.spacenet.run;

import automenta.spacenet.space.Space;
import automenta.spacenet.space.Spacetime;
import automenta.spacenet.space.control.camera.ArdorCamera;
import automenta.spacenet.space.control.pointer.DefaultKeyboard;
import automenta.spacenet.space.control.pointer.DefaultPointer;
import automenta.spacenet.space.video.PropertiesDialog;
import automenta.spacenet.space.video.PropertiesGameSettings;
import automenta.spacenet.var.physical.Color;
import automenta.spacenet.var.vector.V3;
import com.ardor3d.annotation.MainThread;
import com.ardor3d.example.ExampleBase;
import com.ardor3d.framework.Canvas;
import com.ardor3d.framework.DisplaySettings;
import com.ardor3d.framework.FrameHandler;
import com.ardor3d.framework.NativeCanvas;
import com.ardor3d.framework.Scene;
import com.ardor3d.framework.Updater;
import com.ardor3d.framework.jogl.JoglCanvas;
import com.ardor3d.framework.jogl.JoglCanvasRenderer;
import com.ardor3d.image.TextureStoreFormat;
import com.ardor3d.image.util.AWTImageLoader;
import com.ardor3d.image.util.ScreenShotImageExporter;
import com.ardor3d.input.GrabbedState;
import com.ardor3d.input.Key;
import com.ardor3d.input.MouseButton;
import com.ardor3d.input.MouseManager;
import com.ardor3d.input.PhysicalLayer;
import com.ardor3d.input.awt.AwtFocusWrapper;
import com.ardor3d.input.awt.AwtKeyboardWrapper;
import com.ardor3d.input.awt.AwtMouseManager;
import com.ardor3d.input.awt.AwtMouseWrapper;
import com.ardor3d.input.control.FirstPersonControl;
import com.ardor3d.input.logical.AnyKeyCondition;
import com.ardor3d.input.logical.DummyControllerWrapper;
import com.ardor3d.input.logical.InputTrigger;
import com.ardor3d.input.logical.KeyPressedCondition;
import com.ardor3d.input.logical.LogicalLayer;
import com.ardor3d.input.logical.MouseButtonClickedCondition;
import com.ardor3d.input.logical.MouseButtonPressedCondition;
import com.ardor3d.input.logical.MouseButtonReleasedCondition;
import com.ardor3d.input.logical.TriggerAction;
import com.ardor3d.input.logical.TwoInputStates;
import com.ardor3d.intersection.PickData;
import com.ardor3d.intersection.PickResults;
import com.ardor3d.intersection.PickingUtil;
import com.ardor3d.intersection.PrimitivePickResults;
import com.ardor3d.light.PointLight;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.Ray3;
import com.ardor3d.math.Vector2;
import com.ardor3d.math.Vector3;
import com.ardor3d.renderer.Renderer;
import com.ardor3d.renderer.TextureRendererFactory;
import com.ardor3d.renderer.jogl.JoglTextureRendererProvider;
import com.ardor3d.renderer.queue.RenderBucketType;
import com.ardor3d.renderer.state.LightState;
import com.ardor3d.renderer.state.ShadingState;
import com.ardor3d.renderer.state.ShadingState.ShadingMode;
import com.ardor3d.renderer.state.WireframeState;
import com.ardor3d.renderer.state.ZBufferState;
import com.ardor3d.scenegraph.Spatial;
import com.ardor3d.scenegraph.event.DirtyType;
import com.ardor3d.util.Constants;
import com.ardor3d.util.ContextGarbageCollector;
import com.ardor3d.util.GameTaskQueue;
import com.ardor3d.util.GameTaskQueueManager;
import com.ardor3d.util.ReadOnlyTimer;
import com.ardor3d.util.Timer;
import com.ardor3d.util.geom.Debugger;
import com.ardor3d.util.screen.ScreenExporter;
import com.ardor3d.util.stat.StatCollector;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import java.awt.EventQueue;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.net.URL;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;
import javolution.context.ConcurrentContext;

/**
 * Adapted from ExampleBase
 * @author seh
 */
public class ArdorWindow implements Runnable, Updater, Scene, Spacetime {

    private static final Logger logger = Logger.getLogger(ArdorWindow.class.getName());

    protected final LogicalLayer inputLogic = new LogicalLayer();

    protected PhysicalLayer inputPhy;

    protected final Timer timer = new Timer();
    protected final FrameHandler frameHandler = new FrameHandler(timer);

    protected DisplaySettings videoSettings;


    protected LightState lightState;

    protected WireframeState wireframeState;

    protected volatile boolean _exit = false;

    protected static boolean _stereo = false;

    protected boolean _showBounds = false;
    protected boolean _showNormals = false;
    protected boolean _showDepth = false;

    protected boolean _doShot = false;

    protected NativeCanvas canvas;

    protected ScreenShotImageExporter _screenShotExp = new ScreenShotImageExporter();

    protected MouseManager mouseManager;

    protected FirstPersonControl firstPersonControl;

    protected Vector3 worldUp = new Vector3(0, 1, 0);

    protected static int _minDepthBits = -1;
    protected static int _minAlphaBits = -1;
    protected static int _minStencilBits = -1;

    protected Color backgroundColor = new Color(Color.GRAY);
    private ArdorCamera camera;
    private DefaultPointer pointer;

    protected final Space root = new Space();

    private Space face = new Space();
    private Space sky = new Space();
    private Space volume = new Space();

    public ArdorWindow() {
        // Ask for properties
        final PropertiesGameSettings prefs = getAttributes(new PropertiesGameSettings("ardorSettings.properties", null));

        // Convert to DisplayProperties (XXX: maybe merge these classes?)
        final DisplaySettings settings = new DisplaySettings(prefs.getWidth(), prefs.getHeight(), prefs.getDepth(),
                prefs.getFrequency(),
                // alpha
                _minAlphaBits != -1 ? _minAlphaBits : prefs.getAlphaBits(),
                // depth
                _minDepthBits != -1 ? _minDepthBits : prefs.getDepthBits(),
                // stencil
                _minStencilBits != -1 ? _minStencilBits : prefs.getStencilBits(),
                // samples
                prefs.getSamples(),
                // other
                prefs.isFullscreen(), _stereo);


        // get our framework
        if ("LWJGL".equalsIgnoreCase(prefs.getRenderer())) {
            logger.severe("LWJGL not supported.  Use JOGL");
            return;
//            final LwjglCanvasRenderer canvasRenderer = new LwjglCanvasRenderer(this);
//            canvas = new LwjglCanvas(canvasRenderer, settings);
//            physical = new PhysicalLayer(new LwjglKeyboardWrapper(), new LwjglMouseWrapper(),
//                    new LwjglControllerWrapper(), (LwjglCanvas)canvas);
//            mouseManager = new LwjglMouseManager();
//            TextureRendererFactory.INSTANCE.setProvider(new LwjglTextureRendererProvider());
        } else if ("JOGL".equalsIgnoreCase(prefs.getRenderer())) {
            final JoglCanvasRenderer canvasRenderer = new JoglCanvasRenderer(this);
            canvas = new JoglCanvas(canvasRenderer, settings);
            final JoglCanvas jcanvas = (JoglCanvas)this.canvas;
            
            mouseManager = new AwtMouseManager(jcanvas);

            inputPhy = new PhysicalLayer(
                new AwtKeyboardWrapper(jcanvas),
                new AwtMouseWrapper(jcanvas, mouseManager),
                DummyControllerWrapper.INSTANCE,
                new AwtFocusWrapper(jcanvas));

            TextureRendererFactory.INSTANCE.setProvider(new JoglTextureRendererProvider());
        }

        inputLogic.registerInput(canvas, inputPhy);

        // Register our example as an updater.
        frameHandler.addUpdater(this);

        // register our native canvas
        frameHandler.addCanvas(canvas);

        new Thread(this).start();
    }

    public void run() {
        try {
            frameHandler.init();

            while (!_exit) {
                frameHandler.updateFrame();
                Thread.yield();
            }
            
            // grab the graphics context so cleanup will work out.
            canvas.getCanvasRenderer().setCurrentContext();
            quit(canvas.getCanvasRenderer().getRenderer());
        } catch (final Throwable t) {
            System.err.println("Throwable caught in MainThread - exiting");
            t.printStackTrace(System.err);
        }
    }

    public void stop() {
        _exit = true;
    }

    public MouseManager getMouseManager() {
        return mouseManager;
    }

    @MainThread
    public void init() {
        int numProcs = Runtime.getRuntime().availableProcessors();
        logger.info("Processor Cores: " + numProcs);
        ConcurrentContext.setConcurrency(numProcs);

        registerInputTriggers();

        AWTImageLoader.registerLoader();

//        try {
//            SimpleResourceLocator srl = new SimpleResourceLocator(ExampleBase.class.getClassLoader().getResource(
//                    "com/ardor3d/example/media/"));
//            ResourceLocatorTool.addResourceLocator(ResourceLocatorTool.TYPE_TEXTURE, srl);
//            srl = new SimpleResourceLocator(ExampleBase.class.getClassLoader().getResource(
//                    "com/ardor3d/example/media/models/"));
//            ResourceLocatorTool.addResourceLocator(ResourceLocatorTool.TYPE_MODEL, srl);
//        } catch (final URISyntaxException ex) {
//            ex.printStackTrace();
//        }

        root.add(sky);
        root.add(volume);
        root.add(face);

        /**
         * Create a ZBuffer to display pixels closest to the camera above farther ones.
         */
        final ZBufferState buf = new ZBufferState();
        buf.setEnabled(true);
        buf.setFunction(ZBufferState.TestFunction.LessThanOrEqualTo);
        volume.setRenderState(buf);

        final ShadingState shadeState = new ShadingState();
        shadeState.setShadingMode(ShadingMode.Smooth);
        volume.setRenderState(shadeState);
        
        // ---- LIGHTS
        /** Set up a basic, default light. */
        final PointLight light = new PointLight();
        light.setDiffuse(new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f));
        light.setSpecular(new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f));
        light.setAmbient(new ColorRGBA(0.5f, 0.5f, 0.5f, 1.0f));
        light.setLocation(new Vector3(0, 0, 10));        
        light.setEnabled(true);

        /** Attach the light to a lightState and the lightState to rootNode. */
        lightState = new LightState();
        lightState.setEnabled(true);
        lightState.attach(light);
        volume.setRenderState(lightState);

        wireframeState = new WireframeState();
        wireframeState.setEnabled(false);
        volume.setRenderState(wireframeState);

        volume.getSceneHints().setRenderBucketType(RenderBucketType.Opaque);

        canvas.setTitle("");
        if (canvas instanceof JoglCanvas) {
            ((JoglCanvas)canvas).addComponentListener(new ComponentAdapter() {
                @Override public void componentResized(ComponentEvent e) {
                    java.awt.Dimension s = e.getComponent().getSize();
                    canvas.getCanvasRenderer().getCamera().resize(s.width, s.height);
                }
            });
            ((JoglCanvas)canvas).setResizable(true);
        }

        this.camera = new ArdorCamera(this, new V3(0, 0, 4), new V3(0, 0, 0), new V3(0, 1, 0));
        root.add(camera);

        this.pointer = new DefaultPointer(this);
        root.add(pointer);   //must be added w/ add(Repeat r)

        root.add(new DefaultKeyboard(this, pointer));

        afterInit();
    }

    protected void afterInit() {
        
    }

    @MainThread
    public void update(final ReadOnlyTimer timer) {
        if (canvas.isClosing()) {
            stop();
        }

        /** update stats, if enabled. */
        if (Constants.stats) {
            StatCollector.update();
        }

        updateLogicalLayer(timer);

        // Execute updateQueue item
        GameTaskQueueManager.getManager(canvas.getCanvasRenderer().getRenderContext()).getQueue(GameTaskQueue.UPDATE)
                .execute();

        /** Call simpleUpdate in any derived classes of ExampleBase. */
        updateFrame(timer);

        /** Update controllers/render states/transforms/bounds for rootNode. */
        root.updateGeometricState(timer.getTimePerFrame(), true);
    }


    protected void updateLogicalLayer(final ReadOnlyTimer timer) {
        // check and execute any input triggers, if we are concerned with input
        if (inputLogic != null) {
            inputLogic.checkTriggers(timer.getTimePerFrame());
        }
    }

    protected void updateFrame(final ReadOnlyTimer timer) {
    }

    @MainThread
    public boolean renderUnto(final Renderer renderer) {
        // Execute renderQueue item
        GameTaskQueueManager.getManager(canvas.getCanvasRenderer().getRenderContext()).getQueue(GameTaskQueue.RENDER)
                .execute(renderer);

        // Clean up card garbage such as textures, vbos, etc.
        ContextGarbageCollector.doRuntimeCleanup(renderer);

        /** Draw the rootNode and all its children. */
        if (!canvas.isClosing()) {
            /** Call renderExample in any derived classes. */
            render(renderer);
            renderDebug(renderer);

            if (_doShot) {
                // force any waiting scene elements to be renderer.
                renderer.renderBuckets();
                ScreenExporter.exportCurrentScreen(canvas.getCanvasRenderer().getRenderer(), _screenShotExp);
                _doShot = false;
            }
            return true;
        } else {
            return false;
        }
    }

    protected void render(final Renderer renderer) {
        renderer.draw(root);
    }

    protected void renderDebug(final Renderer renderer) {
        if (_showBounds) {
            Debugger.drawBounds(volume, renderer, true);
        }

        if (_showNormals) {
            Debugger.drawNormals(volume, renderer);
            Debugger.drawTangents(volume, renderer);
        }

        if (_showDepth) {
            renderer.renderBuckets();
            Debugger.drawBuffer(TextureStoreFormat.Depth16, Debugger.NORTHEAST, renderer);
        }
    }

    public PickResults doPick(final Ray3 pickRay) {
        final PrimitivePickResults pickResults = new PrimitivePickResults();
        pickResults.setCheckDistance(true);
        PickingUtil.findPick(volume, pickRay, pickResults);
        processPicks(pickResults);
        return pickResults;
    }

    protected void processPicks(final PrimitivePickResults pickResults) {
        int i = 0;
        while (pickResults.getNumber() > 0
                && pickResults.getPickData(i).getIntersectionRecord().getNumberOfIntersections() == 0
                && ++i < pickResults.getNumber()) {
        }
        if (pickResults.getNumber() > i) {
            final PickData pick = pickResults.getPickData(i);
            System.err.println("picked: " + pick.getTargetMesh() + " at: "
                    + pick.getIntersectionRecord().getIntersectionPoint(0));
        } else {
            System.err.println("picked: nothing");
        }
    }

    protected void quit(final Renderer renderer) {
        ContextGarbageCollector.doFinalCleanup(renderer);
        canvas.close();
    }


    protected static PropertiesGameSettings getAttributes(final PropertiesGameSettings settings) {
        // Always show the dialog in these examples.
        URL dialogImage = null;
        final String dflt = settings.getDefaultSettingsWidgetImage();
        if (dflt != null) {
            try {
                dialogImage = ExampleBase.class.getResource(dflt);
            } catch (final Exception e) {
                logger.log(Level.SEVERE, "Resource lookup of '" + dflt + "' failed.  Proceeding.");
            }
        }
        if (dialogImage == null) {
            logger.fine("No dialog image loaded");
        } else {
            logger.fine("Using dialog image '" + dialogImage + "'");
        }

        final URL dialogImageRef = dialogImage;
        final AtomicReference<PropertiesDialog> dialogRef = new AtomicReference<PropertiesDialog>();
        final Stack<Runnable> mainThreadTasks = new Stack<Runnable>();
        try {
            if (EventQueue.isDispatchThread()) {
                dialogRef.set(new PropertiesDialog(settings, dialogImageRef, mainThreadTasks));
            } else {
                EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        dialogRef.set(new PropertiesDialog(settings, dialogImageRef, mainThreadTasks));
                    }
                });
            }
        } catch (final Exception e) {
            logger.logp(Level.SEVERE, ExampleBase.class.getClass().toString(), "ExampleBase.getAttributes(settings)",
                    "Exception", e);
            return null;
        }

        PropertiesDialog dialogCheck = dialogRef.get();
        while (dialogCheck == null || dialogCheck.isVisible()) {
            try {
                // check worker queue for work
                while (!mainThreadTasks.isEmpty()) {
                    mainThreadTasks.pop().run();
                }
                // go back to sleep for a while
                Thread.sleep(50);
            } catch (final InterruptedException e) {
                logger.warning("Error waiting for dialog system, using defaults.");
            }

            dialogCheck = dialogRef.get();
        }

        if (dialogCheck.isCancelled()) {
            System.exit(0);
        }
        return settings;
    }

    protected void registerInputTriggers() {

        // check if this example worries about input at all
        if (inputLogic == null) {
            return;
        }

        firstPersonControl = FirstPersonControl.setupTriggers(inputLogic, worldUp, true);

        inputLogic.registerTrigger(new InputTrigger(new MouseButtonClickedCondition(MouseButton.RIGHT),
                new TriggerAction() {
                    public void perform(final Canvas source, final TwoInputStates inputStates, final double tpf) {

                        final Vector2 pos = Vector2.fetchTempInstance().set(
                                inputStates.getCurrent().getMouseState().getX(),
                                inputStates.getCurrent().getMouseState().getY());
                        final Ray3 pickRay = new Ray3();
                        canvas.getCanvasRenderer().getCamera().getPickRay(pos, false, pickRay);
                        Vector2.releaseTempInstance(pos);
                        doPick(pickRay);
                    }
                }));

        inputLogic.registerTrigger(new InputTrigger(new KeyPressedCondition(Key.ESCAPE), new TriggerAction() {
            public void perform(final Canvas source, final TwoInputStates inputState, final double tpf) {
                stop();
            }
        }));

        inputLogic.registerTrigger(new InputTrigger(new KeyPressedCondition(Key.L), new TriggerAction() {
            public void perform(final Canvas source, final TwoInputStates inputState, final double tpf) {
                lightState.setEnabled(!lightState.isEnabled());
                // Either an update or a markDirty is needed here since we did not touch the affected spatial directly.
                root.markDirty(DirtyType.RenderState);
            }
        }));

        inputLogic.registerTrigger(new InputTrigger(new KeyPressedCondition(Key.F4), new TriggerAction() {
            public void perform(final Canvas source, final TwoInputStates inputState, final double tpf) {
                _showDepth = !_showDepth;
            }
        }));

        inputLogic.registerTrigger(new InputTrigger(new KeyPressedCondition(Key.T), new TriggerAction() {
            public void perform(final Canvas source, final TwoInputStates inputState, final double tpf) {
                wireframeState.setEnabled(!wireframeState.isEnabled());
                // Either an update or a markDirty is needed here since we did not touch the affected spatial directly.
                root.markDirty(DirtyType.RenderState);
            }
        }));

        inputLogic.registerTrigger(new InputTrigger(new KeyPressedCondition(Key.B), new TriggerAction() {
            public void perform(final Canvas source, final TwoInputStates inputState, final double tpf) {
                _showBounds = !_showBounds;
            }
        }));

        inputLogic.registerTrigger(new InputTrigger(new KeyPressedCondition(Key.N), new TriggerAction() {
            public void perform(final Canvas source, final TwoInputStates inputState, final double tpf) {
                _showNormals = !_showNormals;
            }
        }));

        inputLogic.registerTrigger(new InputTrigger(new KeyPressedCondition(Key.F1), new TriggerAction() {
            public void perform(final Canvas source, final TwoInputStates inputState, final double tpf) {
                _doShot = true;
            }
        }));

        final Predicate<TwoInputStates> clickLeftOrRight = Predicates.or(new MouseButtonClickedCondition(
                MouseButton.LEFT), new MouseButtonClickedCondition(MouseButton.RIGHT));

        inputLogic.registerTrigger(new InputTrigger(clickLeftOrRight, new TriggerAction() {
            public void perform(final Canvas source, final TwoInputStates inputStates, final double tpf) {
                System.err.println("clicked: " + inputStates.getCurrent().getMouseState().getClickCounts());
            }
        }));

        inputLogic.registerTrigger(new InputTrigger(new MouseButtonPressedCondition(MouseButton.LEFT),
                new TriggerAction() {
                    public void perform(final Canvas source, final TwoInputStates inputState, final double tpf) {
                        if (mouseManager.isSetGrabbedSupported()) {
                            mouseManager.setGrabbed(GrabbedState.GRABBED);
                        }
                    }
                }));
        inputLogic.registerTrigger(new InputTrigger(new MouseButtonReleasedCondition(MouseButton.LEFT),
                new TriggerAction() {
                    public void perform(final Canvas source, final TwoInputStates inputState, final double tpf) {
                        if (mouseManager.isSetGrabbedSupported()) {
                            mouseManager.setGrabbed(GrabbedState.NOT_GRABBED);
                        }
                    }
                }));

        inputLogic.registerTrigger(new InputTrigger(new AnyKeyCondition(), new TriggerAction() {
            public void perform(final Canvas source, final TwoInputStates inputState, final double tpf) {
                System.out.println("Key character pressed: "
                        + inputState.getCurrent().getKeyboardState().getKeyEvent().getKeyChar());
            }
        }));

    }

    public LogicalLayer getInputLogic() {
        return inputLogic;
    }
    
    public PhysicalLayer getInputPhy() {
        return inputPhy;
    }

    public ArdorCamera getCamera() {
        return camera;
    }

    public DefaultPointer getPointer() {
        return pointer;
    }
    
    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public NativeCanvas getVideo() {
        return canvas;
    }

    public Space getFace() {
        return face;
    }

    public Space getSky() {
        return sky;
    }

    public Space getVolume() {
        return volume;
    }

    public Space getRoot() {
        return root;
    }



    public void addCondition(InputTrigger t) {
        getInputLogic().registerTrigger(t);
    }

    public void removeCondition(InputTrigger t) {
        getInputLogic().deregisterTrigger(t);
    }
    
    public static synchronized void delay(double s) {
        try {
            Thread.sleep((long) (s * 1000.0));
        } catch (InterruptedException ex) {
        }
    }

    public ArdorWindow withVolume(Spatial... addToVolume) {
        for (Spatial s : addToVolume) {
            getVolume().add(s);
        }
        return this;
    }
    
}
