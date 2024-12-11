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
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Position position = (Position) o;
		return _x == position._x && _y == position._y;
	}

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
