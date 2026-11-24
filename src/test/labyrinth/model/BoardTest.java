package labyrinth.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BoardTest {
    private static Board newBoard() {
        return new Board(new Random(42));
    }

    private static List<Tile> allTiles(Board board) {
        List<Tile> tiles = new ArrayList<>();
        for (int row = 0; row < Board.SIZE; row++) {
            for (int col = 0; col < Board.SIZE; col++) {
                tiles.add(board.tileAt(new Position(row, col)));
            }
        }
        tiles.add(board.spareTile());
        return tiles;
    }

    @Test
    void hasTheTilesOfTheBoardGame() {
        Map<TileType, Integer> counts = new EnumMap<>(TileType.class);
        for (Tile tile : allTiles(newBoard())) {
            counts.merge(tile.type(), 1, Integer::sum);
        }
        assertEquals(20, counts.get(TileType.ANGLED));
        assertEquals(12, counts.get(TileType.STRAIGHT));
        assertEquals(18, counts.get(TileType.T_SHAPED));
    }

    @Test
    void everyTileIsDistinct() {
        Set<Tile> tiles = new HashSet<>(allTiles(newBoard()));
        assertEquals(50, tiles.size());
    }

    @Test
    void cornersAreAnglesTurnedTowardsTheInside() {
        Board board = newBoard();
        int last = Board.SIZE - 1;
        Tile topLeft = board.tileAt(new Position(0, 0));
        Tile topRight = board.tileAt(new Position(0, last));
        Tile bottomRight = board.tileAt(new Position(last, last));
        Tile bottomLeft = board.tileAt(new Position(last, 0));
        assertTrue(topLeft.isOpen(Direction.RIGHT) && topLeft.isOpen(Direction.DOWN));
        assertTrue(topRight.isOpen(Direction.LEFT) && topRight.isOpen(Direction.DOWN));
        assertTrue(bottomRight.isOpen(Direction.LEFT) && bottomRight.isOpen(Direction.UP));
        assertTrue(bottomLeft.isOpen(Direction.RIGHT) && bottomLeft.isOpen(Direction.UP));
    }

    @Test
    void fixedTilesOnTheBorderFaceTheInside() {
        Board board = newBoard();
        int last = Board.SIZE - 1;
        assertFalse(board.tileAt(new Position(0, 2)).isOpen(Direction.UP));
        assertFalse(board.tileAt(new Position(last, 2)).isOpen(Direction.DOWN));
        assertFalse(board.tileAt(new Position(2, 0)).isOpen(Direction.LEFT));
        assertFalse(board.tileAt(new Position(2, last)).isOpen(Direction.RIGHT));
        assertEquals(TileType.T_SHAPED, board.tileAt(new Position(4, 4)).type());
    }

    @Test
    void spreadsTheTwentyFourTreasuresOutsideOfTheCorners() {
        Board board = newBoard();
        Set<Integer> found = new HashSet<>();
        for (int treasure = 0; treasure < Board.TREASURE_COUNT; treasure++) {
            Position position = board.positionOfTreasure(treasure).orElseThrow();
            found.add(treasure);
            boolean corner = (position.row() % 6 == 0) && (position.col() % 6 == 0);
            assertFalse(corner);
        }
        assertEquals(Board.TREASURE_COUNT, found.size());
        assertFalse(board.spareTile().hasTreasure());
        assertTrue(board.positionOfTreasure(Board.TREASURE_COUNT).isEmpty());
    }

    @Test
    void onlyOddLinesCanBePushed() {
        assertFalse(Board.isPushable(0));
        assertTrue(Board.isPushable(1));
        assertFalse(Board.isPushable(2));
        assertTrue(Board.isPushable(3));
        assertTrue(Board.isPushable(5));
        assertFalse(Board.isPushable(6));
        assertThrows(IllegalArgumentException.class, () -> newBoard().push(Direction.RIGHT, 2));
    }

    @Test
    void pushingARowToTheRightShiftsItAndEjectsTheLastTile() {
        Board board = newBoard();
        Tile spare = board.spareTile();
        Tile[] before = new Tile[Board.SIZE];
        for (int col = 0; col < Board.SIZE; col++) {
            before[col] = board.tileAt(new Position(3, col));
        }

        board.push(Direction.RIGHT, 3);

        assertSame(spare, board.tileAt(new Position(3, 0)));
        for (int col = 1; col < Board.SIZE; col++) {
            assertSame(before[col - 1], board.tileAt(new Position(3, col)));
        }
        assertSame(before[Board.SIZE - 1], board.spareTile());
    }

    @Test
    void pushingInEveryDirectionMovesTheRightLine() {
        for (Direction direction : Direction.values()) {
            Board board = newBoard();
            Tile spare = board.spareTile();
            board.push(direction, 5);
            Position entry = switch (direction) {
                case RIGHT -> new Position(5, 0);
                case LEFT -> new Position(5, 6);
                case DOWN -> new Position(0, 5);
                case UP -> new Position(6, 5);
            };
            assertSame(spare, board.tileAt(entry), direction.name());
        }
    }

    @Test
    void pushingBackRestoresTheBoard() {
        Board board = newBoard();
        List<Tile> before = allTiles(board);
        board.push(Direction.DOWN, 1);
        board.push(Direction.UP, 1);
        assertEquals(before, allTiles(board));
    }

    @Test
    void fixedTilesNeverMove() {
        Board board = newBoard();
        Tile corner = board.tileAt(new Position(0, 0));
        Tile fixedT = board.tileAt(new Position(2, 2));
        for (int index = 1; index < Board.SIZE; index += 2) {
            for (Direction direction : Direction.values()) {
                board.push(direction, index);
            }
        }
        assertSame(corner, board.tileAt(new Position(0, 0)));
        assertSame(fixedT, board.tileAt(new Position(2, 2)));
    }

    @Test
    void thingsLeavingTheBoardComeBackOnTheOtherSide() {
        Board board = newBoard();
        assertEquals(new Position(3, 4), board.positionAfterPush(new Position(3, 3), Direction.RIGHT, 3));
        assertEquals(new Position(3, 0), board.positionAfterPush(new Position(3, 6), Direction.RIGHT, 3));
        assertEquals(new Position(3, 6), board.positionAfterPush(new Position(3, 0), Direction.LEFT, 3));
        assertEquals(new Position(0, 1), board.positionAfterPush(new Position(6, 1), Direction.DOWN, 1));
        assertEquals(new Position(6, 1), board.positionAfterPush(new Position(0, 1), Direction.UP, 1));
        assertEquals(new Position(2, 2), board.positionAfterPush(new Position(2, 2), Direction.RIGHT, 3));
    }

    @Test
    void passagesAreSymmetricalAndStayOnTheBoard() {
        Board board = newBoard();
        for (int row = 0; row < Board.SIZE; row++) {
            for (int col = 0; col < Board.SIZE; col++) {
                Position from = new Position(row, col);
                for (Direction direction : Direction.values()) {
                    Position to = from.step(direction);
                    if (board.canPass(from, direction)) {
                        assertTrue(to.isOnBoard());
                        assertTrue(board.canPass(to, direction.opposite()));
                        assertTrue(board.connectedNeighbours(from).contains(to));
                    }
                }
            }
        }
    }
}
