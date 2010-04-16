/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.spacenet.space.surface;

import automenta.spacenet.space.SpaceState;
import com.ardor3d.image.Texture;
import com.ardor3d.image.Texture2D;
import com.ardor3d.image.TextureStoreFormat;
import com.ardor3d.image.util.AWTImageLoader;
import com.ardor3d.renderer.state.TextureState;
import com.ardor3d.scenegraph.Spatial;
import com.ardor3d.util.TextureManager;
import com.ardor3d.util.resource.ResourceSource;
import com.ardor3d.util.resource.URLResourceSource;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

/**
 *
 * @author seh
 */
public class BitmapSurface extends TextureState implements SpaceState {

    static {
        AWTImageLoader.registerLoader();
    }

    private URL url;
    private Texture texture;
    private double pixelHeight;
    private double pixelWidth;

    public BitmapSurface(URL url) {
        super();
        setUrl(url);
    }

    public BitmapSurface(String url) throws MalformedURLException {
        this(new URL(url));
    }

    @Override
    public void apply(Spatial s) {
        s.setRenderState(this);
    }

    @Override
    public void unapply(Spatial s) {
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
        texture = new Texture2D();

        try {
            setTexture(TextureManager.load(new URLResourceSource(getUrl()), Texture.MinificationFilter.BilinearNoMipMaps, TextureStoreFormat.GuessNoCompressedFormat, true));
            getTexture().setWrap(Texture.WrapMode.Repeat);

            pixelHeight = getTexture().getImage().getHeight();
            pixelWidth = getTexture().getImage().getWidth();
        }
        catch (Exception e) {
            Logger.getLogger(BitmapSurface.class.getName()).severe(e.toString());
            pixelHeight = 1;
            pixelWidth = 1;
        }


    }

    public double getPixelHeight() {
        return pixelHeight;
    }
    public double getPixelWidth() {
        return pixelWidth;
    }
}
