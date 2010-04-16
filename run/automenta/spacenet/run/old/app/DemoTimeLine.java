package automenta.spacenet.run.old.app;

import automenta.spacenet.run.ArdorSpacetime;
import automenta.spacenet.run.DemoDefaults;
import automenta.spacenet.space.geom.ProcessBox;
import automenta.spacenet.space.widget.button.Button;
import automenta.spacenet.space.widget.window.Window;
import java.io.FileInputStream;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.component.VEvent;

/**
 *
 * @author seh
 */
public class DemoTimeLine extends ProcessBox {
    
    

    @Override protected void start() {

        double initialX = -1;
        
        try {
            //FileInputStream fin = new FileInputStream("/home/seh/USHolidays.ics");
            FileInputStream fin = new FileInputStream("/home/seh/basic.ics");

            CalendarBuilder builder = new CalendarBuilder();

            Calendar calendar = builder.build(fin);

            ComponentList components = calendar.getComponents();
            Iterator<Component> ci = components.iterator();
            while (ci.hasNext()) {
                Component c = ci.next();
                //System.out.println( c );
                if (c instanceof VEvent) {
                    VEvent event = (VEvent)c;

                    Window w = add(new Window());
                    double x = ((double)(event.getStartDate().getDate().getTime())) / (24 * 60 * 60.0 * 1000);
                    double y = 0;

                    if (initialX < x) {
                        initialX = x;
                    }

                    String label = event.getSummary().getValue();
                    w.add(new Button(DemoDefaults.font, label)).moveDZ(0.1).scale(0.9, 0.25);

                    w.move(x, y);

                    System.out.println(" from " + event.getStartDate() + " to " + event.getEndDate() + " at " + x );

                }
            }
            System.out.println(initialX);

            getSpacetime().getCamera().getTargetPosition().set(initialX, 0, 10);
            getSpacetime().getCamera().getTargetTarget().set(initialX, 0, 0);


        } catch (Exception ex) {
            Logger.getLogger(DemoTimeLine.class.getName()).log(Level.SEVERE, null, ex);
        }


    }


    public static void main(String[] args) {
        ArdorSpacetime.newWindow(new DemoTimeLine());
    }


}
