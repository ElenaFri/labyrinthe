package labyrinth.controllers;


import javax.swing.*;
import java.awt.*;

import labyrinth.models.data.ImageStore;
import labyrinth.models.game.GameFacade;
import labyrinth.models.game.Gameboard;
import labyrinth.models.game.Player;
import labyrinth.models.geometry.Position;
import labyrinth.views.ViewsForObservers.GameBoardFacadeView;
import labyrinth.views.ViewsForObservers.EndGameView;

public class GameController {
    private JFrame mainFrame;
    private Gameboard gameboard;
    private ImageStore imageStore;
    private GameFacade gameFacade;
    private GameFacadeController gameFacadeController;
    private TourController tourController;
    private GameBoardFacadeView gameView;

    /**
     * Constructeur de GameController.
     * @param mainFrame La fenêtre principale du jeu.
     */
    public GameController(JFrame mainFrame) {
        this.mainFrame = mainFrame;
        initializeGame();
    }

    /**
     * Initialise tous les composants du jeu.
     */
    public void initializeGame() {
        // Supprimer tous les composants existants pour réinitialiser
        mainFrame.getContentPane().removeAll();
        mainFrame.repaint();

        // Initialiser les modèles
        gameboard = new Gameboard(); // Adapter selon votre constructeur
        imageStore = new ImageStore(); // Adapter selon votre constructeur
        gameFacade = new GameFacade(); // Adapter selon votre constructeur
        gameFacadeController = new GameFacadeController(gameFacade);

        // Créer et afficher la vue principale du jeu avec le callback pour réinitialiser
        gameView = new GameBoardFacadeView(gameboard, gameFacade, imageStore, this::resetGame);
        gameView.setBounds(0, 0, 1920, 1080); // Ajuster selon votre layout
        mainFrame.add(gameView);

        // Enregistrer la vue en tant qu'observateur
        gameFacade.addGameFacadeObserver(gameView);

        // Créer le TourController
        tourController = new TourController(gameFacadeController, gameView, gameFacade);

        // Ajouter un bouton pour simuler une victoire (pour tester)
       // addSimulationButton();

        // Actualiser la fenêtre
        mainFrame.revalidate();
        mainFrame.repaint();
    }

    /**
     * Réinitialise le jeu en appelant initializeGame().
     */
    public void resetGame() {
        System.out.println("Réinitialisation du jeu..."); // Pour vérifier que la méthode est appelée
        initializeGame();
    }
}

