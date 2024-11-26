package main.labyrinth.models.game;
import models.tiles.*;

public class Gameboard {

    private Tile[][] _tiles;

    // Constructeur de Gameboard
    public Gameboard() {
        _tiles = new Tile[7][7]; // Créer un tableau de 7x7 de tuiles
        initializeBoard();
    }

    // Méthode pour initialiser le plateau avec des tuiles
    private void initializeBoard() {
        TileFactory tileFactory = new TileFactory();

        // Remplir chaque case avec une tuile
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                // Logique pour définir quel type de tuile à placer
                _tiles[i][j] = createTile(i, j);
            }
        }
    }

    // Méthode pour choisir la tuile en fonction de la position
    private Tile createTile(int row, int col) {
        Tile tile;
        // Logique pour déterminer le type de la tuile et son orientation
        if (row % 2 == 0 && col % 2 == 0) {
            tile = new StraightTile();  // Tuile droite pour positions paires
            tile.setOrientation(0);  // Orientation horizontale
        } else if (row % 2 != 0 && col % 2 != 0) {
            tile = new TShapedTile();  // Tuile en T pour les positions impaires
            tile.setOrientation(1);  // Orientation en T
        } else {
            tile = new AngledTile();  // Tuile angulaire pour les autres cases
            tile.setOrientation(2);  // Orientation angulaire
        }

        return tile;
    }

    // Méthode pour récupérer une tuile à une position donnée
    public Tile getTile(Position position) {
        return _tiles[position.getX()][position.getY()];
    }
}