package main.labyrinth.controllers;

import main.labyrinth.models.game.Gameboard;
import main.labyrinth.models.game.Player;
import main.labyrinth.models.geometry.Position;
import main.labyrinth.models.tiles.Tile;
import main.labyrinth.views.ViewsForObservers.GameBoardFacadeView;

import java.util.List;

public class TourController {
    private Player joueur;
    private Gameboard plateau;
    private GameFacadeController gameFacadeController;
    private GameBoardFacadeView gameBoardFacadeView;

    private boolean objectifAtteint;

    public TourController(Player joueur, Gameboard plateau) {
        this.joueur = joueur;
        this.plateau = plateau;

    }

    public void commencerTour() {

        // Étape 2 : Le joueur choisit une flèche (condition sur les joueurs en face, etc.)
        //des que il choisit la fleche on bloque le button rotatetile
            if (aAtteintObjectif()) {
                ////////:un label au lieu de ca
                System.out.println("Objectif atteint !");
            }


        // Étape 5 : Fin du tour
        finDuTour();
    }





    private boolean aAtteintObjectif() {
        // Récupérer la position actuelle du joueur
        Position currentPosition = joueur.getCurrentTile();

        // Vérifier si cette position est dans la liste des positions des objectifs
        List<Position> objectifs = plateau.getObjectivePositions();

        return objectifs.contains(currentPosition);
    }

    private void finDuTour() {

        System.out.println("Fin du tour du joueur " + joueur.getName());
        this.gameFacadeController.nextPlayer();


    }
}

