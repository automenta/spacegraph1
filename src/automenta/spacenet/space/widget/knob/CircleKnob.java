package automenta.spacenet.space.widget.knob;

import automenta.spacenet.space.SpaceState;
import automenta.spacenet.space.geom.Rect;
import automenta.spacenet.space.widget.window.Window;
import automenta.spacenet.var.scalar.DoubleVar;
import automenta.spacenet.var.vector.V3;
import automenta.spacenet.var.vector.V3.IfV3Changes;

public class CircleKnob extends Rect {

    //in radians
    public final DoubleVar angle = new DoubleVar(0.0);
    private double centerRad = 0.75;
    private double needleScale = 0.1;
    private final Rect centerCircle;
    private final Window needle = new Window();
    private boolean updateNecessary = true;

    public CircleKnob(SpaceState centerState, SpaceState needleState) {
        super(RectShape.Empty);
        centerCircle = add(new Rect(RectShape.Ellipse));
        centerCircle.scale(centerRad);
        centerCircle.add(centerState);
        add(needle);
        needle.scale(needleScale);
        needle.add(needleState);
        needle.getPosition().add(new IfV3Changes() {

            @Override
            public void onV3Changed(V3 v) {
                updateNeedle();
            }
        });
        updateNeedle();
    }

    protected void updateNeedle() {
        if (!updateNecessary) {
            return;
        }
        //compute angle from its position
        //System.out.println("needle: " + needle.getPosition());
        double a;
        if ((needle.getPosition().getY() == 0) && (needle.getPosition().getX() == 0)) {
            a = 0;
        } else {
            a = Math.atan2(needle.getPosition().getY(), needle.getPosition().getX());
        }
        //System.out.println("  rad: " + angle);
        //constrain needle to circle acccording to position
        angle.set(a);
        double px = Math.cos(a) * (centerRad / 2.0 + needleScale / 2.0);
        double py = Math.sin(a) * (centerRad / 2.0 + needleScale / 2.0);
        updateNecessary = false;
        needle.getPosition().set(px, py, 0);
        updateNecessary = true;
    }

    public DoubleVar getAngle() {
        return angle;
    }
}
