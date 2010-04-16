/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.spacenet.run.old;

import automenta.spacenet.run.ArdorSpacetime;
import automenta.spacenet.run.ArdorSpacetime;
import automenta.spacenet.run.old.widget.DemoButton;
import automenta.spacenet.space.geom.ProcessBox;
import automenta.spacenet.space.video.Exit;
import com.ardor3d.annotation.MainThread;
import com.ardor3d.framework.ArdorModule;
import com.ardor3d.framework.CanvasRenderer;
import com.ardor3d.framework.DisplaySettings;
import com.ardor3d.framework.FrameHandler;
import com.ardor3d.framework.NativeCanvas;
import com.ardor3d.framework.Scene;
import com.ardor3d.framework.jogl.JoglAwtCanvas;
import com.ardor3d.framework.jogl.JoglCanvasRenderer;
import com.ardor3d.image.Image;
import com.ardor3d.image.util.AWTImageLoader;
import com.ardor3d.input.ControllerWrapper;
import com.ardor3d.input.PhysicalLayer;
import com.ardor3d.input.awt.AwtFocusWrapper;
import com.ardor3d.input.awt.AwtKeyboardWrapper;
import com.ardor3d.input.awt.AwtMouseManager;
import com.ardor3d.input.awt.AwtMouseWrapper;
import com.ardor3d.input.logical.DummyControllerWrapper;
import com.ardor3d.input.logical.LogicalLayer;
import com.ardor3d.intersection.PickResults;
import com.ardor3d.math.Ray3;
import com.ardor3d.renderer.Renderer;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.util.Timer;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Stage;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

/**
 *
 * @author seh
 */
public class ArdorPanel extends JPanel implements Runnable {

    private final MyExit exit;
    private final FrameHandler frameWork;
    private ArdorSpacetime root;
    private JoglAwtCanvas theCanvas;

    static {
        //System.setProperty("ardor3d.useMultipleContexts", "true");
    }

    private NativeCanvas nativeCanvas;

    public ArdorPanel(ProcessBox content) {
        super(new BorderLayout());

        final Module ardorModule = new ArdorModule();

        final Injector injector = Guice.createInjector(Stage.PRODUCTION, ardorModule);

        frameWork = injector.getInstance(FrameHandler.class);

        exit = new MyExit();
        final LogicalLayer logicalLayer = injector.getInstance(LogicalLayer.class);

        //AWTImageLoader.registerLoader();

        try {
            addCanvas(new AwtScene(content, logicalLayer, frameWork), logicalLayer, frameWork);
        } catch (Exception ex) {
            Logger.getLogger(ArdorPanel.class.getName()).log(Level.SEVERE, null, ex);
        }

        start();

    }

    protected void start() {
        new Thread(this).start();
    }

    public void run() {
        root.init();

        addComponentListener(new ComponentListener() {

            @Override
            public void componentResized(ComponentEvent e) {
                updateSize();
            }

            @Override
            public void componentMoved(ComponentEvent e) {
            }

            @Override
            public void componentShown(ComponentEvent e) {
            }

            @Override
            public void componentHidden(ComponentEvent e) {
            }
        });
        addAncestorListener(new AncestorListener() {

            @Override
            public void ancestorRemoved(AncestorEvent event) {
                exit.exit = true;
            }

            @Override
            public void ancestorAdded(AncestorEvent event) {
            }

            @Override
            public void ancestorMoved(AncestorEvent event) {
            }


        });
        

        Timer timer = new Timer();

        //TODO un-hack this
        boolean updateSizeOnce = false;

        while (!exit.isExit()) {
            timer.update();

            frameWork.updateFrame();

            //root.update must be called after frameWork.updateFrame()!
            root.update(timer);

            Thread.yield();

            if (!updateSizeOnce) {
                updateSize();
                updateSizeOnce = true;
            }
        }
    }

    public final class AwtScene implements Scene {

        private AwtScene(final ProcessBox content, LogicalLayer logicalLayer, FrameHandler frameWork) {
            root = new ArdorSpacetime(logicalLayer, frameWork) {

                @Override protected void initWindow() {
                    root.add(content);
                }

                public NativeCanvas getCanvas() {
                    return nativeCanvas;
                }

                @Override
                public NativeCanvas getVideo() {
                    return nativeCanvas;
                }
            };

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

    private void addCanvas(final AwtScene scene, final LogicalLayer logicalLayer, final FrameHandler frameWork) throws Exception {
        final JoglCanvasRenderer canvasRenderer = new JoglCanvasRenderer(scene);

        final DisplaySettings settings = new DisplaySettings(400, 300, 24, 0, 0, 16, 0, 0, false, false);
        theCanvas = new JoglAwtCanvas(settings, canvasRenderer);

        nativeCanvas = new NativeCanvas() {

            @Override public void close() {
            }

            @Override
            public boolean isActive() {
                return true;
            }

            @Override
            public boolean isClosing() {
                return false;
            }

            @Override
            public void setVSyncEnabled(boolean arg0) {
            }

            @Override
            public void setTitle(String arg0) {
            }

            @Override
            public void setIcon(Image[] arg0) {
            }

            @Override
            public void moveWindowTo(int arg0, int arg1) {
            }

            @Override
            public void init() {
            }

            @Override
            public void draw(CountDownLatch arg0) {
            }

            @Override
            public CanvasRenderer getCanvasRenderer() {
                return canvasRenderer;
            }
        };

        add(theCanvas, BorderLayout.CENTER);


        theCanvas.setVisible(true);

        final AwtKeyboardWrapper keyboardWrapper = new AwtKeyboardWrapper(theCanvas);
        final AwtFocusWrapper focusWrapper = new AwtFocusWrapper(theCanvas);
        final AwtMouseManager mouseManager = new AwtMouseManager(theCanvas);
        final AwtMouseWrapper mouseWrapper = new AwtMouseWrapper(theCanvas, mouseManager);
        final ControllerWrapper controllerWrapper = new DummyControllerWrapper();

        final PhysicalLayer pl = new PhysicalLayer(keyboardWrapper, mouseWrapper, controllerWrapper, focusWrapper);

        logicalLayer.registerInput(theCanvas, pl);

        frameWork.addCanvas(theCanvas);



    }

    protected void updateSize() {
        Dimension d = ArdorPanel.this.getSize();
        try {
            theCanvas.setSize(d);
            theCanvas.getCanvasRenderer().getCamera().resize(d.width, d.height);
        }
        catch (NullPointerException e) { }
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
}
