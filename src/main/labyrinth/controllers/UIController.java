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

    public UIController(GameboardController gameboardController,
                        GameFacadeController gameFacadeController,
                        TourController tourController) {
        this.gameboardController = gameboardController;
        this.gameFacadeController = gameFacadeController;
        this.tourController = tourController;
       // this.gameboard= gameboard;
    }

    public  void onRotateTileClicked() {
        Tile freeTile = gameboardController.getGameboard().getFreeTile();
        if (freeTile != null) {
            int currentOrientation = freeTile.get_orientation();
            int newOrientation = (currentOrientation + 1) % 4;
            gameboardController.rotateTile(freeTile, newOrientation);
            // La vue sera notifiée par le biais de l'observer du Gameboard
        }
    }


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
    public void DeplacerJoueurEtObjectif(Position position, GameBoardFacadeView gameBoardFacadeView, GameFacade gameFacade)
    {
        System.out.println("Tuile cliquée : " + position);

        // Mettre à jour la position du joueur et la dernière position
        gameFacade.getCurrentPlayer().setLastPosition(gameFacade.getCurrentPlayer().getCurrentTile()); // Stocker l'ancienne position
        gameFacadeController.changePlayerPosition(position, gameBoardFacadeView); // Changer la position actuelle
        gameFacadeController.changePlayerObjective(this.gameboardController.getGameboard(), gameBoardFacadeView);
    }


}
