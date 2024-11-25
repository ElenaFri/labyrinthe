package labyrinth.models.tiles;

import java.util.Random;

public class AngledTile extends Tile {
    /**
     * Angled tile constructor.
     * Must be used via TileFactory.
     */
    public AngledTile() {
        super();
        this._type = "angled";
        setOrientation();
        setOpenSides();
    }

    /**
     * Randomly changes orientation.
     */
    @Override
    public void setOrientation() {
        Random rand = new Random();
        this._orientation = rand.nextInt(4); // as there are four side configuration possible
    }

    /**
     * Updates the sides of the tile, according to the orientation.
     * Corresponds to the pic stored in res/img/tiles.
     */
    @Override
    public void setOpenSides() {
        switch (this._orientation) {
            case 0:
                this._openSides.setSide(1, true);
                this._openSides.setSide(2, true);
                break;
            case 1:
                this._openSides.setSide(2, true);
                this._openSides.setSide(3, true);
                break;
            case 2:
                this._openSides.setSide(3, true);
                this._openSides.setSide(0, true);
                break;
            case 3:
                this._openSides.setSide(0, true);
                this._openSides.setSide(1, true);
                break;
            default:
                break;
        }
    }
}
