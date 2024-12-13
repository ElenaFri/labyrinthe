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
        //////////////efaccze l curent player du gamefacde
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
    public void TuileAccesibleAuPosition()
    {
        for (int x = 0; x < 7; x++) { // Parcourir les lignes
            for (int y = 0; y < 7; y++) { // Parcourir les colonnes
                Position position = new Position(x, y);
                gameBoardFacadeView.makeTileClickable(position);
            }
        }
        Position start = currentPlayer.getCurrentTile();


        gameBoardFacadeView.showAdjacentAccessibleTiles(start); // Méthode pour activer/désactiver les boutons
    }
   /* public void changePlayerPositionAndObjective(Position position,Gameboard gameBoard) {
        // Mettre à jour la position du joueur et la dernière position
        gameFacade.getCurrentPlayer().setLastPosition(gameFacade.getCurrentPlayer().getCurrentTile());
        gameFacadeController.changePlayerPosition(position, gameBoard);

        // Modifier l'objectif du joueur
        gameFacadeController.changePlayerObjective(gameBoard);

        // Afficher l'index de l'objectif actuel
        System.out.println("/////////////////////////////////////////////////////////////////////: l index de l objectif current est " + gameFacade.getCurrentPlayer().getCurrentObjective());
    }*/
    public void TourSuivant()
    {
        gameFacadeController.nextPlayer();
    }


    // Méthode pour attendre que le joueur choisisse une tuile

}
