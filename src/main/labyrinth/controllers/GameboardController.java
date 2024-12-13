package main.labyrinth.controllers;

import main.labyrinth.models.observers.GameBoardObserver;
import main.labyrinth.models.tiles.Tile;
import main.labyrinth.models.geometry.Position;
import main.labyrinth.models.game.Gameboard;

import java.util.Map;

// Manipulates Gameboard.
public class GameboardController {
    private final Gameboard gameboard;

    /**
     * Constructs a GameboardController with a specified Gameboard.
     * @param gameboard : Gameboard instance to be controlled by this controller
     */
    public GameboardController(Gameboard gameboard) {

        this.gameboard = gameboard;
    }
    public Gameboard getGameboard()
    {
        return this.gameboard;
    }


    /**
     * Shifts the tiles in a specified row of the gameboard in the given direction.
     * If the direction is left, the row will be shifted to the left. If the direction
     * is right, the row will be shifted to the right. Only valid indices and directions
     * are accepted.
     * @param index : index of the row to be shifted. Must be between 0 and 6 inclusive
     * @param direction : direction to shift the row. Use 1 for left and 3 for right
     * @throws IllegalArgumentException if the index is out of bounds or if the direction is invalid
     */
    public void shiftRow(int index, int direction) {
        if (index < 0 || index >= 7) {
            throw new IllegalArgumentException("Indice de ligne invalide : " + index);
        }

        if (direction != 1 && direction != 3) {  // Direction 1 (gauche) ou 3 (droite)
            throw new IllegalArgumentException("Direction invalide. Utilisez 1 pour gauche ou 3 pour droite.");
        }

        if (direction == 1) {  // droite
            gameboard.shiftRowRight(index);
        } else if (direction == 3) {  // gauche
            gameboard.shiftRowLeft(index);
        }

    }

    /**
     * Shifts a column on the gameboard at a specified index in the given direction.
     * The column can be shifted upwards or downwards. Only valid indices and
     * directions are accepted.
     * @param index : index of the column to be shifted. Must be between 0 and 6 inclusive
     * @param direction : direction to shift the column. 0 for up and 2 for down
     * @throws IllegalArgumentException if the index is out of bounds or if the direction is invalid
     */
    public void shiftColumn(int index, int direction) {
        if (index < 0 || index >= 7) {
            throw new IllegalArgumentException("Indice de colonne invalide : " + index);
        }

        if (direction < 0 || direction > 3) {
            throw new IllegalArgumentException("Direction invalide. Utilisez 0 pour haut, 1 pour gauche, 2 pour bas, 3 pour droite.");
        }

        if (direction == 0) {  // Haut
            gameboard.shiftColumnUp(index);
        } else if (direction == 2) {  // Bas
            gameboard.shiftColumnDown(index);
        }
    }

    /**
     * Rotates the specified tile to a given orientation.
     * @param tile : tile to be rotated. Must not be null
     * @param orientation : the desired orientation of the tile. Must be an integer between 0 and 3 inclusive,
     * where each number represents a specific orientation state.
     * @throws IllegalArgumentException if the provided tile is null or
     * if the orientation is outside the valid range
     */
    public void rotateTile(Tile tile, int orientation) {
        if (tile == null) {
            throw new IllegalArgumentException("La tuile à pivoter ne peut pas être nulle.");
        }

        if (orientation < 0 || orientation > 3) {
            throw new IllegalArgumentException("Orientation invalide. Utilisez une valeur entre 0 et 3.");
        }

        tile.setOrientation(orientation);
        //tile.setOpenSides();
        gameboard.notifyGameboardChange();
        tile.setOpenSides();
    }
}
