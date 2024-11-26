package labyrinth.models.tiles;

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
