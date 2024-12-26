package labyrinth.models.tiles;

import labyrinth.models.data.ImageStore;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

// Implements angled tiles.
public class AngledTile extends Tile {
    /**
     * Angled tile constructor.
     * Must be used via TileFactory.
     */
    public AngledTile() {
        super();
        this._type = "angled";
        initOrientation();
        setOpenSides();

    }

    /**
     * Randomly changes orientation.
     */
    @Override
    public void initOrientation() {
        Random rand = new Random();
        this._orientation = rand.nextInt(4); // as there are four side configuration possible
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

        // Définir les côtés ouverts selon l'orientation
        switch (this._orientation) {
            case 0:
                this._openSides.setSide(1, true); // Droite
                this._openSides.setSide(2, true); // Bas
                System.out.println("Orientation 0: Droite Bas");
                break;
            case 1:
                this._openSides.setSide(2, true); // Bas
                this._openSides.setSide(3, true); // Gauche
                System.out.println("Orientation 1: Bas Gauche");
                break;
            case 2:
                this._openSides.setSide(3, true); // Gauche
                this._openSides.setSide(0, true); // Haut
                System.out.println("Orientation 2: Gauche Haut");
                break;
            case 3:
                this._openSides.setSide(0, true); // Haut
                this._openSides.setSide(1, true); // Droite
                System.out.println("Orientation 3: Haut Droite");
                break;
            default:
                System.out.println("Orientation invalide");
                break;
        }
    }

}
