package labyrinth.controllers;

import labyrinth.models.game.Card;
import labyrinth.models.game.GameFacade;
import labyrinth.models.game.Gameboard;
import labyrinth.models.game.Player;
import labyrinth.models.geometry.Position;
import labyrinth.views.ViewsForObservers.GameBoardFacadeView;

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


    }

    /**
     * Changes the current objective of the player in the game.
     * This method checks if the player has reached their current objective and,
     * if so, progresses the player to the next objective.
     * Additionally, it validates if the player has completed all objectives.
     * @param gameboard : current gameboard containing the layout and objectives
     * @param gameBoardFacadeView : interface view to facilitate player notifications and updates in the game
     */
    public void changePlayerObjective(Gameboard gameboard,GameBoardFacadeView gameBoardFacadeView) {
        if (aAtteintObjectif(gameboard)) {

            // Marquer l'objectif actuel comme trouvé
            Player currentPlayer = this.getCurrentPlayer();
            Card currentObjective = currentPlayer.getCurrentObjective();
            gameFacade.getCurrentPlayer().getCurrentObjective().setFound(true);


        System.out.println("poooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo"+ currentObjective.isFound());
            gameBoardFacadeView.afficherFelicitation(gameFacade.getCurrentPlayer());
            System.out.println("poooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo");
            // Passer à l'objectif suivant
            gameFacade.playerNextObjective();
            //gameBoardFacadeView.repaint();

            // Optionnel : vérifier si le joueur a terminé tous ses objectifs
            if (currentPlayer.hasCompletedAllObjectives()) {
                System.out.println("Le joueur " + currentPlayer.getName() + " a terminé tous ses objectifs !");
            }
        }
    }

    /**
     * Determines whether the current player has reached their objective on the gameboard.
     * @param gameboard : current gameboard containing the layout and objectives
     * @return true if the current player's position matches the objective's position, false otherwise
     */
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
}
