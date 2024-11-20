package labyrinth.models.geometry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PositionTest {

    private Position position;

    @BeforeEach
    public void setUp() {
        position = new Position(5, 10);
    }

    @Test
    public void testGetX() {
        assertEquals(5, position.getX());
    }

    @Test
    public void testGetY() {
        assertEquals(10, position.getY());
    }

    @Test
    public void testSetPosition() {
        position.setPosition(15, 20);
        assertEquals(15, position.getX());
        assertEquals(20, position.getY());
    }

    @Test
    public void testToString() {
        assertEquals("(5, 10)", position.toString());
    }
}
