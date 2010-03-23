package automenta.spacenet.space.widget.text;

import automenta.spacenet.space.SpaceState;
import automenta.spacenet.space.geom.Box;
import automenta.spacenet.space.geom.Rect;
import automenta.spacenet.space.geom.Rect.RectShape;
import automenta.spacenet.space.geom.text3d.Font3D;
import automenta.spacenet.space.geom.text3d.Text3D;
import automenta.spacenet.space.surface.ColorSurface;
import automenta.spacenet.space.widget.panel.DefaultPanelModel;
import automenta.spacenet.space.widget.panel.Panel;
import automenta.spacenet.space.widget.panel.PanelModel;
import automenta.spacenet.var.physical.Color;
import automenta.spacenet.var.scalar.IntVar;
import automenta.spacenet.var.string.StringVar;
import automenta.spacenet.var.string.StringVar.IfStringChanges;
import automenta.spacenet.var.string.WordWrap;
import com.ardor3d.scenegraph.Spatial;
import java.util.Arrays;

public class TextPanel extends Panel implements IfStringChanges {

    private final StringVar text;
    private IntVar maxCharsPerLine = new IntVar(0);
    private final double textDZ = 0.02;
    private Rect lineRect;
    private final Font3D font;
    private SpaceState defaultState = new ColorSurface(Color.Black);

    //TODO organize these constructors
    
    public TextPanel(PanelModel model, Font3D font, String text) {
        this(model, font, new StringVar(text));
    }

    public TextPanel(Font3D font, String text) {
        this(new DefaultPanelModel(), font, new StringVar(text));
    }

    public TextPanel(PanelModel panelModel, Font3D font, StringVar text) {
        super(panelModel);
        this.font = font;
        this.text = text;
    }

    public TextPanel(Font3D font3d, String text, int maxLineChars) {
        this(font3d, new StringVar(text), maxLineChars);
    }

    public TextPanel(Font3D font, StringVar str, int maxLineChars) {
        this(new DefaultPanelModel(), font, str);
        maxCharsPerLine.set(maxLineChars);
    }

    public StringVar getText() {
        return text;
    }

    @Override
    protected void afterAttached(Spatial newParent) {
        super.afterAttached(newParent);
        text.add(this);
        updateTextPanel();
    }

    @Override
    protected void beforeDetached(Spatial parent) {
        text.remove(this);
        super.beforeDetached(parent);
    }

    @Override
    public void onStringChange(StringVar s) {
        if (s == text) {
            updateTextPanel();
        }
    }

    public IntVar getMaxCharsPerLine() {
        return maxCharsPerLine;
    }

    public int getCharsPerLine() {
        int i = getMaxCharsPerLine().i();
        if (i == 0) {
            //TODO auto-compute best
            return 40;
        }
        return i;
    }

    protected void updateTextPanel() {
        if (lineRect != null) {
            remove(lineRect);
        }

        lineRect = null;

        if (getText().length() == 0) {
            return;
        }

        int lineChars = getCharsPerLine();
        lineChars = Math.min(lineChars, getText().length());

        String[] lineStrings = WordWrap.wrapStringToArray(getText().s(), lineChars);


        double lineAspect = 1.6 / ((double) lineChars);

        int numLines = lineStrings.length;
        if (numLines == 0) {
            return;
        }

        lineRect = newTextLinesRect();
        lineRect.moveDZ(textDZ);

        double y = 0.5;

        double lineHeight = 1.0 / ((double) numLines);

        Box[] textRects = new Box[numLines];

        for (int i = 0; i < numLines; i++) {
            Text3D t3 = new Text3D(font, lineStrings[i]);
            textRects[i] = t3;
            t3.move(0, y, 0);
            t3.add(getTextState(lineStrings[i]));
            double widthFraction = ((double)lineStrings[i].length()) / ((double)lineChars);
            t3.aspectXY(1.0 / widthFraction);
            t3.scale(1.0, 1.0 / lineHeight, 1.0);
            lineRect.add(t3);
            y -= lineHeight;
        }

        //double paragraphAspect = lineAspect * ((double)numLines);
        //System.out.println("line aspect=" + lineAspect + " , pgh aspect=" + paragraphAspect);

        add(lineRect);
    }

    public SpaceState getTextState(String line) {
        return defaultState;
    }

    protected Rect newTextLinesRect() {
        return new Rect(RectShape.Empty);
    }
}
