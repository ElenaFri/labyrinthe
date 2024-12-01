package main.labyrinth.models.data;

import main.labyrinth.views.helpers.ImageHelper;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import main.labyrinth.models.data.Screen;

public class ImageStore {

    private BufferedImage[] _tileImages;
    private BufferedImage[] _cardImages;       // Recto et verso des cartes
    private BufferedImage[] _treasureImages;   // Trésors à superposer
    private BufferedImage[] _pieceImages;      // 4 pions de couleurs différentes
    private BufferedImage _handBackground;     // Zone joueur
    private Screen _screen;

    // Constructeur
    public ImageStore() {
        _tileImages = chargerImagesPourTuiles();
        _cardImages = chargerImagesPourCartes();
        _treasureImages = chargerImagesPourTresors();
        _pieceImages = chargerImagesPourPions();
        _handBackground = chargerImageDeFond();
        try {
            _screen = new Screen();
        } catch (IOException e) {
            e.printStackTrace();
            _screen = null; // Optionnel, selon le comportement attendu.
        }
    }

    // Obtenir une image de tuile avec une rotation et éventuellement un trésor
    public BufferedImage getTileImage(int index, int orientation, boolean withTreasure, int treasureIndex) throws IOException {
        if (index < 0 || index >= _tileImages.length) {
            throw new IllegalArgumentException("Index de tuile invalide.");
        }
        // Charger la base de la tuile
        BufferedImage baseTile = _tileImages[index]; // Correction: Accès direct à l'index

        // Ajouter le trésor si demandé
        BufferedImage tileWithTreasure = baseTile;
        if (withTreasure) {
            tileWithTreasure = ImageHelper.merge(
                    "assets/images/tiles/tile_" + index + ".png",
                    "assets/images/treasures/treasure_" + treasureIndex + ".png"
            );
        }

        // Appliquer la rotation sur la tuile combinée
        switch (orientation) {
            case 90:
                return ImageHelper.rotateClockwise(tileWithTreasure);
            case 180:
                return ImageHelper.rotate(tileWithTreasure, Math.PI);
            case 270:
                return ImageHelper.rotateCounterClockwise(tileWithTreasure);
            default:
                return tileWithTreasure; // Pas de rotation
        }
    }

    // Obtenir une image de carte
    public BufferedImage getCardImage(int index, boolean isOpen) {
        return _cardImages[isOpen ? index * 2 : index * 2 + 1];
    }

    // Obtenir une image de pion
    public BufferedImage getPieceImage(int index) {
        return _pieceImages[index];
    }

    // Obtenir le fond de la zone joueur
    public BufferedImage getHandBackground() {
        return _handBackground;
    }

    // Obtenir l'écran principal ou de fin de jeu
    /**
     * Obtenir l'écran principal ou de fin de jeu
     *
     * @param isRunning True si le jeu est en cours, false si le jeu est terminé
     * @return L'image de l'écran correspondant
     */
    public BufferedImage getScreen(boolean isRunning) {
        return _screen.getScreenImage(isRunning);
    }

    // Charger les images des tuiles
    private BufferedImage[] chargerImagesPourTuiles() {
        BufferedImage[] tileImages = new BufferedImage[3]; // 16 tuiles
        for (int i = 0; i < 3; i++) {
            tileImages[i] = loadImage("assets/images/tiles/tile_" + i + ".png");
        }
        return tileImages;
    }

    // Charger les images des cartes (recto et verso)
    private BufferedImage[] chargerImagesPourCartes() {
        BufferedImage[] cardImages = new BufferedImage[25]; // 24 cartes + 1 verso
        for (int i = 0; i < 25; i++) {
            cardImages[i] = loadImage("assets/images/cards/card_" + i + ".png");
        }
        cardImages[24] = loadImage("assets/images/cards/card_back.png"); // Carte verso
        return cardImages;
    }

    // Charger les images des trésors
    private BufferedImage[] chargerImagesPourTresors() {
        BufferedImage[] treasureImages = new BufferedImage[14];
        for (int i = 0; i < 14; i++) {
            treasureImages[i] = loadImage("assets/images/treasures/treasure_" + i + ".png");
        }
        return treasureImages;
    }

    // Charger les images des pions
    private BufferedImage[] chargerImagesPourPions() {
        BufferedImage[] pieceImages = new BufferedImage[4]; // 4 pions de couleurs différentes
        for (int i = 0; i < 4; i++) {
            pieceImages[i] = loadImage("assets/images/pieces/piece_" + i + ".png");
        }
        return pieceImages;
    }

    // Charger l'image de fond de la zone joueur
    private BufferedImage chargerImageDeFond() {
        return loadImage("assets/images/background/hand_background.png");
    }

    // Méthode utilitaire pour charger une image depuis un chemin donné
    private BufferedImage loadImage(String path) {
        try {
            File file = new File(path);
            if (file.exists()) {
                return ImageIO.read(file);
            } else {
                System.err.println("Image non trouvée : " + path);
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
