/**
 * Copyright (c) 2008-2009 Ardor Labs, Inc.
 *
 * This file is part of Ardor3D.
 *
 * Ardor3D is free software: you can redistribute it and/or modify it 
 * under the terms of its license which may be found in the accompanying
 * LICENSE file or at <http://www.ardor3d.com/LICENSE>.
 */
package automenta.spacenet.run;

import automenta.spacenet.space.video.PropertiesDialog;
import automenta.spacenet.space.video.PropertiesGameSettings;
import automenta.spacenet.space.video.JoglModule;
import automenta.spacenet.space.video.Exit;
import automenta.spacenet.space.*;
import automenta.spacenet.space.control.camera.ArdorCamera;
import automenta.spacenet.space.control.pointer.DefaultKeyboard;
import automenta.spacenet.space.control.pointer.DefaultPointer;
import automenta.spacenet.var.physical.Color;
import automenta.spacenet.var.scalar.DoubleVar;
import automenta.spacenet.var.vector.V3;
import com.ardor3d.intersection.PickResults;
import java.awt.EventQueue;
import java.awt.event.ComponentEvent;
import java.net.URL;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ardor3d.annotation.MainThread;
import com.ardor3d.framework.ArdorModule;
import com.ardor3d.framework.Canvas;
import com.ardor3d.framework.DisplaySettings;
import com.ardor3d.framework.FrameHandler;
import com.ardor3d.framework.NativeCanvas;
import com.ardor3d.framework.Scene;
import com.ardor3d.framework.Updater;
import com.ardor3d.framework.jogl.JoglCanvas;
import com.ardor3d.image.Image.Format;
import com.ardor3d.image.util.AWTImageLoader;
import com.ardor3d.image.util.ScreenShotImageExporter;
import com.ardor3d.input.ControllerWrapper;
import com.ardor3d.input.FocusWrapper;
import com.ardor3d.input.Key;
import com.ardor3d.input.KeyboardWrapper;
import com.ardor3d.input.MouseManager;
import com.ardor3d.input.MouseWrapper;
import com.ardor3d.input.PhysicalLayer;
import com.ardor3d.input.control.FirstPersonControl;
import com.ardor3d.input.logical.InputTrigger;
import com.ardor3d.input.logical.KeyPressedCondition;
import com.ardor3d.input.logical.LogicalLayer;
import com.ardor3d.input.logical.TriggerAction;
import com.ardor3d.input.logical.TwoInputStates;
import com.ardor3d.light.PointLight;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.Ray3;
import com.ardor3d.math.Vector3;
import com.ardor3d.renderer.Renderer;
import com.ardor3d.renderer.TextureRendererFactory;
import com.ardor3d.renderer.jogl.JoglTextureRendererProvider;
import com.ardor3d.renderer.queue.RenderBucketType;
import com.ardor3d.renderer.state.LightState;
import com.ardor3d.renderer.state.WireframeState;
import com.ardor3d.renderer.state.ZBufferState;
import com.ardor3d.scenegraph.Spatial;
import com.ardor3d.scenegraph.event.DirtyType;
import com.ardor3d.util.Constants;
import com.ardor3d.util.ContextGarbageCollector;
import com.ardor3d.util.GameTaskQueue;
import com.ardor3d.util.GameTaskQueueManager;
import com.ardor3d.util.ReadOnlyTimer;
import com.ardor3d.util.geom.Debugger;
import com.ardor3d.util.screen.ScreenExporter;
import com.ardor3d.util.stat.StatCollector;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.Scopes;
import com.google.inject.Stage;
import java.awt.event.ComponentAdapter;
import javax.media.opengl.GLException;
import javolution.context.ConcurrentContext;

abstract public class ArdorSpacetime extends Space implements Runnable, Updater, Scene, Exit, Spacetime {

