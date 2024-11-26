package labyrinth.models.tiles;


import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TileTest {
    private Tile angledTile;
    private Tile straightTile;
    private Tile tShapedTile;

    @BeforeEach
    public void setUp() {
        angledTile = new AngledTile();
        straightTile = new StraightTile();
        tShapedTile = new TShapedTile();
    }

    @Test
    public void testAngledTileCreation() {
        assertNotNull(angledTile);
        assertEquals(angledTile.getType(), "angled");
    }

    @Test
    public void testStraightTileCreation() {
        assertNotNull(straightTile);
        assertEquals(straightTile.getType(), "straight");
    }

    @Test
    public void testTShapedTileCreation() {
        assertNotNull(tShapedTile);
        assertEquals(tShapedTile.getType(), "t-shaped");
    }

    @Test
    public void testTreasureManagement() {
        angledTile.setTreasure(5);
        assertEquals(angledTile.getTreasure(), 5);

        angledTile.setTreasure(0); // Assume that 0 means no treasure
        assertEquals(angledTile.getTreasure(), 0);
        assertEquals(angledTile.checkIfTreasure(), 0);
    }

    @Test
    public void testMovementCapabilities() {
        assertTrue(angledTile.checkIfMoves()); // Assuming default is true
        angledTile.initOrientation(); // Setting orientation
        assertEquals(angledTile.getOpenSides().getSides().get(2), true); // Check a specific side
    }
}
