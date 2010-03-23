/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.spacenet.run.swing;

import automenta.spacenet.space.video.Exit;
import com.ardor3d.annotation.MainThread;
import com.ardor3d.framework.ArdorModule;
import com.ardor3d.framework.Canvas;
import com.ardor3d.framework.DisplaySettings;
import com.ardor3d.framework.FrameHandler;
import com.ardor3d.framework.Scene;
import com.ardor3d.framework.Updater;
import com.ardor3d.framework.jogl.JoglAwtCanvas;
import com.ardor3d.framework.jogl.JoglCanvasRenderer;
import com.ardor3d.image.util.AWTImageLoader;
import com.ardor3d.input.ButtonState;
import com.ardor3d.input.ControllerWrapper;
import com.ardor3d.input.InputState;
import com.ardor3d.input.Key;
import com.ardor3d.input.MouseCursor;
import com.ardor3d.input.MouseState;
import com.ardor3d.input.PhysicalLayer;
import com.ardor3d.input.awt.AwtFocusWrapper;
import com.ardor3d.input.awt.AwtKeyboardWrapper;
import com.ardor3d.input.awt.AwtMouseManager;
import com.ardor3d.input.awt.AwtMouseWrapper;
import com.ardor3d.input.logical.AnyKeyCondition;
import com.ardor3d.input.logical.DummyControllerWrapper;
import com.ardor3d.input.logical.InputTrigger;
import com.ardor3d.input.logical.KeyHeldCondition;
import com.ardor3d.input.logical.KeyPressedCondition;
import com.ardor3d.input.logical.KeyReleasedCondition;
import com.ardor3d.input.logical.LogicalLayer;
import com.ardor3d.input.logical.MouseButtonCondition;
import com.ardor3d.input.logical.TriggerAction;
import com.ardor3d.input.logical.TriggerConditions;
import com.ardor3d.input.logical.TwoInputStates;
import com.ardor3d.intersection.PickResults;
import com.ardor3d.light.PointLight;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.Matrix3;
import com.ardor3d.math.Ray3;
import com.ardor3d.math.Vector3;
import com.ardor3d.renderer.Camera;
import com.ardor3d.renderer.Camera.ProjectionMode;
import com.ardor3d.renderer.Renderer;
import com.ardor3d.renderer.state.LightState;
import com.ardor3d.renderer.state.ZBufferState;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.shape.Box;
import com.ardor3d.util.ReadOnlyTimer;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Stage;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

/**
 *
 * @author seh
 */
public class ArdorAwtExample {

    public final static class ExampleScene implements Scene {

        private final Node root;

        public ExampleScene() {
            root = new Node("root");
        }

        public Node getRoot() {
            return root;
        }

        @MainThread
        public boolean renderUnto(final Renderer renderer) {
            renderer.draw(root);
            return true;
        }

        public PickResults doPick(final Ray3 pickRay) {
            // does nothing.
            return null;
        }
    }
    static MouseCursor _cursor1;
    static MouseCursor _cursor2;
    static Map<Canvas, Boolean> _showCursor1 = new HashMap<Canvas, Boolean>();

    public static void main(final String[] args) throws Exception {
        System.setProperty("ardor3d.useMultipleContexts", "true");

        final Module ardorModule = new ArdorModule();
        // final Module systemModule = new LwjglAwtModule();

        final Injector injector = Guice.createInjector(Stage.PRODUCTION, ardorModule);

        final FrameHandler frameWork = injector.getInstance(FrameHandler.class);

        final MyExit exit = new MyExit();
        final LogicalLayer logicalLayer = injector.getInstance(LogicalLayer.class);

        final ExampleScene scene1 = new ExampleScene();
        final RotatingCubeGame game1 = new RotatingCubeGame(scene1, exit, logicalLayer, Key.T);
        frameWork.addUpdater(game1);

        final ExampleScene scene2 = new ExampleScene();
        final RotatingCubeGame game2 = new RotatingCubeGame(scene2, exit, logicalLayer, Key.G);
        frameWork.addUpdater(game2);

        final JFrame frame = new JFrame("AWT Example");
        frame.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(final WindowEvent e) {
                exit.exit();
            }
        });

        frame.setLayout(new GridLayout(2, 3));

        AWTImageLoader.registerLoader();

