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

// Manages the game process.
public class GameController {
    private JFrame mainFrame;
    private Gameboard gameboard;
    private ImageStore imageStore;
    private GameFacade gameFacade;
    private GameFacadeController gameFacadeController;
    private TourController tourController;
    private GameBoardFacadeView gameView;

    /**
     * Initializes a new instance of the GameController class, sets up the mainFrame,
     * and initializes the game components.
     *
     * @param mainFrame : the main JFrame that serves as the primary window for the game
     */
    public GameController(JFrame mainFrame) {
        this.mainFrame = mainFrame;
        initializeGame();
    }

    /**
     * Initializes the game by resetting the main game window, setting up necessary models,
     * controllers, and views, and registering observers for game updates. This method ensures
     * that the game starts in a fresh state with all components properly initialized and displayed.
     * The method performs the following tasks:
     * - Removes all components from the main window and repaints it to clear the display.
     * - Instantiates core models including Gameboard, ImageStore, and GameFacade,
     * along with their associated controllers.
     * - Creates and configures the main game view and sets it up as an observer of the game state.
     * - Initializes a controller to oversee game turns and interactions.
     * - Refreshes the main game display to reflect the initialized state.
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
     * Resets the game to its initial state by invoking the initializeGame method.
     * This method ensures that all game components are reinitialized and the game
     * setup is restored to its default starting configuration. It is typically used
     * to restart the game after a session ends or when a full reset is necessary.
     */
    public void resetGame() {
        System.out.println("Réinitialisation du jeu..."); // Pour vérifier que la méthode est appelée
        initializeGame();
    }
}
