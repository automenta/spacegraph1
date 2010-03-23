/**
 * Copyright (c) 2008-2009 Ardor Labs, Inc.
 *
 * This file is part of Ardor3D.
 *
 * Ardor3D is free software: you can redistribute it and/or modify it 
 * under the terms of its license which may be found in the accompanying
 * LICENSE file or at <http://www.ardor3d.com/LICENSE>.
 */
package automenta.spacenet.run.widget;

import automenta.spacenet.run.ArdorSpacetime;
import automenta.spacenet.space.Repeat;
import com.ardor3d.extension.ui.UIButton;
import com.ardor3d.extension.ui.UICheckBox;
import com.ardor3d.extension.ui.UIComponent;
import com.ardor3d.extension.ui.UIFrame;
import com.ardor3d.extension.ui.UIHud;
import com.ardor3d.extension.ui.UILabel;
import com.ardor3d.extension.ui.UIPanel;
import com.ardor3d.extension.ui.UIProgressBar;
import com.ardor3d.extension.ui.UIRadioButton;
import com.ardor3d.extension.ui.layout.BorderLayout;
import com.ardor3d.extension.ui.layout.BorderLayoutData;
import com.ardor3d.extension.ui.layout.RowLayout;
import com.ardor3d.extension.ui.util.ButtonGroup;
import com.ardor3d.extension.ui.util.Dimension;
import com.ardor3d.extension.ui.util.SubTex;
import com.ardor3d.image.Texture;
import com.ardor3d.image.Image.Format;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.scenegraph.Spatial;
import com.ardor3d.util.TextureManager;
import com.ardor3d.util.Timer;

public class DemoArdorUI extends DemoButton {

    UIHud hud;
    UILabel fpslabel;
    UIProgressBar bar;
    Timer timer;

    public static void main(final String[] args) {
        ArdorSpacetime.newWindow(new DemoArdorUI());
    }

    @Override
    protected void start() {
        super.start();

        UIComponent.setUseTransparency(true);

        final UIFrame frame = new UIFrame("Sample");
        frame.setContentPanel(makeWidgetPanel());
        frame.updateMinimumSizeFromContents();
        frame.layout();
        frame.pack();

        frame.setUseStandin(true);
        frame.setOpacity(1f);
        //frame.setLocationRelativeTo(_canvas.getCanvasRenderer().getCamera());
        frame.setName("sample");

        hud = new UIHud();
        hud.add(frame);

        getSpacetime().getFace().add(hud);

//        System.out.println("video: " + getSpacetime().getVideo());
//        System.out.println("video: " + getSpacetime().getInputPhy());
        hud.setupInput(getSpacetime().getVideo(), getSpacetime().getInputPhy(), getSpacetime().getInputLogic());

        add(new Repeat(0.01) {

            @Override protected void update(double t, double dt, Spatial s) {
                
                    hud.setupInput(getSpacetime().getVideo(), getSpacetime().getInputPhy(), getSpacetime().getInputLogic());
                
                counter += dt;
                frames++;
                if (counter > 1) {
                    final double fps = (frames / counter);
                    counter = 0;
                    frames = 0;
                    System.out.printf("%7.1f FPS\n", fps);
                    fpslabel.setText(fps + " FPS");
                    bar.setPercentFilled(t / 15);
                    bar.updateMinimumSizeFromContents();
                }

                hud.getLogicalLayer().checkTriggers(dt);
                hud.updateGeometricState(dt);
            }
        });

    }

//    @Inject
//    public DemoArdorUI(final LogicalLayer layer, final FrameHandler frameWork, final Timer timer) {
//        super(layer, frameWork);
//        this.timer = timer;
//    }
    private UIPanel makeWidgetPanel() {

        final UIPanel panel = new UIPanel();
        panel.setForegroundColor(ColorRGBA.DARK_GRAY);
        panel.setLayout(new BorderLayout());

        final UIButton button = new UIButton("Button A");
        final Texture tex = TextureManager.load("images/ardor3d_white_256.jpg", Texture.MinificationFilter.Trilinear,
            Format.GuessNoCompression, false);
        button.setIcon(new SubTex(tex));
        button.setIconDimensions(new Dimension(26, 26));
        button.setGap(10);
        button.setLayoutData(BorderLayoutData.NORTH);
        button.setTooltipText("This is a tooltip!");
        panel.add(button);

        final RowLayout rowLay = new RowLayout(false, false, false);
        final UIPanel centerPanel = new UIPanel(rowLay);
        centerPanel.setLayoutData(BorderLayoutData.CENTER);
        panel.add(centerPanel);

        final UICheckBox check1 = new UICheckBox("Hello");
        check1.setSelected(true);
        check1.setEnabled(false);
        centerPanel.add(check1);
        final UICheckBox check2 = new UICheckBox("World");
        centerPanel.add(check2);

        final ButtonGroup group = new ButtonGroup();
        final UIRadioButton radio1 = new UIRadioButton("option A");
        radio1.setGroup(group);
        centerPanel.add(radio1);
        final UIRadioButton radio2 = new UIRadioButton("option B");
        radio2.setGroup(group);
        centerPanel.add(radio2);

        bar = new UIProgressBar("Loading: ", true);
        bar.setPercentFilled(0);
        bar.setLocalComponentWidth(250);
        bar.setMaximumContentWidth(bar.getContentWidth());
        centerPanel.add(bar);

        fpslabel = new UILabel("FPS");
        fpslabel.setLayoutData(BorderLayoutData.SOUTH);
        panel.add(fpslabel);
        return panel;
    }
//    protected void updateLogicalLayer(final ReadOnlyTimer timer) {
//        hud.getLogicalLayer().checkTriggers(timer.getTimePerFrame());
//    }
//
//    @Override
//    protected void renderRoot(final Renderer renderer) {
//        super.renderRoot(renderer);
//        renderer.renderBuckets();
//        renderer.draw(hud);
//    }
    private double counter = 0;
    private int frames = 0;
//    @Override
//    protected void updateWindow(final ReadOnlyTimer timer) {
//        counter += timer.getTimePerFrame();
//        frames++;
//        if (counter > 1) {
//            final double fps = (frames / counter);
//            counter = 0;
//            frames = 0;
//            System.out.printf("%7.1f FPS\n", fps);
//            fpslabel.setText(fps + " FPS");
//            bar.setPercentFilled(timer.getTimeInSeconds() / 15);
//            bar.updateMinimumSizeFromContents();
//        }
//        hud.updateGeometricState(timer.getTimePerFrame());
//    }
}
