package main.labyrinth.models.data;

import main.labyrinth.models.game.Card;
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
    private BufferedImage[] playerIcons;
    private Screen _screen;

    // Constructeur
    public ImageStore() {
        _tileImages = chargerImagesPourTuiles();
        _cardImages = chargerImagesPourCartes();
        _treasureImages = chargerImagesPourTresors();
        _pieceImages = chargerImagesPourPions();
        _handBackground = chargerImageDeFond();
        this.playerIcons = chargerImagesPourJoueurs();
        try {
            _screen = new Screen();
        } catch (IOException e) {
            e.printStackTrace();
            _screen = null; // Optionnel, selon le comportement attendu.
        }
    }
    public BufferedImage getCardWithTreasure(int index, boolean isOpen) {
        if (index < 0 || index >= 24) { // 24 cartes au total
            throw new IllegalArgumentException("Index de carte invalide.");
        }
        try {
            // Si la carte est ouverte, on retourne l'image correspondante à l'index (entre 0 et 23).
            // Sinon, on retourne l'image du dos de la carte (index 24).
            if (isOpen) {
                // Essayer de fusionner l'image de la carte avec le trésor associé
                return ImageHelper.merge(
                        "/home/ychettati/Bureau/a-31-labyrinthe/res/img/cards/cardFront.png",
                        "/home/ychettati/Bureau/a-31-labyrinthe/res/img/treasures/treasure" + index + ".png"
                );
            } else {
                // Si la carte est fermée, retourner l'image du dos de la carte
                return _cardImages[24];  // L'image du dos de la carte est toujours à l'indice 24.
            }
        } catch (IOException e) {
            // En cas d'exception (par exemple si une image est introuvable), on log l'erreur
            System.err.println("Erreur lors du chargement des images : " + e.getMessage());
            e.printStackTrace();
            return null;  // Retourner null ou une image par défaut si une erreur survient.
        }
    }



    // Obtenir une image de tuile avec une rotation et éventuellement un trésor
    // index 0 pour tile angle , 1 pour tile droite , 2 pour les tiles T
    public BufferedImage getTileImage(int index, int orientation, boolean withTreasure, int treasureIndex) throws IOException {
        if (index < 0 || index >= _tileImages.length) {
            throw new IllegalArgumentException("Index de tuile invalide.");
        }

        // Charger la base de la tuile
        BufferedImage baseTile = _tileImages[index];

        // Ajouter le trésor si demandé
        BufferedImage tileWithTreasure = baseTile;
        if (withTreasure) {
            tileWithTreasure = ImageHelper.merge(
                    "/home/ychettati/Bureau/a-31-labyrinthe/res/img/tiles/tile_" + index + ".png",
                    "/home/ychettati/Bureau/a-31-labyrinthe/res/img/treasures/treasure" + treasureIndex + ".png"
            );
        }

        // Affichage des informations de débogage pour vérifier l'orientation
        System.out.println("Orientation de la tuile: " + orientation);

        // Appliquer la rotation sur la tuile combinée
        switch (orientation) {
            case 1:
                System.out.println("Appliquer rotation de 90°");
                return ImageHelper.rotateClockwise(tileWithTreasure);
            case 2:
                System.out.println("Appliquer rotation de 180°");
                return ImageHelper.rotate(tileWithTreasure, Math.PI);
            case 3:
                System.out.println("Appliquer rotation de 270°");
                return ImageHelper.rotateCounterClockwise(tileWithTreasure);
            default:
                System.out.println("Pas de rotation");
                return tileWithTreasure;  // Pas de rotation
        }
    }





    // Obtenir une image de pion
    public BufferedImage getPieceImage(int index) {

        return _pieceImages[index];
    }

    // Récupérer l'image d'un joueur par index
    public BufferedImage getPlayerIcons(int index) {
        if (index >= 0 && index < playerIcons.length) {
            return playerIcons[index];
        }
        return null;  // or handle the case where the index is out of bounds
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
            tileImages[i] = loadImage("/home/ychettati/Bureau/a-31-labyrinthe/res/img/tiles/tile_" + i + ".png");
        }
        return tileImages;
    }

    // Charger les images des cartes (recto et verso)
    private BufferedImage[] chargerImagesPourCartes() {
        BufferedImage[] cardImages = new BufferedImage[25]; // 24 cartes + 1 verso
        for (int i = 0; i < 25; i++) {
            cardImages[i] = loadImage("/home/ychettati/Bureau/a-31-labyrinthe/res/img/cards/cardFront.png");
        }
        cardImages[24] = loadImage("/home/ychettati/Bureau/a-31-labyrinthe/res/img/cards/cardBack.png"); // Carte verso
        return cardImages;
    }

    // Charger les images des trésors
    private BufferedImage[] chargerImagesPourTresors() {
        BufferedImage[] treasureImages = new BufferedImage[14];
        for (int i = 0; i < 14; i++) {
            treasureImages[i] = loadImage("/home/ychettati/Bureau/a-31-labyrinthe/res/img/treasures/treasure" + i + ".png");
        }
        return treasureImages;
    }
    private BufferedImage[] chargerImagesPourJoueurs() {
        BufferedImage[] playersImages = new BufferedImage[4];

        playersImages[0] = loadImage("/home/ychettati/Bureau/a-31-labyrinthe/res/img/blueplayer.png");
        playersImages[1] = loadImage("/home/ychettati/Bureau/a-31-labyrinthe/res/img/greenplayer.png");
        playersImages[2] = loadImage("/home/ychettati/Bureau/a-31-labyrinthe/res/img/redplayer.png");
        playersImages[3] = loadImage("/home/ychettati/Bureau/a-31-labyrinthe/res/img/yellowplayer.png");

        return playersImages;
    }

    // Charger les images des pions
    private BufferedImage[] chargerImagesPourPions() {
        BufferedImage[] pieceImages = new BufferedImage[4]; // 4 pions de couleurs différentes
        for (int i = 0; i < 4; i++) {
            pieceImages[i] = loadImage("/home/ychettati/Bureau/a-31-labyrinthe/res/img/pieces/piece_" + i + ".png");
        }
        return pieceImages;
    }

    // Charger l'image de fond de la zone joueur
    private BufferedImage chargerImageDeFond() {
        return loadImage("/home/ychettati/Bureau/a-31-labyrinthe/res/img/background.png");
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
    public BufferedImage getCardBackImage() {
        try {
            return ImageIO.read(new File("/home/ychettati/Bureau/a-31-labyrinthe/res/img/cards/cardBack.png"));  // Chemin vers l'image du dos de la carte
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Exemple de méthode pour obtenir l'image d'une carte spécifique
    public BufferedImage getCardImage(Card card) {
        try {
            String cardImagePath = getCardImagePath(card);  // Logic to get the correct path based on card data
            return ImageIO.read(new File(cardImagePath));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Logic to generate the path to card images based on card details
    private String getCardImagePath(Card card) {
        // Ex: Return image path based on card type or name
        return "/home/ychettati/Bureau/a-31-labyrinthe/res/img/cards/" + card.getName() + ".png";
    }
}