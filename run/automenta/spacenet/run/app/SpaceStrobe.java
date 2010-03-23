/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.spacenet.run.app;

import automenta.spacenet.run.ArdorSpacetime;
import automenta.spacenet.run.DemoDefaults;
import automenta.spacenet.space.Repeat;
import automenta.spacenet.space.control.Pressable;
import automenta.spacenet.space.geom.Box;
import automenta.spacenet.space.geom.ProcessBox;
import automenta.spacenet.space.geom.Rect;
import automenta.spacenet.space.geom.Rect.RectShape;
import automenta.spacenet.space.widget.spinner.Spinner;
import automenta.spacenet.var.physical.Color;
import automenta.spacenet.var.scalar.DoubleRange;
import automenta.spacenet.var.scalar.DoubleVar;
import com.ardor3d.framework.Canvas;
import com.ardor3d.input.Key;
import com.ardor3d.input.logical.InputTrigger;
import com.ardor3d.input.logical.KeyPressedCondition;
import com.ardor3d.input.logical.TriggerAction;
import com.ardor3d.input.logical.TwoInputStates;
import com.ardor3d.intersection.PickData;
import com.ardor3d.math.Vector3;
import com.ardor3d.scenegraph.Spatial;

/**
 *
 * @author seh
 */
public class SpaceStrobe extends ProcessBox {

    public static class BrainwaveState {
        //TODO set at modern schumann resonance frequency

        private DoubleVar primaryFrequency = new DoubleVar(7.4);
        private DoubleVar sharpness = new DoubleVar(1.0); //1.0 = flat line, > 1.0 = sharper distortion
        private Color primaryColor = new Color(Color.White);

        public DoubleVar getPrimaryFrequency() {
            return primaryFrequency;
        }

        public DoubleVar getSharpness() {
            return sharpness;
        }

        public Color getPrimaryColor() {
            return primaryColor;
        }        

    }

    public static class BrainwaveSpaceMenu extends Box {

        public BrainwaveSpaceMenu(final BrainwaveSpace space) {
            super(BoxShape.Empty);

            Spinner freqSpin = add(new Spinner(DemoDefaults.font, new DoubleRange(space.state.getPrimaryFrequency(), new DoubleVar(0), new DoubleVar(30.0)), new DoubleVar(0.1)));
            freqSpin.span(-0.5, 0.25, -0.05, -0.25);
            
            ColorChooser primColorChooser = add(new ColorChooser() {
                @Override public void onColorSelected(Color c) {
                    space.state.getPrimaryColor().set(c);
                }
            });
            primColorChooser.span(0.5, 0.25, 0.05, -0.25);

        }
    }

    public static class BrainwaveSpace extends Box {

        public final BrainwaveState state;
        private BrainwaveSpaceMenu menu;
        private boolean showingMenu;

        public class StrobeRect extends Rect implements Pressable {

            public StrobeRect() {
                super(RectShape.Rect);
            }


            @Override
            public void onPressStart(PickData pick) {
            }

            @Override
            public void onPressStop(PickData pick) {
                toggleMenu();
            }

            @Override
            public boolean isTangible() {
                return true;
            }
        }

        public BrainwaveSpace(BrainwaveState bstate) {
            super(BoxShape.Empty);


            this.state = bstate;

            final Rect rect = add(new StrobeRect());
            rect.scale(4);

            add(new Repeat() {

                @Override protected void update(double t, double dt, Spatial s) {
                    double f = Math.PI * state.getPrimaryFrequency().d();

                    double w = (Math.sin(t * f) * state.getSharpness().d());

                    if (w > 1.0) {
                        w = 1.0;
                    }
                    if (w < -1.0) {
                        w = -1.0;
                    }

                    w += 1.0;
                    w /= 2.0;

                    double r = (float) w * state.getPrimaryColor().getRed();
                    double g = (float) w * state.getPrimaryColor().getGreen();
                    double b = (float) w * state.getPrimaryColor().getBlue();
                    double a = 1.0f;

                    rect.color(new Color(r, g, b, a));
                }
            });
            toggleMenu();

        }

        protected void resetCamera() {
            try {
                getSpacetime().getCamera().getTargetPosition().set(new Vector3(0,0,3));
                getSpacetime().getCamera().getTargetTarget().set(new Vector3(0,0,0));
            }
            catch (NullPointerException npe) { }
        }
        
        protected void toggleMenu() {
            resetCamera();

            if (showingMenu) {
                remove(menu);
                showingMenu = false;
            } else {
                menu = add(new BrainwaveSpaceMenu(this));
                menu.moveDZ(1.5);
                showingMenu = true;
            }
        }
    }

    public static abstract class ColorChooser extends Rect {

        public class ColorButton extends Rect implements Pressable {
            private final Color color;

            public ColorButton(Color color) {
                super(RectShape.Rect);

                this.color = color;

                color(color);
            }

            @Override
            public void onPressStart(PickData pick) {
            }

            @Override
            public void onPressStop(PickData pick) {
                onColorSelected(this.color);
            }

            @Override
            public boolean isTangible() {
                return true;
            }
        
        }

        public ColorChooser() {
            super(RectShape.Empty);
            
            int hueDivisions = 16;
            int satDivisions = 16;
            double hueWidth = 1.0 / ((double)hueDivisions);
            double satWidth = 1.0 / ((double)satDivisions);
            double x;
            double y = -0.5;
            double hue = 0.0;
            double sat = 0.0;

            for (int i = 0; i < hueDivisions; i++) {
                x = -0.5;

                for (int j = 0; j < satDivisions; j++) {
                    ColorButton b = add(new ColorButton(Color.hsb(hue, sat, 1.0)));
                    b.span(x, y, x+hueWidth, y+satWidth);
                    b.moveDZ(0.1);

                    hue += hueWidth;
                    x += hueWidth;
                }

                sat += satWidth;
                y += satWidth;

            }
        }

        abstract public void onColorSelected(Color c);


    }

    @Override protected void start() {
        final BrainwaveState bstate = new BrainwaveState();
        add(new BrainwaveSpace(bstate));

        getSpacetime().getInputLogic().registerTrigger(new InputTrigger(new KeyPressedCondition(Key.PAGEUP_PRIOR), new TriggerAction() {

            public void perform(final Canvas source, final TwoInputStates inputState, final double tpf) {
                bstate.getPrimaryFrequency().add(0.5);
            }
        }));
        getSpacetime().getInputLogic().registerTrigger(new InputTrigger(new KeyPressedCondition(Key.PAGEDOWN_NEXT), new TriggerAction() {

            public void perform(final Canvas source, final TwoInputStates inputState, final double tpf) {
                bstate.getPrimaryFrequency().add(-0.5);
            }
        }));

    }

    public static void main(String[] args) {
        ArdorSpacetime.newWindow(new SpaceStrobe());
    }
}
