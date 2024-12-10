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
<<<<<<< Updated upstream

    /**
     * Retrieves the current position of the player in the labyrinth.
     * This method delegates the call to the game facade to get the current player
     * and then returns the player's current tile position.
     * @return current Position object representing the player's coordinates on the gameboard
     */
=======
    // Méthode pour obtenir la position du joueur actuel
>>>>>>> Stashed changes
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

<<<<<<< Updated upstream
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
=======

    public void ChangePlayerPosition(Position newPosition) {
        gameFacade.movePlayer(newPosition);
    }

    // Méthode pour notifier l'objectif du joueur
    public void ChangePlayerObjective(int objective) {
        gameFacade.PlayernextObjective();
>>>>>>> Stashed changes
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

    // Méthode pour afficher un message au joueur pour l'informer que c'est son tour
    public String displayTurnMessage() {
        Player currentPlayer = gameFacade.getCurrentPlayer();
        return "C'est à votre tour, " + currentPlayer.getName() + ". Votre objectif actuel est : " + getCurrentPlayerObjective();
    }


    // Méthode pour vérifier si c'est le bon joueur
    public boolean isPlayerTurn(Player player) {
        return player.equals(gameFacade.getCurrentPlayer());  // Vérifie si c'est le tour du joueur donné
    }

    // Méthode pour gérer la fin du tour du joueur
    public void endTurn() {
        System.out.println("Fin du tour de " + getCurrentPlayer().getName());
        nextPlayer();  // Passer au joueur suivant
        System.out.println("C'est maintenant le tour de " + getCurrentPlayer().getName());
    }

    // Méthode pour vérifier si la partie est terminée
    public boolean isGameOver() {
        return gameFacade.isGameOver();  // Vérifie la condition de fin de partie
    }
}
