package main.labyrinth;
import main.labyrinth.models.game.*;
import javax.swing.*;
import java.awt.*;

public class GameWindow extends JFrame {
    private Gameboard gameboard;

    public GameWindow() {
        gameboard = new Gameboard();
        setTitle("Labyrinth Game");
        setSize(1000, 1000);  // Taille de la fenêtre
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);  // Centrer la fenêtre à l'écran

        // Ajoutez un panneau pour dessiner le plateau de jeu
        add(new GamePanel(gameboard));

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GameWindow());
    }
}
