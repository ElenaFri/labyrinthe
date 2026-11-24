package labyrinth;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

/**
 * Gives access to the game resources (images, sounds...).
 * <p>
 * A resource such as <code>res/img/tiles/tile_0.png</code> is looked for in the class path first (this is the case
 * when the game is packaged in a jar), then in the working directory.
 */
public final class Resources {
    private Resources() {
    }

    /**
     * Opens a resource.
     *
     * @param path : path of the resource, relative to the root of the project
     * @return a stream to read it; it must be closed by the caller
     * @throws IOException if the resource does not exist
     */
    public static InputStream open(String path) throws IOException {
        InputStream stream = Resources.class.getResourceAsStream("/" + path);
        return stream != null ? stream : new FileInputStream(path);
    }

    /**
     * Loads an image.
     *
     * @param path : path of the image, relative to the root of the project
     * @return the image
     * @throws UncheckedIOException if the image cannot be read: the game cannot do anything without its images
     */
    public static BufferedImage image(String path) {
        try (InputStream stream = open(path)) {
            BufferedImage image = ImageIO.read(stream);
            if (image == null) {
                throw new IOException("Unsupported image format");
            }
            return image;
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot load the image " + path, e);
        }
    }
}
