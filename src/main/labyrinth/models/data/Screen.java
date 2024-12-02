package main.labyrinth.models.data;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Screen {
    private BufferedImage _mainScreen;      // Écran principal du jeu
    private BufferedImage _gameoverScreen; // Écran de fin de jeu

    /**
     * Constructeur : charge les images des écrans
     */
    public Screen() throws IOException {
        _mainScreen = ImageIO.read(new File("/home/elena/Documents/a-31-labyrinthe/res/img/screens/main_screen.png"));
        _gameoverScreen = ImageIO.read(new File("/home/elena/Documents/a-31-labyrinthe/res/img/screens/gameover_screen.png"));
    }

    /**
     * Retourne l'image appropriée selon l'état du jeu.
     *
     * @param isRunning true si le jeu est en cours, false si le jeu est terminé.
     * @return l'image de l'écran correspondant
     */
    public BufferedImage getScreenImage(boolean isRunning) {
        return isRunning ? _mainScreen : _gameoverScreen;
    }
}
