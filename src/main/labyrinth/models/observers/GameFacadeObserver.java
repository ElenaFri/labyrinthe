package labyrinth.models.observers;

import labyrinth.models.game.Player;
import labyrinth.models.geometry.Position;

// Observer for GameFacade.
public interface GameFacadeObserver {
    /**
     * Notifies that the current player has changed in the game.
     * @param currentPlayer : Player object representing the new current player
     */
    void UpdateCurrentPlayerChanged(Player currentPlayer);

    /**
     * Notifies observers that the player's position has changed.
     * This method is a callback that should be implemented by observers
     * to respond to updates in player positioning within the game.
     * @param newPosition : new Position object representing the updated
     *        coordinates of the player on the game board
     */
    void UpdatePlayerPositionChanged(Position newPosition);

    /**
     * Notifies observers that the positions of one or more players have changed.
     * This method serves as a callback to update observers with an array of
     * updated player positions on the game board.
     * @param positions an array of Position objects representing the updated
     *                  coordinates of one or more players on the game board
     */
    void  UpdatePlayerPositionChanged(Position[] positions);

    /**
     * Notifies observers that the player's current objective has changed.
     * This method is intended for implementation by observers to update
     * their state or view according to the player's progress in their objectives.
     * @param objective : new objective index for the player, indicating
     *                  the updated state or advancement in the player's
     *                  objectives within the game
     */
    void UpdatePlayerObjectiveChanged(int objective);
}
