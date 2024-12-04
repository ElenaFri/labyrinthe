package main.labyrinth;

import main.labyrinth.models.game.*;
import javax.swing.*;
import java.awt.*;

public class GameWindow extends JFrame {
    private Gameboard gameboard;
    private GameFacade gameFacade;  // Gestionnaire principal du jeu
    private static final int TILE_SIZE = 143; // Taille de chaque tuile
    private static final int PLAYER_SIZE = 90; // Taille des joueurs
    private static final int BOARD_SIZE = 1000; // Taille du plateau
    private static final int WINDOW_WIDTH = 1920;  // Taille de la fenêtre
    private static final int WINDOW_HEIGHT = 1080; // Taille de la fenêtre

    public GameWindow(GameFacade gameFacade) {
        this.gameFacade = gameFacade;
        gameboard = new Gameboard();

        // Définir la taille de la fenêtre
        setTitle("Labyrinth Game");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);  // Taille de la fenêtre 1920x1080
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);  // Centrer la fenêtre à l'écran

        // Ajoutez un panneau pour dessiner le plateau de jeu et les joueurs
        add(new GamePanel(gameboard, gameFacade));

        setVisible(true);
    }

    public static void main(String[] args) {
        // Initialisation de la logique principale du jeu
        GameFacade gameFacade = new GameFacade();

        // Création de la fenêtre du jeu avec GameFacade
        SwingUtilities.invokeLater(() -> new GameWindow(gameFacade));
    }
}
