package labyrinth;

import javax.swing.*;
import java.awt.*;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

import labyrinth.controllers.GameController;
import labyrinth.controllers.GameFacadeController;
import labyrinth.controllers.TourController;
import labyrinth.models.game.GameFacade;
import labyrinth.models.game.Gameboard;
import labyrinth.models.game.Player;
import labyrinth.models.data.ImageStore;
import labyrinth.models.geometry.Position;
import labyrinth.views.ViewsForObservers.EndGameView;
import labyrinth.views.ViewsForObservers.GameBoardFacadeView;

/**
 * Implements the classical Labyrinth game.
 * See here for details: https://www.regledujeu.fr/labyrinthe/.
 *
 * @author Yasmine Chettati
 * @author Elena Frison
 */
public class Labyrinth {
	/**
	 * Plays the Labyrinth game.
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			// Démarrer la lecture de la musique dans un thread séparé
			new Thread(() -> {
				playMusic("res/snd/la_moldau.wav");
			}).start();

			// Jeu
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

	/**
	 * Plays an audio file continuously in a separate thread.
	 * This method blocks to ensure the clip remains playing until interrupted or stopped.
	 * @param filePath : file path of the audio file to play
	 */
	private static void playMusic(String filePath) {
		try {
			File musicFile = new File(filePath);
			if (!musicFile.exists()) {
				System.out.println("Fichier audio introuvable : " + musicFile.getAbsolutePath());
				return;
			}

			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(musicFile);
			Clip clip = AudioSystem.getClip();
			clip.open(audioInputStream);
			clip.loop(Clip.LOOP_CONTINUOUSLY);
			clip.start(); // Démarrer la lecture

			// Garder le clip en marche jusqu'à ce qu'il soit arrêté
			// Ainsi, le programme ne se termine pas avant que le clip ne soit arrêté
			while (true) {
				Thread.sleep(1000);
			}
		} catch (UnsupportedAudioFileException e) {
			System.out.println("Format de fichier non pris en charge : " + e);
		} catch (IOException e) {
			System.out.println("Erreur d'entrée/sortie : " + e);
		} catch (LineUnavailableException e) {
			System.out.println("Ligne audio non disponible : " + e);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt(); // Réinitialiser l'état d'interruption
			System.out.println("Le thread de musique a été interrompu : " + e);
		}
	}
}
