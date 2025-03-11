package labyrinth.models.observers;

import labyrinth.models.game.Gameboard;

// Observer for Gameboard.
public interface GameBoardObserver {
    /**
     * Updates the state of the observer with the current state of the gameboard.
     *
     * @param gameboard : gameboard instance containing the current state to be observed
     */
    void update(Gameboard gameboard);
}
