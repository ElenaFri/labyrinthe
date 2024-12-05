package main.labyrinth.models.tiles;

import java.util.Random;

// Implements straight tiles.
public class StraightTile extends Tile {
    /**
     * Straight Tile constructor.
     * Must be used via TileFactory.
     */
    public StraightTile() {
        super();
        this._type = "straight";
        initOrientation();
        setOpenSides();
    }

    /**
     * Randomly changes orientation.
     */
    @Override
    public void initOrientation() {
        Random rand = new Random();
        this._orientation = rand.nextInt(2); // There are only two orientations possible for straight-shaped tiles, vertical and horizontal
    }

    /**
     * Orientation setter.
     * @param orientation : orientation code
     */
    @Override
    public void setOrientation(int orientation) {
        this._orientation = orientation;
    }

    /**
     * Updates the sides of the tile, according to the orientation.
     * Corresponds to the pic stored in res/img/tiles.
     */
    @Override
    public void setOpenSides() {
        switch (this._orientation) {
            case 0:
                this._openSides.setSide(0, true);
                this._openSides.setSide(2, true);
                break;
            case 1:
                this._openSides.setSide(1, true);
                this._openSides.setSide(3, true);
                break;
            default:
                break;
        }
    }
}
