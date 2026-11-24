package labyrinth.view;

import labyrinth.model.Direction;
import labyrinth.model.Position;

/**
 * Something on the screen that the player can point at, click on, or select with the keyboard.
 */
sealed interface Target {
    /**
     * An arrow around the board, to push the spare tile.
     *
     * @param direction : the direction in which the tiles would move
     * @param index     : the index of the row or of the column
     */
    record Push(Direction direction, int index) implements Target {
    }

    /**
     * A tile to which the pawn can go (or the tile of the pawn, to end the turn).
     *
     * @param position : the position of the tile
     */
    record Move(Position position) implements Target {
    }

    /**
     * The buttons of the screen.
     */
    enum Button implements Target {
        /**
         * Turns the spare tile.
         */
        ROTATE,
        /**
         * Quits the game.
         */
        EXIT
    }
}
