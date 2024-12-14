package main.labyrinth;

import javax.swing.*;
import java.awt.*;

import main.labyrinth.controllers.GameController;
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
			JFrame mainFrame = new JFrame("Mon Jeu Labyrinthe");
			mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			mainFrame.setSize(1920, 1080);
			mainFrame.setLayout(null); // Utilisation d'un layout nul (absolu).
			mainFrame.setLocationRelativeTo(null); // Centrer la fenêtre sur l'écran

			try {
				// Créer une instance de GameController en passant la fenêtre principale
				GameController gameController = new GameController(mainFrame);

				// Rendre la fenêtre visible
				mainFrame.setVisible(true);

				// Passer en mode plein écran
				GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
				GraphicsDevice graphicsDevice = graphicsEnvironment.getDefaultScreenDevice();

				// Vérifiez si le mode plein écran est supporté
				if (graphicsDevice.isFullScreenSupported()) {
					mainFrame.dispose(); // Fermer la fenêtre actuelle
					mainFrame.setUndecorated(true); // Supprimer les bordures de la fenêtre
					graphicsDevice.setFullScreenWindow(mainFrame); // Mettre la fenêtre en plein écran
				}
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "Erreur lors de l'initialisation du jeu : " + e.getMessage());
				System.exit(1);
			}
		});
	}
}
