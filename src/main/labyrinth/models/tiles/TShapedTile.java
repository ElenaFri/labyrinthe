package labyrinth.models.tiles;

import labyrinth.models.data.ImageStore;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

// Implements T-shaped tiles.
public class TShapedTile extends Tile {
    /**
     * T-Shaped Tile constructor.
     * Must be used via TileFactory.
     */
    public TShapedTile() {
        super();
        this._type = "t-shaped";
        initOrientation();
        setOpenSides();

    }

    /**
     * Randomly changes orientation.
     */
    @Override
    public void initOrientation() {
        Random rand = new Random();
        this._orientation = rand.nextInt(4); // as there are four side configurations possible
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
     */
    @Override
    public void setOpenSides() {
        // Réinitialiser tous les côtés à false
        for (int i = 0; i < 4; i++) {
            this._openSides.setSide(i, false);
        }

        switch (this._orientation) {
            case 0:
                this._openSides.setSide(0, true);
                this._openSides.setSide(1, true);
                this._openSides.setSide(3, true);



                break;
            case 1:
                this._openSides.setSide(0, true);
                this._openSides.setSide(1, true);
                this._openSides.setSide(2, true);

                break;
            case 2:
                this._openSides.setSide(1, true);
                this._openSides.setSide(2, true);
                this._openSides.setSide(3, true);


                break;
            case 3:
                this._openSides.setSide(2, true);
                this._openSides.setSide(3, true);
                this._openSides.setSide(0, true);


                break;
            default:
                break;
        }
    }
}
