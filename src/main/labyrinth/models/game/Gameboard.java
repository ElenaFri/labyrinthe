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

// Implements the Labyrinthe gameboard - INCLUDING the free tile used to modify the labyrinth.
public class Gameboard {
    private List<GameBoardObserver> gameBoardObservers = new ArrayList<>();
    private Tile[][] _tiles;
    private TileFactory tileFactory;
    private Tile freeTile;

    private Random random;

    /**
     * Constructs a new Gameboard, initializing the board and its various components.
     * Creates a 7x7 grid of tiles, initializes the tile factory,
     * sets a random generator for tile placement and movement,
     * and sets the initial free tile as an angled tile.
     * Calls the initializeBoard method to fill the board with tiles.
     */
    public Gameboard() {
        _tiles = new Tile[7][7]; // Créer un tableau de 7x7 de tuiles
        tileFactory = new TileFactory(); // Initialisation de la factory
        random = new Random();
        freeTile = new AngledTile();
        initializeBoard();
    }

    /**
     * Adds a GameBoardObserver to the list of observers for the gameboard.
     * Observers are notified of changes to the gameboard state.
     * @param observer : GameBoardObserver instance to be added
     */
    public void addGameboardObserver(GameBoardObserver observer) {
        gameBoardObservers.add(observer);
    }

    /**
     * Notifies all registered observers about a change in the gameboard's state.
     * This method iterates over the list of GameBoardObserver objects and calls
     * their update method, passing the current instance of the Gameboard as a parameter.
     * It is used to inform observers that they should refresh or reconsider their state
     * due to changes that occur in the gameboard.
     */
    public void notifyGameboardChange() {
        // Notifier tous les observateurs du changement d'état du plateau
        for (GameBoardObserver observer : gameBoardObservers) {
            observer.update(this);
        }
    }

    /**
     * Retrieves the free tile currently available for movement or placement on the gameboard.
     * @return the tile that is currently free, ready to be inserted into the board
     */
    public Tile getFreeTile() {
        return freeTile;
    }

    /**
     * Sets the free tile for the gameboard. This tile is available for movement
     * or placement within the board configuration. Setting a new free tile will
     * trigger notifications to observers about the change in the gameboard state.
     * @param : Tile instance to be set as the free tile for the gameboard
     */
    public void setFreeTile(Tile freeTile) {
        this.freeTile = freeTile;
        notifyGameboardChange();  // Informer les observateurs
    }


    /**
     * Initializes the game board by populating each cell with a tile,
     * ensuring that fixed and movable tile constraints are respected.
     * This method places all fixed tiles on the game board in predefined
     * positions and then fills the remaining spaces with movable tiles.
     * Additionally, it positions objectives on the game board.
     * The board is populated by calling helper methods:
     * 1. `placeFixedTiles()` to position all fixed tiles at their specified locations.
     * 2. `placeMovableTiles()` to fill all remaining spaces with movable tiles.
     * 3. `placeObjectives()` to place game objectives on the board.
     * This method is fundamental in setting the initial game state.
     */
    private void initializeBoard() {        // Remplir chaque case avec une tuile, en respectant les contraintes de tuiles fixes et déplaçables
        placeFixedTiles(); // Placer les tuiles fixes
        placeMovableTiles(); // Placer les tuiles déplaçables
        placeObjectives();

    }

