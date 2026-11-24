package labyrinth.model;

/**
 * The four sides of a tile, in clockwise order starting from the top.
 */
public enum Direction {
    UP(-1, 0),
    RIGHT(0, 1),
    DOWN(1, 0),
    LEFT(0, -1);

    private static final Direction[] VALUES = values();

    private final int rowStep;
    private final int colStep;

    Direction(int rowStep, int colStep) {
        this.rowStep = rowStep;
        this.colStep = colStep;
    }

    /**
     * @return the row offset to apply to go one tile in this direction
     */
    public int rowStep() {
        return rowStep;
    }

    /**
     * @return the column offset to apply to go one tile in this direction
     */
    public int colStep() {
        return colStep;
    }

    /**
     * @return the opposite direction
     */
    public Direction opposite() {
        return rotate(2);
    }

    /**
     * Rotates this direction clockwise.
     *
     * @param quarterTurns : number of quarter turns (may be negative)
     * @return the resulting direction
     */
    public Direction rotate(int quarterTurns) {
        return VALUES[Math.floorMod(ordinal() + quarterTurns, VALUES.length)];
    }
}
