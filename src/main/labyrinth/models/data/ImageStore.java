package labyrinth.models.data;

import labyrinth.models.game.Card;
import labyrinth.views.helpers.ImageHelper;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;


// Provides resources for all graphical elements of the game.
public class ImageStore {

    private BufferedImage[] _tileImages;
    private BufferedImage[] _cardImages;       // Recto et verso des cartes
    private BufferedImage[] _treasureImages;   // Trésors à superposer
    private BufferedImage[] _pieceImages;      // 4 pions de couleurs différentes
    private BufferedImage _handBackground;     // Zone joueur
    private BufferedImage[] _playerIcons;
    private BufferedImage _gameBoardBackground; // Fond du tableau


    /**
     * Constructs a new ImageStore instance, initializing various game-related
     * images and resources needed for the labyrinth game. This constructor
     * loads images for tiles, cards, treasures, playing pieces, player icons,
     * and the background for the player's hand area. An instance of the Screen
     * class is also initialized to manage screen resources, handling any potential
     * IOExceptions that occur during the loading of screen images.
     */
    public ImageStore() {

        _tileImages = chargerImagesPourTuiles();
        _cardImages = chargerImagesPourCartes();
        _treasureImages = chargerImagesPourTresors();
        _pieceImages = chargerImagesPourPions();
        _handBackground = chargerImageDeFond();
        _playerIcons = chargerImagesPourJoueurs();
        _gameBoardBackground = chargerImageFondTableau();
    }