//        try {
//            final SimpleResourceLocator srl = new SimpleResourceLocator(ExampleBase.class.getClassLoader().getResource(
//                "com/ardor3d/example/media/"));
//            ResourceLocatorTool.addResourceLocator(ResourceLocatorTool.TYPE_TEXTURE, srl);
//        } catch (final URISyntaxException ex) {
//            ex.printStackTrace();
//        }

        final AWTImageLoader awtImageLoader = new AWTImageLoader();
//        _cursor1 = createMouseCursor(awtImageLoader, "/com/ardor3d/example/media/input/wait_cursor.png");
//        _cursor2 = createMouseCursor(awtImageLoader, "/com/ardor3d/example/media/input/movedata.gif");

        addCanvas(frame, scene1, logicalLayer, frameWork);
        frame.add(new JLabel(
            "<html>" + "<table>" + "<tr><th align=\"left\" style=\"font-size: 16\">Action</th><th align=\"left\" style=\"font-size: 16\">Command</th></tr>" + "<tr><td>WS</td><td>Move camera position forward/back</td></tr>" + "<tr><td>AD</td><td>Turn camera left/right</td></tr>" + "<tr><td>QE</td><td>Strafe camera left/right</td></tr>" + "<tr><td>T</td><td>Toggle cube rotation for scene 1 on press</td></tr>" + "<tr><td>G</td><td>Toggle cube rotation for scene 2 on press</td></tr>" + "<tr><td>U</td><td>Toggle both cube rotations on release</td></tr>" + "<tr><td>0 (zero)</td><td>Reset camera position</td></tr>" + "<tr><td>9</td><td>Face camera towards cube without changing position</td></tr>" + "<tr><td>ESC</td><td>Quit</td></tr>" + "<tr><td>Mouse</td><td>Press left button to rotate camera.</td></tr>" + "</table>" + "</html>", SwingConstants.CENTER));
        addCanvas(frame, scene1, logicalLayer, frameWork);
        frame.add(new JLabel("", SwingConstants.CENTER));
        addCanvas(frame, scene2, logicalLayer, frameWork);
        frame.add(new JLabel("", SwingConstants.CENTER));

        frame.pack();
        frame.setVisible(true);

        game1.init();
        game2.init();

        while (!exit.isExit()) {
            frameWork.updateFrame();
            Thread.yield();
        }

        frame.dispose();
        System.exit(0);
    }

