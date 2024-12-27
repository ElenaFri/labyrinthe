package labyrinth.views.ViewsForObservers;

import labyrinth.controllers.GameFacadeController;
import labyrinth.controllers.GameboardController;
import labyrinth.controllers.TourController;
import labyrinth.controllers.UIController;
import labyrinth.models.game.Gameboard;
import labyrinth.models.tiles.Tile;
import labyrinth.models.geometry.Position;
import labyrinth.models.observers.GameBoardObserver;
import labyrinth.models.observers.GameFacadeObserver;
import labyrinth.models.data.ImageStore;
import labyrinth.models.game.GameFacade;
import labyrinth.models.game.Player;
import labyrinth.models.game.Card;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.util.Timer;
import java.util.TimerTask;

// Displays the main game window and all of its components.
public class GameBoardFacadeView extends JPanel implements GameBoardObserver, GameFacadeObserver {
    private static final int PLAYER_SPACING = 40; // Espace supplémentaire entre les joueurs
    private static final int CARD_SPACING = 5; // Distance entre l'image du joueur et la première carte.
    private static final int TILE_SIZE = 128;
    private static final int BOARD_SIZE = 896;
    private static final int PADDING = 50;
    private static final int PLAYER_SIZE = 90;
    private final TourController tourController;
    private final GameFacade gameFacade;
    private final ImageStore imageStore;
    private final GameboardController gameboardController;
    private final GameFacadeController gameFacadeController;
    private final UIController uiController;
    private final Gameboard gameboard;
    // Couleurs utilisées
    Color beige = new Color(222, 198, 150);
    Color navy = new Color(0, 0, 90);
    Color orange = new Color(255, 165, 0);
    Color menthol_green = new Color(46, 204, 113);
    Color shadow = new Color(0, 0, 0, 100);
    private Runnable onNewGame; // Champ pour le callback
    private JPanel tourPanel;
    private JLabel tourLabel;
    private JLabel playerIconLabel;
    // Variable pour suivre le bouton sélectionné
    private JButton selectedButton;
    // Positions de certains éléments
    private Position[] playerPositions;
    private Position freeTilePosition;
    private List<Position> clickedTiles = new ArrayList<>();
    private List<JButton> arrowButtons = new ArrayList<>(); // les boutons flèches
    private JButton rotateTileButton;
    private String lastArrowDirection = null; // Direction de la dernière flèche cliquée
    private int lastArrowIndex = -1;          // Indice de la dernière flèche cliquée

    private Map<Position, JButton> tileButtons = new HashMap<>();

    /**
     * Constructs an instance of the GameBoardFacadeView.
     *
     * @param gameboard  : the game board representation that manages the layout and tiles of the game
     * @param gameFacade : the facade used to manage player actions and game logic
     * @param imageStore : storage reference for the images used in the game UI
     * @param onNewGame  : a callback to be invoked when a new game is initialized
     */
    public GameBoardFacadeView(Gameboard gameboard, GameFacade gameFacade, ImageStore imageStore, Runnable onNewGame) {
        // Initialisation du tableau playerPositions
        playerPositions = new Position[4];
        playerPositions[0] = gameFacade.getPlayer(0).getCurrentTile();
        playerPositions[1] = gameFacade.getPlayer(1).getCurrentTile();
        playerPositions[2] = gameFacade.getPlayer(2).getCurrentTile();
        playerPositions[3] = gameFacade.getPlayer(3).getCurrentTile();

        this.gameboard = gameboard;
        gameboard.addGameboardObserver(this);
        gameFacade.addGameFacadeObserver(this);
        this.gameFacade = gameFacade;
        this.imageStore = imageStore;
        this.gameboardController = new GameboardController(gameboard);

        this.gameFacadeController = new GameFacadeController(gameFacade);
        this.tourController = new TourController(gameFacadeController, this, gameFacade);
        this.uiController = new UIController(gameboardController, gameFacadeController, tourController);
        this.onNewGame = onNewGame; // Assignation du callback

        setLayout(null);  // Layout absolu pour la gestion de la position des éléments
        setPreferredSize(new Dimension(BOARD_SIZE + 100, BOARD_SIZE + 100));

        // Initialisation du bouton Rotate Tile
        rotateTileButton = new JButton("TOURNER");

        Font buttonFont = new Font("Arial", Font.BOLD, 20);
        rotateTileButton.setFont(buttonFont);
        FontMetrics metrics = getFontMetrics(buttonFont);
        int buttonWidth = metrics.stringWidth("TOURNER") + 20; // Largeur du bouton avec un peu de remplissage
        int buttonHeight = metrics.getHeight() + 40; // Hauteur du bouton, y compris un peu de remplissage

        rotateTileButton.setBounds(1530, 600, buttonWidth, buttonHeight);  // Position du bouton à l'écran
        rotateTileButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // Transforme le curseur en un doigt qui pointe

        rotateTileButton.setBorder(BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        rotateTileButton.setFocusable(false); // Pour désactiver le contour de focus
        rotateTileButton.setBackground(beige);
        rotateTileButton.setForeground(navy);

        rotateTileButton.addActionListener(e -> uiController.onRotateTileClicked());
        add(rotateTileButton);

        // Initialiser un bouton de sortie
        JButton exitButton = new JButton("x");
        exitButton.setBounds(1870, 13, 35, 35);  // Position du bouton à l'écran
        exitButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // Transforme le curseur
        exitButton.setBackground(shadow);
        exitButton.setForeground(beige);
        exitButton.setBorder(BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        exitButton.setFocusable(false);
        Font exitFont = new Font("Arial", Font.BOLD, 10);
        exitButton.addActionListener(e -> {
            System.exit(0);
        });
        add(exitButton);

        createArrowButtons();
        initTourPanel(gameFacade.getCurrentPlayer(), imageStore);
    }

    /**
     * Retrieves the direction of the last arrow clicked on the game board.
     *
     * @return a string representing the direction of the last arrow clicked.
     * Possible values could include directions such as "left", "right", "up", or "down".
     */
    public String getLastArrowDirection() {
        return lastArrowDirection;
    }

    /**
     * Retrieves the index of the last arrow clicked on the game board.
     *
     * @return An integer representing the index of the last arrow clicked.
     */
    public int getLastArrowIndex() {
        return lastArrowIndex;
    }

    /**
     * Sets the positions of the players in the game.
     * This method validates the number of player positions and assigns them to the internal state.
     *
     * @param playerPositions : an array containing the positions of the players.
     *                        The array must contain exactly 4 positions, one for each player.
     *                        If the array length is not 4, an IllegalArgumentException is thrown.
     */
    public void setPlayerPositions(Position[] playerPositions) {
        // Vérifie si les positions sont valides (optionnel)
        if (playerPositions.length != 4) {
            throw new IllegalArgumentException("Il doit y avoir 4 joueurs.");
        }
        this.playerPositions = playerPositions;
    }

    /**
     * Makes a specific tile at the given position clickable and interactive.
     * The method creates a button corresponding to the tile, configures its style,
     * adds visual effects on hover, and specifies the actions to be performed when
     * the tile is clicked, such as moving a player or displaying adjacent accessible tiles.
     * The button is added to the game view and stored for further reference.
     *
     * @param position : the position of the tile to make clickable.
     *                 It determines where the button is placed on the game board and the related actions when the tile is interacted with.
     */
    public void makeTileClickable(Position position) {
        JButton tileButton = new JButton();

        // Position et style du bouton
        tileButton.setBounds(
                1920 - 900 - 510 + 10 + position.getY() * TILE_SIZE,
                1080 - 900 - 100 - 5 + position.getX() * TILE_SIZE,
                TILE_SIZE - 20, TILE_SIZE - 20
        );
        tileButton.setOpaque(false);
        tileButton.setContentAreaFilled(false);
        tileButton.setBorderPainted(true);
        tileButton.setBorder(BorderFactory.createLineBorder(Color.RED, 2));

        // Ajouter effets visuels
        tileButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                tileButton.setBorder(BorderFactory.createLineBorder(Color.GREEN, 2));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                tileButton.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
            }
        });