    /**
     * Retrieves the image of a card with an optional treasure overlay.
     *
     * @param index  : index of the card (0 to 23). An index of 24 refers to the back of the card if it is not open
     * @param isOpen : boolean indicating whether the card is open (true) or closed (false)
     * @return A BufferedImage of the card with the treasure if open; otherwise, the back of the card
     * @throws IllegalArgumentException if the index is invalid (not between 0 and 23)
     */
    public BufferedImage getCardWithTreasure(int index, boolean isOpen) {
        if (index < 0 || index >= 24) { // 24 cartes au total
            throw new IllegalArgumentException("Index de carte invalide.");
        }
        try {
            // Si la carte est ouverte, on retourne l'image correspondante à l'index (entre 0 et 23).
            // Sinon, on retourne l'image du dos de la carte (index 24).
            if (isOpen) {
                // Essayer de fusionner l'image de la carte avec le trésor associé
                return ImageHelper.merge_central(
                        "res/img/cards/cardFront.png",
                        "res/img/treasures/treasure" + index + ".png"
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

    /**
     * Retrieves a tile image based on specified parameters including index,
     * orientation, and whether a treasure is to be included.
     *
     * @param index         : index of the tile, must be within the range of available tiles
     * @param orientation   : orientation of the tile, where 0 is no rotation, 1 is 90 degrees clockwise,
     *                      2 is 180 degrees, and 3 is 270 degrees counter-clockwise
     * @param withTreasure  : boolean flag indicating whether to overlay a treasure image on the tile
     * @param treasureIndex : index of the treasure image to overlay if withTreasure is true
     * @return a BufferedImage representing the requested tile, optionally with a treasure
     * and applied orientation
     * @throws IOException              if there is an error in loading the images
     * @throws IllegalArgumentException if the tile index is invalid
     */
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
                    "res/img/tiles/tile_" + index + ".png",
                    "res/img/treasures/treasure" + treasureIndex + ".png"
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

    /**
     * Retrieves the image of a playing piece based on the specified index.
     *
     * @param index : index of the playing piece image to retrieve, where each
     *              piece corresponds to a predefined index within the range
     *              of available piece images
     * @return a BufferedImage representing the playing piece at the specified index
     */
    public BufferedImage getPieceImage(int index) {

        return _pieceImages[index];
    }

    /**
     * Retrieves the background image for the player's hand area.
     *
     * @return a BufferedImage representing the background of the player's hand area.
     */
    public BufferedImage get_handBackground() {
        return this._handBackground;
    }

    /**
     * Retrieves the image icon for a player based on the specified index.
     *
     * @param index : index of the player icon to retrieve, where each player
     *              corresponds to a predefined index within the range of available
     *              player icons
     * @return a BufferedImage representing the player icon at the specified index,
     * or null if the index is out of bounds
     */
    public BufferedImage getPlayerIcons(int index) {
        if (index >= 0 && index < _playerIcons.length) {
            return _playerIcons[index];
        }
        return null;  // or handle the case where the index is out of bounds
    }

    /**
     * Loads an array of tile images from predefined file paths. This method
     * attempts to load three distinct tile images from the specified directory
     * and returns them as an array of BufferedImages.
     *
     * @return a BufferedImage array containing three tile images loaded from
     * predefined paths. Each index in the array corresponds to a specific
     * tile image, in the order they were loaded.
     */
    private BufferedImage[] chargerImagesPourTuiles() {
        BufferedImage[] tileImages = new BufferedImage[3]; // 3 typestuiles
        for (int i = 0; i < 3; i++) {
            tileImages[i] = loadImage("res/img/tiles/tile_" + i + ".png");
        }
        return tileImages;
    }

    /**
     * Loads images for 24 card fronts and 1 card back to be used in the labyrinth game.
     * This method initializes an array of BufferedImages and sequentially loads each image
     * from the specified file paths. It loads a default front image for all 24 card positions
     * and loads a specific image for the card back.
     *
     * @return a BufferedImage array containing 24 images for card fronts and 1 image
     * for the card back
     */
    private BufferedImage[] chargerImagesPourCartes() {
        BufferedImage[] cardImages = new BufferedImage[25]; // 24 cartes + 1 verso
        for (int i = 0; i < 25; i++) {
            cardImages[i] = loadImage("res/img/cards/cardFront.png");
        }
        cardImages[24] = loadImage("res/img/cards/cardBack.png"); // Carte verso
        return cardImages;
    }

    /**
     * Loads and returns an array of images representing treasures used in the game.
     * This method iterates over a predefined set of file paths to retrieve images
     * associated with each treasure type.
     *
     * @return a BufferedImage array containing 14 treasure images, each loaded
     * from the specified file paths
     */
    private BufferedImage[] chargerImagesPourTresors() {
        BufferedImage[] treasureImages = new BufferedImage[14];
        for (int i = 0; i < 14; i++) {
            treasureImages[i] = loadImage("res/img/treasures/treasure" + i + ".png");
        }
        return treasureImages;
    }

    /**
     * Loads and returns an array of images representing the players in the game.
     * This method initializes an array of BufferedImages for four distinct player
     * colors: blue, green, red, and yellow. Each image is loaded from a specified
     * file path corresponding to the player's color.
     *
     * @return a BufferedImage array containing images for four players, each player
     * represented by a different color
     */
    private BufferedImage[] chargerImagesPourJoueurs() {
        BufferedImage[] playersImages = new BufferedImage[4];

        playersImages[0] = loadImage("res/img/players/redplayer.png");
        playersImages[1] = loadImage("res/img/players/greenplayer.png");
        playersImages[2] = loadImage("res/img/players/blueplayer.png");
        playersImages[3] = loadImage("res/img/players/yellowplayer.png");

        return playersImages;
    }

    /**
     * Loads images for playing pieces, representing different colors used in the game.
     * This method initializes an array of BufferedImages, each image being loaded
     * from a predefined file path corresponding to each playing piece color.
     *
     * @return a BufferedImage array containing images for four playing pieces, each
     * represented by a different color
     */
    private BufferedImage[] chargerImagesPourPions() {
        BufferedImage[] pieceImages = new BufferedImage[4]; // 4 pions de couleurs différentes
        for (int i = 0; i < 4; i++) {
            pieceImages[i] = loadImage("res/img/pieces/piece_" + i + ".png");
        }
        return pieceImages;
    }

    /**
     * Loads the background image for the labyrinth game from a predefined file path.
     * This method utilizes the loadImage utility to retrieve the image file and
     * return it as a BufferedImage.
     *
     * @return a BufferedImage representing the background of the labyrinth, or null if the image cannot be loaded
     */
    private BufferedImage chargerImageDeFond() {
        return loadImage("res/img/screens/main_screen.png");
    }

    private BufferedImage chargerImageFondTableau() {
        return loadImage("res/img/screens/gameboard.png");
    }

    /**
     * Retrieves the background image of the game board.
     *
     * @return a BufferedImage representing the background of the game board.
     */
    public BufferedImage getGameBoardBackground() {
        return _gameBoardBackground;
    }

    /**
     * Loads an image from the specified file path.
     *
     * @param path : file path of the image to be loaded
     * @return a BufferedImage object if the image is successfully loaded,
     * or null if the file does not exist or an IOException occurs
     */
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

    /**
     * Retrieves the image used as the back of a card.
     * Attempts to load an image from a specific file path representing the back of a card
     * used in the game. Returns the card back image if successfully loaded; otherwise,
     * returns null if an IOException occurs.
     *
     * @return a BufferedImage representing the card back image, or null if an error occurs during loading
     */
    public BufferedImage getCardBackImage() {
        try {
            return ImageIO.read(new File("res/img/cards/cardBack.png"));  // Chemin vers l'image du dos de la carte
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Retrieves the image of the specified card.
     *
     * @param card :  Card object for which the image is to be retrieved
     * @return a BufferedImage representing the card's image, or null if the image cannot be loaded
     */
    public BufferedImage getCardImage(Card card) {
        try {
            String cardImagePath = getCardImagePath(card);  // Logic to get the correct path based on card data
            return ImageIO.read(new File(cardImagePath));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Generates and returns the file path for a card's image based on its name.
     * The image path is composed by appending the card's name followed by ".png"
     * to the predefined directory path for card images.
     *
     * @param card : Card object whose image path is to be retrieved
     * @return a String representing the full file path of the card's image
     */
    private String getCardImagePath(Card card) {
        // Ex: Return image path based on card type or name
        return "res/img/cards/" + card.getName() + ".png";
    }
}
