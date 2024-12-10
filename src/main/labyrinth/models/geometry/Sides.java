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
     * Getter pour un côté spécifique.
     * @param i : index du côté (0 = haut, 1 = droite, 2 = bas, 3 = gauche).
     * @return true si le côté est ouvert, false sinon.
     */
    public Boolean getSide(int i) {
        if (i < 0 || i > 3) {
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
    // Nouvelle méthode pour vérifier si un côté est ouvert
    public boolean isSideOpen(int side) {
        return getSide(side);
    }

}
