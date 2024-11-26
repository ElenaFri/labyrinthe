package labyrinth.models.tiles;

import labyrinth.models.geometry.Position;
import labyrinth.models.geometry.Sides;

public abstract class Tile {
    protected String _type;
    protected Boolean _hasTreasure;
    protected int _treasure; // equals -1 if none, between 0 and 23 (included) if any
    protected Boolean _canMove;
    protected Sides _openSides;
    protected Position _position;
    protected int _orientation;

    /**
     * Generic Tile constructor.
     * Cannot be instantiated directly, to be used by children only.
     * At creation time, tiles are all movable and with no treasure on them.
     */
    public Tile() {
        this._hasTreasure = false;
        this._treasure = -1;
        this._canMove = true;
        this._position = new Position(-1, -1); // conventional, to say they have none for the time being
        this._orientation = 0;
        this._openSides = new Sides();
    }

    /**
     * Tile constructor with position.
     * Cannot be instantiated directly, to be used by children only.
     * These tiles are not movable, as they have a position defined at creation time.
     * @param position : Position aka (x,y) coordinates of the tile.
     */
    public Tile(Position position) {
        this._hasTreasure = false;
        this._treasure = -1;
        this._canMove = false;
        this._position = position;
        this._orientation = 0;
        this._openSides = new Sides();
    }

    /**
     * Type getter.
     * @return : type as a string.
     */
    public String getType() { return this._type; }

    /**
     * Sides getter.
     * @return : passage ways as an array of booleans, starting with the top.
     */
    public Sides getOpenSides() { return this._openSides; }

    /**
     * Getter, checks if there is a treasure on the tile.
     * @return : true if treasure, false otherwise.
     */
    public Boolean checkIfTreasure() { return this._hasTreasure; }

    /**
     * Getter, checks if the tile is movable or part of the gameboard.
     * @return : true if movable, false otherwise.
     */
    public Boolean checkIfMoves() { return this._canMove; }

    /**
     * Treasure getter.
     * @return : integer indexing the treasure imprinted on the tile, in any. If there are none, returns -1.
     */
    public int getTreasure() { return this._treasure; }

    /**
     * Treasure setter (activator only, as a treasure cannot be removed from the tile).
     */
    public void setTreasure(int treasure) {
        this._hasTreasure = true;
        this._treasure = treasure;
    }

    /**
     * Abstract method for random orientation reinitialization.
     */
    public abstract void initOrientation();

    /**
     * Abstract orientation setter.
     * @param orientation : orientation code
     */
    public abstract void setOrientation(int orientation);

    /**
     * Abstract method for side management.
     */
    public abstract void setOpenSides();
}