    private static final Logger logger = Logger.getLogger(ArdorSpacetime.class.getName());
    protected Color backgroundColor = new Color(Color.GRAY);
    protected final LogicalLayer logicalLayer;
    protected PhysicalLayer _physicalLayer;
    protected final Space volume;
    protected final FrameHandler _frameHandler;
    protected LightState defaultLightState;
    protected WireframeState _wireframeState;
    protected volatile boolean _exit = false;
    protected static boolean _stereo = false;
    protected boolean _showBounds = false;
    protected boolean _showNormals = false;
    protected boolean _showDepth = false;
    protected boolean _doShot = false;
    protected NativeCanvas canvas;
    protected ScreenShotImageExporter _screenShotExp = new ScreenShotImageExporter();
    protected MouseManager _mouseManager;
    protected FirstPersonControl _controlHandle;
    protected Vector3 _worldUp = new Vector3(0, 1, 0);
    protected static int _minDepthBits = -1;
    protected static int _minAlphaBits = -1;
    protected static int _minStencilBits = -1;
    private DefaultPointer pointer;
    private ArdorCamera camera;
    private DoubleVar frameDelay = new DoubleVar(0.01);
    final private Space face, sky;
    private PointLight defaultLight;

    @Inject
    public ArdorSpacetime(final LogicalLayer logicalLayer, final FrameHandler frameHandler) {
        super();

        this.logicalLayer = logicalLayer;
        _frameHandler = frameHandler;
        volume = this;

        //do not add face and sky nodes to root (volume) - they will be rendered in separate passes
        face = new Space();

        sky = new Space();

    }

    public MouseManager getMouseManager() {
        return _mouseManager;
    }

    protected void delay(double s) {
        try {
            Thread.sleep((long) (s * 1000.0));
        } catch (InterruptedException ex) {
        }
    }

