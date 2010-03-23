package automenta.spacenet.space.widget.button;

import automenta.spacenet.space.geom.text3d.Font3D;
import automenta.spacenet.space.geom.text3d.Text3D;
import automenta.spacenet.var.physical.Color;

public class DefaultTextButtonModel extends DefaultEmptyButtonModel {

    private String label;
    private final Font3D font;
    private static final Color normalTextColor = Color.Black;
    private static final Color touchedTextColor = Color.GrayMinusMinus;
    private static final Color pressedTextColor = Color.GrayMinus;
    private final Text3D labelNode;

    public DefaultTextButtonModel(Font3D font, String label) {
        super();
        this.labelNode = new Text3D(font, label);
        this.font = font;
        setLabel(label);
    }

    @Override
    public void initButton(Button b) {
        super.initButton(b);

        b.add(labelNode).move(0, 0, 0.05);
        labelNode.color(Color.Black);
    }

    @Override
    public void onNormal() {
        super.onNormal();
        labelNode.color(normalTextColor);
    }

    @Override
    public void onTouchStart() {
        super.onTouchStart();
        labelNode.color(touchedTextColor);
    }

    @Override
    public void onPressStart() {
        super.onPressStart();
        labelNode.color(pressedTextColor);
    }

    private void setLabel(String label) {
        this.label = label;
    }
}
