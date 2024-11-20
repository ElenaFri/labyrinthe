package labyrinth.models.geometry;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SidesTest {

    private Sides sides;

    @BeforeEach
    public void setUp() {
        sides = new Sides();
    }

    @Test
    public void testInitialState() {
        for (int i = 0; i < 4; i++) {
            assertFalse(sides.getSides().get(i), "Side " + i + " should be closed initially.");
        }
    }

    @Test
    public void testSetSideValid() {
        sides.setSide(0, true);
        assertTrue(sides.getSides().get(0), "Top side should be open.");

        sides.setSide(1, true);
        assertTrue(sides.getSides().get(1), "Right side should be open.");

        sides.setSide(2, false);
        assertFalse(sides.getSides().get(2), "Bottom side should be closed.");

        sides.setSide(3, false);
        assertFalse(sides.getSides().get(3), "Left side should be closed.");
    }

    @Test
    public void testSetSideInvalidIndex() {
        assertThrows(IllegalArgumentException.class, () -> sides.setSide(-1, true));
    }

    @Test
    public void testSetSideIndexTooHigh() {
        assertThrows(IllegalArgumentException.class, () -> sides.setSide(4, true));
    }

    @Test
    public void testSetSideWhenNotInitialized() {
        sides.setSide(0, true);
    }
}
