package main.labyrinth.models.observers;

import main.labyrinth.models.game.Player;
import main.labyrinth.models.geometry.Position;

public interface GameFacadeObserver {
    void onCurrentPlayerChanged(Player currentPlayer);
    void onPlayerPositionChanged(Position newPosition);
    void onPlayerObjectiveChanged(int objective);
}
