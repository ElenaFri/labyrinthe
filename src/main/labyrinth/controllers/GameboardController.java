package main.labyrinth.controllers;

import main.labyrinth.models.tiles.Tile;
import main.labyrinth.models.geometry.Position;
import main.labyrinth.models.game.Gameboard;

import java.util.Map;

// Makes the
public class GameboardController {
    private final Gameboard gameboard;

    public GameboardController(Gameboard gameboard) {
        this.gameboard = gameboard;
    }

    // Retourne les mouvements valides pour une position donnée
    public Map<String, Boolean> getAvailableMoves(Position currentPosition) {
        Tile tile = gameboard.getTile(currentPosition);  // Obtenir la tuile à la position donnée
        if (tile == null) {
            throw new IllegalArgumentException("Aucune tuile à cette position.");
        }
        return gameboard.checkNeighbors(currentPosition);  // Vérifie les voisins valides
    }

    // Effectuer un mouvement sur une ligne du plateau
    public void shiftRow(int index, int direction) {
        if (index < 0 || index >= 7) {
            throw new IllegalArgumentException("Indice de ligne invalide : " + index);
        }

        if (direction != 1 && direction != 3) {  // Direction 1 (gauche) ou 3 (droite)
            throw new IllegalArgumentException("Direction invalide. Utilisez 1 pour gauche ou 3 pour droite.");
        }

        if (direction == 1) {  // Gauche
            gameboard.shiftRowLeft(index);
        } else if (direction == 3) {  // Droite
            gameboard.shiftRowRight(index);
        }
    }

    // Effectuer un mouvement sur une colonne du plateau
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

    // Faire pivoter une tuile
    public void rotateTile(Tile tile, int orientation) {
        if (tile == null) {
            throw new IllegalArgumentException("La tuile à pivoter ne peut pas être nulle.");
        }

        if (orientation < 0 || orientation > 3) {
            throw new IllegalArgumentException("Orientation invalide. Utilisez une valeur entre 0 et 3.");
        }

        tile.setOrientation(orientation);  // Supposons que Tile a une méthode setOrientation(int)
    }


}
