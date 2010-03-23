/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.spacenet.run.control;

import automenta.spacenet.space.widget.DragRect;
import automenta.spacenet.run.ArdorSpacetime;
import automenta.spacenet.space.control.Zoomable;
import automenta.spacenet.space.geom.Box;
import automenta.spacenet.space.geom.ProcessBox;
import automenta.spacenet.var.physical.Color;
import com.ardor3d.math.Vector3;

/**
 *
 * @author seh
 */
public class DemoDragRectSketching extends ProcessBox {


    public class TestDragPanel extends DragRect implements Zoomable {

        //TODO use world -> local coordinates to add the drawn objects to the panel rather than to the global.  then if the panel moves or rotates, the drawn objects will remain attached to it
        
        @Override
        protected void onDragging(Vector3 c) {            
            final Box b = DemoDragRectSketching.this.add(new Box(BoxShape.Spheroid));
            b.color(Color.newRandomHSB(0.25, 1.0));
            b.scale(0.1);
            b.move(c);
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

        @Override
        protected void onDragStart(Vector3 currentIntersect) {
        }
    }

    @Override protected void start() {


        final DragRect dp = add(new TestDragPanel());
        dp.scale(4, 4).move(0, 0, -2);

        final DragRect dp2 = add(new TestDragPanel());
        dp2.scale(3, 5).move(-4, 0, -2).rotate(Math.PI/4, 0, 0);

        final DragRect dp3 = add(new TestDragPanel());
        dp3.scale(2, 2).move(4, 0, -2).rotate(0, Math.PI/4, 0);
    }

    public static void main(String[] args) {
        ArdorSpacetime.newWindow(new DemoDragRectSketching());
    }
}