//    private static MouseCursor createMouseCursor(final AWTImageLoader awtImageLoader, final String resourceName) throws IOException {
//        final com.ardor3d.image.Image image = awtImageLoader.load(JoglAwtExample.class.getResourceAsStream(resourceName), false);
//
//        return new MouseCursor("cursor1", image, 0, image.getHeight() - 1);
//    }
    private static void addCanvas(final JFrame frame, final ExampleScene scene, final LogicalLayer logicalLayer,
        final FrameHandler frameWork) throws Exception {
        final JoglCanvasRenderer canvasRenderer = new JoglCanvasRenderer(scene);

        final DisplaySettings settings = new DisplaySettings(400, 300, 24, 0, 0, 16, 0, 0, false, false);
        final JoglAwtCanvas theCanvas = new JoglAwtCanvas(settings, canvasRenderer);

        frame.add(theCanvas);

        _showCursor1.put(theCanvas, true);

        theCanvas.setSize(new Dimension(400, 300));
        theCanvas.setVisible(true);

        final AwtKeyboardWrapper keyboardWrapper = new AwtKeyboardWrapper(theCanvas);
        final AwtFocusWrapper focusWrapper = new AwtFocusWrapper(theCanvas);
        final AwtMouseManager mouseManager = new AwtMouseManager(theCanvas);
        final AwtMouseWrapper mouseWrapper = new AwtMouseWrapper(theCanvas, mouseManager);
        final ControllerWrapper controllerWrapper = new DummyControllerWrapper();

        final PhysicalLayer pl = new PhysicalLayer(keyboardWrapper, mouseWrapper, controllerWrapper, focusWrapper);

        logicalLayer.registerInput(theCanvas, pl);

        logicalLayer.registerTrigger(new InputTrigger(new KeyPressedCondition(Key.H), new TriggerAction() {

            public void perform(final Canvas source, final TwoInputStates inputStates, final double tpf) {
                if (source != theCanvas) {
                    return;
                }

                if (_showCursor1.get(theCanvas)) {
                    mouseManager.setCursor(_cursor1);
                } else {
                    mouseManager.setCursor(_cursor2);
                }

                _showCursor1.put(theCanvas, !_showCursor1.get(theCanvas));
            }
        }));
        logicalLayer.registerTrigger(new InputTrigger(new KeyPressedCondition(Key.J), new TriggerAction() {

            public void perform(final Canvas source, final TwoInputStates inputStates, final double tpf) {
                if (source != theCanvas) {
                    return;
                }

                mouseManager.setCursor(MouseCursor.SYSTEM_DEFAULT);
            }
        }));

        frameWork.addCanvas(theCanvas);

    }

    private static class MyExit implements Exit {

        private volatile boolean exit = false;

        public void exit() {
            exit = true;
        }

        public boolean isExit() {
            return exit;
        }
    }

    public static class RotatingCubeGame implements Updater {
        // private final Canvas view;

        private final ExampleScene scene;
        private final Exit exit;
        private final LogicalLayer logicalLayer;
        private final Key toggleRotationKey;
        private final static float CUBE_ROTATE_SPEED = 1;
        private final Vector3 rotationAxis = new Vector3(1, 1, 0);
        private double angle = 0;
        private Mesh box = new Box("The cube", new Vector3(-1, -1, -1), new Vector3(1, 1, 1));
        private final Matrix3 rotation = new Matrix3();
        private static final int MOVE_SPEED = 4;
        private static final double TURN_SPEED = 0.5;
        private final Matrix3 _incr = new Matrix3();
        private static final double MOUSE_TURN_SPEED = 1;
        private int rotationSign = 1;

        @Inject
        public RotatingCubeGame(final ExampleScene scene, final Exit exit, final LogicalLayer logicalLayer,
            final Key toggleRotationKey) {
            this.scene = scene;
            this.exit = exit;
            this.logicalLayer = logicalLayer;
            this.toggleRotationKey = toggleRotationKey;
        }

        @MainThread
        public void init() {
            // add a cube to the scene
            // add a rotating controller to the cube
            // add a light

            // box = SimpleShapeFactory.createQuad("the 'box'", 1, 1);

            final ZBufferState buf = new ZBufferState();
            buf.setEnabled(true);
            buf.setFunction(ZBufferState.TestFunction.LessThanOrEqualTo);
            scene.getRoot().setRenderState(buf);

            // Add a texture to the box.
//            final TextureState ts = new TextureState();
//            ts.setTexture(TextureManager.load("images/ardor3d_white_256.jpg", Texture.MinificationFilter.Trilinear,
//                Format.GuessNoCompression, true));
//            box.setRenderState(ts);
            box.setRandomColors();

            final PointLight light = new PointLight();

            final Random random = new Random();

            final float r = random.nextFloat();
            final float g = random.nextFloat();
            final float b = random.nextFloat();
            final float a = random.nextFloat();

            light.setDiffuse(new ColorRGBA(r, g, b, a));
            light.setAmbient(new ColorRGBA(0.5f, 0.5f, 0.5f, 1.0f));
            light.setLocation(new Vector3(MOVE_SPEED, MOVE_SPEED, MOVE_SPEED));
            light.setEnabled(true);

            /** Attach the light to a lightState and the lightState to rootNode. */
            final LightState lightState = new LightState();
            lightState.setEnabled(true);
            lightState.attach(light);
            scene.getRoot().setRenderState(lightState);

            scene.getRoot().attachChild(box);

            registerInputTriggers();
        }

        private void registerInputTriggers() {
            logicalLayer.registerTrigger(new InputTrigger(new KeyHeldCondition(Key.W), new TriggerAction() {

                public void perform(final Canvas source, final TwoInputStates inputStates, final double tpf) {
                    moveForward(source, tpf);
                }
            }));
            logicalLayer.registerTrigger(new InputTrigger(new KeyHeldCondition(Key.S), new TriggerAction() {

                public void perform(final Canvas source, final TwoInputStates inputStates, final double tpf) {
                    moveBack(source, tpf);
                }
            }));
            logicalLayer.registerTrigger(new InputTrigger(new KeyHeldCondition(Key.A), new TriggerAction() {

                public void perform(final Canvas source, final TwoInputStates inputStates, final double tpf) {
                    turnLeft(source, tpf);
                }
            }));
            logicalLayer.registerTrigger(new InputTrigger(new KeyHeldCondition(Key.D), new TriggerAction() {

                public void perform(final Canvas source, final TwoInputStates inputStates, final double tpf) {
                    turnRight(source, tpf);
                }
            }));
            logicalLayer.registerTrigger(new InputTrigger(new KeyHeldCondition(Key.Q), new TriggerAction() {

                public void perform(final Canvas source, final TwoInputStates inputStates, final double tpf) {
                    moveLeft(source, tpf);
                }
            }));
            logicalLayer.registerTrigger(new InputTrigger(new KeyHeldCondition(Key.E), new TriggerAction() {

                public void perform(final Canvas source, final TwoInputStates inputStates, final double tpf) {
                    moveRight(source, tpf);
                }
            }));

            logicalLayer.registerTrigger(new InputTrigger(new KeyPressedCondition(Key.ESCAPE), new TriggerAction() {

                public void perform(final Canvas source, final TwoInputStates inputStates, final double tpf) {
                    exit.exit();
                }
            }));

            logicalLayer.registerTrigger(new InputTrigger(new KeyPressedCondition(toggleRotationKey), new TriggerAction() {

                public void perform(final Canvas source, final TwoInputStates inputStates, final double tpf) {
                    toggleRotation();
                }
            }));
            logicalLayer.registerTrigger(new InputTrigger(new KeyReleasedCondition(Key.U), new TriggerAction() {

                public void perform(final Canvas source, final TwoInputStates inputStates, final double tpf) {
                    toggleRotation();
                }
            }));

            logicalLayer.registerTrigger(new InputTrigger(new KeyPressedCondition(Key.ZERO), new TriggerAction() {

                public void perform(final Canvas source, final TwoInputStates inputStates, final double tpf) {
                    resetCamera(source);
                }
            }));
            logicalLayer.registerTrigger(new InputTrigger(new KeyPressedCondition(Key.NINE), new TriggerAction() {

                public void perform(final Canvas source, final TwoInputStates inputStates, final double tpf) {
                    lookAtZero(source);
                }
            }));

            final Predicate<TwoInputStates> mouseMovedAndOneButtonPressed = Predicates.and(TriggerConditions.mouseMoved(),
                Predicates.or(TriggerConditions.leftButtonDown(), TriggerConditions.rightButtonDown()));

            logicalLayer.registerTrigger(new InputTrigger(mouseMovedAndOneButtonPressed, new TriggerAction() {

                public void perform(final Canvas source, final TwoInputStates inputStates, final double tpf) {
                    final MouseState mouseState = inputStates.getCurrent().getMouseState();

                    turn(source, mouseState.getDx() * tpf * -MOUSE_TURN_SPEED);
                    rotateUpDown(source, mouseState.getDy() * tpf * -MOUSE_TURN_SPEED);
                }
            }));
            logicalLayer.registerTrigger(new InputTrigger(new MouseButtonCondition(ButtonState.DOWN, ButtonState.DOWN,
                ButtonState.UNDEFINED), new TriggerAction() {

                public void perform(final Canvas source, final TwoInputStates inputStates, final double tpf) {
                    moveForward(source, tpf);
                }
            }));

            logicalLayer.registerTrigger(new InputTrigger(new AnyKeyCondition(), new TriggerAction() {

                public void perform(final Canvas source, final TwoInputStates inputStates, final double tpf) {
                    final InputState current = inputStates.getCurrent();

                    System.out.println("Key character pressed: " + current.getKeyboardState().getKeyEvent().getKeyChar());
                }
            }));
        }

        private void lookAtZero(final Canvas source) {
            source.getCanvasRenderer().getCamera().lookAt(Vector3.ZERO, Vector3.UNIT_Y);
        }

        private void resetCamera(final Canvas source) {
            final Vector3 loc = new Vector3(0.0f, 0.0f, 10.0f);
            final Vector3 left = new Vector3(-1.0f, 0.0f, 0.0f);
            final Vector3 up = new Vector3(0.0f, 1.0f, 0.0f);
            final Vector3 dir = new Vector3(0.0f, 0f, -1.0f);

            source.getCanvasRenderer().getCamera().setFrame(loc, left, up, dir);
        }

        private void toggleRotation() {
            rotationSign *= -1;
        }

        @MainThread
        public void update(final ReadOnlyTimer timer) {
            final double tpf = timer.getTimePerFrame();

            logicalLayer.checkTriggers(tpf);

            // rotate away

            angle += tpf * CUBE_ROTATE_SPEED * rotationSign;

            rotation.fromAngleAxis(angle, rotationAxis);
            if (box!=null) {
                box.setRotation(rotation);

                box.updateGeometricState(tpf, true);
            }
        }

        private void rotateUpDown(final Canvas canvas, final double speed) {
            final Camera camera = canvas.getCanvasRenderer().getCamera();

            final Vector3 temp = Vector3.fetchTempInstance();
            _incr.fromAngleNormalAxis(speed, camera.getLeft());

            _incr.applyPost(camera.getLeft(), temp);
            camera.setLeft(temp);

            _incr.applyPost(camera.getDirection(), temp);
            camera.setDirection(temp);

            _incr.applyPost(camera.getUp(), temp);
            camera.setUp(temp);

            Vector3.releaseTempInstance(temp);

            camera.normalize();

        }

        private void turnRight(final Canvas canvas, final double tpf) {
            turn(canvas, -TURN_SPEED * tpf);
        }

        private void turn(final Canvas canvas, final double speed) {
            final Camera camera = canvas.getCanvasRenderer().getCamera();

            final Vector3 temp = Vector3.fetchTempInstance();
            _incr.fromAngleNormalAxis(speed, camera.getUp());

            _incr.applyPost(camera.getLeft(), temp);
            camera.setLeft(temp);

            _incr.applyPost(camera.getDirection(), temp);
            camera.setDirection(temp);

            _incr.applyPost(camera.getUp(), temp);
            camera.setUp(temp);
            Vector3.releaseTempInstance(temp);

            camera.normalize();
        }

        private void turnLeft(final Canvas canvas, final double tpf) {
            turn(canvas, TURN_SPEED * tpf);
        }

        private void moveForward(final Canvas canvas, final double tpf) {
            final Camera camera = canvas.getCanvasRenderer().getCamera();
            final Vector3 loc = Vector3.fetchTempInstance().set(camera.getLocation());
            final Vector3 dir = Vector3.fetchTempInstance();
            if (camera.getProjectionMode() == ProjectionMode.Perspective) {
                dir.set(camera.getDirection());
            } else {
                // move up if in parallel mode
                dir.set(camera.getUp());
            }
            dir.multiplyLocal(MOVE_SPEED * tpf);
            loc.addLocal(dir);
            camera.setLocation(loc);
            Vector3.releaseTempInstance(loc);
            Vector3.releaseTempInstance(dir);
        }

        private void moveLeft(final Canvas canvas, final double tpf) {
            final Camera camera = canvas.getCanvasRenderer().getCamera();
            final Vector3 loc = Vector3.fetchTempInstance().set(camera.getLocation());
            final Vector3 dir = Vector3.fetchTempInstance();

            dir.set(camera.getLeft());

            dir.multiplyLocal(MOVE_SPEED * tpf);
            loc.addLocal(dir);
            camera.setLocation(loc);
            Vector3.releaseTempInstance(loc);
            Vector3.releaseTempInstance(dir);
        }

        private void moveRight(final Canvas canvas, final double tpf) {
            final Camera camera = canvas.getCanvasRenderer().getCamera();
            final Vector3 loc = Vector3.fetchTempInstance().set(camera.getLocation());
            final Vector3 dir = Vector3.fetchTempInstance();

            dir.set(camera.getLeft());

            dir.multiplyLocal(-MOVE_SPEED * tpf);
            loc.addLocal(dir);
            camera.setLocation(loc);
            Vector3.releaseTempInstance(loc);
            Vector3.releaseTempInstance(dir);
        }

        private void moveBack(final Canvas canvas, final double tpf) {
            final Camera camera = canvas.getCanvasRenderer().getCamera();
            final Vector3 loc = Vector3.fetchTempInstance().set(camera.getLocation());
            final Vector3 dir = Vector3.fetchTempInstance();
            if (camera.getProjectionMode() == ProjectionMode.Perspective) {
                dir.set(camera.getDirection());
            } else {
                // move up if in parallel mode
                dir.set(camera.getUp());
            }
            dir.multiplyLocal(-MOVE_SPEED * tpf);
            loc.addLocal(dir);
            camera.setLocation(loc);
            Vector3.releaseTempInstance(loc);
            Vector3.releaseTempInstance(dir);
        }
    }
}
