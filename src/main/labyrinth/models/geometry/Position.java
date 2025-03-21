package main.labyrinth.models.geometry;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// Handles the coordinates of tiles and pieces.
public class Position {
	private int _x;
	private int _y;

	/**
	 * Constructor of a point on the gameboard.
	 * @param x : X-axis coordinate
	 * @param y : Y-axis coordinate
	 */
	public Position(int x, int y) {
		this._x = x;
		this._y = y;
	}

	/**
	 * Compares this position to the specified object. Returns true if the specified
	 * object is also a Position instance with the same X and Y coordinates.
	 * @param o : object to be compared for equality with this position
	 * @return true if the specified object is equal to this position, false otherwise
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Position position = (Position) o;
		return _x == position._x && _y == position._y;
	}

	/**
	 * Computes and returns the hash code value for this object, based on its
	 * X and Y coordinates. This method is consistent with the equals(Object)
	 * method; if two objects are equal according to the {@code equals(Object)} method,
	 * then calling hashCode() on each of them must produce the same integer result.
	 * @return an integer hash code value derived from the X and Y coordinates of this object
	 */
	@Override
	public int hashCode() {
		return
				Objects.hash(_x, _y);
	}

	/**
	 * Horizontal position getter.
	 * @return the coordinate of the position as on the X-axis,
	 * from left to right
	 */
	public int getX() { return this._x; }
	public List<Position> getNeighbors(Position current) {
		List<Position> neighbors = new ArrayList<>();
		int x = current.getX();
		int y = current.getY();

		// Ajouter les voisins (haut, bas, gauche, droite)
		neighbors.add(new Position(x - 1, y)); // Haut
		neighbors.add(new Position(x + 1, y)); // Bas
		neighbors.add(new Position(x, y - 1)); // Gauche
		neighbors.add(new Position(x, y + 1)); // Droite

		return neighbors;
	}


	/**
	 * Vertical position getter.
	 * @return the coordinate of the position as on the Y-axis,
	 * from top to bottom
	 */
	public int getY() { return this._y; }

	/**
	 * Position (re)setter.
	 * @param x : X-axis coordinate
	 * @param y : Y-axis coordinate
	 */
	public void setPosition(int x, int y) {
		this._x = x;
		this._y = y;
	}

	/**
	 * Returns a position in a conventional form.
	 * @return (x, y)
	 */
	@Override
	public String toString() {
		return "(" + this._x + ", " + this._y + ")";
	}
}
