package labyrinth.models.geometry;

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
	 * Horizontal position getter.
	 * @return : the coordinate of the position as on the X-axis,
	 * from left to right
	 */
	public int getX() { return this._x; }

	/**
	 * Vertical position getter.
	 * @return : the coordinate of the position as on the Y-axis,
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
	 * @return : (x, y)
	 */
	@Override
	public String toString() {
		return "(" + this._x + ", " + this._y + ")";
	}
}
