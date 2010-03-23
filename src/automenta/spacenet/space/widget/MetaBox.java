/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.spacenet.space.widget;

import automenta.spacenet.run.DemoDefaults;
import automenta.spacenet.space.Space;
import automenta.spacenet.space.control.Pressable;
import automenta.spacenet.space.control.Zoomable;
import automenta.spacenet.space.geom.Box;
import automenta.spacenet.space.geom.Rect;
import automenta.spacenet.space.geom.layout.ColRect;
import automenta.spacenet.space.geom.layout.RowRect;
import automenta.spacenet.space.geom.text3d.Font3D;
import automenta.spacenet.space.surface.ColorSurface;
import automenta.spacenet.space.widget.button.Button;
import automenta.spacenet.space.widget.button.ButtonAction;
import automenta.spacenet.space.widget.knob.CircleKnob;
import automenta.spacenet.space.widget.panel.Panel;
import automenta.spacenet.var.physical.Color;
import automenta.spacenet.var.scalar.DoubleVar;
import automenta.spacenet.var.scalar.DoubleVar.IfDoubleChanges;
import com.ardor3d.intersection.PickData;
import com.ardor3d.scenegraph.Spatial;

/**
 * an empty fillable box surrounded by control widgets.
 * ex: control window icon expanders  on each of the 8 corners of the box
 */
public class MetaBox extends Box {

    private final Space content;

    class IconExpander extends Box {

        private final Space content;
        private boolean expanded = false;
        private final IconExpanderBox iconBox;

        class IconExpanderBox extends Box implements Pressable, Zoomable {

            public IconExpanderBox() {
                super(BoxShape.Cubic);
            }

            @Override
            public void onPressStart(PickData pick) {
            }

            @Override
            public void onPressStop(PickData pick) {
                setExpanded(!isExpanded());
            }

            @Override
            public boolean isTangible() {
                return true;
            }

            @Override
            public void onZoomStart() {
            }

            @Override
            public void onZoomStop() {
            }

            @Override
            public boolean isZoomable() {
                return true;
            }
        }
        double dz = 1.0;

        public IconExpander(Space content) {
            super(BoxShape.Empty);

            this.content = content;

            iconBox = add(new IconExpanderBox());
            iconBox.color(Color.White);

            setExpanded(false);

        }

        public boolean isExpanded() {
            return expanded;
        }

        public synchronized void setExpanded(boolean e) {
            this.expanded = e;
            if (e) {
                iconBox.move(0, 0, -dz);
                add(content);
            } else {
                remove(content);
                iconBox.move(0, 0, 0);
            }
        }
    }

    //TODO abstract this into pluggalbe MetaBoxModel
    public MetaBox(Space content) {
        super(BoxShape.Empty);

        this.content = add(content);

        //TODO move this into a pluggable model
        for (double x = -0.5; x <= 0.5; x += 1.0) {
            for (double y = -0.5; y <= 0.5; y += 1.0) {
                for (double z = -0.5; z <= 0.5; z += 1.0) {
                    Panel p = newControlPanel();
                    p.scale(4.0);
                    add(new IconExpander(p).move(x, y, z).face(x, y, z).scale(0.05));
                }
            }
        }
    }

    //TODO make this a pluggable model
    protected Panel newControlPanel() {
        final Panel p = new Panel() {

            boolean initializedPanel = false;

            @Override
            protected void afterAttached(Spatial newParent) {
                super.afterAttached(newParent);
                if (!initializedPanel) {
                    initialize();

                    
                }
            }

            protected synchronized void initialize() {
                Font3D font = DemoDefaults.font;


                CircleKnob rotate1 = new CircleKnob(new ColorSurface(Color.GrayMinusMinus), new ColorSurface(Color.Purple));
                rotate1.getAngle().add(new IfDoubleChanges() {
                    @Override public void onDoubleChange(DoubleVar d) {
                        rotateContent(d.d(), 0, 0);
                    }
                });
                CircleKnob rotate2 = new CircleKnob(new ColorSurface(Color.GrayMinusMinus), new ColorSurface(Color.Blue));
                rotate2.getAngle().add(new IfDoubleChanges() {
                    @Override public void onDoubleChange(DoubleVar d) {
                        rotateContent(0, d.d(), 0);
                    }
                });

                //TODO replace with scale Spinner
                Button grow = new Button(font, "+");
                grow.add(new ButtonAction() {

                    @Override public void onButtonClicked(Button b) {
                        MetaBox.this.getSize().multiplyLocal(1.5);
                    }
                });

                Button shrink = new Button(font, "-");
                shrink.add(new ButtonAction() {

                    @Override public void onButtonClicked(Button b) {
                        MetaBox.this.getSize().multiplyLocal(0.7);
                    }
                });

                final Panel p = this;

                Button closeButton = new Button(font, "X");
                closeButton.add(new ButtonAction() {

                    @Override public void onButtonClicked(Button b) {
                        //HACK find a better way to do this
                        ((IconExpander)p.getParent()).setExpanded(false);
                    }
                });


                Button delete = new Button(font, "Delete");
                delete.add(new ButtonAction() {

                    @Override
                    public void onButtonClicked(Button b) {
                        close();
                    }
                });

                add(new ColRect(0.1,
                    new RowRect(0.1, rotate1, rotate2),
                    new RowRect(0.1, grow, shrink),
                    new RowRect(0.1, delete))).moveDZ(0.05);
                add(closeButton).span(0.5, 0.5, 0.6, 0.6);

                initializedPanel = true;
            }
        };

        return p;
    }

    double contentR1 = 0;
    double contentR2 = 0;
    double contentR3 = 0;

    protected void rotateContent(double dr1, double dr2, double dr3) {
        if (dr1!=0)
            contentR1 = dr1;
        if (dr2!=0)
            contentR2 = dr2;
        if (dr3!=0)
            contentR3 = dr3;

        //TODO use interface Rotateable3
        if (content instanceof Box) {
            ((Box) content).rotate(contentR1, contentR2, contentR3);
        } else if (content instanceof Rect) {
            ((Rect) content).rotate(contentR1, contentR2, contentR3);
        }
    }

    public void close() {
        getParent().detachChild(this);
    }
}
