/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package automenta.spacenet.space.widget.button;

import automenta.spacenet.space.geom.Box;
import automenta.spacenet.space.widget.panel.Panel;
import automenta.spacenet.space.control.Pressable;
import automenta.spacenet.space.geom.text3d.Font3D;
import automenta.spacenet.space.geom.text3d.Text3D;
import automenta.spacenet.space.widget.panel.DefaultPanelModel;
import automenta.spacenet.space.widget.panel.PanelModel;
import automenta.spacenet.var.physical.Color;
import com.ardor3d.intersection.PickData;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author seh
 */
public class Button extends Panel implements Pressable {

    private List<ButtonAction> buttonActions = new LinkedList();
    
    public Button() {
        this(new DefaultEmptyButtonModel());
    }

    public Button(Font3D font, String label) {
        this(new DefaultTextButtonModel(font, label));
    }

    public Button(ButtonModel model) {
        super(model);
    }

    @Override
    public void setModel(PanelModel m) {
        super.setModel(m);
        ((ButtonModel)m).initButton(this);
    }




    @Override public ButtonModel getModel() {
        return (ButtonModel) super.getModel();
    }


    @Override public void onPressStart(PickData pick) {
        getModel().onPressStart();
    }

    @Override public void onPressStop(PickData pick) {
        
        if (isTouched()) {
            getModel().onTouchStart();
        }
        else {
            getModel().onNormal();
        }

        //TODO test if still picking
        for (ButtonAction ba : buttonActions)
            ba.onButtonClicked(this);
    }

    public void add(ButtonAction a) {
        buttonActions.add(a);
    }
    public void remove(ButtonAction a) {
        buttonActions.remove(a);
    }

}
