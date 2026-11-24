/**
 * The rules of the game: the board, the tiles, the players and the course of a turn.
 * <p>
 * This package does not depend on anything graphical. Everything can be read from the outside, but the state
 * can only be changed through {@link labyrinth.model.Game}, which enforces the rules. Observers are told about the
 * changes through {@link labyrinth.model.GameListener}.
 */
package labyrinth.model;
