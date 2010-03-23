/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.spacenet.run.widget3d;

import automenta.spacenet.space.widget3d.Dialog3D;
import automenta.spacenet.run.ArdorSpacetime;
import automenta.spacenet.run.DemoDefaults;
import automenta.spacenet.space.control.Zoomable;
import automenta.spacenet.space.geom.Box;
import automenta.spacenet.space.geom.ProcessBox;
import automenta.spacenet.space.geom.text3d.Font3D;

/**
 *
 */
public class DemoSphereWithWindows2 extends ProcessBox {

    Font3D font = DemoDefaults.font;

    public abstract class SphereWithWindows extends Box implements Zoomable {

        public SphereWithWindows(int numWindows) {
            super(BoxShape.Spheroid);
            double t = 0.0;
            for (int i = 0; i < numWindows; i++) {
                double x = Math.sin(t) / 2.0;
                double y = 0;
                double z = Math.cos(t) / 2.0;
                double normal = t;

                add(newSide(i)).move(x, y, z).rotate(normal, 0, 0);
                t += Math.PI * 2.0 / ((double) numWindows);
            }
        }

        abstract public Box newSide(int n);

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

        @Override
        public boolean isTangible() {
            return true;
        }
    }

    @Override protected void start() {
        add(new SphereWithWindows(5) {

            @Override
            public Box newSide(int n) {
                final double w = 0.5;
                final double h = 0.35;
                final double d = 0.05;
                Dialog3D b = new Dialog3D(font, " ", w, h, d);

                b.add(new SphereWithWindows(4) {
                    @Override public Box newSide(int n) {
                        return new Dialog3D(font, " ", w, h, d);
                    }
                }).move(0,0,0.5).scale(0.5, 0.5, 0.5).rotate(0, 0, Math.PI/2.0);
                return b;
            }

        });
    }

    public static void main(String[] args) {
        ArdorSpacetime.newWindow(new DemoSphereWithWindows2());
    }
}
