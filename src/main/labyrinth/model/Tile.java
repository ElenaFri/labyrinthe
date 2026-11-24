package labyrinth.model;

/**
 * A tile of the labyrinth: a shape, an orientation and possibly a treasure.
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
     * @param type        : shape of the tile
     * @param orientation : number of clockwise quarter turns (taken modulo 4)
     */
    public Tile(TileType type, int orientation) {
        this.type = type;
        this.orientation = Math.floorMod(orientation, 4);
    }

    /**
     * @return the shape of the tile
     */
    public TileType type() {
        return type;
    }

    /**
     * @return the number of clockwise quarter turns applied to the tile (0 to 3)
     */
    public int orientation() {
        return orientation;
    }

    /**
     * Turns the tile by a quarter turn, clockwise.
     */
    public void rotateClockwise() {
        orientation = (orientation + 1) % 4;
    }

    /**
     * @param side : a side of the tile
     * @return true if one can go through this side
     */
    public boolean isOpen(Direction side) {
        return type.isOpen(side, orientation);
    }

    /**
     * @return the number of the treasure lying on the tile, or {@link #NO_TREASURE}
     */
    public int treasure() {
        return treasure;
    }

    /**
     * @return true if there is a treasure on the tile
     */
    public boolean hasTreasure() {
        return treasure != NO_TREASURE;
    }

    /**
     * @param treasure : the number of the treasure to put on the tile
     */
    public void setTreasure(int treasure) {
        this.treasure = treasure;
    }
}
