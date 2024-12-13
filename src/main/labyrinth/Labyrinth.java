package main.labyrinth;

import javax.swing.*;
import java.awt.*;

import main.labyrinth.controllers.GameFacadeController;
import main.labyrinth.controllers.TourController;
import main.labyrinth.models.game.GameFacade;
import main.labyrinth.models.game.Gameboard;
import main.labyrinth.models.game.Player;
import main.labyrinth.models.data.ImageStore;
import main.labyrinth.models.geometry.Position;
import main.labyrinth.views.ViewsForObservers.EndGameView;
import main.labyrinth.views.ViewsForObservers.GameBoardFacadeView;

public class Labyrinth {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			try {
				// Créer la fenêtre principale
				JFrame mainFrame = new JFrame("Mon Jeu Labyrinthe");
				mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				mainFrame.setSize(1920, 1080);
				mainFrame.setLayout(null); // Utilisation d'un layout nul (absolu).
				mainFrame.setLocationRelativeTo(null); // Centrer la fenêtre sur l'écran

				// Définir une méthode d'initialisation pour pouvoir la réutiliser
				Runnable initializeGame = new Runnable() {
					@Override
					public void run() {
						// Supprimer tous les composants existants pour réinitialiser
						mainFrame.getContentPane().removeAll();
						mainFrame.repaint();

						// Initialiser le modèle
						Gameboard gameboard = new Gameboard(); // Adapter selon votre constructeur
						ImageStore imageStore = new ImageStore(); // Adapter selon votre constructeur
						GameFacade gameFacade = new GameFacade(); // Adapter selon votre constructeur
						GameFacadeController gameFacadeController = new GameFacadeController(gameFacade);

						// Créer et afficher la vue principale du jeu avec le callback pour réinitialiser
						GameBoardFacadeView gameView = new GameBoardFacadeView(gameboard, gameFacade, imageStore, this::run);
						gameView.setBounds(0, 0, 1920, 1080); // Ajuster selon votre layout
						mainFrame.add(gameView);

						// Enregistrer la vue en tant qu'observateur
						gameFacade.addGameFacadeObserver(gameView);

						// Créer le TourController
						TourController tourController = new TourController(gameFacadeController, gameView, gameFacade);



						// Actualiser la fenêtre
						mainFrame.revalidate();
						mainFrame.repaint();
					}


				};

				// Initialiser le jeu pour la première fois
				initializeGame.run();

				mainFrame.setVisible(true);

			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "Erreur lors de l'initialisation du jeu : " + e.getMessage());
				System.exit(1);
			}
		});
	}
}