        // Action au clic
        tileButton.addActionListener(e -> {
            uiController.DeplacerJoueurEtObjectif(position, this, gameFacade);
            showAdjacentAccessibleTiles(position);
        });

        // Ajouter le bouton à l'interface
        add(tileButton);
        tileButtons.put(position, tileButton); // Stocker le bouton dans la map
    }

    /**
     * Initializes the Tour Panel, which displays the current player's turn along with their associated icon and a message.
     *
     * @param currentPlayer : the player whose turn is being displayed. The player's name is included in the display message
     * @param imageStore    : the image storage component used to retrieve and manage the player's icon
     */
    public void initTourPanel(Player currentPlayer, ImageStore imageStore) {
        // Créer le panneau
        tourPanel = new JPanel();
        tourPanel.setLayout(new BoxLayout(tourPanel, BoxLayout.Y_AXIS));
        tourPanel.setBackground(beige);
        tourPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(127, 140, 141), 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Récupérer l'image du joueur et la redimensionner
        Image originalImage = imageStore.getPlayerIcons(gameFacade.getCurrentPlayerIndex());
        Image scaledImage = originalImage.getScaledInstance(200, 200, Image.SCALE_SMOOTH); // Image redimensionnée à 200x200
        playerIconLabel = new JLabel(new ImageIcon(scaledImage));

        // Avec retour à la ligne avant le nom du personnage
        String textHtml = "<html><div style='text-align:center; width:140px;'>C'est à toi de jouer, <br />"
                + currentPlayer.getName() + " !</div></html>";

        tourLabel = new JLabel(textHtml);
        tourLabel.setFont(new Font("Arial", Font.PLAIN, 18)); // Taille de police de 14 pixels pour le texte
        tourLabel.setForeground(navy);
        tourLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Centrer l'étiquette dans le panneau
        playerIconLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Centrer l'image aussi

        // Ajouter l'image puis le texte au panneau
        tourPanel.add(playerIconLabel);
        tourPanel.add(Box.createVerticalStrut(10)); // Espace vertical entre l'image et le texte
        tourPanel.add(tourLabel);

        // Ajuster les dimensions et la position du panneau (ajustez en fonction de vos besoins)
        tourPanel.setBounds(125, 400, 200, 275); // Ajuster la taille pour le rendre suffisamment grand
        add(tourPanel);
    }

    /**
     * Displays the adjacent accessible tiles to the provided current position.
     * This method updates the state of the game board by enabling and showing buttons
     * corresponding to the tiles that are reachable from the specified position, while
     * disabling and hiding other tiles. Additionally, if the player remains in the same position,
     * it ends the current turn.
     *
     * @param currentPosition : current position of the player for which adjacent accessible tiles
     *                        will be determined and displayed. If the player remains in the
     *                        same position as their last position, the turn is advanced.
     */
    public void showAdjacentAccessibleTiles(Position currentPosition) {
        // Désactiver et masquer tous les boutons
        tileButtons.values().forEach(button -> {
            button.setEnabled(false);
            button.setVisible(false); // Rendre le bouton invisible
        });

        // Vérifier si le joueur reste sur la même case
        if (currentPosition.equals(gameFacade.getCurrentPlayer().get_lastPosition())) {
            System.out.println("Le joueur reste sur la même position : " + currentPosition);
            JButton button = tileButtons.get(currentPosition);
            if (button != null) {
                button.setEnabled(false);
                button.setVisible(false);
                // gameFacade.nextPlayer();
                tourController.TourSuivant();
                disableOppositeArrow();
                // afficherTourSuivant(gameFacade.getCurrentPlayer());
                // ActiverFleche();
            }
            return; // Sortir de la méthode car il n'y a pas de tuiles adjacentes à afficher
        }

        // Obtenir les tuiles accessibles adjacentes
        List<Position> accessibleTiles = gameboard.getAccessibleTiles(currentPosition);

        for (Position position : accessibleTiles) {
            if (!position.equals(gameFacade.getCurrentPlayer().get_lastPosition())) { // Exclure la dernière position
                JButton button = tileButtons.get(position);
                if (button != null) {
                    button.setEnabled(true); // Activer le bouton
                    button.setVisible(true); // Rendre le bouton visible
                }
            }
        }

        System.out.println("Tuiles accessibles depuis " + currentPosition + ": " + accessibleTiles);
    }

    /**
     * Displays the end-game view by showing the winner details and setting up the UI for game reset.
     *
     * @param winner : the player who has won the game. This player's information will be used
     *               to display relevant details and their icon in the end-game view
     */
    public void showEndGameView(Player winner) {
        this.removeAll(); // Retirer tous les composants actuels

        // Récupérer les images depuis ImageStore
        Image backgroundImage = imageStore.get_handBackground();
        Image winnerImage = imageStore.getPlayerIcons(winner.get_id());

        // Créer une instance de EndGameView avec le callback pour réinitialiser le jeu
        EndGameView endGameView = new EndGameView(winner, backgroundImage, winnerImage, onNewGame);
        endGameView.setBounds(0, 0, getWidth(), getHeight()); // Ajuster selon votre layout
        add(endGameView);

        // Revalider et redessiner le panneau
        this.revalidate();
        this.repaint();
    }

    /**
     * Overrides the paintComponent method to render custom graphics on the component.
     * This method draws the background, gameboard, shadows, borders, players, and player cards.
     * It also updates the position of the rotate button and handles game-related visual elements.
     *
     * @param g : Graphics object used for drawing shapes, images, and text on the component
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(imageStore.get_handBackground(), 0, 0, getWidth(), getHeight(), this);
        int xOffset = (getWidth() - BOARD_SIZE) / 2;
        int yOffset = (getHeight() - BOARD_SIZE) / 2;

        // Dessiner l'ombre du plateau
        Graphics2D g2d = (Graphics2D) g;  // Convertir Graphics en Graphics2D
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(new Color(0, 0, 0, 100)); // Ombre noire avec transparence
        RoundRectangle2D shadowRect = new RoundRectangle2D.Double(xOffset + 18, yOffset + 20, BOARD_SIZE, BOARD_SIZE, 15, 15); // Arrondie de 3px
        g2d.fill(shadowRect);

        // Dessiner la bordure du plateau
        g2d.setColor(navy);  // Couleur de la bordure
        g2d.setStroke(new BasicStroke(10)); // Épaisseur de la bordure
        RoundRectangle2D roundRect = new RoundRectangle2D.Double(xOffset - 5, yOffset - 5, BOARD_SIZE + 10, BOARD_SIZE + 10, 15, 15); // Rayon de 5 pixels
        g2d.draw(roundRect);

        // Dessiner le plateau de jeu
        drawGameboard(g, xOffset, yOffset);

        // Positionner le bouton
        updateRotateButtonPosition(freeTilePosition, xOffset, yOffset);

        // Dessiner les joueurs et leurs pièces
        drawPlayersAndPieces(g, xOffset, yOffset);

        // Dessiner les cartes pour chaque joueur
        Player[] players = gameFacade.get_players();
        for (int i = 0; i < players.length; i++) {
            drawPlayerCards(g, players[i], xOffset, yOffset, 60, 100, -8, i);
        }
    }

    /**
     * Updates the position and size of the rotate button based on the free tile's position
     * and the specified offsets. This method calculates the coordinates for the button
     * and sets its bounds accordingly.
     *
     * @param freeTilePosition : position of the free tile on the game board. Used
     *                         to determine the base position of the rotate button
     * @param xOffset          : horizontal offset to apply to the calculated button position
     * @param yOffset          : vertical offset to apply to the calculated button position
     */
    private void updateRotateButtonPosition(Position freeTilePosition, int xOffset, int yOffset) {
        if (freeTilePosition != null) {
            int buttonX = xOffset + (freeTilePosition.getY() * TILE_SIZE) + TILE_SIZE - 4;
            int buttonY = yOffset + (freeTilePosition.getX() * TILE_SIZE) + TILE_SIZE + 150; // Calculer Y pour le positionner en bas

            rotateTileButton.setBounds(buttonX, buttonY, 130, 30); // Mettre à jour la position et la taille du bouton
        }
    }

    /**
     * Disables all arrow buttons on the game board.
     * This method iterates through the list of arrow buttons and sets their enabled state to false,
     * effectively preventing the user from interacting with them until they are re-enabled.
     */
    public void DesactiverFleche() {
        // Désactiver tous les autres boutons flèches
        for (JButton b : arrowButtons) {
            b.setEnabled(false);
        }
    }

    /**
     * Activates all arrow buttons in the interface.
     * This method enables all buttons associated with navigation or arrow
     * functionality, allowing them to be clickable and functional in the user
     * interface. It iterates through a collection of JButton objects named
     * `arrowButtons` and sets each to an enabled state.
     */
    public void ActiverFleche() {
        // Activer tous les autres boutons flèches
        for (JButton b : arrowButtons) {
            b.setEnabled(true);
        }
    }

    /**
     * Creates an arrow button with the given image, index, and direction. The button is configured
     * with specific client properties to identify its direction and index, and an action listener
     * is added to handle actions when the button is clicked, including game logic related to
     * row or column shifts and updating player positions.
     *
     * @param imagePath : file path of the image to be used on the arrow button
     * @param index     : index of the row or column the button corresponds to
     * @param direction : direction of the arrow (e.g., "droite", "gauche", "haut", "bas")
     * @return a JButton instance representing the configured arrow button
     */
    private JButton createArrowButton(String imagePath, int index, String direction) {

        JButton button = new JButton(new ImageIcon(imagePath));
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);

        // Définir des propriétés client pour identifier la direction et l'indice
        button.putClientProperty("direction", direction);
        button.putClientProperty("index", index);

        // Ajouter un ActionListener pour gérer la sélection du bouton
        button.addActionListener(e -> {
            // Enregistrer la direction et l'indice de la dernière flèche cliquée
            lastArrowDirection = direction;
            lastArrowIndex = index;
            System.out.println("Dernière flèche cliquée: Direction = " + lastArrowDirection + ", Indice = " + lastArrowIndex);
            System.out.println("Bouton flèche " + direction + " cliqué.");
            gameFacade.getCurrentPlayer().setLastPosition(null);

            // Désactiver toutes les flèches
            DesactiverFleche();

            // Appeler la méthode appropriée selon la direction du bouton via GameFacadeController
            if (direction.equalsIgnoreCase("droite") || direction.equalsIgnoreCase("gauche")) {
                // Déterminer la direction numérique
                int shiftDirection = direction.equalsIgnoreCase("droite") ? 1 : 3;
                // Appeler le contrôleur pour déplacer la ligne
                gameboardController.shiftRow(index, shiftDirection);
                // Mettre à jour les joueurs sur la ligne déplacée
                updatePlayersAfterRowShift(index, shiftDirection);
            } else if (direction.equalsIgnoreCase("haut") || direction.equalsIgnoreCase("bas")) {
                // Déterminer la direction numérique
                int shiftDirection = direction.equalsIgnoreCase("haut") ? 0 : 2;
                // Appeler le contrôleur pour déplacer la colonne
                gameboardController.shiftColumn(index, shiftDirection);
                // Mettre à jour les joueurs sur la colonne déplacée
                updatePlayersAfterColumnShift(index, shiftDirection);
            }

            // Rendre les tuiles cliquables et afficher les tuiles adjacentes accessibles
            System.out.println("Réaffichage des tuiles accessibles.");
            RendreTabCliquable();
            showAdjacentAccessibleTiles(gameFacade.getCurrentPlayer().getCurrentTile());
            ///////////////////////////////////////////////////////////////////////////////////


            System.out.println("Action exécutée pour " + direction + " !");
        });

        return button;
    }

    /**
     * Updates the positions of players on the game board after a row shift.
     * This method adjusts player positions based on the specified row and direction of the shift.
     * Players at the affected row are moved horizontally, with wrapping logic applied if they move
     * beyond the board's boundary.
     *
     * @param rowIndex  : index of the row being shifted. Players in this row will have their positions updated
     * @param direction : direction of the row shift. A value of 1 shifts the row to the right,
     *                  while a value of 3 shifts the row to the left
     */
    private void updatePlayersAfterRowShift(int rowIndex, int direction) {
        Player[] players = gameFacade.get_players();
        for (Player player : players) {
            Position pos = player.getCurrentTile();
            if (pos.getX() == rowIndex) {
                int newY = pos.getY();
                if (direction == 1) { // Droite
                    newY = pos.getY() + 1;
                    if (newY >= 7) {
                        newY = 0; // Réapparaître de l'autre côté
                    }
                } else if (direction == 3) { // Gauche
                    newY = pos.getY() - 1;
                    if (newY < 0) {
                        newY = 6; // Réapparaître de l'autre côté
                    }
                }
                // Mettre à jour les positions des joueurs
                player.setLastPosition(new Position(pos.getX(), pos.getY()));
                player.setCurrentTile(new Position(rowIndex, newY));
                System.out.println("Joueur " + player.getName() + " déplacé à la position " + player.getCurrentTile());
            }
        }
        // Notifier les observateurs que les positions des joueurs ont changé
        gameFacade.notifyPlayerPositionChange(gameFacade.getPlayersPositions());
    }

    /**
     * Updates the positions of players whose current tile is affected by a column shift.
     * Players will be repositioned to the opposite end of the column if they move out of bounds.
     * Observers are notified of player position changes after the shift.
     *
     * @param columnIndex : index of the column being shifted
     * @param direction   : direction of the shift, where 0 represents upward shift and 2 represents downward shift
     */
    private void updatePlayersAfterColumnShift(int columnIndex, int direction) {
        Player[] players = gameFacade.get_players();
        for (Player player : players) {
            Position pos = player.getCurrentTile();
            if (pos.getY() == columnIndex) {
                int newX = pos.getX();
                if (direction == 0) { // Haut
                    newX = pos.getX() - 1;
                    if (newX < 0) {
                        newX = 6; // Réapparaître de l'autre côté
                    }
                } else if (direction == 2) { // Bas
                    newX = pos.getX() + 1;
                    if (newX >= 7) {
                        newX = 0; // Réapparaître de l'autre côté
                    }
                }
                // Mettre à jour les positions des joueurs
                player.setLastPosition(new Position(pos.getX(), pos.getY()));
                player.setCurrentTile(new Position(newX, columnIndex));
                System.out.println("Joueur " + player.getName() + " déplacé à la position " + player.getCurrentTile());
            }
        }
        // Notifier les observateurs que les positions des joueurs ont changé
        gameFacade.notifyPlayerPositionChange(gameFacade.getPlayersPositions());
    }

    /**
     * Updates the positions of all players on the game board.
     * This method processes an array of new positions for the players,
     * updates the internal state, and redraws the interface to reflect the changes.
     *
     * @param newPositions an array containing the updated positions of players.
     *                     The number of positions should align with the number of players in the game.
     */
    @Override
    public void UpdatePlayerPositionChanged(Position[] newPositions) {
        System.out.println("\nLes positions des joueurs ont été mises à jour.");

        // Mettre à jour le tableau des positions des joueurs
        for (int i = 0; i < newPositions.length; i++) {
            playerPositions[i] = newPositions[i];
            System.out.println("Joueur " + gameFacade.get_players()[i].getName() + " à la position " + newPositions[i]);
        }

        // Redessiner l'interface
        repaint();
        //////////////////////////////////////////////////////////
    }


    /**
     * Determines the opposite direction of the given direction input.
     *
     * @param direction : current direction as a string (e.g., "droite", "gauche", "haut", "bas")
     * @return the opposite direction as a string ("gauche" for "droite", "droite" for "gauche",
     * "bas" for "haut", "haut" for "bas"), or null if the input does not match
     * any recognized direction
     */
    private String getOppositeDirection(String direction) {
        switch (direction.toLowerCase()) {
            case "droite":
                return "gauche";
            case "gauche":
                return "droite";
            case "haut":
                return "bas";
            case "bas":
                return "haut";
            default:
                return null;
        }
    }

    /**
     * Disables the opposite arrow button based on the last recorded arrow direction
     * and index. This method checks the opposite direction of the last clicked arrow
     * and disables the corresponding button in the `arrowButtons` list.
     * The method first verifies if the `lastArrowDirection` or `lastArrowIndex` are
     * not properly set, and exits early if they are null or invalid. It then computes
     * the opposite direction for the last arrow and disables the button matching
     * both the opposite direction and the last arrow index.
     * If a matching button is found, it is disabled and a message is logged
     * to indicate the action. The process stops after disabling the first matching
     * button, ensuring only one arrow button is affected.
     */
    public void disableOppositeArrow() {
        if (lastArrowDirection == null || lastArrowIndex == -1) return;

        String opposite = getOppositeDirection(lastArrowDirection);
        if (opposite == null) return;

        for (JButton button : arrowButtons) {
            String buttonDirection = (String) button.getClientProperty("direction");
            int buttonIndex = (int) button.getClientProperty("index");
            if (opposite.equals(buttonDirection) && buttonIndex == lastArrowIndex) {
                button.setEnabled(false);
                System.out.println("Flèche " + opposite + " avec Indice " + buttonIndex + " désactivée pour le prochain joueur.");
                break; // Désactiver uniquement une flèche spécifique
            }
        }
    }

    /**
     * Enables the opposite arrow button corresponding to the last selected arrow direction
     * and index, if applicable. This method checks the last arrow direction and index,
     * identifies the opposite direction, and reactivates the button associated with that direction
     * and index. If no valid opposite direction or index is found, it exits without performing any action.
     * This method requires that the arrow buttons store relevant properties such as "direction"
     * and "index" as client properties to match against.
     */
    public void enableOppositeArrow() {
        if (lastArrowDirection == null || lastArrowIndex == -1) return;

        String opposite = getOppositeDirection(lastArrowDirection);
        if (opposite == null) return;

        for (JButton button : arrowButtons) {
            String buttonDirection = (String) button.getClientProperty("direction");
            int buttonIndex = (int) button.getClientProperty("index");
            if (opposite.equals(buttonDirection) && buttonIndex == lastArrowIndex) {
                button.setEnabled(true);
                System.out.println("Flèche " + opposite + " avec Indice " + buttonIndex + " réactivée.");
                break;
            }
        }
    }

    /**
     * Makes a grid of tiles clickable by iterating through all rows and columns
     * within a predefined 7x7 grid. For each tile, it invokes the method
     * to enable click functionality on the respective position.
     */
    public void RendreTabCliquable() {
        for (int x = 0; x < 7; x++) { // Parcourir les lignes
            for (int y = 0; y < 7; y++) { // Parcourir les colonnes
                Position position = new Position(x, y);
                makeTileClickable(position);
            }
        }
    }

    /**
     * Creates and positions arrow buttons on the user interface. Each arrow button represents
     * a directional control (left, right, up, down) and is placed at specified positions
     * corresponding to predefined row and column indices.
     * The method defines positions for the arrow buttons (right, left, up, down) for each row
     * based on predefined arrays. It then iterates over the row indices to create and configure
     * buttons for each direction and adds them to both the UI container and a collection of arrow buttons.
     * Each button is assigned an image, a specific cursor style, and corresponding directional metadata.
     */
    private void createArrowButtons() {
        // Définir les indices spécifiques pour les lignes et les colonnes
        int[] rowIndices = {1, 3, 5};  // Indices pour les lignes
        int[] colIndices = {1, 3, 5};  // Indices pour les colonnes

        // Positionnement des boutons pour chaque direction
        int[][] rightPositions = {
                {450, 260}, // Position pour 1ère ligne
                {450, 515}, // Position pour 2ème ligne
                {450, 775}  // Position pour 3ème ligne
        };

        int[][] leftPositions = {
                {1417, 260},
                {1417, 515},
                {1417, 775}
        };

        int[][] upPositions = {
                {680, 1000},
                {935, 1000},
                {1190, 1000}
        };

        int[][] downPositions = {
                {680, 30},
                {935, 30},
                {1190, 30}
        };

        // Création des boutons pour chaque direction
        for (int i = 0; i < rowIndices.length; i++) {  // Parcours des indices de lignes
            int rowIndex = rowIndices[i];

            // Droite
            JButton rightButton = createArrowButton("res/img/arrows/arrowD.png", rowIndex, "droite");
            rightButton.setBounds(rightPositions[i][0], rightPositions[i][1], 50, 50);
            rightButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            add(rightButton);
            arrowButtons.add(rightButton);

            // Gauche
            JButton leftButton = createArrowButton("res/img/arrows/arrowG.png", rowIndex, "gauche");
            leftButton.setBounds(leftPositions[i][0], leftPositions[i][1], 50, 50);
            leftButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            add(leftButton);
            arrowButtons.add(leftButton);

            // Haut
            JButton upButton = createArrowButton("res/img/arrows/arrowH.png", rowIndex, "haut");
            upButton.setBounds(upPositions[i][0], upPositions[i][1], 50, 50);
            upButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            add(upButton);
            arrowButtons.add(upButton);

            // Bas
            JButton downButton = createArrowButton("res/img/arrows/arrowB.png", rowIndex, "bas");
            downButton.setBounds(downPositions[i][0], downPositions[i][1], 50, 50);
            downButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // Rétablir le curseur pointer
            add(downButton);
            arrowButtons.add(downButton);
        }
    }

    /**
     * Draws the gameboard on the given graphics context, including the background, tiles,
     * and free tile. The board is drawn starting from the specified X and Y offsets.
     *
     * @param g       : Graphics object used for drawing the gameboard
     * @param xOffset : the starting x-coordinate for drawing the gameboard
     * @param yOffset : the starting y-coordinate for drawing the gameboard
     */
    private void drawGameboard(Graphics g, int xOffset, int yOffset) {
        // Dessiner l'image de fond du plateau
        BufferedImage gameBoardBackground = imageStore.getGameBoardBackground(); // Obtenez le fond du tableau depuis ImageStore

        if (gameBoardBackground != null) {
            g.drawImage(gameBoardBackground, xOffset, yOffset, BOARD_SIZE, BOARD_SIZE, null); // Dessiner l'image de fond
        } else {
            System.err.println("Le fond du tableau n'a pas pu être chargé.");
        }

        // Ajouter un buffer pour l'espacement entre le plateau et les bords
        int BUFFER = 2;

        // Calculez les décalages pour centrer les tuiles selon l'espacement
        int tileOffsetX = xOffset + BUFFER;
        int tileOffsetY = yOffset + BUFFER;

        // Dessiner les tuiles du plateau de jeu
        for (int row = 0; row < 7; row++) {
            for (int col = 0; col < 7; col++) {
                Tile tile = gameboard.getTile(new Position(row, col));
                if (tile != null) {
                    int tileIndex = getTileIndex(tile);
                    int orientation = tile.get_orientation();
                    System.out.println("Tile at position (" + row + "," + col + ") has orientation: " + orientation);
                    boolean hasTreasure = tile._hasTreasure;
                    int treasureIndex = tile.getTreasure();

                    try {
                        BufferedImage tileImage = imageStore.getTileImage(tileIndex, orientation, hasTreasure, treasureIndex);
                        g.drawImage(tileImage, xOffset + col * TILE_SIZE, yOffset + row * TILE_SIZE, TILE_SIZE, TILE_SIZE, null);
                        // Afficher le type de la tuile dans la console
                        String tileType = tile.getType();
                        System.out.println("Tile at position (" + row + "," + col + ") has type: " + tileType);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        // Dessiner la tuile libre
        drawFreeTile(g, xOffset);
    }

    /**
     * Draws the free tile on the game board at the specified offset. Includes the rendering of a shadow,
     * the tile image, and a rounded border for the free tile. Ensures proper alignment and presentation
     * of the free tile on the interface.
     *
     * @param g       : Graphics object used to render the tile and its components
     * @param xOffset : horizontal offset to position the free tile correctly on the board
     */
    private void drawFreeTile(Graphics g, int xOffset) {
        int freeTileX = xOffset + BOARD_SIZE + PADDING + 70;
        int freeTileY = (getHeight() - TILE_SIZE) / 2;

        Tile freeTile = gameboard.getFreeTile();
        if (freeTile != null) {
            freeTilePosition = new Position((freeTileY - PADDING - 70) / TILE_SIZE, (freeTileX - xOffset) / TILE_SIZE);
            int tileIndex = getTileIndex(freeTile);
            int orientation = freeTile.get_orientation();
            boolean hasTreasure = freeTile._hasTreasure;
            int treasureIndex = freeTile.getTreasure();

            try {
                // Dessiner l'ombre de la tuile libre avec des coins arrondis
                Graphics2D g2d = (Graphics2D) g; // Convertir pour Graphics2D
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(shadow); // Ombre noire avec transparence
                RoundRectangle2D shadowRectangle = new RoundRectangle2D.Double(
                        freeTileX + 10, freeTileY + 12, TILE_SIZE, TILE_SIZE, 20, 20);
                g2d.fill(shadowRectangle); // Dessiner l'ombre avec coins arrondis

                // Dessiner la tuile
                BufferedImage tileImage = imageStore.getTileImage(tileIndex, orientation, hasTreasure, treasureIndex);
                g.drawImage(tileImage, freeTileX, freeTileY, TILE_SIZE, TILE_SIZE, null);

                // Dessiner le cadre de la tuile
                g2d.setColor(navy); // Couleur du cadre
                g2d.setStroke(new BasicStroke(4)); // Épaisseur du cadre (ajustez à votre goût)
                RoundRectangle2D roundedRectangle = new RoundRectangle2D.Double(
                        freeTileX, freeTileY, TILE_SIZE, TILE_SIZE, 50, 50);  // 20 pixels pour le rayon des coins arrondis
                g2d.draw(roundedRectangle); // Dessiner le cadre arrondi
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println(gameboard.getFreeTile().toString() + " //////////////////////////////// ////////////////////////////////");
    }

    /**
     * Determines the index of the tile image based on the type of tile provided.
     *
     * @param tile : tile object for which the image index needs to be determined
     * @return index representing the image type (e.g., 0 for AngledTile, 2 for TShapedTile, 1 for others)
     */
    private int getTileIndex(Tile tile) {
        // Détermine l'index de l'image en fonction du type de tuile
        if (tile instanceof labyrinth.models.tiles.AngledTile) return 0;
        if (tile instanceof labyrinth.models.tiles.TShapedTile) return 2;
        return 1; // Par défaut, pour les tuiles droites ou autres
    }

    /**
     * Draws the players and their pieces on the screen with the given offsets.
     *
     * @param g       : Graphics object used for drawing
     * @param xOffset : horizontal offset for drawing
     * @param yOffset : vertical offset for drawing
     */
    private void drawPlayersAndPieces(Graphics g, int xOffset, int yOffset) {
        // Dessiner les joueurs et leurs pièces
        drawPlayers(g, xOffset, yOffset);
        drawPieces(g, xOffset, yOffset);
    }

    /**
     * Draws the player icons and their corresponding labels at specified positions on the game board.
     *
     * @param g       : Graphics object used for rendering
     * @param xOffset : horizontal offset to adjust the players' positions
     * @param yOffset : vertical offset to adjust the players' positions
     */
    private void drawPlayers(Graphics g, int xOffset, int yOffset) {
        Player[] players = gameFacade.get_players();
        Graphics2D g2d = (Graphics2D) g;  // Convertir Graphics en Graphics2D
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // Activer l'anti-aliasing pour améliorer le rendu

        for (int i = 0; i < players.length; i++) {
            int x = 0, y = 0;
            switch (i) {
                case 0: // Joueur à gauche (index 0)
                    x = xOffset - PLAYER_SIZE / 1 - PLAYER_SPACING; // Décalage vers la gauche (ajout de PLAYER_SPACING)
                    y = yOffset - PLAYER_SIZE / 4; // Ajustement vertical
                    break;
                case 1: // Joueur à droite (index 1)
                    x = xOffset + BOARD_SIZE - PLAYER_SIZE / 150 + PLAYER_SPACING; // Décalage vers la droite (ajout de PLAYER_SPACING)
                    y = yOffset - PLAYER_SIZE / 4; // Ajustement vertical
                    break;
                case 2: // Joueur à gauche (index 2)
                    x = xOffset - PLAYER_SIZE / 1 - PLAYER_SPACING; // Décalage vers la gauche (ajout de PLAYER_SPACING)
                    y = yOffset + 18 + BOARD_SIZE - PLAYER_SIZE / 1; // Ajustement vertical
                    break;
                case 3: // Joueur à droite (index 3)
                    x = xOffset + BOARD_SIZE - PLAYER_SIZE / 150 + PLAYER_SPACING; // Décalage vers la droite (ajout de PLAYER_SPACING)
                    y = yOffset + 18 + BOARD_SIZE - PLAYER_SIZE; // Ajustement vertical
                    break;
            }

            BufferedImage playerImage = imageStore.getPlayerIcons(i);
            if (playerImage != null) {
                g2d.setColor(shadow); // Ombre noire (avec transparence)
                RoundRectangle2D shadowRectangle = new RoundRectangle2D.Double(x + 8, y + 10, PLAYER_SIZE, PLAYER_SIZE, 20, 20); // 20 pour le rayon des coins arrondis
                g2d.fill(shadowRectangle); // Dessiner l'ombre

                // Créer une forme arrondie
                RoundRectangle2D roundedRectangle = new RoundRectangle2D.Double(x, y, PLAYER_SIZE, PLAYER_SIZE, 20, 20); // 20 pixels de rayon pour les coins arrondis

                // Définir le clip pour dessiner l'image avec des coins arrondis
                g2d.setClip(roundedRectangle);

                // Dessiner l'image dans la zone découpée
                g2d.drawImage(playerImage, x, y, PLAYER_SIZE, PLAYER_SIZE, null);

                // Réinitialiser le clip
                g2d.setClip(null);
            }

            // Définir la police de caractères
            g2d.setFont(new Font("Arial", Font.BOLD, 12)); // Choisir la police en gros caractères
            FontMetrics fm = g2d.getFontMetrics(); // Obtenir les métriques de la police

            String playerName = players[i].getName().toUpperCase(); // Convertir le nom en majuscules

            int textWidth = fm.stringWidth(playerName); // Largeur du texte

            // Centrer le label
            int labelX = x + (PLAYER_SIZE - textWidth) / 2 - 10; // Ajout de marge pour le label
            int labelY = y + PLAYER_SIZE + 5; // Position y constante pour le label
            int labelWidth = textWidth + 20; // Largeur du label (ajouter un peu de remplissage)
            int labelHeight = 25;

            // Dessiner le label arrondi
            g2d.setColor(beige); // Couleur de fond du label
            RoundRectangle2D labelRectangle = new RoundRectangle2D.Double(labelX, labelY, labelWidth, labelHeight, 10, 10); // Création du rectangle arrondi
            g2d.fill(labelRectangle);

            // Ecrire le nom
            g2d.setColor(navy);
            int textX = labelX + (labelWidth - textWidth) / 2; // Centrer le texte horizontalement dans le label
            int textY = labelY + labelHeight / 2 + fm.getAscent() / 2 - 2; // Centrer verticalement (ajuster pour le décalage)

            g2d.drawString(playerName, textX, textY);
        }
    }

    /**
     * Displays a congratulatory message in a pop-up window for the specified player.
     * The window is styled with custom fonts and colors and automatically closes
     * after 5 seconds.
     *
     * @param currentPlayer : the Player object representing the player to whom the congratulatory message is addressed.
     *                      The player's name will be displayed in the message.
     */
    public void afficherFelicitation(Player currentPlayer) {
        // Créer une fenêtre pour afficher le message
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(700, 200);
        frame.setTitle("Félicitations");

        // Créer un panneau principal avec un fond vert
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(orange); // Fond orange

        // Ajouter un message avec une belle police et couleur
        JLabel label = new JLabel("Félicitations, " + currentPlayer.getName() + " ! Objectif trouvé !");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 22));
        label.setForeground(navy); // Texte en bleu très foncé

        // Ajouter le label au panneau
        panel.add(label, BorderLayout.CENTER);

        // Ajouter un effet de bordure pour plus d'esthétique
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(menthol_green, 5), // Bordure vert agréable
                BorderFactory.createEmptyBorder(3, 3, 3, 3) // Espace de 3px à l'intérieur
        ));

        // Ajouter le panneau à la fenêtre
        frame.add(panel);
        frame.setLocationRelativeTo(null); // Centrer la fenêtre sur l'écran
        frame.setVisible(true);

        // Utiliser un Timer pour fermer la fenêtre après 5 secondes
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                frame.dispose();
            }
        }, 5000);
    }


    /**
     * Updates the user interface to display information about the next player's turn.
     * This includes updating a label with the player's name and refreshing the player's icon.
     *
     * @param nextPlayer : player whose turn is next. The player's name and associated icon will be displayed in the user interface
     */
    public void afficherTourSuivant(Player nextPlayer) {
        // Mettre à jour le texte. Même logique avec HTML.
        // Avec retour à la ligne avant le nom du personnage
        String textHtml = "<html><div style='text-align:center; width:140px;'>C'est à toi de jouer, <br />"
                + nextPlayer.getName() + " !</div></html>";
        tourLabel.setText(textHtml);

        // Mettre à jour l'image si nécessaire, si le joueur change
        Image originalImage = imageStore.getPlayerIcons(gameFacade.getCurrentPlayerIndex());
        Image scaledImage = originalImage.getScaledInstance(200, 200, Image.SCALE_SMOOTH);
        playerIconLabel.setIcon(new ImageIcon(scaledImage));

        repaint();
    }


    /**
     * Draws the player's pieces on the game board, handling their positions, shadows, and zoom effects
     * for the current player's piece.
     *
     * @param g       : Graphics object used to draw on the game board
     * @param xOffset : horizontal offset to position the pieces correctly on the board
     * @param yOffset : vertical offset to position the pieces correctly on the board
     */
    private void drawPieces(Graphics g, int xOffset, int yOffset) {
        for (int i = 0; i < playerPositions.length; i++) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // Activer l'anti-aliasing pour améliorer le rendu

            Position position = playerPositions[i];  // Accéder à la position du joueur à l'index i

            // Obtenir les coordonnées x et y depuis l'objet Position
            int x = xOffset + position.getY() * TILE_SIZE;
            int y = yOffset + position.getX() * TILE_SIZE;

            // Charger l'image de la pièce du joueur
            BufferedImage pieceImage = imageStore.getPieceImage(i);
            if (pieceImage != null) {
                // Dessiner l'ombre pour tous les pions, y compris celui qui est zoomé
                g2d.setColor(shadow); // Ombre noire avec transparence
                int shadowOffsetX = 5;
                int shadowOffsetY = 5;

                // Dessiner l'ombre ovale derrière le pion
                g2d.fillOval(x + shadowOffsetX + 30, y + shadowOffsetY + 30, PLAYER_SIZE - 22, PLAYER_SIZE - 24);

                // Vérifier si le joueur est le joueur courant
                if (i == gameFacade.getCurrentPlayerIndex()) {
                    // Zoomer sur le pion du joueur courant
                    int zoomFactor = 130; // Facteur de zoom
                    int zoomedCenterX = x + (TILE_SIZE - zoomFactor) / 2;
                    int zoomedCenterY = y + (TILE_SIZE - zoomFactor) / 2;

                    // Dessiner l'ombre du pion zoomé
                    g2d.fillOval(zoomedCenterX + shadowOffsetX + 15, zoomedCenterY + shadowOffsetY + 15, zoomFactor - 30, zoomFactor - 32); // Ombre pour le pion zoomé

                    // Dessiner l'image du pion zoomé
                    g.drawImage(pieceImage, zoomedCenterX, zoomedCenterY, zoomFactor, zoomFactor, null);
                } else {
                    // Dessiner l'image de la pièce centrée dans la case
                    g.drawImage(pieceImage, x + (TILE_SIZE - PLAYER_SIZE) / 2, y + (TILE_SIZE - PLAYER_SIZE) / 2, PLAYER_SIZE, PLAYER_SIZE, null);
                }
            }
        }
    }

    /**
     * Draws the player's cards on the screen at a specified position and with specified card properties (size, overlap).
     *
     * @param g           the graphics context used for rendering the cards
     * @param player      : player whose cards will be drawn
     * @param xOffset     : x-coordinate offset for the cards' starting position
     * @param yOffset     : y-coordinate offset for the cards' starting position
     * @param cardWidth   : width of each card to be drawn
     * @param cardHeight  : height of each card to be drawn
     * @param cardOverlap : vertical overlap value determining how much of one card overlaps on the other
     * @param playerIndex : index of the player used to determine the orientation and position of the card display
     */
    private void drawPlayerCards(Graphics g, Player player, int xOffset, int yOffset, int cardWidth, int cardHeight, int cardOverlap, int playerIndex) {
        Card[] playerCards = player.getCards();

        int startX = 0, startY = 0;

        switch (playerIndex) {
            case 0:
                startX = xOffset - 180;
                startY = yOffset + 30;
                break;
            case 1:
                startX = xOffset + BOARD_SIZE + 120;
                startY = yOffset + 30;
                break;
            case 2:
                startX = xOffset - 180;
                startY = yOffset + BOARD_SIZE - 90;
                break;
            case 3:
                startX = xOffset + BOARD_SIZE + 120;
                startY = yOffset + BOARD_SIZE - 90;
                break;
        }

        // On superpose les cartes verticalement.
        // 'cardOverlap' représente le décalage vertical entre le haut de chaque carte et la précédente.
        // Par exemple, si cardOverlap est petit, les cartes se chevaucheront beaucoup.
        // Si cardOverlap est proche de 0, la carte suivante recouvre presque totalement la précédente.
        // Si cardOverlap est plus grand, on voit plus de chaque carte.
        for (int i = playerCards.length - 1; i >= 0; i--) {
            Card card = playerCards[i];
            int cardId = card.getTreasure();
            BufferedImage cardImage = imageStore.getCardWithTreasure(cardId, true);

            if (cardImage != null && !card.isFound()) {
                // En inversant la boucle, i=playerCards.length-1 est la première carte dessinée (en bas)
                // et i=0 sera dessinée en dernier (au-dessus)
                int x = startX;
                int y = startY + i * cardOverlap;
                g.drawImage(cardImage, x, y, cardWidth, cardHeight, null);
            }
        }
    }

    /**
     * Updates the state of the given gameboard and triggers a repaint of the component.
     *
     * @param gameboard : the Gameboard object to be updated
     */
    @Override
    public void update(Gameboard gameboard) {
        System.out.println("game board updated!");

        this.repaint();     // Redessine le composant
    }

    /**
     * Updates the player's objective and triggers a redraw action.
     *
     * @param objective : the new objective for the player
     */
    @Override
    public void UpdatePlayerObjectiveChanged(int objective) {
        System.out.println("Objectif du joueur mis à jour : " + objective);
        repaint(); // Cela redessinera les cartes du joueur
    }

    /**
     * Updates the player's position when it has changed.
     *
     * @param newPosition : the new position of the player
     */
    @Override
    public void UpdatePlayerPositionChanged(Position newPosition) {
        System.out.println("\nLa position a été mise à jour : " + newPosition);
        repaint();
    }

    /**
     * Updates the current player and performs actions related to the player's turn change.
     *
     * @param currentPlayer : current player whose turn it is now
     */
    @Override
    public void UpdateCurrentPlayerChanged(Player currentPlayer) {
        System.out.println("\nLe joueur actuel est maintenant : " + currentPlayer.getName());
        afficherTourSuivant(currentPlayer);
    }
}
