package labyrinth.models.tiles;

import labyrinth.models.geometry.Sides;

public abstract class Tile {
    protected String type;
    protected Boolean _hasTreasure;
    protected int _treasure; // equals -1 if none, between 0 and 23 (included) if any
    protected Boolean _canMove;
    protected Sides _openSides;

    /**
     * Generic Tile constructor.
     * Cannot be instantiated directly, to be used by children only.
     * At creation time, tiles are all movable and with no treasure on them.
     */
    public Tile() {
        this._hasTreasure = false;
        this._treasure = -1;
        this._canMove = false;

    }

    /**
     * Sides getter.
     * @return : passage ways as an array of booleans, starting with the top.
     */
    public Sides getOpenSides() { return this._openSides; }

    /**
     * Treasure setter (activator only, as a treasure cannot be removed from the tile).
     */
    public void setTreasure() {
        this._hasTreasure = true;
    }



    /**
     * Abstract method for random orientation reinitialization.
     */
    public abstract void setOrientation();
}
