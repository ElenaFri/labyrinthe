package main.labyrinth.controllers;

import main.labyrinth.models.game.GameFacade;
import main.labyrinth.models.game.Gameboard;
import main.labyrinth.models.game.Player;
import main.labyrinth.models.geometry.Position;
import main.labyrinth.views.ViewsForObservers.GameBoardFacadeView;

import java.util.List;

public class TourController {
    private GameFacade gameFacade;
    private Gameboard gameboard;
    private GameFacadeController gameFacadeController;
    private GameBoardFacadeView gameBoardFacadeView;
    private Player currentPlayer;

    public TourController(GameFacade gameFacade, Gameboard gameboard, GameFacadeController gameFacadeController, GameBoardFacadeView gameBoardFacadeView) {
        this.gameFacade = gameFacade;
        this.gameboard = gameboard;
        this.gameFacadeController = gameFacadeController;
        this.gameBoardFacadeView = gameBoardFacadeView;
        this.currentPlayer = gameFacade.getCurrentPlayer();
    }

    // Méthode pour afficher les tuiles accessibles et gérer le déplacement
   /* public void handlePlayerTurn() {
        //gameBoardFacadeView.cliquerSurButtonFleche();
        Position start = gameFacade.getCurrentPlayer().getCurrentTile();
        List<Position> accessibleTiles = gameboard.getAllAccessibleTiles(start);
        System.out.println("Tuiles accessibles : " + accessibleTiles);
        gameBoardFacadeView.showAdjacentAccessibleTiles(start); // Méthode pour activer/désactiver les boutons
       // gameBoardFacadeView.gererDeplacementPionParPionEtTrouverObjectif();
        gameFacadeController.nextPlayer();
    }*/


    // Méthode pour attendre que le joueur choisisse une tuile

}
