package labyrinth.models.tiles;

import labyrinth.models.data.ImageStore;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
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
        this._orientation = rand.nextInt(4);
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

        // Réinitialiser tous les côtés à false
        for (int i = 0; i < 4; i++) {
            this._openSides.setSide(i, false);
        }
        // Définir les côtés ouverts en fonction de l'orientation
        switch (this._orientation) {
            case 0: // Orientation verticale (Haut et Bas)
                this._openSides.setSide(0, true); // Haut
                this._openSides.setSide(2, true); // Bas
                break;
            case 1: // Orientation horizontale (Gauche et Droite)
                this._openSides.setSide(1, true); // Droite
                this._openSides.setSide(3, true); // Gauche
                break;
            case 2: // Orientation horizontale (Gauche et Droite)
                this._openSides.setSide(0, true); // Droite
                this._openSides.setSide(2, true); // Gauche
                break;
            case 3: // Orientation horizontale (Gauche et Droite)
                this._openSides.setSide(1, true); // Droite
                this._openSides.setSide(3, true); // Gauche
                break;

            default:
                break;
        }
    }
}
