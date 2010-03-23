package automenta.spacenet.space.geom.border;

import automenta.spacenet.space.geom.Box;

public class SolidBoxBorder extends Box {

    private final double xStart;
    private final double xStop;
    private final double yStart;
    private final double yStop;

    public SolidBoxBorder(double xStart, double xStop, double yStart, double yStop) {
        super(BoxShape.Empty);
        this.xStart = xStart;
        this.xStop = xStop;
        this.yStart = yStart;
        this.yStop = yStop;
        refreshBorder();
    }

    protected void refreshBorder() {
        removeAll();
        //add(new Box(BoxShape.Cubic).move(0,0-0.2));
        add(new Box(BoxShape.Cubic).span(-xStop / 2.0, 0.5, -xStart / 2.0, -0.5));
        add(new Box(BoxShape.Cubic).span(xStart / 2.0, 0.5, xStop / 2.0, -0.5));
        add(new Box(BoxShape.Cubic).span(0.5, -yStop / 2.0, -0.5, -yStart / 2.0));
        add(new Box(BoxShape.Cubic).span(0.5, yStop / 2.0, -0.5, yStart / 2.0));
    }
}
