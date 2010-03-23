/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package automenta.spacenet.run.control;

import automenta.spacenet.run.ArdorSpacetime;
import automenta.spacenet.space.control.Touchable;
import automenta.spacenet.space.control.Pressable;
import automenta.spacenet.space.geom.Box;
import automenta.spacenet.space.geom.Rect;
import automenta.spacenet.space.surface.ColorSurface;
import automenta.spacenet.space.geom.ProcessBox;
import com.ardor3d.intersection.PickData;
import com.ardor3d.math.Vector3;

public class DemoTouchClick extends ProcessBox {


    public class PickableRect extends Rect implements Touchable {
        private final ColorSurface cs;

        public PickableRect() {
            super(RectShape.Rect);

            cs = add(new ColorSurface());
        }

        @Override
        public void onTouchStart(PickData pick) {
        }

        @Override
        public void onTouching(PickData pick) {
            
            int numInt = pick.getIntersectionRecord().getNumberOfIntersections();
            if (numInt != 1) {
                System.out.println("odd, # of intersections = " + numInt);
                return;
            }

            Vector3 p = pick.getIntersectionRecord().getIntersectionPoint(0);
            
            cs.color(Math.cos(p.getX()), Math.sin(p.getY()), Math.cos(p.getZ()));
        }

        @Override
        public void onTouchStop() {
        }

        @Override public boolean isTangible() { return true; }

    }

    public class PickableBox extends Box implements Touchable {
        private final ColorSurface color;

        public PickableBox() {
            super(BoxShape.Cubic);

            color = add(new ColorSurface(1,1,1));

            onTouchStop();
        }

        @Override
        public void onTouchStart(PickData pick) {
            color.color(1, 0, 0);
        }

        @Override
        public void onTouchStop() {
            color.color(0, 0, 1);
        }

        @Override
        public void onTouching(PickData pick) {
        }

        @Override public boolean isTangible() { return true; }

        
    }

    public class PressableBox extends Box implements Pressable {
        private final ColorSurface color;

        public PressableBox() {
            super(BoxShape.Cubic);

            color = add(new ColorSurface(1,1,1));

            onPressStop(null);
        }


        @Override public boolean isTangible() { return true; }

        @Override
        public void onPressStart(PickData pick) {
            color.color(0,1,0);
        }

        @Override
        public void onPressStop(PickData pick) {
            color.color(0,0,1);
        }


    }




    

    
    @Override
    protected void start() {
        add(new PickableBox().move(-1, 1, 0));
        add(new PickableRect().move(1, 1, 0));
        add(new PressableBox().move(-1, -1, 0));

    }


    public static void main(String[] args) {
        ArdorSpacetime.newWindow(new DemoTouchClick());
    }

    
}
