package main.labyrinth.controllers;

import main.labyrinth.models.game.GameFacade;
import main.labyrinth.models.game.Player;
import main.labyrinth.models.geometry.Position;
import main.labyrinth.models.observers.GameFacadeObserver;

// Manipulates GameFacade.
public class GameFacadeController {
    private final GameFacade gameFacade;

    /**
     * Constructs a GameFacadeController with the specified GameFacade.
     * @param gameFacade : GameFacade instance to be used by the controller
     */
    public GameFacadeController(GameFacade gameFacade) {
        this.gameFacade = gameFacade;
    }

    /**
     * Retrieves the current position of the player in the labyrinth.
     * This method delegates the call to the game facade to get the current player
     * and then returns the player's current tile position.
     * @return current Position object representing the player's coordinates on the gameboard
     */
    public Position getCurrentPlayerPosition() {
        Player currentPlayer = gameFacade.getCurrentPlayer();
        return currentPlayer.getCurrentTile();  // Retourne la position du joueur actuel
    }

    /**
     * Retrieves the current objective of the current player in the game.
     * This method accesses the current player through the game facade and
     * fetches their current objective card's associated treasure description.
     * @return a String representing the description of the current player's objective
     *         in the format "Objectif actuel: [Treasure]", where [Treasure] is the
     *         name or identifier of the treasure on the card
     */
    public String getCurrentPlayerObjective() {
        Player currentPlayer = gameFacade.getCurrentPlayer();
        return "Objectif actuel: " + currentPlayer.getCurrentObjective().getTreasure();
    }

    /**
     * Notifies the game facade about a change in the player's position.
     * This method is responsible for updating all relevant observers
     * with the new position of the player.
     * @param newPosition : new Position object representing the player's
     *                    updated coordinates on the game board
     */
    public void notifyPlayerPosition(Position newPosition) {
        gameFacade.notifyPlayerPositionChange(newPosition);  // Notifie les observateurs
    }

    /**
     * Notifies the game facade about a change in the player's objective.
     * This method is responsible for informing all relevant observers
     * of the new objective assigned to the player.
     * @param objective : the new objective index for the player, indicating
     *                  which objective has been changed or advanced to.
     *                  This integer represents the current state or progress
     *                  of the player's objectives within the game.
     */
    public void notifyPlayerObjective(int objective) {
        gameFacade.notifyPlayerObjectiveChange(objective);  // Notifie les observateurs
    }

    /**
     * Advances the game to the next player.
     * This method delegates the action to the GameFacade to update the current player
     * to the subsequent player in the sequence. It typically cycles through all players,
     * moving the turn to the next one and wrapping around to the first player if
     * the end of the player list is reached. It ensures that the game continues
     * smoothly from one turn to the next.
     */
    public void nextPlayer() {
        gameFacade.nextPlayer();  // Passe au joueur suivant
    }

    /**
     * Retrieves the current player in the game.
     * @return Player object representing the current player in the game
     */
    public Player getCurrentPlayer() {
        return gameFacade.getCurrentPlayer();  // Retourne le joueur actuel
    }

}