    public void run() {
        try {
            _frameHandler.init();

            long lastFrame = System.nanoTime(), nowFrame;
            while (!_exit) {
                nowFrame = System.nanoTime();
                double framePeriod = (nowFrame - lastFrame) * 1e-9;
                //System.out.println("fps=" + 1.0 / framePeriod);

                _frameHandler.updateFrame();

                lastFrame = nowFrame;

                delay(getFrameDelay().d());

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

    @Deprecated public void exit() {
        _exit = true;
    }
    public void stop() {
        exit();
    }


    @Override public LogicalLayer getInputLogic() {
        return logicalLayer;
    }

    @Override public NativeCanvas getVideo() {
        return canvas;
    }

    private void updateBackgroundColor() {
        if (getVideo() != null) {
            if (getVideo().getCanvasRenderer() != null) {
                if (getVideo().getCanvasRenderer().getRenderer() != null) {
                    try {
                        getVideo().getCanvasRenderer().getRenderer().setBackgroundColor(backgroundColor);
                    } catch (GLException e) {
                        e.printStackTrace();                        
                    }
                }
            }
        }
    }

    @MainThread
    public void init() {

        int numProcs = Runtime.getRuntime().availableProcessors();
        System.out.println("Num processors: " + numProcs);
        ConcurrentContext.setConcurrency(numProcs);
        

        initInput();

        AWTImageLoader.registerLoader();

//        try {
//            SimpleResourceLocator srl = new SimpleResourceLocator(ArdorSpaceTime.class.getClassLoader().getResource(
//                    "com/ardor3d/example/media/"));
//            ResourceLocatorTool.addResourceLocator(ResourceLocatorTool.TYPE_TEXTURE, srl);
//            srl = new SimpleResourceLocator(ArdorSpaceTime.class.getClassLoader().getResource(
//                    "com/ardor3d/example/media/models/"));
//            ResourceLocatorTool.addResourceLocator(ResourceLocatorTool.TYPE_MODEL, srl);
//        } catch (final URISyntaxException ex) {
//            ex.printStackTrace();
//        }


//        getBackgroundColor().add(new IfColorChanges() {
//            @Override public void onColorChanged(Color c) {
//                //updateBackgroundColor();
//            }
//        });
        updateBackgroundColor();

        /**
         * Create a ZBuffer to display pixels closest to the camera above farther ones.
         */
        final ZBufferState buf = new ZBufferState();
        buf.setEnabled(true);
        buf.setFunction(ZBufferState.TestFunction.LessThanOrEqualTo);
        volume.setRenderState(buf);

        // ---- LIGHTS
        /** Set up a basic, default light. */
        defaultLight = new PointLight();
        defaultLight.setDiffuse(new ColorRGBA(0.75f, 0.75f, 0.75f, 0.75f));
        defaultLight.setAmbient(new ColorRGBA(0.7f, 0.7f, 0.7f, 0.75f));
        defaultLight.setLocation(new Vector3(0, 0, 50));
        defaultLight.setAttenuate(true);
        defaultLight.setLinear(0.003f);
        defaultLight.setQuadratic(0.001f);
        
        defaultLight.setEnabled(true);

        /** Attach the light to a lightState and the lightState to rootNode. */
        defaultLightState = new LightState();
        defaultLightState.setEnabled(true);
        defaultLightState.attach(defaultLight);
        volume.setRenderState(defaultLightState);

        _wireframeState = new WireframeState();
        _wireframeState.setEnabled(false);
        volume.setRenderState(_wireframeState);

        volume.getSceneHints().setRenderBucketType(RenderBucketType.Opaque);

        if (canvas instanceof JoglCanvas) {
            ((JoglCanvas) canvas).addComponentListener(new ComponentAdapter() {

                @Override public void componentResized(ComponentEvent e) {
                    java.awt.Dimension s = e.getComponent().getSize();
                    canvas.getCanvasRenderer().getCamera().resize(s.width, s.height);
                }
            });
        }

        this.camera = new ArdorCamera(this, new V3(0, 0, 4), new V3(0, 0, 0), new V3(0, 1, 0));
        add(camera);

        this.pointer = new DefaultPointer(this);
        add(pointer);   //must be added w/ add(Repeat r)

        add(new DefaultKeyboard(this, pointer));

        initWindow();
    }

    abstract protected void initWindow();

    @MainThread
    public void update(final ReadOnlyTimer timer) {
        if (getVideo().isClosing()) {
            //needs to be first in this method
            exit();
        }

        /** update stats, if enabled. */
        if (Constants.stats) {
            StatCollector.update();
        }


        updateLogicalLayer(timer);

        // Execute updateQueue item
        GameTaskQueueManager.getManager(getVideo().getCanvasRenderer().getRenderContext()).getQueue(GameTaskQueue.UPDATE).execute();

        /** Call simpleUpdate in any derived classes of ExampleBase. */
        updateWindow(timer);

        updateLights();

        /** Update controllers/render states/transforms/bounds for rootNode. */
        ConcurrentContext.enter();
        try {
            getSky().updateGeometricState(timer.getTimePerFrame(), true);
            getRoot().updateGeometricState(timer.getTimePerFrame(), true);
            getFace().updateGeometricState(timer.getTimePerFrame(), true);
        }
        finally {
            ConcurrentContext.exit();
        }
    }

    protected void updateLights() {
        defaultLight.setLocation(getCamera().getCurrentPosition());
        
    }
    
//    public static void updateLater(final Runnable r) {
//
//        GameTaskQueueManager.getManager(_canvas.getCanvasRenderer().getRenderContext()).getQueue(GameTaskQueue.UPDATE).enqueue(new Callable() {
//            @Override public Object call() throws Exception {
//                r.run();
//            }
//        });
//    }

    protected void updateLogicalLayer(final ReadOnlyTimer timer) {
        // check and execute any input triggers, if we are concerned with input
        if (logicalLayer != null) {
            logicalLayer.checkTriggers(timer.getTimePerFrame());
        }
    }

    protected void updateWindow(final ReadOnlyTimer timer) {
        // does nothing
    }

    @MainThread
    public boolean renderUnto(final Renderer renderer) {
        // Execute renderQueue item
        GameTaskQueueManager.getManager(canvas.getCanvasRenderer().getRenderContext()).getQueue(GameTaskQueue.RENDER).execute(renderer);

        // Clean up card garbage such as textures, vbos, etc.
        ContextGarbageCollector.doRuntimeCleanup(renderer);

        /** Draw the rootNode and all its children. */
        if (!canvas.isClosing()) {

            renderer.draw(sky);
            renderer.renderBuckets();

            /** Call renderExample in any derived classes. */
            renderRoot(renderer);
            renderDebug(renderer);
            renderer.renderBuckets();

            renderer.draw(face);

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

    protected void renderRoot(final Renderer renderer) {
        renderer.draw(volume);
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
            Debugger.drawBuffer(Format.Depth16, Debugger.NORTHEAST, renderer);
        }
    }

    protected void quit(final Renderer renderer) {
        ContextGarbageCollector.doFinalCleanup(renderer);
        canvas.close();
    }

    public ArdorCamera getCamera() {
        return camera;
    }

    private DoubleVar getFrameDelay() {
        return frameDelay;
    }

    public static class ArdorSpaceTimeProcess extends ArdorSpacetime {

        @Inject public ArdorSpaceTimeProcess(final LogicalLayer logicalLayer, final FrameHandler frameWork) {
            super(logicalLayer, frameWork);
        }

        @Override protected void initWindow() {
        }
    }

//    public static void newWindow(Class<? extends Spatial> spaceClass) {
//        try {
//            newWindow(spaceClass.newInstance());
//        } catch (InstantiationException ex) {
//            Logger.getLogger(ArdorSpaceTime.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IllegalAccessException ex) {
//            Logger.getLogger(ArdorSpaceTime.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
    public static void newWindow(final Spatial s) {
        ArdorSpacetime a = newWindow(ArdorSpaceTimeProcess.class);
        a.getRoot().add(s);
    }

    public static ArdorSpacetime newWindow(final Class<? extends ArdorSpacetime> exampleClazz) {

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
        final ArdorModule ardorModule = new ArdorModule();
        Module systemModule = null;

//        if ("LWJGL".equalsIgnoreCase(prefs.getRenderer())) {
//            systemModule = new LwjglModule();
//            TextureRendererFactory.INSTANCE.setProvider(new LwjglTextureRendererProvider());
//        } else if ("JOGL".equalsIgnoreCase(prefs.getRenderer())) {
            systemModule = new JoglModule();
            TextureRendererFactory.INSTANCE.setProvider(new JoglTextureRendererProvider());
//        }

        final Module exampleModule = new AbstractModule() {
            @Override
            protected void configure() {
                bind(ArdorSpacetime.class).to(exampleClazz).in(Scopes.SINGLETON);
                bind(Scene.class).to(ArdorSpacetime.class);
                bind(Updater.class).to(ArdorSpacetime.class);
                bind(Exit.class).to(ArdorSpacetime.class);
            }
        };
        final Provider<DisplaySettings> settingsProvider = new Provider<DisplaySettings>() {

            public DisplaySettings get() {
                return settings;
            }
        };

        // Setup our injector.
        final Injector injector = Guice.createInjector(Stage.PRODUCTION, ardorModule, systemModule, exampleModule,
            new AbstractModule() {

                @Override
                protected void configure() {
                    bind(DisplaySettings.class).toProvider(settingsProvider);
                }
            });

        final LogicalLayer ll = injector.getInstance(LogicalLayer.class);
        final FrameHandler frameWork = injector.getInstance(FrameHandler.class);
        final ArdorSpacetime ardorSpacetime = injector.getInstance(ArdorSpacetime.class);
        final NativeCanvas canvas = injector.getInstance(NativeCanvas.class);
        final Updater updater = injector.getInstance(Updater.class);
        final PhysicalLayer physicalLayer = new PhysicalLayer(injector.getInstance(KeyboardWrapper.class), injector.getInstance(MouseWrapper.class), injector.getInstance(ControllerWrapper.class), injector.getInstance(FocusWrapper.class));

        // set the mouse manager member. It's a bit of a hack to do that this way.
        ardorSpacetime._mouseManager = injector.getInstance(MouseManager.class);

        ll.registerInput(canvas, physicalLayer);

        // Register our example as an updater.
        frameWork.addUpdater(updater);

        // Make a native canvas and register it.
        frameWork.addCanvas(canvas);

        ardorSpacetime.canvas = canvas;
        ardorSpacetime._physicalLayer = physicalLayer;

        new Thread(ardorSpacetime).start();

        return ardorSpacetime;
    }

    protected static PropertiesGameSettings getAttributes(final PropertiesGameSettings settings) {
        // Always show the dialog in these examples.
        URL dialogImage = null;
        final String dflt = settings.getDefaultSettingsWidgetImage();
        if (dflt != null) {
            try {
                dialogImage = ArdorSpacetime.class.getResource(dflt);
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
            logger.logp(Level.SEVERE, ArdorSpacetime.class.getClass().toString(), "ExampleBase.getAttributes(settings)",
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

    public ArdorSpacetime getRoot() {
        return this;
    }

    protected void initInput() {

        // check if this example worries about input at all
        if (logicalLayer == null) {
            return;
        }

        _controlHandle = FirstPersonControl.setupTriggers(logicalLayer, _worldUp, true);

        logicalLayer.registerTrigger(new InputTrigger(new KeyPressedCondition(Key.L), new TriggerAction() {

            public void perform(final Canvas source, final TwoInputStates inputState, final double tpf) {
                defaultLightState.setEnabled(!defaultLightState.isEnabled());
                // Either an update or a markDirty is needed here since we did not touch the affected spatial directly.
                volume.markDirty(DirtyType.RenderState);
            }
        }));

        logicalLayer.registerTrigger(new InputTrigger(new KeyPressedCondition(Key.F4), new TriggerAction() {

            public void perform(final Canvas source, final TwoInputStates inputState, final double tpf) {
                _showDepth = !_showDepth;
            }
        }));

        logicalLayer.registerTrigger(new InputTrigger(new KeyPressedCondition(Key.T), new TriggerAction() {

            public void perform(final Canvas source, final TwoInputStates inputState, final double tpf) {
                _wireframeState.setEnabled(!_wireframeState.isEnabled());
                // Either an update or a markDirty is needed here since we did not touch the affected spatial directly.
                volume.markDirty(DirtyType.RenderState);
            }
        }));

        logicalLayer.registerTrigger(new InputTrigger(new KeyPressedCondition(Key.B), new TriggerAction() {

            public void perform(final Canvas source, final TwoInputStates inputState, final double tpf) {
                _showBounds = !_showBounds;
            }
        }));

        logicalLayer.registerTrigger(new InputTrigger(new KeyPressedCondition(Key.N), new TriggerAction() {

            public void perform(final Canvas source, final TwoInputStates inputState, final double tpf) {
                _showNormals = !_showNormals;
            }
        }));

        //TODO move this to a separate class that opens a save file window
        logicalLayer.registerTrigger(new InputTrigger(new KeyPressedCondition(Key.F1), new TriggerAction() {

            public void perform(final Canvas source, final TwoInputStates inputState, final double tpf) {
                _doShot = true;
            }
        }));

//        _logicalLayer.registerTrigger(new InputTrigger(new AnyKeyCondition(), new TriggerAction() {
//
//            public void perform(final Canvas source, final TwoInputStates inputState, final double tpf) {
//                System.out.println("Key character pressed: " + inputState.getCurrent().getKeyboardState().getKeyEvent().getKeyChar());
//            }
//        }));


    }

    @Override
    public PickResults doPick(Ray3 arg0) {
        return null;
    }

    @Override public Color getBackgroundColor() {
        return backgroundColor;
    }

    @Override
    public void addCondition(InputTrigger t) {
        getInputLogic().registerTrigger(t);
    }

    @Override
    public void removeCondition(InputTrigger t) {
        getInputLogic().deregisterTrigger(t);
    }

    @Override
    public Space getFace() {
        return face;
    }

    @Override
    public Space getSky() {
        return sky;
    }

    @Override public PhysicalLayer getInputPhy() {
        return _physicalLayer;
    }

    @Override public DefaultPointer getPointer() {
        return pointer;
    }


}
