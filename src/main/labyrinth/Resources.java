package labyrinth;

import java.io.File;

/**
 * Locates the game resources (images, sounds...).
 * <p>
 * By default, resource paths such as <code>res/img/tiles/tile_0.png</code> are relative to the working
 * directory. The system property <code>labyrinth.base</code> can define a prefix to prepend to every path
 * (e.g. <code>/app/</code> when the game runs in a browser, where there is no working directory).
 */
public final class Resources {
    private static final String BASE = System.getProperty("labyrinth.base", "");

    private Resources() {
    }

    /**
     * Gives the path to use to access a resource.
     *
     * @param path : path of the resource, relative to the root of the project
     * @return the path, prefixed with the base directory if any
     */
    public static String path(String path) {
        return BASE + path;
    }

    /**
     * Gives the file of a resource.
     *
     * @param path : path of the resource, relative to the root of the project
     * @return the corresponding file
     */
    public static File file(String path) {
        return new File(path(path));
    }
}
