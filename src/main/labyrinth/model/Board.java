package labyrinth.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * The 7x7 board of the labyrinth, and the spare tile used to push its rows and columns.
 * <p>
 * Tiles are fixed on the even rows and columns; only the odd rows and columns can be pushed.
 * <p>
 * The board can be read by anybody, but only the {@link Game} can change it.
 */
public final class Board {
    /**
     * Number of rows and of columns.
     */
    public static final int SIZE = 7;
    /**
     * Number of treasures.
     */
    public static final int TREASURE_COUNT = 24;

    private static final int MOVABLE_STRAIGHT_COUNT = 12;
    private static final int MOVABLE_T_SHAPED_COUNT = 6;
    private static final int MOVABLE_ANGLED_COUNT = 16;

    private final Tile[][] tiles = new Tile[SIZE][SIZE];
    private Tile spare;

    /**
     * Builds a board with the standard set of 50 tiles: the 16 fixed tiles are put in their places, the
     * 34 movable ones are shuffled (one of them being left aside as the spare tile), then the 24 treasures
     * are spread on tiles other than the starting corners.
     *
     * @param random : source of randomness
     */
    Board(Random random) {
        placeFixedTiles(random);
        placeMovableTiles(random);
        placeTreasures(random);
    }

    /**
     * Gives the tile at a position.
     *
     * @param position : a position on the board
     * @return the tile at this position
     */
    public Tile tileAt(Position position) {
        return tiles[position.row()][position.col()];
    }

    /**
     * Gives the tile which is out of the board, ready to be pushed in.
     *
     * @return the tile which is out of the board, ready to be pushed in
     */
    public Tile spareTile() {
        return spare;
    }

    /**
     * Tells whether a row or a column can be pushed.
     *
     * @param index : the index of a row or of a column
     * @return true if this row or column can be pushed
     */
    public static boolean isPushable(int index) {
        return index > 0 && index < SIZE - 1 && index % 2 == 1;
    }

    /**
     * Pushes the spare tile in a row or a column. The tile which comes out at the other end becomes the spare tile.
     *
     * @param direction : the direction in which the tiles move ({@link Direction#RIGHT} and {@link Direction#LEFT}
     *                  push a row, {@link Direction#DOWN} and {@link Direction#UP} push a column)
     * @param index     : the index of the row or of the column; it must be {@linkplain #isPushable(int) pushable}
     * @throws IllegalArgumentException if the row or column cannot be pushed
     */
    void push(Direction direction, int index) {
        if (!isPushable(index)) {
            throw new IllegalArgumentException("Cannot push the line " + index);
        }
        Position entry = entryOf(direction, index);
        Position[] line = new Position[SIZE];
        line[0] = entry;
        for (int i = 1; i < SIZE; i++) {
            line[i] = line[i - 1].step(direction);
        }

        Tile ejected = tileAt(line[SIZE - 1]);
        for (int i = SIZE - 1; i > 0; i--) {
            tiles[line[i].row()][line[i].col()] = tileAt(line[i - 1]);
        }
        tiles[entry.row()][entry.col()] = spare;
        spare = ejected;
    }

    /**
     * Tells where something standing at a position goes when a row or a column is pushed. What leaves the board
     * on one side comes back on the opposite side.
     *
     * @param position  : the position of something on the board
     * @param direction : the direction of the push
     * @param index     : the index of the pushed row or column
     * @return the new position (the same one if the row or column of the push does not contain it)
     */
    public Position positionAfterPush(Position position, Direction direction, int index) {
        boolean horizontal = direction == Direction.LEFT || direction == Direction.RIGHT;
        boolean concerned = horizontal ? position.row() == index : position.col() == index;
        if (!concerned) {
            return position;
        }
        Position next = position.step(direction);
        return new Position(Math.floorMod(next.row(), SIZE), Math.floorMod(next.col(), SIZE));
    }

