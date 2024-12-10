package main.labyrinth.controllers;

import main.labyrinth.models.game.GameFacade;
import main.labyrinth.models.game.Player;
import main.labyrinth.models.geometry.Position;

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
        return currentPlayer.getCurrentTile(); // Retourne la position du joueur actuel
    }

    /**
     * Retrieves the current objective of the current player in the game.
     * @return a String representing the description of the current player's objective
     */
    public String getCurrentPlayerObjective() {
        Player currentPlayer = gameFacade.getCurrentPlayer();
        return "Objectif actuel: " + currentPlayer.getCurrentObjective().getTreasure();
    }

    /**
     * Notifies the game facade about a change in the player's position.
     * @param newPosition : new Position object representing the player's updated coordinates on the game board
     */
    public void notifyPlayerPosition(Position newPosition) {
        gameFacade.notifyPlayerPositionChange(newPosition);
    }

    /**
     * Notifies the game facade about a change in the player's objective.
     * @param objective : the new objective index for the player
     */
    public void notifyPlayerObjective(int objective) {
        gameFacade.notifyPlayerObjectiveChange(objective);
    }

    /**
     * Updates the player's position on the game board.
     * @param newPosition : the new position of the player
     */
    public void changePlayerPosition(Position newPosition) {
        gameFacade.movePlayer(newPosition);
    }

    /**
     * Advances the player's objective to the next one.
     */
    public void changePlayerObjective() {
        gameFacade.playerNextObjective();
    }

    /**
     * Advances the game to the next player.
     */
    public void nextPlayer() {
        gameFacade.nextPlayer();
    }

    /**
     * Retrieves the current player in the game.
     * @return Player object representing the current player
     */
    public Player getCurrentPlayer() {
        return gameFacade.getCurrentPlayer();
    }

    /**
     * Displays a message to the player indicating it's their turn.
     * @return A string message for the current player
     */
    public String displayTurnMessage() {
        Player currentPlayer = gameFacade.getCurrentPlayer();
        return "C'est à votre tour, " + currentPlayer.getName() + ". Votre objectif actuel est : " + getCurrentPlayerObjective();
    }

    /**
     * Checks if it is the given player's turn.
     * @param player : the player to check
     * @return true if it is the player's turn, false otherwise
     */
    public boolean isPlayerTurn(Player player) {
        return player.equals(gameFacade.getCurrentPlayer());
    }

    /**
     * Ends the current player's turn and moves to the next player.
     */
    public void endTurn() {
        System.out.println("Fin du tour de " + getCurrentPlayer().getName());
        nextPlayer();
        System.out.println("C'est maintenant le tour de " + getCurrentPlayer().getName());
    }

    /**
     * Checks if the game is over.
     * @return true if the game is over, false otherwise
     */
    public boolean isGameOver() {
        return gameFacade.isGameOver();
    }
}
