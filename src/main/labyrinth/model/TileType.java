package labyrinth.model;

/**
 * The three shapes of tiles. Each shape is described for the orientation 0, the other orientations being
 * obtained by turning the tile clockwise (this matches the images in <code>res/img/tiles</code>).
 */
public enum TileType {
    /**
     * A corner: open on the right and at the bottom.
     */
    ANGLED,
    /**
     * A straight line: open at the top and at the bottom.
     */
    STRAIGHT,
    /**
     * A "T": open everywhere but at the bottom.
     */
    T_SHAPED;

    /**
     * @param side        : a side of the tile
     * @param orientation : number of clockwise quarter turns applied to the tile (0 to 3)
     * @return true if one can go through the given side
     */
    public boolean isOpen(Direction side, int orientation) {
        Direction unrotated = side.rotate(-orientation);
        return switch (this) {
            case ANGLED -> unrotated == Direction.RIGHT || unrotated == Direction.DOWN;
            case STRAIGHT -> unrotated == Direction.UP || unrotated == Direction.DOWN;
            case T_SHAPED -> unrotated != Direction.DOWN;
        };
    }
}
