package labyrinth.model;

/**
 * Gets told when the state of a {@link Game} changes.
 */
public interface GameListener {
    /**
     * Called after any change, once the game is in a consistent state again.
     */
    void gameChanged();

    /**
     * Called when the current player has just found a treasure.
     *
     * @param player : the player who found it
     */
    default void objectiveFound(Player player) {
    }
}
