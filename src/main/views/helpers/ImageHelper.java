import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.AlphaComposite;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Set of static functions allowing to manipulate images
 *
 * @author Adrien Krähenbühl
 */
public class ImageHelper {
	/**
	 *  Generate a new image from a background image and a foreground image
	 *
	 * @param backgroundPath is the path of the background image
	 * @param foregroundPaths is the list of path of the other images
	 * @return an image combining the foreground image over the background image
	 */
	public static BufferedImage merge(String backgroundPath, String... foregroundPaths ) throws IOException {
		BufferedImage image1 = ImageIO.read(new File(backgroundPath));
		BufferedImage mergedImage = new BufferedImage( image1.getWidth(), image1.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = mergedImage.createGraphics();
		g2d.drawImage(image1, 0, 0, null);
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
		for ( String path : foregroundPaths ) {
			BufferedImage image2 = ImageIO.read(new File(path));
			g2d.drawImage(image2, 0, 0, null);
		}
		g2d.dispose();
		return mergedImage;
	}

	/**
	 *  Rotate an original image from the center by a defined angle.
	 *  The original image MUST HAVE the same width and height.
	 *
	 * @param original is the input image to rotate
	 * @param angle is the angle of rotation in radian
	 * @return a new image representing the original image rotated of angle radian from its center.
	 */
	public static BufferedImage rotate( final BufferedImage original, double angle ) throws IllegalArgumentException {
		if ( original.getWidth() != original.getHeight() )
			throw new IllegalArgumentException("Original image must have same width and height.");
		BufferedImage rotated = new BufferedImage(original.getWidth(), original.getHeight(), original.getType());
		Graphics2D graphic = rotated.createGraphics();
		graphic.rotate(angle, original.getWidth()/2., original.getHeight()/2.);
		graphic.drawRenderedImage(original, null);
		graphic.dispose();
		return rotated;
	}

	/**
	 *  Rotate an original image from the center by 90 degrees in clockwise
	 *
	 * @param original is the input image to rotate
	 * @return a new image representing the original image rotated by 90 degrees from its center.
	 */
	public static BufferedImage rotateClockwise( final BufferedImage original ) throws IllegalArgumentException {
		return rotate( original, 0.5*Math.PI );
	}

	/**
	 *  Rotate an original image from the center by 90 degrees in counterclockwise
	 *
	 * @param original is the input image to rotate
	 * @return a new image representing the original image rotated by 90 degrees from its center.
	 */
	public static BufferedImage rotateCounterClockwise( final BufferedImage original ) throws IllegalArgumentException {
		return rotate( original, 1.5*Math.PI );
	}
}
