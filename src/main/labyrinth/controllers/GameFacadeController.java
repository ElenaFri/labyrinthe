package main.labyrinth.controllers;

import main.labyrinth.models.game.GameFacade;
import main.labyrinth.models.game.Player;
import main.labyrinth.models.geometry.Position;
import main.labyrinth.models.observers.GameFacadeObserver;

public class GameFacadeController {
    private final GameFacade gameFacade;

    public GameFacadeController(GameFacade gameFacade) {
        this.gameFacade = gameFacade;
    }

    // Méthode pour obtenir la position du joueur actuel
    public Position getCurrentPlayerPosition() {
        Player currentPlayer = gameFacade.getCurrentPlayer();
        return currentPlayer.getCurrentTile();  // Retourne la position du joueur actuel
    }

    // Méthode pour obtenir l'objectif actuel du joueur
    public String getCurrentPlayerObjective() {
        Player currentPlayer = gameFacade.getCurrentPlayer();
        return "Objectif actuel: " + currentPlayer.getCurrentObjective().getTreasure();
    }

    // Méthode pour notifier la position du joueur
    public void notifyPlayerPosition(Position newPosition) {
        gameFacade.notifyPlayerPositionChange(newPosition);  // Notifie les observateurs
    }

    // Méthode pour notifier l'objectif du joueur
    public void notifyPlayerObjective(int objective) {
        gameFacade.notifyPlayerObjectiveChange(objective);  // Notifie les observateurs
    }

    // Méthode pour passer au joueur suivant
    public void nextPlayer() {
        gameFacade.nextPlayer();  // Passe au joueur suivant
    }

    // Méthode pour obtenir le joueur actuel
    public Player getCurrentPlayer() {
        return gameFacade.getCurrentPlayer();  // Retourne le joueur actuel
    }

}
