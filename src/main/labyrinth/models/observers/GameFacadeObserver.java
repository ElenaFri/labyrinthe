package main.labyrinth.models.observers;

import main.labyrinth.models.game.Player;
import main.labyrinth.models.geometry.Position;

public interface GameFacadeObserver {
    void UpdateCurrentPlayerChanged(Player currentPlayer);
    void UpdatePlayerPositionChanged(Position newPosition);
    void UpdatePlayerObjectiveChanged(int objective);
}
