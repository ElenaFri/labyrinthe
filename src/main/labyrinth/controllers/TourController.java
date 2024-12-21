package main.labyrinth.controllers;

import main.labyrinth.models.game.GameFacade;
import main.labyrinth.models.game.Gameboard;
import main.labyrinth.models.game.Player;
import main.labyrinth.models.geometry.Position;
import main.labyrinth.views.ViewsForObservers.GameBoardFacadeView;

import java.util.List;

public class TourController {
    private GameFacadeController gameFacadeController;
    private GameBoardFacadeView gameBoardFacadeView;
    private GameFacade gameFacade;

    /**
     * Constructs a TourController object used to manage the game tour logic.
     * @param gameFacadeController : controller that manages the interaction between the game state and the user interface
     * @param gameBoardFacadeView : view responsible for displaying game information and updating game UI components
     * @param gameFacade : core model representing the game state and logic
     */
    public TourController( GameFacadeController gameFacadeController, GameBoardFacadeView gameBoardFacadeView,GameFacade gameFacade) {
        this.gameFacadeController = gameFacadeController;
        this.gameBoardFacadeView = gameBoardFacadeView;
        this.gameFacade=gameFacade;


    }

    /**
     * Manages the actions required to advance the game to the next player's turn.
     * This method handles updating the game state, activating relevant user interface
     * components, and checking if the game has ended. If the game is over, it will display
     * the end-game view with the winner information.
     * Responsibilities:
     * - Advances to the next player by invoking the gameFacade's nextPlayer method.
     * - Activates the arrow buttons on the game interface using the ActiverFleche method.
     * - Updates the interface to indicate whose turn it is through afficherTourSuivant.
     * - Verifies if the game is finished using the isGameOver method in gameFacade. If the game
     *   is over, it retrieves the winner and invokes the showEndGameView method to show the
     *   final game results.
     * Precondition:
     * The gameFacade, gameBoardFacadeView, and other required objects should be properly initialized
     * before calling this method to ensure accurate state management and UI updates.
     * Postcondition:
     * - The game advances to the next player.
     * - The UI is updated to reflect the current player's turn.
     * - If the game is over, the end game view is displayed, showing the winner.
     */
    public void TourSuivant() {
        gameFacade.nextPlayer();
        this.gameBoardFacadeView.ActiverFleche();
        this.gameBoardFacadeView.afficherTourSuivant(gameFacade.getCurrentPlayer());

        // Vérifier si le jeu est terminé
        if (gameFacade.isGameOver()) {
            Player winner = gameFacade.getWinner();
            if (winner != null) {
                showEndGameView(winner);
            }
        }
    }

    /**
     * Displays the end-game view to indicate the winner of the game.
     * This method invokes the game board view to present the end-game interface,
     * including the victorious player's details.
     * @param winner : Player object representing the winner of the game. This player's information will be displayed in the end-game view.
     */
    public void showEndGameView(Player winner) {
        gameBoardFacadeView.showEndGameView(winner);
    }
}
