package automenta.spacenet.run.old;

import automenta.spacenet.plugin.comm.Agent;
import automenta.spacenet.plugin.comm.Message;
import automenta.spacenet.run.DemoDefaults;
import automenta.spacenet.space.Repeat;
import automenta.spacenet.space.control.Zoomable;
import automenta.spacenet.space.geom.Rect;
import automenta.spacenet.space.geom.Rect.RectShape;
import automenta.spacenet.space.geom.layout.ColRect;
import automenta.spacenet.space.geom.layout.RowRect;
import automenta.spacenet.space.geom.text3d.Font3D;
import automenta.spacenet.space.geom.text3d.Text3D;
import automenta.spacenet.space.widget.BitmapRect;
import automenta.spacenet.space.widget.text.TextEditPanel;
import automenta.spacenet.space.widget.text.TextPanel;
import automenta.spacenet.space.widget3d.Window3D;
import automenta.spacenet.var.physical.Color;
import automenta.spacenet.var.string.StringVar;
import com.ardor3d.scenegraph.Spatial;
import org.neuroph.core.Connection;
import org.neuroph.core.Neuron;
import org.openrdf.model.URI;

public class DefaultObjectWindow3D extends Window3D implements Zoomable {

    public final static Font3D font3d = DemoDefaults.font; //new Font3D(new Font("Monospace", Font.PLAIN, 12), 1.0, true, false, false);
    double dz = 0.05;

    public DefaultObjectWindow3D(Object object) {
        super();

        scale(1,1,0.5);
        //FacesCameraBox fn = add(new FacesCameraBox());

        Rect backRect = add(new Rect(RectShape.Rect)).scale(0.9).moveDZ(0.55);
        backRect.color(getColor(object));

        Rect content = backRect.add(new Rect(RectShape.Empty)).moveDZ(dz).scale(0.9);

        Text3D textLabel = null;
        if (object instanceof Neuron) {
            initNeuron(content, (Neuron) object);
        } else if (object instanceof Connection) {
            initNeuronConnection(content, (Connection) object);
        } else if (object instanceof Message) {
            initMessage(content, (Message) object);
        } else if (object instanceof Agent) {
            initAgent(content, (Agent) object);
        } else if (object instanceof URI) {
            initURI(content, (URI) object);
        } else if (object instanceof StringVar) {
            initStringVar(content, (StringVar) object);
        } else {
            initString(content, object.toString());
        }

    }

    public int getMaxStringLength() {
        return 16;
    }

    public static Color getColor(Object o) {
        int h = o.getClass().hashCode();
        double hf = ((double) (h % 512)) / 512.0;
        return Color.hsb(hf, 0.9, 0.9);
    }

    @Override
    public void onZoomStart() {
        //System.out.println("netInput=" + getNeuron().getNetInput() + ", output=" + getNeuron().getOutput());
    }

    @Override
    public void onZoomStop() {
    }

    @Override
    public boolean isZoomable() {
        return true;
    }

    @Override
    public boolean isTangible() {
        return true;
    }

    private void initMessage(Rect content, Message m) {
        content.add(new TextPanel(font3d, m.text, 20)).scale(0.9);
    }

    private void initAgent(Rect content, Agent a) {
        content.add(new BitmapRect(a.imageURL)).span(-0.5, 0.5, 0.5, -0.4);
        content.add(new Text3D(font3d, a.id)).span(-0.5, -0.4, 0.5, -0.5).color(Color.Black);
    }

    private void initURI(Rect content, URI u) {
        content.add(new TextPanel(font3d, u.getLocalName())).scale(0.9);
    }

    private void initNeuronConnection(Rect content, final Connection c) {


        final Rect inRect = new Rect(RectShape.Rect);
        final Rect weightRect = new Rect(RectShape.Rect);

        content.add(new ColRect(0.05,
            inRect, weightRect));

        double neuronUpdatePeriod = 0.05;
        inRect.add(new Repeat(neuronUpdatePeriod) {

            @Override protected void update(double t, double dt, Spatial parent) {
                double input = 0.5 * (c.getInput() + 1.0);
                double weight = 0.5 * (c.getWeight().getValue() + 1.0);

                Color inColor = new Color(input, input, input);
                inRect.color(inColor);

                Color wtColor = new Color(weight, weight, weight);
                weightRect.color(wtColor);
            }
        });


    }

    private void initNeuron(Rect content, final Neuron n) {
        //String ioString = n.getInputConnections().size() + " / " + n.getOutConnections().size();
        double in = n.getInputConnections().size();
        double out = n.getOutConnections().size();
        double totalConnections = n.getInputConnections().size() + n.getOutConnections().size();
        Color ioColor = new Color(in / totalConnections, 0.0, out / totalConnections, 1.0);
        final Rect inRect = new Rect(RectShape.Empty);
        final Rect outRect = new Rect(RectShape.Ellipse);
        content.add(new ColRect(0.05,
            //new Text3D(font3d, ioString),
            new RowRect(inRect, outRect),
            new Rect(ioColor)));

        double neuronUpdatePeriod = 0.05;
        outRect.add(new Repeat(neuronUpdatePeriod) {

            @Override protected void update(double t, double dt, Spatial parent) {
                inRect.removeAll();

                Rect[] inPortRects = new Rect[n.getInputConnections().size()];
                int i = 0;
                for (Connection c : n.getInputConnections()) {
                    inPortRects[i] = new Rect(RectShape.Ellipse);
                    Color color = new Color(c.getInput(), c.getWeightedInput(), 0);
                    inPortRects[i].color(color);
                    i++;
                }

                inRect.add(new ColRect(0.01, inPortRects));

                double o = n.getOutput();
                Color outColor = new Color(o, o, o);
                outRect.color(outColor);
            }
        });
//            if (!n.hasInputConnections()) {
//                iconShape.color(Color.Purple);
//            } else if (n.getOutConnections().size() == 0) {
//                iconShape.color(Color.White);
//            } else {
//                iconShape.color(Color.Orange);
//            }
    }

    private void initStringVar(Rect content, StringVar s) {
        content.add(new TextEditPanel(font3d, s)).scale(0.9);
    }

    private void initString(Rect content, String s) {
        if (s.length() > getMaxStringLength()) {
            s = s.substring(0, getMaxStringLength());
        }
        content.add(new TextPanel(font3d, s).scale(0.9));
    }
}
