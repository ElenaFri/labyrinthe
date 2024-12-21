package main.labyrinth.controllers;

import main.labyrinth.models.game.GameFacade;
import main.labyrinth.models.game.Gameboard;
import main.labyrinth.models.game.Player;
import main.labyrinth.models.geometry.Position;
import main.labyrinth.models.tiles.Tile;
import main.labyrinth.views.ViewsForObservers.GameBoardFacadeView;


import java.util.List;

public class UIController {
    private final GameboardController gameboardController;
    //private final Gameboard gameboard;
    private final GameFacadeController gameFacadeController;
    private final TourController tourController;

    /**
     * Constructs an instance of the UIController class.
     * @param gameboardController : controller responsible for managing the gameboard state and operations.
     * @param gameFacadeController : controller responsible for managing the game facade, including interactions
     *                              with the game's main mechanisms and player state.
     * @param tourController : controller responsible for managing the game turns and transitions
     *                       between players.
     */
    public UIController(GameboardController gameboardController,
                        GameFacadeController gameFacadeController,
                        TourController tourController) {
        this.gameboardController = gameboardController;
        this.gameFacadeController = gameFacadeController;
        this.tourController = tourController;
       // this.gameboard= gameboard;
    }

    /**
     * Handles the event triggered when the rotate tile button is clicked.
     * This method retrieves the current free tile from the gameboard and updates its orientation.
     * If a free tile exists, it increments its orientation clockwise by 90 degrees.
     * The orientation value is wrapped around at 360 degrees using modulo operation, as the range is 0 to 3.
     * After updating the orientation, the gameboard controller notifies observers of the modification,
     * enabling the view to update accordingly.
     */
    public  void onRotateTileClicked() {
        Tile freeTile = gameboardController.getGameboard().getFreeTile();
        if (freeTile != null) {
            int currentOrientation = freeTile.get_orientation();
            int newOrientation = (currentOrientation + 1) % 4;
            gameboardController.rotateTile(freeTile, newOrientation);
            // La vue sera notifiée par le biais de l'observer du Gameboard
        }
    }

    /**
     * Handles the event triggered when an arrow button is clicked.
     * This method determines the movement based on the specified direction and index.
     * It shifts a row or column of the gameboard depending on the arrow direction.
     * After modifying the gameboard, the model notifies the view via its observer mechanism.
     * @param direction : direction in which the arrow button was clicked. Supported values are "droite" (right), "gauche" (left),
     *                  "haut" (up), and "bas" (down).
     * @param index : ndex of the row or column to shift on the gameboard. For a row, it indicates the horizontal position.
     *                  For a column, it indicates the vertical position.
     */
    public void onArrowButtonClicked(String direction, int index) {
        switch (direction) {
            case "droite":
                gameboardController.shiftRow(index, 1);
                break;
            case "gauche":
                gameboardController.shiftRow(index, 3);
                break;
            case "haut":
                gameboardController.shiftColumn(index, 0);
                break;
            case "bas":
                gameboardController.shiftColumn(index, 2);
                break;
        }
        // Après avoir modifié le plateau, le modèle notifie la vue via l'observer.

    }

    /**
     * Updates the player's current position and objective based on the specified position.
     * It first saves the player's last position for potential future reference, then updates
     * the player's current position on the gameboard and manages their objective accordingly.
     * @param position : new position of the player on the gameboard.
     * @param gameBoardFacadeView : facade view providing an abstracted interface to the gameboard.
     * @param gameFacade : facade responsible for managing the game state and current player's state.
     */
    public void DeplacerJoueurEtObjectif(Position position, GameBoardFacadeView gameBoardFacadeView, GameFacade gameFacade)
    {
        System.out.println("Tuile cliquée : " + position);

        // Mettre à jour la position du joueur et la dernière position
        gameFacade.getCurrentPlayer().setLastPosition(gameFacade.getCurrentPlayer().getCurrentTile()); // Stocker l'ancienne position
        gameFacadeController.changePlayerPosition(position, gameBoardFacadeView); // Changer la position actuelle
        gameFacadeController.changePlayerObjective(this.gameboardController.getGameboard(), gameBoardFacadeView);
    }


}
