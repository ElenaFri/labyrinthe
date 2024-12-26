package labyrinth.models.tiles;

// A tile factory, generic for tile creation.
public class TileFactory {

    /**
     * Constructor of the Factory.
     */
    public TileFactory() {
    }

    /**
     * Angled tile creator.
     */
    public Tile createAngledTile() {
        return new AngledTile();
    }

    /**
     * Straight tile creator.
     */
    public Tile createStraightTile() {
        return new StraightTile();
    }

    /**
     * T-shaped tile creator.
     */
    public Tile createTShapedTile() {
        return new TShapedTile();
    }
}
