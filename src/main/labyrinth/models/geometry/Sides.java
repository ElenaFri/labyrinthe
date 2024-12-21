package main.labyrinth.models.geometry;

import java.util.ArrayList;

// Handles a tile's perimeter and thus, the itinerary of a player, using a logic of booleans (can or not pass).
public class Sides {
    private ArrayList<Boolean> _openSides;

    /**
     * Constructor of a tile's perimeter as an array of booleans:
     * true if a side gives way to a passage, false otherwise.
     * All closed (set to false) at init time.
     */
    public Sides() {
        this._openSides = new ArrayList<>(4); // important to pre-allocate
        for (int i = 0; i < 4; i++) {
            this._openSides.add(false); // then initialize all to false
        }
    }

    /**
     * Provides a textual representation of the sides of a tile that are open.
     * Each open side (top, right, bottom, left) is appended to the string in
     * a human-readable format.
     * @return a string describing the open sides of the tile. The format is
     *         "Côtés ouverts : " followed by the names of the open sides
     *         (e.g., "Haut", "Droite", "Bas", "Gauche"), separated by spaces.
     *         If no sides are open, "Côtés ouverts :" is returned.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Côtés ouverts : ");
        if (isSideOpen(0)) sb.append("Haut ");
        if (isSideOpen(1)) sb.append("Droite ");
        if (isSideOpen(2)) sb.append("Bas ");
        if (isSideOpen(3)) sb.append("Gauche ");
        return sb.toString().trim();
    }

    /**
     * Sides getter.
     * @return an array of booleans
     */
    public ArrayList<Boolean> getSides() { return this._openSides; }

    /**
     * Retrieves the state of a specific side of the tile.
     * A side is considered open if its value is true.
     * @param i : index of the side (0 for top, 1 for right, 2 for bottom, 3 for left)
     * @return true if the specified side is open, false otherwise
     * @throws IllegalArgumentException if the index is not between 0 and 3
     */
    public Boolean getSide(int i) {
        if (i < 0 || i > 3) {array
            throw new IllegalArgumentException("Index must be between 0 and 3.");
        }
        return this._openSides.get(i);
    }

    /**
     * Sides setter.
     * To repeat for each side that changes.
     * @param i : index of the side (0 stands for top, then clockwise)
     * @param isOpen : true if the passage opens, false if it closes
     */
    public void setSide(int i, Boolean isOpen) {
        // Checks if the index is comprised between 0 and 3 (four sides all in all)
        if (i < 0 || i > 3) {
            throw new IllegalArgumentException("Index must be between 0 and 3.");
        }

        // Checks if the array has been initialized
        if (this._openSides == null) {
            throw new IllegalStateException("Open sides list is not initialized.");
        }

        // Assigns the new value.
        this._openSides.set(i, isOpen);
    }

    /**
     * New method to check if a specific side of the tile is open.
     * A side is considered open if its value is true.
     * @param side : index of the side to check (0 for top, 1 for right, 2 for bottom, 3 for left)
     * @return true if the specified side is open, false otherwise
     * @throws IllegalArgumentException if the index is not between 0 and 3
     */
    public boolean isSideOpen(int side) {
        return getSide(side);
    }

}
