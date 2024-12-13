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


    public TourController( GameFacadeController gameFacadeController, GameBoardFacadeView gameBoardFacadeView,GameFacade gameFacade) {

        this.gameFacadeController = gameFacadeController;
        this.gameBoardFacadeView = gameBoardFacadeView;
        this.gameFacade=gameFacade;


    }

    public void TourSuivant()
    {
        gameFacade.nextPlayer();
        this.gameBoardFacadeView.ActiverFleche();
       this.gameBoardFacadeView.afficherTourSuivant(gameFacade.getCurrentPlayer());

    }
    public void FinJour()
    {

    }





}
