package automenta.spacenet.space.widget.button;

import automenta.spacenet.space.geom.border.SolidRectBorder;
import automenta.spacenet.space.widget.panel.DefaultPanelModel;
import automenta.spacenet.var.physical.Color;

public class DefaultEmptyButtonModel extends DefaultPanelModel implements ButtonModel {

    public static final Color defaultPressColor = Color.GrayPlus;
    private SolidRectBorder border = new SolidRectBorder(0.98, 1.08, 0.98, 1.08);

    public DefaultEmptyButtonModel() {
        super();
    }

    @Override
    public void initButton(Button b) {
        b.add(border);
        //border.move(0,0,0.1);
        
        onNormal(); //TODO this shouldnt be necessary
    }

    public void onPressStart() {
        getPanel().color(defaultPressColor);
    }

    @Override
    public void onTouchStart() {
        super.onTouchStart();
        border.color(Color.Orange);
    }


    @Override
    public void onNormal() {
        super.onNormal();
        border.color(Color.Gray);
    }


}
