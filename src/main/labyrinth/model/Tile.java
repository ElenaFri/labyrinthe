package labyrinth.model;

/**
 * A tile of the labyrinth: a shape, an orientation and possibly a treasure.
 * <p>
 * Tiles can only be changed by the model (the {@link Game} turns the spare tile, the {@link Board} deals the
 * treasures), so that the rules cannot be bypassed.
 */
public final class Tile {
    /**
     * Value of {@link #treasure()} for a tile without any treasure.
     */
    public static final int NO_TREASURE = -1;

    private final TileType type;
    private int orientation;
    private int treasure = NO_TREASURE;

    /**
     * Creates a tile.
     *
     * @param type        : shape of the tile
     * @param orientation : number of clockwise quarter turns (taken modulo 4)
     */
    Tile(TileType type, int orientation) {
        this.type = type;
        this.orientation = Math.floorMod(orientation, 4);
    }

    /**
     * Gives the shape of the tile.
     *
     * @return the shape of the tile
     */
    public TileType type() {
        return type;
    }

    /**
     * Gives the orientation of the tile.
     *
     * @return the number of clockwise quarter turns applied to the tile (0 to 3)
     */
    public int orientation() {
        return orientation;
    }

    /**
     * Turns the tile by a quarter turn, clockwise.
     */
    void rotateClockwise() {
        orientation = (orientation + 1) % 4;
    }

    /**
     * Tells whether one can go through a side of the tile.
     *
     * @param side : a side of the tile
     * @return true if one can go through this side
     */
    public boolean isOpen(Direction side) {
        return type.isOpen(side, orientation);
    }

    /**
     * Gives the treasure lying on the tile.
     *
     * @return the number of the treasure lying on the tile, or {@link #NO_TREASURE}
     */
    public int treasure() {
        return treasure;
    }

    /**
     * Tells whether there is a treasure on the tile.
     *
     * @return true if there is a treasure on the tile
     */
    public boolean hasTreasure() {
        return treasure != NO_TREASURE;
    }

    /**
     * Puts a treasure on the tile.
     *
     * @param treasure : the number of the treasure to put on the tile
     */
    void setTreasure(int treasure) {
        this.treasure = treasure;
    }
}
