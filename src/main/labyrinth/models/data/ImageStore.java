package data;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

class ImageStore {
    private BufferedImage[][] _tileImages;    // Images pour les tuiles
    private BufferedImage[] _cardImages;      // Images pour les cartes (1 image pour toutes les cartes)
    private BufferedImage[] _treasureImages;  // Images pour les trésors
    private BufferedImage[] _pieceImages;     // Images pour les pions
    private BufferedImage _handBackground;    // Arrière-plan de la main du joueur
    private Screen _screen;                   // L'objet Screen pour gérer l'écran

    // Constructeur
    public ImageStore(Screen screen) {
        // Initialisation des tableaux ou chargement des images
        _tileImages = new BufferedImage[3][4];   // 3 types de tuiles (angles, sections droites, T) et 4 orientations possibles
        _treasureImages = new BufferedImage[24]; // 24 objectifs (trésors)
        _cardImages = new BufferedImage[25];     // 24 cartes + 1 image pour le dos
        _pieceImages = new BufferedImage[4];     // 4 pions de couleur différente
        _handBackground = new BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB); // Exemple d'arrière-plan

        this._screen = screen;  // Initialisation de l'attribut screen avec l'objet Screen fourni

        // Chargement des images
        load();
    }
////////////:
    // Méthode pour charger toutes les images nécessaires
    public void load() {
        // Charger les images des tuiles
        // 0 : Angle, 1 : Section droite, 2 : T
        for (int i = 0; i < _tileImages.length; i++) {
            for (int j = 0; j < _tileImages[i].length; j++) {
                // Par exemple, "angle_0_0.png" pour la tuile d'angle, orientation 0
                _tileImages[i][j] = loadImage("tiles/" + getTileType(i) + "_" + j + ".png");
            }
        }

        // Charger les images des objectifs (24 trésors)
        for (int i = 0; i < _treasureImages.length; i++) {
            _treasureImages[i] = loadImage("treasures/objective_" + i + ".png");
        }

        // Charger toutes les images des cartes (24 cartes avec la même image de face)
        for (int i = 0; i < 24; i++) {
            _cardImages[i] = loadImage("cards/card_front.png"); // Utilise la même image pour chaque carte (face)
        }
        // Charger l'image du dos de la carte
        _cardImages[24] = loadImage("cards/card_back.png"); // Le dos des cartes

        // Charger les images des 4 pions
        for (int i = 0; i < _pieceImages.length; i++) {
            _pieceImages[i] = loadImage("pieces/piece_" + i + ".png");
        }

        // Charger l'image de l'arrière-plan de la main du joueur
        _handBackground = loadImage("backgrounds/hand_background.png");
    }

    // Méthode pour charger une image depuis un fichier
    private BufferedImage loadImage(String path) {
        try {
            return ImageIO.read(new File(path));  // Charger l'image depuis le fichier
        } catch (IOException e) {
            System.out.println("Erreur de chargement de l'image : " + path);
            e.printStackTrace();
            return null; // Retourner null en cas d'erreur de chargement
        }
    }

    // Méthode pour récupérer le type de tuile en fonction de l'index
    private String getTileType(int index) {
        switch (index) {
            case 0: return "angle";   // Type "angle"
            case 1: return "straight"; // Type "section droite"
            case 2: return "T";        // Type "T"
            default: return "unknown"; // Par défaut, "inconnu"
        }
    }

    // Méthode pour récupérer l'image de la tuile avec l'index, l'orientation et l'ajout d'un trésor
    public BufferedImage getTileImage(int index, int orientation, boolean withTreasure) {
        if (index < 0 || index >= _tileImages.length || orientation < 0 || orientation >= _tileImages[index].length) {
            throw new IllegalArgumentException("Index ou orientation invalide");
        }

        BufferedImage tileImage = _tileImages[index][orientation];

        // Ajouter un trésor si nécessaire
        if (withTreasure && _treasureImages.length > 0) {
            BufferedImage treasureImage = _treasureImages[0]; // Exemple simplifié (on prend le premier trésor)
            tileImage = overlayImages(tileImage, treasureImage);
        }

        return tileImage;
    }

    // Méthode pour superposer deux images
    private BufferedImage overlayImages(BufferedImage baseImage, BufferedImage overlayImage) {
        BufferedImage combined = new BufferedImage(baseImage.getWidth(), baseImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
        combined.getGraphics().drawImage(baseImage, 0, 0, null);
        combined.getGraphics().drawImage(overlayImage, 0, 0, null);
        return combined;
    }

    // Méthode pour obtenir l'image de l'écran, selon l'état du jeu (en cours ou game over)
    public BufferedImage getScreen(boolean isRunning) {
        return _screen.getScreenImage(isRunning);  // Appeler la méthode de Screen pour obtenir l'écran approprié
    }
/////////////////////:
    // Méthode pour récupérer l'image d'une carte selon son index et son état (ouverte ou fermée)
public BufferedImage getCardImage(int index, boolean isOpen) {
    // Vérification des limites de l'indice
    if (index < 0 || index >= 24) {
        throw new IllegalArgumentException("Index de carte invalide : " + index);
    }

    // Récupérer la carte correspondante à l'index
    Card card = _cards.get(index); // Liste _cards qui contient toutes les cartes du jeu (24 cartes)

    // Si la carte est ouverte, retourner l'image correspondante
    if (isOpen) {
        if (card.isBackCard()) {
            return _cardImages[24]; // Image du dos de la carte si c'est une carte "dos"
        } else {
            // Sinon, ajouter l'image du trésor à l'image de la carte
            return overlayImages(_cardImages[0], _treasureImages[card.getTreasure()]);
        }
    }

    // Si la carte est fermée, retourner l'image du dos de la carte
    return _cardImages[24];  // Image du dos de la carte
}

    ////////////////////////
    public BufferedImage getPieceImage(int index) {
        // Vérification des limites de l'indice
        if (index < 0 || index >= _pieceImages.length) {
            throw new IllegalArgumentException("Index de pion invalide : " + index);
        }

        // Vérification si l'image est correctement chargée
        if (_pieceImages[index] == null) {
            throw new IllegalStateException("Image du pion à l'index " + index + " non configurée.");
        }

        return _pieceImages[index];
    }

    public BufferedImage getHandBackground() {
        // Vérification si l'arrière-plan est configuré
        if (_handBackground == null) {
            throw new IllegalStateException("Arrière-plan de la main non configuré.");
        }

        return _handBackground;
    }
}
