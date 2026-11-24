package labyrinth.view;

import labyrinth.model.Direction;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests of the drawn arrows. They must be run from the root of the project, where the pictures are.
 */
class SpritesTest {
    private static Sprites sprites;

    @BeforeAll
    static void loadImages() {
        System.setProperty("java.awt.headless", "true");
        sprites = new Sprites(new Images());
    }

    private static boolean hasPixels(BufferedImage image) {
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                if (image.getRGB(x, y) >>> 24 != 0) {
                    return true;
                }
            }
        }
        return false;
    }

    private static int[] pixels(BufferedImage image) {
        return image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
    }

    @Test
    void everyArrowIsDrawnInTheSizeOfTheArrowButtons() {
        for (Direction direction : Direction.values()) {
            for (Sprites.ArrowState state : Sprites.ArrowState.values()) {
                BufferedImage arrow = sprites.arrow(direction, state);
                assertEquals(Layout.ARROW_SIZE, arrow.getWidth());
                assertEquals(Layout.ARROW_SIZE, arrow.getHeight());
                assertTrue(hasPixels(arrow), direction + " " + state);
            }
        }
    }

    @Test
    void theStatesLookDifferent() {
        for (Direction direction : Direction.values()) {
            int[] normal = pixels(sprites.arrow(direction, Sprites.ArrowState.NORMAL));
            int[] highlighted = pixels(sprites.arrow(direction, Sprites.ArrowState.HIGHLIGHTED));
            int[] disabled = pixels(sprites.arrow(direction, Sprites.ArrowState.DISABLED));
            int[] forbidden = pixels(sprites.arrow(direction, Sprites.ArrowState.FORBIDDEN));
            assertFalse(Arrays.equals(normal, highlighted));
            assertFalse(Arrays.equals(normal, disabled));
            assertFalse(Arrays.equals(disabled, forbidden));
        }
    }

    @Test
    void theArrowsPointInTheirDirection() {
        // The tip is the end of the arrow which is far from the centre of the button, the notch the one near to it:
        // the pixel just beyond the tip is empty, the one just before the notch is empty too, the body is full.
        int middle = Layout.ARROW_SIZE / 2;
        assertTrue(opaque(sprites.arrow(Direction.UP, Sprites.ArrowState.NORMAL), middle, 12));
        assertTrue(opaque(sprites.arrow(Direction.DOWN, Sprites.ArrowState.NORMAL), middle, Layout.ARROW_SIZE - 13));
        assertTrue(opaque(sprites.arrow(Direction.LEFT, Sprites.ArrowState.NORMAL), 12, middle));
        assertTrue(opaque(sprites.arrow(Direction.RIGHT, Sprites.ArrowState.NORMAL), Layout.ARROW_SIZE - 13, middle));
        assertFalse(opaque(sprites.arrow(Direction.UP, Sprites.ArrowState.NORMAL), middle, Layout.ARROW_SIZE - 3));
        assertFalse(opaque(sprites.arrow(Direction.RIGHT, Sprites.ArrowState.NORMAL), 3, middle));
    }

    private static boolean opaque(BufferedImage image, int x, int y) {
        return image.getRGB(x, y) >>> 24 > 200;
    }
}
