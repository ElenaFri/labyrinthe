package main.labyrinth.controllers;

import main.labyrinth.models.game.Card;
import main.labyrinth.models.game.GameFacade;
import main.labyrinth.models.game.Gameboard;
import main.labyrinth.models.game.Player;
import main.labyrinth.models.geometry.Position;
import main.labyrinth.views.ViewsForObservers.GameBoardFacadeView;

import java.util.List;

// Manipulates GameFacade.
public class GameFacadeController {
    private final GameFacade gameFacade;
    //private final GameBoardFacadeView gameBoardFacadeView;

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
    public void changePlayerPosition(Position newPosition, GameBoardFacadeView gameBoardFacadeView) {
        // Déplace le joueur via le gameFacade
        gameFacade.movePlayer(newPosition, getCurrentPlayer());
        System.out.println("Joueur déplacé : " + getCurrentPlayer().getName());

        // Récupérer les nouvelles positions des joueurs sous forme de tableau de Position
        Position[] updatedPositions = gameFacade.getPlayersPositions(); // getplayerspos()  retourner un tableau de Position[]
        System.out.println("les nouvelles positions du joueurs sontnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn   "+  updatedPositions);

        // Met à jour les positions sur le plateau
        gameBoardFacadeView.setPlayerPositions(updatedPositions); // Met à jour le plateau avec les nouvelles positions

       ////////////:faux ajouter une methode updateon setposition

    }




    /**
     * Advances the player's objective to the next one.
     */
    public void changePlayerObjective(Gameboard gameboard) {
        if(aAtteintObjectif(gameboard)){ gameFacade.playerNextObjective();}
       // gameFacade.playerNextObjective();
    }
    public boolean aAtteintObjectif(Gameboard gameboard) {
        // Récupérer l'objectif actuel du joueur
        Card currentObjective = this.getCurrentPlayer().getCurrentObjective();

        // Vérifier si la position actuelle du joueur correspond à l'emplacement de l'objectif sur le plateau
        Position objectivePosition = gameboard.getObjectivePosition(currentObjective.getTreasure());

        // Comparer la position actuelle du joueur avec celle de l'objectif
        return this.getCurrentPlayer().getCurrentTile().equals(objectivePosition);
    }


    /**
     * Advances the game to the next player.
     */
    public void nextPlayer() {
        gameFacade.nextPlayer();
    }
    public void changePlayerLastPosition(Position position)
    {
        this.getCurrentPlayer().setLastPosition(position);
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
