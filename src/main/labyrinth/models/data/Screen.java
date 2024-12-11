package main.labyrinth.models.data;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

// Screen resources provider.
public class Screen {
    private BufferedImage _mainScreen;      // Écran principal du jeu
    private BufferedImage _gameoverScreen; // Écran de fin de jeu

    /**
     * Constructs a new Screen instance and loads the necessary images for the main
     * and game over screens. This constructor attempts to read the images from
     * predefined file paths and throws an IOException if any image cannot be loaded.
     * @throws IOException if there is an issue reading the image files from disk
     */
    public Screen() throws IOException {
        _mainScreen = ImageIO.read(new File("../res/img/screens/main_screen.png"));
        _gameoverScreen = ImageIO.read(new File("../res/img/screens/gameover_screen.png"));
    }

    /**
     * Returns the appropriate screen image based on the current game state.
     * @param isRunning : a boolean indicating whether the game is currently running.
     *                  If true, the main screen image is returned. If false, the
     *                  game over screen image is returned.
     * @return a BufferedImage representing the screen to be displayed for the
     *         current game state
     */
    public BufferedImage getScreenImage(boolean isRunning) {
        return isRunning ? _mainScreen : _gameoverScreen;
    }
}
