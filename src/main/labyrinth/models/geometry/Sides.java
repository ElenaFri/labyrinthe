package labyrinth.models.geometry;

import java.util.ArrayList;

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
     * Sides getter.
     * @return : an array of booleans
     */
    public ArrayList<Boolean> getSides() { return this._openSides; }

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
}
