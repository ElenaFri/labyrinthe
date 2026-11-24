package labyrinth.model;

import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.Set;

import static labyrinth.model.Direction.DOWN;
import static labyrinth.model.Direction.LEFT;
import static labyrinth.model.Direction.RIGHT;
import static labyrinth.model.Direction.UP;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TileTypeTest {
    private static Set<Direction> openSides(TileType type, int orientation) {
        Tile tile = new Tile(type, orientation);
        Set<Direction> open = EnumSet.noneOf(Direction.class);
        for (Direction direction : Direction.values()) {
            if (tile.isOpen(direction)) {
                open.add(direction);
            }
        }
        return open;
    }

    @Test
    void angledTilesAreOpenOnTwoAdjacentSides() {
        assertEquals(Set.of(RIGHT, DOWN), openSides(TileType.ANGLED, 0));
        assertEquals(Set.of(DOWN, LEFT), openSides(TileType.ANGLED, 1));
        assertEquals(Set.of(LEFT, UP), openSides(TileType.ANGLED, 2));
        assertEquals(Set.of(UP, RIGHT), openSides(TileType.ANGLED, 3));
    }

    @Test
    void straightTilesAreOpenOnTwoOppositeSides() {
        assertEquals(Set.of(UP, DOWN), openSides(TileType.STRAIGHT, 0));
        assertEquals(Set.of(RIGHT, LEFT), openSides(TileType.STRAIGHT, 1));
        assertEquals(Set.of(UP, DOWN), openSides(TileType.STRAIGHT, 2));
        assertEquals(Set.of(RIGHT, LEFT), openSides(TileType.STRAIGHT, 3));
    }

    @Test
    void tShapedTilesAreOpenOnThreeSides() {
        assertEquals(Set.of(UP, RIGHT, LEFT), openSides(TileType.T_SHAPED, 0));
        assertEquals(Set.of(UP, RIGHT, DOWN), openSides(TileType.T_SHAPED, 1));
        assertEquals(Set.of(RIGHT, DOWN, LEFT), openSides(TileType.T_SHAPED, 2));
        assertEquals(Set.of(DOWN, LEFT, UP), openSides(TileType.T_SHAPED, 3));
    }

    @Test
    void rotatingATileFourTimesBringsItBack() {
        Tile tile = new Tile(TileType.ANGLED, 2);
        for (int i = 0; i < 4; i++) {
            tile.rotateClockwise();
        }
        assertEquals(2, tile.orientation());
    }

    @Test
    void directionsRotateClockwise() {
        assertEquals(DOWN, UP.opposite());
        assertEquals(RIGHT, UP.rotate(1));
        assertEquals(LEFT, UP.rotate(-1));
        assertEquals(UP, LEFT.rotate(1));
    }
}
