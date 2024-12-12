package main.labyrinth;

import main.labyrinth.models.data.ImageStore;
import main.labyrinth.models.game.GameFacade;
import main.labyrinth.models.game.Gameboard;
import main.labyrinth.views.ViewsForObservers.GameBoardFacadeView;

import javax.swing.*;
import java.awt.*;

public class Labyrinth {

	public static void main(String[] args) {
		// Initialisation du modèle et des composants nécessaires
		Gameboard gameboard = new Gameboard();
		GameFacade gameFacade = new GameFacade();
		ImageStore imageStore = new ImageStore();

		// Créez l'interface graphique
		JFrame frame = new JFrame("Labyrinth Game");
		GameBoardFacadeView view = new GameBoardFacadeView(gameboard, gameFacade, imageStore);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(view);
		frame.pack(); // Ajuste la taille de la fenêtre pour contenir le JPanel

		// Maximiser la fenêtre à son ouverture
		//frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Ouvrir en mode maximisé
		// Passer en mode plein écran
		GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice graphicsDevice = graphicsEnvironment.getDefaultScreenDevice();

		// Vérifiez si le mode plein écran est supporté
		if (graphicsDevice.isFullScreenSupported()) {
			frame.dispose(); // Fermer la fenêtre actuelle
			frame.setUndecorated(true); // Supprimer les bordures de la fenêtre
			graphicsDevice.setFullScreenWindow(frame); // Mettre la fenêtre en plein écran
		}

		frame.setLocationRelativeTo(null); // Centrer la fenêtre
		frame.setVisible(true);
	}
}