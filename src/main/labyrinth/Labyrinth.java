package main.labyrinth;

import main.labyrinth.models.data.ImageStore;
import main.labyrinth.models.game.GameFacade;
import main.labyrinth.models.game.Gameboard;
import main.labyrinth.views.ViewsForObservers.GameBoardFacadeView;

import javax.swing.*;

public  class Labyrinth {

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
		frame.pack();
		frame.setSize(1920, 1080);
		frame.setLocationRelativeTo(null); // Centrer la fenêtre
		frame.setVisible(true);
	}
}