    /**
     * Tells whether one can go from a tile to the next one in a direction.
     *
     * @param from      : a position on the board
     * @param direction : the direction in which one wants to go
     * @return true if the position next to <code>from</code> in this direction is on the board and connected to it
     */
    public boolean canPass(Position from, Direction direction) {
        Position to = from.step(direction);
        return to.isOnBoard() && tileAt(from).isOpen(direction) && tileAt(to).isOpen(direction.opposite());
    }

    /**
     * Lists the neighbours of a tile to which one can directly go.
     *
     * @param from : a position on the board
     * @return the neighbouring positions to which one can directly go
     */
    public List<Position> connectedNeighbours(Position from) {
        List<Position> neighbours = new ArrayList<>(4);
        for (Direction direction : Direction.values()) {
            if (canPass(from, direction)) {
                neighbours.add(from.step(direction));
            }
        }
        return neighbours;
    }

    /**
     * Finds where a treasure is.
     *
     * @param treasure : the number of a treasure
     * @return the position of the tile bearing this treasure, or nothing if it lies on the spare tile
     */
    public Optional<Position> positionOfTreasure(int treasure) {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (tiles[row][col].treasure() == treasure) {
                    return Optional.of(new Position(row, col));
                }
            }
        }
        return Optional.empty();
    }

    private static Position entryOf(Direction direction, int index) {
        return switch (direction) {
            case RIGHT -> new Position(index, 0);
            case LEFT -> new Position(index, SIZE - 1);
            case DOWN -> new Position(0, index);
            case UP -> new Position(SIZE - 1, index);
        };
    }

    private void placeFixedTiles(Random random) {
        // The four corners, turned towards the inside of the board.
        int last = SIZE - 1;
        put(0, 0, TileType.ANGLED, 0);
        put(0, last, TileType.ANGLED, 1);
        put(last, last, TileType.ANGLED, 2);
        put(last, 0, TileType.ANGLED, 3);

        // The twelve fixed "T": the ones on the border face the inside of the board, the others are random.
        for (int row = 0; row < SIZE; row += 2) {
            for (int col = 0; col < SIZE; col += 2) {
                if (tiles[row][col] != null) {
                    continue;
                }
                int orientation;
                if (row == 0) {
                    orientation = 2;
                } else if (row == last) {
                    orientation = 0;
                } else if (col == 0) {
                    orientation = 1;
                } else if (col == last) {
                    orientation = 3;
                } else {
                    orientation = random.nextInt(4);
                }
                put(row, col, TileType.T_SHAPED, orientation);
            }
        }
    }

    private void placeMovableTiles(Random random) {
        List<Tile> movable = new ArrayList<>();
        addTiles(movable, TileType.STRAIGHT, MOVABLE_STRAIGHT_COUNT, random);
        addTiles(movable, TileType.T_SHAPED, MOVABLE_T_SHAPED_COUNT, random);
        addTiles(movable, TileType.ANGLED, MOVABLE_ANGLED_COUNT, random);
        Collections.shuffle(movable, random);

        int next = 0;
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (tiles[row][col] == null) {
                    tiles[row][col] = movable.get(next++);
                }
            }
        }
        spare = movable.get(next);
    }

    private void placeTreasures(Random random) {
        List<Position> candidates = new ArrayList<>();
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                boolean corner = (row == 0 || row == SIZE - 1) && (col == 0 || col == SIZE - 1);
                if (!corner) {
                    candidates.add(new Position(row, col));
                }
            }
        }
        Collections.shuffle(candidates, random);
        for (int treasure = 0; treasure < TREASURE_COUNT; treasure++) {
            tileAt(candidates.get(treasure)).setTreasure(treasure);
        }
    }

    private void put(int row, int col, TileType type, int orientation) {
        tiles[row][col] = new Tile(type, orientation);
    }

    private static void addTiles(List<Tile> tiles, TileType type, int count, Random random) {
        for (int i = 0; i < count; i++) {
            tiles.add(new Tile(type, random.nextInt(4)));
        }
    }
}
