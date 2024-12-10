package main.labyrinth.models.game;

import main.labyrinth.models.geometry.*;
import main.labyrinth.models.observers.GameBoardObserver;
import main.labyrinth.models.tiles.*;

import java.awt.*;
import java.util.*;
import java.util.List;

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
     *
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
     *
     * @return the tile that is currently free, ready to be inserted into the board
     */

    public Tile getFreeTile() {
        return freeTile;
    }

    /**
     * Sets the free tile for the gameboard. This tile is available for movement
     * or placement within the board configuration. Setting a new free tile will
     * trigger notifications to observers about the change in the gameboard state.
     *
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
    private void initializeBoard() {
        // Remplir chaque case avec une tuile, en respectant les contraintes de tuiles fixes et déplaçables
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
                //////////////////////////////
                tile.setOpenSides();
                _tiles[row[i]][col[i]] = tile; // Placement de la tuile
                fixedCount++;
            }
        }

        // Placer les 12 tuiles "T" fixes aux positions spécifiques avec orientations définies ou aléatoires
        int[] tRows = {0, 0, 2, 2, 2, 2, 4, 4, 4, 4, 6, 6}; // Lignes des tuiles "T"
        int[] tCols = {2, 4, 2, 4, 0, 6, 0, 2, 4, 6, 2, 4}; // Colonnes des tuiles "T"
        int[] fixedTRows = {0, 0, 2, 4, 6, 6, 2, 4};  // Lignes des tuiles "T" avec orientation définie pour la bordure
        int[] fixedTCols = {2, 4, 0, 0, 2, 4, 6, 6}; // Colonnes des tuiles "T" avec orientation définie

        for (int i = 0; i < fixedTRows.length; i++) {
            if (_tiles[fixedTRows[i]][fixedTCols[i]] == null) {
                Tile tile = tileFactory.createTShapedTile();

                // Définir les orientations spécifiques en fonction des positions de bordure
                if (fixedTRows[i] == 0) {
                    tile.setOrientation(2); // Orientation vers le haut pour les tuiles du haut
                    //////////////////////////////
                    tile.setOpenSides();
                } else if (fixedTRows[i] == 6) {
                    tile.setOrientation(0); // Orientation vers le bas pour les tuiles du bas
                    //////////////////////////////
                    tile.setOpenSides();
                } else if (fixedTCols[i] == 0) {
                    tile.setOrientation(1); // Orientation vers la gauche pour les tuiles de gauche
                    //////////////////////////////
                    tile.setOpenSides();
                } else if (fixedTCols[i] == 6) {
                    tile.setOrientation(3); // Orientation vers la droite pour les tuiles de droite
                    //////////////////////////////
                    tile.setOpenSides();
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
                //////////////////////////////////////////////////////
                tile.setOpenSides();

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
     *
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
        tile.setOpenSides();
        tile.setCanMove();
        _tiles[row][col] = tile;
    }

    /**
     * Retrieves the tile located at the specified position on the gameboard.
     *
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


    public boolean shiftRowLeft(int rowIndex) {
        boolean moved = false;  // Indique si un mouvement a eu lieu

        // Vérifie si l'index de la ligne est valide
        if (rowIndex < 0 || rowIndex >= _tiles.length) {
            throw new IllegalArgumentException("Index de ligne invalide !");

        }

        // Stocke la première tuile avant de la déplacer
        Tile firstTile = _tiles[rowIndex][0];

        // Décale toutes les tuiles vers la gauche
        for (int col = 0; col < _tiles[rowIndex].length - 1; col++) {
            _tiles[rowIndex][col] = _tiles[rowIndex][col + 1];
        }

        // La dernière tuile de la ligne prend la freeTile
        _tiles[rowIndex][_tiles[rowIndex].length - 1] = this.freeTile;

        // Met à jour la freeTile avec la tuile initialement à la première position
        this.freeTile = firstTile;

        // Notifie le changement si une modification a été effectuée
        moved = true;
        notifyGameboardChange();  // Signale que le plateau a été mis à jour

        return moved;
    }


    public boolean shiftRowRight(int rowIndex) {
        boolean moved = false;  // Indique si un mouvement a eu lieu

        // Vérifie si l'index de la ligne est valide
        if (rowIndex < 0 || rowIndex >= _tiles.length) {
            throw new IllegalArgumentException("Index de ligne invalide !");
        }

        // Stocke la dernière tuile avant de la déplacer
        Tile lastTile = _tiles[rowIndex][_tiles[rowIndex].length - 1];

        // Décale toutes les tuiles vers la droite
        for (int col = _tiles[rowIndex].length - 1; col > 0; col--) {
            _tiles[rowIndex][col] = _tiles[rowIndex][col - 1];
        }

        // La première tuile de la ligne prend la freeTile
        _tiles[rowIndex][0] = this.freeTile;

        // Met à jour la freeTile avec la tuile initialement à la dernière position
        this.freeTile = lastTile;

        // Notifie le changement si une modification a été effectuée
        moved = true;
        notifyGameboardChange();  // Signale que le plateau a été mis à jour

        return moved;
    }


    public boolean shiftColumnUp(int colIndex) {
        boolean moved = false;  // Indique si un mouvement a eu lieu

        // Vérifie si l'index de la colonne est valide
        if (colIndex < 0 || colIndex >= _tiles[0].length) {
            throw new IllegalArgumentException("Index de colonne invalide !");

        }

        // Stocke la première tuile de la colonne avant de la déplacer
        Tile firstTile = _tiles[0][colIndex];

        // Décale toutes les tuiles vers le haut
        for (int row = 0; row < _tiles.length - 1; row++) {
            _tiles[row][colIndex] = _tiles[row + 1][colIndex];
        }

        // La dernière tuile de la colonne prend la freeTile
        _tiles[_tiles.length - 1][colIndex] = this.freeTile;

        // Met à jour la freeTile avec la tuile initialement à la première position
        this.freeTile = firstTile;

        // Notifie le changement si une modification a été effectuée
        moved = true;
        notifyGameboardChange();  // Signale que le plateau a été mis à jour

        return moved;
    }


    public boolean shiftColumnDown(int colIndex) {
        boolean moved = false;  // Flag pour indiquer si un mouvement a eu lieu

        // Vérifie si l'index de la colonne est valide
        if (colIndex < 0 || colIndex >= _tiles[0].length) {
            throw new IllegalArgumentException("Index de colonne invalide !");

        }

        // Stocke la dernière tuile de la colonne avant de la déplacer
        Tile lastTile = _tiles[_tiles.length - 1][colIndex];

        // Décale toutes les tuiles vers le bas
        for (int row = _tiles.length - 1; row > 0; row--) {
            _tiles[row][colIndex] = _tiles[row - 1][colIndex];
        }

        // La première tuile de la colonne prend la freeTile
        _tiles[0][colIndex] = this.freeTile;

        // Met à jour la freeTile avec la tuile initialement à la dernière position
        this.freeTile = lastTile;

        // Notifie le changement si une modification a été effectuée
        moved = true;
        notifyGameboardChange();  // Signale que le plateau a été mis à jour

        return moved;
    }

    public List<Position> getAccessibleTiles(Position position) {
        List<Position> accessibleTiles = new ArrayList<>();
        accessibleTiles.add(position);

        // Récupérer la tuile à la position donnée
        Tile currentTile = getTile(position);
        System.out.println("Position actuelle : " + position + " -> Tuile : " + currentTile);
        // Récupérer et afficher les côtés ouverts de la tuile actuelle
        Sides openSides = currentTile.getOpenSides();
        System.out.println("orientation d ela tuile ctuelle  : " + currentTile.get_orientation());
        System.out.println("Côtés ouverts de la tuile actuelle : " + openSides);

        // Vérifier les côtés ouverts de la tuile actuelle
        // Vérification pour ne pas dépasser les limites du plateau
        if (currentTile.getOpenSides().isSideOpen(0)) { // haut
            Position upPosition = new Position(position.getX() - 1, position.getY());
            // System.out.println("Vérification côté haut pour la position : " + upPosition);
            if (isPositionValid(upPosition)) {
                //  System.out.println("Position haut valide : " + upPosition);
                if (isOppositeSideOpen(upPosition, 2)) { // Vérifier côté bas de la tuile voisine
                    System.out.println("Côté bas de la tuile voisine ouvert pour : " + upPosition);
                    accessibleTiles.add(upPosition);
                } else {
                    System.out.println("Côté bas de la tuile voisine fermé pour : " + upPosition);
                }
            } else {
                System.out.println("Position haut invalide : " + upPosition);
            }
        }

        if (currentTile.getOpenSides().isSideOpen(1)) { // Côté droite
            Position rightPosition = new Position(position.getX(), position.getY() + 1);
            //  System.out.println("Vérification côté droit pour la position : " + rightPosition);
            if (isPositionValid(rightPosition)) {
                //    System.out.println("Position droite valide : " + rightPosition);
                if (isOppositeSideOpen(rightPosition, 3)) { // Vérifier côté gauche de la tuile voisine
                    System.out.println("Côté gauche de la tuile voisine ouvert pour : " + rightPosition);
                    accessibleTiles.add(rightPosition);
                } else {
                    System.out.println("Côté gauche de la tuile voisine fermé pour : " + rightPosition);
                }
            } else {
                System.out.println("Position droite invalide : " + rightPosition);
            }
        }

        if (currentTile.getOpenSides().isSideOpen(2)) { // Côté bas
            Position downPosition = new Position(position.getX() + 1, position.getY());
            //System.out.println("Vérification côté bas pour la position : " + downPosition);
            if (isPositionValid(downPosition)) {
                //System.out.println("Position bas valide : " + downPosition);
                if (isOppositeSideOpen(downPosition, 0)) { // Vérifier côté haut de la tuile voisine
                    System.out.println("Côté haut de la tuile voisine ouvert pour : " + downPosition);
                    accessibleTiles.add(downPosition);
                } else {
                    System.out.println("Côté haut de la tuile voisine fermé pour : " + downPosition);
                }
            } else {
                System.out.println("Position bas invalide : " + downPosition);
            }
        }

        if (currentTile.getOpenSides().isSideOpen(3)) { // Côté gauche
            Position leftPosition = new Position(position.getX(), position.getY() - 1);
            // System.out.println("Vérification côté gauche pour la position : " + leftPosition);
            if (isPositionValid(leftPosition)) {
                //  System.out.println("Position gauche valide : " + leftPosition);
                if (isOppositeSideOpen(leftPosition, 1)) { // Vérifier côté droite de la tuile voisine
                    System.out.println("Côté droite de la tuile voisine ouvert pour : " + leftPosition);
                    accessibleTiles.add(leftPosition);
                } else {
                    System.out.println("Côté droite de la tuile voisine fermé pour : " + leftPosition);
                }
            } else {
                System.out.println("Position gauche invalide : " + leftPosition);
            }
        }

        System.out.println("Tuiles accessibles : " + accessibleTiles);
        return accessibleTiles;
    }


    // Méthode pour vérifier si un côté opposé d'une tuile est ouvert
    public boolean isOppositeSideOpen(Position position, int oppositeSide) {
        Tile neighborTile = getTile(position);
        if (neighborTile != null) {
            System.out.println("Tuile voisine : " + neighborTile + ", orientation : " + neighborTile.get_orientation());
            System.out.println("Côtés ouverts de la tuile voisine : " + neighborTile.getOpenSides());

            boolean isOpen = neighborTile.getOpenSides().isSideOpen(oppositeSide);
            System.out.println("Côté opposé " + oppositeSide + " est " + (isOpen ? "ouvert" : "fermé"));
            return isOpen;
        }
        System.out.println("Tuile voisine nulle pour la position : " + position);
        return false;
    }


    // Méthode pour vérifier si la position est valide (dans les limites du plateau)
    private boolean isPositionValid(Position position) {
        boolean valid = position.getX() >= 0 && position.getX() < 7 && position.getY() >= 0 && position.getY() < 7; // Vérifier si la position X et Y sont dans les limites du plateau
        System.out.println("Vérification de la validité de la position " + position + " : " + (valid ? "Valide" : "Invalide"));
        return valid;
    }


}












