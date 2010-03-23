/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package automenta.spacenet.run.widget;

import automenta.spacenet.run.ArdorSpacetime;
import automenta.spacenet.space.geom.Box;
import automenta.spacenet.space.geom.ProcessBox;
import automenta.spacenet.space.widget.MetaBox;

/**
 *
 * @author seh
 */
public class DemoMetaBox extends ProcessBox {

    @Override
    protected void start() {

        for (int x = -2; x < 2; x++) {
            Box content;
            if (x % 2 == 0)
                content = new Box(BoxShape.Cubic);
            else
                content = new Box(BoxShape.Spheroid);

            content.scale(0.75);

            MetaBox mb = add(new MetaBox(content));
            mb.move(x*1.3,0,0);
        }
        
    }

    
    public static void main(String[] args) {
        ArdorSpacetime.newWindow(new DemoMetaBox());
    }



}