    /**
     * Places all fixed tiles on the game board according to predefined rules and positions.
     * This method positions the four fixed corner tiles ('angled tiles') and twelve fixed 'T' shaped tiles
     * at specific locations on the game board while defining or randomizing their orientations.
     * The steps to place fixed tiles are:
     * 1. Place four corner tiles at predetermined positions with fixed orientations in each corner of the board.
     * 2. Place eight 'T' shaped tiles with specific orientations on the borders.
     * 3. Position the remaining 'T' shaped tiles with random orientations at specified board locations.
     * The method increases the fixed tile count for each successfully placed tile.
     * It ensures that no duplicate tile is placed by checking if a tile already exists at the intended position.
     */
    private void placeFixedTiles() {
        int fixedCount = 0;

        // Placer les 4 tuiles angulaires fixes aux positions prédéfinies
        // Les 4 coins du plateau
        int[] row = {0, 0, 6, 6};  // Lignes des coins (haut-gauche, haut-droit, bas-gauche, bas-droit)
        int[] col = {0, 6, 6, 0};  // Colonnes des coins

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
                    tile.setOrientation(2); // Orientation vers le haut pour les tuiles du haut
                } else if (fixedTRows[i] == 6) {
                    tile.setOrientation(0); // Orientation vers le bas pour les tuiles du bas
                } else if (fixedTCols[i] == 0) {
                    tile.setOrientation(1); // Orientation vers la gauche pour les tuiles de gauche
                } else if (fixedTCols[i] == 6) {
                    tile.setOrientation(3); // Orientation vers la droite pour les tuiles de droite
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

    /**
     * Places all movable tiles on the gameboard. This method is responsible
     * for distributing a predefined number of different types of movable tiles:
     * angled, T-shaped, and straight tiles. It calls specific helper methods
     * to place each category of tile. Specifically, it places 16 angled tiles,
     * 6 T-shaped tiles, and 12 straight tiles onto the board. This setup is
     * essential for configuring the initial state of the gameboard, ensuring
     * that there is a correct and balanced distribution of movable tiles.
     */
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

    /**
     * Places a movable angled tile onto the game board. This method utilizes
     * the generic tile placement logic defined in {@code placeMovableTile}
     * by passing an instance of {@code AngledTile} to it.
     * The method is responsible for ensuring that the placed tile is
     * randomly positioned on the 7x7 grid and oriented in one of the
     * four possible directions. The logic attempts to find an empty
     * spot on the game board to place the angled tile. If it cannot
     * do so after 50 attempts, it logs a message indicating that
     * placement was unsuccessful. This is a critical part of
     * initializing or modifying the game board state with movable tiles.
     */
    private void placeMovableTileAngled() {
        placeMovableTile(new AngledTile());
    }

    /**
     * Places a movable T-shaped tile onto the game board. This method utilizes
     * the general tile placement logic defined in {@code placeMovableTile} by
     * creating an instance of {@code TShapedTile} and passing it to the
     * placement method. It is responsible for ensuring the T-shaped tile is
     * placed at a random empty location on the 7x7 grid and assigned one of
     * the four possible orientations. If placement cannot be achieved within
     * 50 attempts, a message indicating failure is logged. This function is
     * crucial for initializing or adjusting the game board state with movable
     * T-shaped tiles.
     */
    private void placeMovableTileTShaped() {
        placeMovableTile(new TShapedTile());
    }

    /**
     * Places a movable straight tile onto the game board.
     * This method utilizes the general tile placement logic defined in
     * {@code placeMovableTile} by creating an instance of {@code StraightTile}
     * and passing it to the placement method. It is responsible for ensuring
     * the straight tile is positioned at a random empty location on the 7x7 grid
     * and assigned one of the four possible orientations. If placement cannot
     * be achieved after 50 attempts, it logs an indication of failure.
     * This function is crucial for initializing or adjusting the game board
     * state with movable straight tiles.
     */
    private void placeMovableTileStraight() {
        placeMovableTile(new StraightTile());
    }

    /**
     * Places a movable tile onto the gameboard at a random unoccupied position
     * within a 7x7 grid and assigns it a random orientation. If placement is
     * unsuccessful after 50 attempts, a message is logged indicating failure.
     * @param tile : Tile object to be placed on the gameboard. It must be
     *             capable of being moved and reoriented, and should not already
     *             occupy a position on the board
     */
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

    /**
     * Retrieves the tile located at the specified position on the gameboard.
     * @param position : Position object containing the x and y coordinates for the tile location
     * @return Tile object found at the given position on the gameboard
     */
    public Tile getTile(Position position) {
        return _tiles[position.getX()][position.getY()];
    }

    /**
     * Distributes objectives randomly on a game board while avoiding specific reserved corner positions.
     * The method initializes a list of available positions that excludes the reserved corners.
     * It then shuffles this list to randomize the placement of objectives.
     * Finally, it places a predefined number of objectives, up to 24, onto the available positions.
     * The objectives are placed only on tiles that are not null, ensuring a valid target for each objective.
     */
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
     * Checks the neighboring tiles of the given position for possible connections.
     * This method evaluates the tiles neighboring the specified position in four directions:
     * left, right, up, and down. For each direction, it determines whether a connection
     * is possible based on the current tile's position and the tile present in the
     * neighboring position.
     * @param position : position of the tile to check connections for, consisting of x and y coordinates
     * @return a map where the keys represent directions ("Gauche", "Droite", "Haut", "Bas")
     *         and the values are booleans indicating if a connection is possible in that direction
     * @throws IllegalArgumentException if the given position is out of bounds or the tile at the given position is null
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
     * Determines if two tiles can be connected based on their sides.
     * This method checks whether the specified sides of two tiles are open
     * and can therefore connect to each other.
     * @param tile1 : first tile to be checked for connectivity
     * @param tile2 : second tile to be checked for connectivity
     * @param side : the side of the first tile to be checked for an open connection
     * @return true if the tiles can be connected on the specified sides; false otherwise
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

    /**
     * Shifts the tiles in a specified row to the right. If a tile can be moved to the right into an empty space,
     * it is moved, and the formerly occupied position is replaced with the free tile. If any tile is shifted,
     * the free tile is updated and the game board is notified of the change.
     * @param rowIndex : index of the row to be shifted
     * @return true if any tile was moved; false otherwise
     */
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

    /**
     * Shifts the tiles in a specified column upwards by one position where possible.
     * If a tile is moved, the tile that is moved out becomes the free tile.
     * This method also notifies any listeners that the game board has changed.
     * @param colIndex : index of the column to shift upwards
     * @return true if any tiles were moved, false otherwise
     */
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

    /**
     * Shifts the tiles in the specified column downwards if there are empty spaces.
     * If a tile moves, the method updates the game board and sets the last moved tile as the free tile.
     * @param colIndex : index of the column to be shifted down
     * @return true if any tiles were moved; false otherwise
     */
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
