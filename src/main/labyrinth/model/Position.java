package labyrinth.model;

/**
 * A cell of the board.
 *
 * @param row : row index, from the top (0 to {@link Board#SIZE} - 1)
 * @param col : column index, from the left (0 to {@link Board#SIZE} - 1)
 */
public record Position(int row, int col) {

    /**
     * Gives the neighbouring position in a direction.
     *
     * @param direction : the direction to go to
     * @return the neighbouring position in this direction (possibly outside of the board)
     */
    public Position step(Direction direction) {
        return new Position(row + direction.rowStep(), col + direction.colStep());
    }

    /**
     * Tells whether the position is inside of the board.
     *
     * @return true if the position is inside of the board
     */
    public boolean isOnBoard() {
        return row >= 0 && row < Board.SIZE && col >= 0 && col < Board.SIZE;
    }
}
