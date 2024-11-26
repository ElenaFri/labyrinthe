package data;

import java.awt.image.BufferedImage;

class Screen {
    private BufferedImage _mainScreen;  // Image pour l'écran principal
    private BufferedImage _gameoverScreen; // Image pour l'écran de fin de jeu

    // Constructeur
    public Screen() {
        // Initialisation des images pour les écrans
        _mainScreen = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);  // Exemple d'écran principal
        _gameoverScreen = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);  // Exemple d'écran de fin de jeu
    }

    // Méthode pour récupérer l'image de l'écran en fonction de l'état du jeu
    public BufferedImage getScreenImage(boolean isRunning) {
        return isRunning ? _mainScreen : _gameoverScreen;  // Si le jeu est en cours, renvoyer l'écran principal, sinon l'écran de fin de jeu
    }
}