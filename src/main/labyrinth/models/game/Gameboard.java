package main.labyrinth.models.game;

import main.labyrinth.models.geometry.*;
import main.labyrinth.models.observers.GameBoardObserver;
import main.labyrinth.models.tiles.*;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Gameboard {
    private List<GameBoardObserver> gameBoardObservers = new ArrayList<>();
    private Tile[][] _tiles;
    private TileFactory tileFactory;
    private Tile freeTile;

    private Random random;

    // Constructeur de Gameboard
    public Gameboard() {
        _tiles = new Tile[7][7]; // Créer un tableau de 7x7 de tuiles
        tileFactory = new TileFactory(); // Initialisation de la factory
        random = new Random();
        freeTile = new AngledTile();
        initializeBoard();
    }
    public void addGameboardObserver(GameBoardObserver observer) {
        gameBoardObservers.add(observer);
    }
    public void notifyGameboardChange() {
        // Notifier tous les observateurs du changement d'état du plateau
        for (GameBoardObserver observer : gameBoardObservers) {
            observer.update(this);
        }
    }

    // Getter pour obtenir la freeTile actuelle
    public Tile getFreeTile() {
        return freeTile;
    }

    // Setter pour modifier la freeTile et notifier
    public void setFreeTile(Tile freeTile) {
        this.freeTile = freeTile;
        notifyGameboardChange();  // Informer les observateurs
    }


    // Méthode pour initialiser le plateau avec des tuiles
    private void initializeBoard() {
        // Remplir chaque case avec une tuile, en respectant les contraintes de tuiles fixes et déplaçables
        placeFixedTiles(); // Placer les tuiles fixes
        placeMovableTiles(); // Placer les tuiles déplaçables

        placeObjectives();

    }


    // Placer les tuiles fixes
    private void placeFixedTiles() {
        int fixedCount = 0;

        // Placer les 4 tuiles angulaires fixes aux positions prédéfinies
        // Les 4 coins du plateau
        int[] row = {0, 0, 6, 6};  // Lignes des coins (haut-gauche, haut-droit, bas-gauche, bas-droit)
        int[] col = {0, 6, 0, 6};  // Colonnes des coins

        // Définir les orientations pour chaque coin
        int[] orientations = {0, 1, 2, 3}; // Orientation des tuiles angulaires (0, 1, 2, 3) pour former un carré

        for (int i = 0; i < 4; i++) {
            if (_tiles[row[i]][col[i]] == null) {
                Tile tile = tileFactory.createAngledTile();
               // Cette tuile ne peut pas être déplacée et c est par defaut dans tile
                tile.setOrientation(orientations[i]); // Définir l'orientation appropriée
                _tiles[row[i]][col[i]] = tile; // Placement de la tuile
                fixedCount++;
            }
        }

        // Placer les 12 tuiles "T" fixes aux positions spécifiques avec orientations définies ou aléatoires
        int[] tRows = {0, 0, 2, 2, 2, 2, 4, 4, 4, 4, 6, 6}; // Lignes des tuiles "T"
        int[] tCols = {2, 4, 2, 4, 0, 6, 0, 2, 4, 6, 2, 4}; // Colonnes des tuiles "T"
        int[] fixedTRows = {0, 0, 2, 4, 6, 6, 2, 4 };  // Lignes des tuiles "T" avec orientation définie pour la bordure
        int[] fixedTCols = {2, 4, 0, 0, 2, 4, 6, 6}; // Colonnes des tuiles "T" avec orientation définie

        for (int i = 0; i < fixedTRows.length; i++) {
            if (_tiles[fixedTRows[i]][fixedTCols[i]] == null) {
                Tile tile = tileFactory.createTShapedTile();

                // Définir les orientations spécifiques en fonction des positions de bordure
                if (fixedTRows[i] == 0) {
                    tile.setOrientation(0); // Orientation vers le haut pour les tuiles du haut
                } else if (fixedTRows[i] == 6) {
                    tile.setOrientation(2); // Orientation vers le bas pour les tuiles du bas
                } else if (fixedTCols[i] == 0) {
                    tile.setOrientation(3); // Orientation vers la gauche pour les tuiles de gauche
                } else if (fixedTCols[i] == 6) {
                    tile.setOrientation(1); // Orientation vers la droite pour les tuiles de droite
                }

                _tiles[fixedTRows[i]][fixedTCols[i]] = tile; // Placement de la tuile
                fixedCount++;
            }
        }

        // Placer les autres tuiles "T" fixes avec orientations aléatoires
        for (int i = 0; i < tRows.length; i++) {
            if (_tiles[tRows[i]][tCols[i]] == null) {
                Tile tile = tileFactory.createTShapedTile();

                int randomOrientation = (int) (Math.random() * 4); // Orientation aléatoire (0, 1, 2, 3)
                tile.setOrientation(randomOrientation);
                _tiles[tRows[i]][tCols[i]] = tile; // Placement de la tuile
                fixedCount++;
            }
        }
    }
    // Placer les tuiles déplaçables
    private void placeMovableTiles() {
        // Placer les tuiles angulaires déplaçables
        for (int i = 0; i < 16; i++) {
            placeMovableTileAngled();
        }

        // Placer les tuiles "T" déplaçables
        for (int i = 0; i < 6; i++) {
            placeMovableTileTShaped();
        }

        // Placer toutes les tuiles droites déplaçables
        for (int i = 0; i < 12; i++) {
            placeMovableTileStraight();
        }
    }
    // Placer une tuile angulaire déplaçable
    private void placeMovableTileAngled() {
        placeMovableTile(new AngledTile());
    }

    // Placer une tuile "T" déplaçable
    private void placeMovableTileTShaped() {
        placeMovableTile(new TShapedTile());
    }

    // Placer une tuile droite déplaçable
    private void placeMovableTileStraight() {
        placeMovableTile(new StraightTile());
    }

    // Méthode commune pour placer une tuile déplaçable
    private void placeMovableTile(Tile tile) {
        int row, col;
        int attemptCount = 0;
        do {
            row = random.nextInt(7);
            col = random.nextInt(7);
            attemptCount++;

            if (attemptCount > 49) {
                System.out.println("Impossible de placer une tuile après 50 tentatives.");
                return;
            }
        } while (_tiles[row][col] != null);

        int orientation = random.nextInt(4);
        tile.setOrientation(orientation);
        tile.setCanMove();
        _tiles[row][col] = tile;
    }






    // Méthode pour récupérer une tuile à une position donnée
    public Tile getTile(Position position) {
        return _tiles[position.getX()][position.getY()];
    }







    private void placeObjectives() {
        // Tableau des positions des coins réservés aux joueurs
        int[] row = {0, 0, 6, 6};  // Indices des lignes des coins
        int[] col = {0, 6, 0, 6};  // Indices des colonnes des coins

        // Créer une liste des positions disponibles
        List<Position> availablePositions = new ArrayList<>();

        // Remplir la liste avec toutes les positions sauf les coins réservés
        for (int i = 0; i < _tiles.length; i++) {
            for (int j = 0; j < _tiles[i].length; j++) {
                // Vérifier si la position est un coin réservé
                boolean isCorner = false;
                for (int k = 0; k < 4; k++) {
                    if (i == row[k] && j == col[k]) {
                        isCorner = true;
                        break;
                    }
                }

                // Si ce n'est pas un coin réservé et qu'il y a une tuile, ajouter à la liste
                if (!isCorner && _tiles[i][j] != null) {
                    availablePositions.add(new Position(i, j));
                }
            }
        }

        // Mélanger les positions disponibles pour un placement aléatoire
        Collections.shuffle(availablePositions);

        // Placer les objectifs sur les positions disponibles
        int objectifCount = 0;
        for (Position position : availablePositions) {
            if (objectifCount < 24) {
                _tiles[position.getX()][position.getY()].setTreasure(objectifCount);
                objectifCount++;
            } else {
                break;
            }
        }
    }
    /**
     * Vérifie si la tuile à une position donnée peut se connecter à ses voisins un par un.
     *
     * @param position : Les  coordonnée x et y de la tuile.
     *
     * @return : Une map contenant la direction et un booléen indiquant si la connexion est possible.
 exemple:
    Direction: Gauche - Connectable: true
    Direction: Droite - Connectable: false
    Direction: Haut - Connectable: true
    Direction: Bas - Connectable: false
*/
    public Map<String, Boolean> checkNeighbors(Position position) {
        if (position.getY() < 0 || position.getY() >= _tiles.length || position.getX()< 0 || position.getX() >= _tiles[0].length) {
            throw new IllegalArgumentException("Les coordonnées (x, y) sont hors limites.");
        }

        Tile tile = _tiles[position.getY()][position.getX()];
        if (tile == null) {
            throw new IllegalArgumentException("La tuile à cette position est nulle.");
        }

        // Map pour stocker les résultats par direction
        Map<String, Boolean> connections = new HashMap<>();

        // Vérification des connexions avec les voisins
        connections.put("Gauche", position.getX() > 0 && _tiles[position.getY()][position.getX() - 1] != null && canConnect(tile, _tiles[position.getY()][position.getX() - 1], 3)); // 3 = gauche
        connections.put("Droite", position.getX() < _tiles[0].length - 1 && _tiles[position.getY()][position.getX() + 1] != null && canConnect(tile, _tiles[position.getY()][position.getX() + 1], 1)); // 1 = droite
        connections.put("Haut", position.getY() > 0 && _tiles[position.getY()- 1][position.getX()] != null && canConnect(tile, _tiles[position.getY() - 1][position.getX()], 0)); // 0 = haut
        connections.put("Bas", position.getY() < _tiles.length - 1 && _tiles[position.getY() + 1][position.getX()] != null && canConnect(tile, _tiles[position.getY() + 1][position.getX()], 2)); // 2 = bas

        return connections;
    }


    /**
     * Vérifie si deux tuiles peuvent se connecter via un côté spécifique.
     *
     * @param tile1 : La première tuile.
     * @param tile2 : La deuxième tuile.
     * @param side : Le côté de la première tuile où la connexion est testée (0 = haut, 1 = droite, 2 = bas, 3 = gauche).
     * @return : true si les tuiles peuvent se connecter via le côté spécifié, false sinon.
     */
    public boolean canConnect(Tile tile1, Tile tile2, int side) {
        if (tile1 == null || tile2 == null) {
            return false;
        }

        // Vérifier si les côtés correspondants sont ouverts (côté à côté)
        boolean canConnect = tile1.getOpenSides().getSide(side)
                && tile2.getOpenSides().getSide((side + 2) % 4);

        return canConnect;
    }
    public boolean shiftRowLeft(int rowIndex) {
        boolean moved = false;
        Tile lastTile = null; // Variable pour mémoriser la dernière tuile déplacée

        for (int col = 1; col < _tiles[rowIndex].length; col++) {
            // Si une tuile est trouvée et l'espace à gauche est vide
            if (_tiles[rowIndex][col] != null && _tiles[rowIndex][col - 1] == null) {
                // Déplace la tuile vers la gauche
                _tiles[rowIndex][col - 1] = _tiles[rowIndex][col];
                // Mémorise la tuile qui sort pour la rendre freeTile
                lastTile = _tiles[rowIndex][col];
                // Remplace la position d'origine de la tuile par la freeTile
                _tiles[rowIndex][col] = freeTile;
                moved = true;
            }
        }

        // Si un mouvement a été effectué, mettre à jour la freeTile
        if (moved) {
            // La tuile qui est sortie devient la freeTile
            this.freeTile = lastTile;
            notifyGameboardChange(); // Notifie que le tableau a changé
        }

        return moved;
    }

    public boolean shiftRowRight(int rowIndex) {
        boolean moved = false;
        Tile lastTile = null; // Variable pour mémoriser la dernière tuile déplacée

        for (int col = _tiles[rowIndex].length - 2; col >= 0; col--) {
            // Si une tuile est trouvée et l'espace à droite est vide
            if (_tiles[rowIndex][col] != null && _tiles[rowIndex][col + 1] == null) {
                // Déplace la tuile vers la droite
                _tiles[rowIndex][col + 1] = _tiles[rowIndex][col];
                // Mémorise la tuile qui sort pour la rendre freeTile
                lastTile = _tiles[rowIndex][col];
                // Remplace la position d'origine de la tuile par la freeTile
                _tiles[rowIndex][col] = freeTile;
                moved = true;
            }
        }

        // Si un mouvement a été effectué, mettre à jour la freeTile
        if (moved) {
            // La tuile qui est sortie devient la freeTile
            this.freeTile = lastTile;
            notifyGameboardChange(); // Notifie que le tableau a changé
        }

        return moved;
    }

    public boolean shiftColumnUp(int colIndex) {
        boolean moved = false;
        Tile lastTile = null; // Variable pour mémoriser la dernière tuile déplacée

        for (int row = 1; row < _tiles.length; row++) {
            // Si une tuile est trouvée et l'espace au-dessus est vide
            if (_tiles[row][colIndex] != null && _tiles[row - 1][colIndex] == null) {
                // Déplace la tuile vers le haut
                _tiles[row - 1][colIndex] = _tiles[row][colIndex];
                // Mémorise la tuile qui sort pour la rendre freeTile
                lastTile = _tiles[row][colIndex];
                // Remplace la position d'origine de la tuile par la freeTile
                _tiles[row][colIndex] = freeTile;
                moved = true;
            }
        }

        // Si un mouvement a été effectué, mettre à jour la freeTile
        if (moved) {
            // La tuile qui est sortie devient la freeTile
            this.freeTile = lastTile;
            notifyGameboardChange(); // Notifie que le tableau a changé
        }

        return moved;
    }

    public boolean shiftColumnDown(int colIndex) {
        boolean moved = false;
        Tile lastTile = null; // Variable pour mémoriser la dernière tuile déplacée

        for (int row = _tiles.length - 2; row >= 0; row--) {
            // Si une tuile est trouvée et l'espace en dessous est vide
            if (_tiles[row][colIndex] != null && _tiles[row + 1][colIndex] == null) {
                // Déplace la tuile vers le bas
                _tiles[row + 1][colIndex] = _tiles[row][colIndex];
                // Mémorise la tuile qui sort pour la rendre freeTile
                lastTile = _tiles[row][colIndex];
                // Remplace la position d'origine de la tuile par la freeTile
                _tiles[row][colIndex] = freeTile;
                moved = true;
            }
        }

        // Si un mouvement a été effectué, mettre à jour la freeTile
        if (moved) {
            // La tuile qui est sortie devient la freeTile
            this.freeTile = lastTile;
            notifyGameboardChange(); // Notifie que le tableau a changé
        }

        return moved;
    }












}
