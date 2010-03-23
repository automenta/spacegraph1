package automenta.spacenet.space.geom.text2d;

import com.ardor3d.image.Image;
import com.ardor3d.renderer.state.TextureState;
import com.ardor3d.util.geom.BufferUtils;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.PixelGrabber;
import java.nio.ByteBuffer;
import java.util.logging.Logger;

public class TextureManagerExt {
    private static final Logger logger = Logger.getLogger(TextureManagerExt.class.getName());
    
    private static boolean createOnHeap = false;

    public static Image loadImage(BufferedImage image, boolean flipImage) {
        if (image == null) {
            return null;
        }
        boolean hasAlpha = hasAlpha(image);
        boolean grayscale = isGreyscale(image);
        BufferedImage tex;
        if (flipImage || !(image instanceof BufferedImage) || (((BufferedImage) image).getType() != BufferedImage.TYPE_BYTE_GRAY && (hasAlpha ? ((BufferedImage) image).getType() != BufferedImage.TYPE_4BYTE_ABGR : ((BufferedImage) image).getType() != BufferedImage.TYPE_3BYTE_BGR))) {
            try {
                tex = new BufferedImage(image.getWidth(null), image.getHeight(null), grayscale ? BufferedImage.TYPE_BYTE_GRAY : hasAlpha ? BufferedImage.TYPE_4BYTE_ABGR : BufferedImage.TYPE_3BYTE_BGR);
            } catch (IllegalArgumentException e) {
                logger.warning("Problem creating buffered Image: " + e.getMessage());
                return TextureState.getDefaultTextureImage();
            }
            image.getWidth(null);
            image.getHeight(null);
            if (image instanceof BufferedImage) {
                int imageWidth = image.getWidth(null);
                int[] tmpData = new int[imageWidth];
                int row = 0;
                BufferedImage bufferedImage = (BufferedImage) image;
                for (int y = image.getHeight(null) - 1; y >= 0; y--) {
                    bufferedImage.getRGB(0, flipImage ? row++ : y, imageWidth, 1, tmpData, 0, imageWidth);
                    tex.setRGB(0, y, imageWidth, 1, tmpData, 0, imageWidth);
                }
            }
        } else {
            tex = (BufferedImage) image;
        }
        // Get a pointer to the image memory
        byte[] data = (byte[]) tex.getRaster().getDataElements(0, 0, tex.getWidth(), tex.getHeight(), null);
        ByteBuffer scratch = createOnHeap ? BufferUtils.createByteBufferOnHeap(data.length) : BufferUtils.createByteBuffer(data.length);
        scratch.clear();
        scratch.put(data);
        scratch.flip();
        Image textureImage = new Image();
        textureImage.setFormat(grayscale ? Image.Format.Alpha8 : hasAlpha ? Image.Format.RGBA8 : Image.Format.RGB8);
        textureImage.setWidth(tex.getWidth());
        textureImage.setHeight(tex.getHeight());
        textureImage.setData(scratch);
        return textureImage;
    }

    /**
     * <code>hasAlpha</code> returns true if the specified image has
     * transparent pixels
     *
     * @param image
     *            Image to check
     * @return true if the specified image has transparent pixels
     */
    public static boolean hasAlpha(java.awt.Image image) {
        if (null == image) {
            return false;
        }
        if (image instanceof BufferedImage) {
            BufferedImage bufferedImage = (BufferedImage) image;
            return bufferedImage.getColorModel().hasAlpha();
        }
        PixelGrabber pixelGrabber = new PixelGrabber(image, 0, 0, 1, 1, false);
        try {
            pixelGrabber.grabPixels();
            ColorModel colorModel = pixelGrabber.getColorModel();
            if (colorModel != null) {
                return colorModel.hasAlpha();
            }
            return false;
        } catch (InterruptedException e) {
            logger.warning("Unable to determine alpha of image: " + image);
        }
        return false;
    }

    /**
     * <code>isGreyscale</code> returns true if the specified image is greyscale.
     *
     * @param image
     *            Image to check
     * @return true if the specified image is greyscale.
     */
    public static boolean isGreyscale(java.awt.Image image) {
        if (null == image) {
            return false;
        }
        if (image instanceof BufferedImage) {
            BufferedImage bufferedImage = (BufferedImage) image;
            return bufferedImage.getColorModel().getNumComponents() == 1;
        }
        PixelGrabber pixelGrabber = new PixelGrabber(image, 0, 0, 1, 1, false);
        try {
            pixelGrabber.grabPixels();
            ColorModel colorModel = pixelGrabber.getColorModel();
            if (colorModel != null) {
                return colorModel.getNumComponents() == 1;
            }
            return false;
        } catch (InterruptedException e) {
            logger.warning("Unable to determine if image is greyscale: " + image);
        }
        return false;
    }
}
