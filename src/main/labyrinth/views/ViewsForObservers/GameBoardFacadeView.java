package main.labyrinth.views.ViewsForObservers;

import main.labyrinth.controllers.GameFacadeController;
import main.labyrinth.controllers.GameboardController;
import main.labyrinth.controllers.TourController;
import main.labyrinth.controllers.UIController;
import main.labyrinth.models.game.Gameboard;
import main.labyrinth.models.tiles.Tile;
import main.labyrinth.models.geometry.Position;
import main.labyrinth.models.observers.GameBoardObserver;
import main.labyrinth.models.observers.GameFacadeObserver;
import main.labyrinth.models.data.ImageStore;
import main.labyrinth.models.game.GameFacade;
import main.labyrinth.models.game.Player;
import main.labyrinth.models.game.Card;

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

public class GameBoardFacadeView extends JPanel implements GameBoardObserver, GameFacadeObserver {
    private Runnable onNewGame; // Champ pour le callback
    private static final int PLAYER_SPACING = 20; // Espace supplémentaire entre les joueurs
    private static final int CARD_SPACING = 5; // Distance entre l'image du joueur et la première carte.

    private List<Position> clickedTiles = new ArrayList<>();

    private List<JButton> arrowButtons = new ArrayList<>(); // les boutons flèches

    private JButton rotateTileButton;
    private final Gameboard gameboard;
    private String lastArrowDirection = null; // Direction de la dernière flèche cliquée
    private int lastArrowIndex = -1;          // Indice de la dernière flèche cliquée

    // Getters
    public String getLastArrowDirection() {
        return lastArrowDirection;
    }

    public int getLastArrowIndex() {
        return lastArrowIndex;
    }
    private final TourController tourController;
    private JPanel tourPanel;
    private JLabel tourLabel;
    private JLabel playerIconLabel;

    private final GameFacade gameFacade;
    private final ImageStore imageStore;
    private final GameboardController gameboardController;
    private final GameFacadeController gameFacadeController;
    private final UIController uiController;

    private static final int TILE_SIZE = 128;
    private static final int BOARD_SIZE = 900;
    private static final int PADDING = 50;
    private static final int PLAYER_SIZE = 90;
    // Variable pour suivre le bouton sélectionné
    private JButton selectedButton;

    private Position[] playerPositions;

    // Définir les nouvelles positions des joueurs
    public void setPlayerPositions(Position[] playerPositions) {
        // Vérifie si les positions sont valides (optionnel)
        if (playerPositions.length != 4) {
            throw new IllegalArgumentException("Il doit y avoir 4 joueurs.");
        }
        this.playerPositions = playerPositions;
    }

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
        setPreferredSize(new Dimension(BOARD_SIZE + 100, BOARD_SIZE + 100)); // Marge supplémentaire pour l'affichage

        // Initialisation du bouton Rotate Tile
        rotateTileButton = new JButton("TOURNER");
        rotateTileButton.setBounds(1530, 600, 129, 30);  // Position du bouton à l'écran
        Font buttonFont = new Font("Arial", Font.BOLD, 14); // Choisir la police, style et taille
        rotateTileButton.setFont(buttonFont);
        Color beige = new Color(222, 198, 150);
        rotateTileButton.setBackground(beige); // Changer la couleur d'arrière-plan
        rotateTileButton.setForeground(Color.DARK_GRAY); // Changer la couleur du texte
        rotateTileButton.addActionListener(e -> uiController.onRotateTileClicked());
        add(rotateTileButton);

        createArrowButtons();
        initTourPanel(gameFacade.getCurrentPlayer(), imageStore);
    }

    private Map<Position, JButton> tileButtons = new HashMap<>();

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

    public void initTourPanel(Player currentPlayer, ImageStore imageStore) {
        // Créer le panneau
        tourPanel = new JPanel();
        tourPanel.setLayout(new BoxLayout(tourPanel, BoxLayout.Y_AXIS));
        tourPanel.setBackground(new Color(174, 214, 241));
        tourPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(127, 140, 141), 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Récupérer l'image du joueur et la redimensionner
        Image originalImage = imageStore.getPlayerIcons(gameFacade.getCurrentPlayerIndex());
        Image scaledImage = originalImage.getScaledInstance(60, 60, Image.SCALE_SMOOTH);
        playerIconLabel = new JLabel(new ImageIcon(scaledImage));

        // Créer le texte avec une largeur fixée (50px),
        // qui forcera le retour à la ligne si le texte est trop long
        String textHtml = "<html><div style='text-align:center; width:50px;'>C'est à toi de jouer "
                + currentPlayer.getName() + " !</div></html>";

        tourLabel = new JLabel(textHtml);
        tourLabel.setFont(new Font("Arial", Font.BOLD, 14));
        tourLabel.setForeground(new Color(44, 62, 80));
        tourLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Centrer l'étiquette dans le panneau
        playerIconLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Centrer l'image aussi

        // Ajouter l'image puis le texte au panneau
        tourPanel.add(playerIconLabel);
        tourPanel.add(Box.createVerticalStrut(10)); // Espace vertical entre l'image et le texte
        tourPanel.add(tourLabel);

        // Ajuster les dimensions et la position du panneau (selon ton layout)
        tourPanel.setBounds(150, 400, 110, 200);
        add(tourPanel);
    }

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

    // Méthode pour afficher la vue de fin de partie
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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(imageStore.get_handBackground(), 0, 0, getWidth(), getHeight(), this);
        int xOffset = (getWidth() - BOARD_SIZE) / 2;
        int yOffset = (getHeight() - BOARD_SIZE) / 2;

        // Dessiner la bordure noire autour du plateau
        Graphics2D g2d = (Graphics2D) g;  // Convertir Graphics en Graphics2D
        g2d.setColor(Color.DARK_GRAY);  // Couleur de la bordure
        g2d.setStroke(new BasicStroke(8)); // Épaisseur de la bordure (8 pixels)
        g2d.drawRect(xOffset - 4, yOffset - 4, BOARD_SIZE + 4, BOARD_SIZE + 4); // Dessine le rectangle

        // Dessiner le plateau de jeu
        drawGameboard(g, xOffset, yOffset);

        // Dessiner les joueurs et leurs pièces
        drawPlayersAndPieces(g, xOffset, yOffset);

        // Dessiner les cartes pour chaque joueur
        Player[] players = gameFacade.get_players();
        for (int i = 0; i < players.length; i++) {
            drawPlayerCards(g, players[i], xOffset, yOffset, 60, 100, -5, i);
        }
    }

    public void DesactiverFleche() {
        // Désactiver tous les autres boutons flèches
        for (JButton b : arrowButtons) {
            b.setEnabled(false);
        }
    }

    public void ActiverFleche() {
        // Activer tous les autres boutons flèches
        for (JButton b : arrowButtons) {
            b.setEnabled(true);
        }
    }

   /* private JButton createArrowButton(String imagePath, int index, String direction) {
        JButton button = new JButton(new ImageIcon(imagePath));
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);

        // Ajouter un ActionListener pour gérer la sélection du bouton
        button.addActionListener(e -> {
            System.out.println("Bouton flèche " + direction + " cliqué.");

            gameFacade.getCurrentPlayer().setLastPosition(null);

            DesactiverFleche();

            // Appeler la méthode appropriée selon la direction du bouton
            uiController.onArrowButtonClicked(direction, index);

            // Rendre les tuiles cliquables et afficher les tuiles adjacentes accessibles
            System.out.println("Réaffichage des tuiles accessibles.");
            RendreTabCliquable();
            showAdjacentAccessibleTiles(gameFacade.getCurrentPlayer().getCurrentTile());

            System.out.println("Action exécutée pour " + direction + " !");
        });

        return button;
    }*/
  /* private JButton createArrowButton(String imagePath, int index, String direction) {
       JButton button = new JButton(new ImageIcon(imagePath));
       button.setBorderPainted(false);
       button.setContentAreaFilled(false);
       button.setFocusPainted(false);

       // Définir des propriétés client pour identifier la direction et l'indice
       button.putClientProperty("direction", direction);
       button.putClientProperty("index", index);

       // Ajouter un ActionListener pour gérer le clic
       button.addActionListener(e -> {
           System.out.println("Bouton flèche " + direction + " cliqué. Indice: " + index);

           // Enregistrer la direction et l'indice de la dernière flèche cliquée
           lastArrowDirection = direction;
           lastArrowIndex = index;
           System.out.println("Dernière flèche cliquée: Direction = " + lastArrowDirection + ", Indice = " + lastArrowIndex);
           gameFacade.getCurrentPlayer().setLastPosition(null);

           // Désactiver toutes les flèches
           DesactiverFleche();

           // Appeler la méthode appropriée selon la direction du bouton
           uiController.onArrowButtonClicked(direction, index);

           // Rendre les tuiles cliquables et afficher les tuiles adjacentes accessibles
           System.out.println("Réaffichage des tuiles accessibles.");
           RendreTabCliquable();
           showAdjacentAccessibleTiles(gameFacade.getCurrentPlayer().getCurrentTile());


       });

       return button;
   }*/
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


    // Méthode pour obtenir la direction opposée
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

    // Méthode pour désactiver la flèche opposée spécifique
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

    // Optionnel: Méthode pour réactiver la flèche opposée si nécessaire
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

    public void RendreTabCliquable() {
        for (int x = 0; x < 7; x++) { // Parcourir les lignes
            for (int y = 0; y < 7; y++) { // Parcourir les colonnes
                Position position = new Position(x, y);
                makeTileClickable(position);
            }
        }
    }

    private void createArrowButtons() {
        // Définir les indices spécifiques pour les lignes et les colonnes
        int[] rowIndices = {1, 3, 5};  // Indices pour les lignes
        int[] colIndices = {1, 3, 5};  // Indices pour les colonnes

        // Positionnement des boutons pour chaque direction
        int[][] rightPositions = {
                {450, 220}, // Position pour 1ère ligne
                {450, 500}, // Position pour 2ème ligne
                {450, 730}  // Position pour 3ème ligne
        };

        int[][] leftPositions = {
                {1420, 220},
                {1420, 500},
                {1420, 730}
        };

        int[][] upPositions = {
                {680, 960},
                {920, 960},
                {1200, 960}
        };

        int[][] downPositions = {
                {680, 10},
                {920, 10},
                {1200, 10}
        };

        // Création des boutons pour chaque direction
        for (int i = 0; i < rowIndices.length; i++) {  // Parcours des indices de lignes
            int rowIndex = rowIndices[i];

            // Droite
            JButton rightButton = createArrowButton("../res/img/arrows/arrowD.png", rowIndex, "droite");
            rightButton.setBounds(rightPositions[i][0], rightPositions[i][1], 50, 50);
            add(rightButton);
            arrowButtons.add(rightButton);

            // Gauche
            JButton leftButton = createArrowButton("../res/img/arrows/arrowG.png", rowIndex, "gauche");
            leftButton.setBounds(leftPositions[i][0], leftPositions[i][1], 50, 50);
            add(leftButton);
            arrowButtons.add(leftButton);

            // Haut
            JButton upButton = createArrowButton("../res/img/arrows/arrowH.png", rowIndex, "haut");
            upButton.setBounds(upPositions[i][0], upPositions[i][1], 50, 50);
            add(upButton);
            arrowButtons.add(upButton);

            // Bas
            JButton downButton = createArrowButton("../res/img/arrows/arrowB.png", rowIndex, "bas");
            downButton.setBounds(downPositions[i][0], downPositions[i][1], 50, 50);
            add(downButton);
            arrowButtons.add(downButton);
        }
    }

    private void drawGameboard(Graphics g, int xOffset, int yOffset) {
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

    private void drawFreeTile(Graphics g, int xOffset) {
        int freeTileX = xOffset + BOARD_SIZE + PADDING + 70;
        int freeTileY = (getHeight() - TILE_SIZE) / 2;

        Tile freeTile = gameboard.getFreeTile();
        if (freeTile != null) {
            int tileIndex = getTileIndex(freeTile);
            int orientation = freeTile.get_orientation();
            boolean hasTreasure = freeTile._hasTreasure;
            int treasureIndex = freeTile.getTreasure();

            try {
                BufferedImage tileImage = imageStore.getTileImage(tileIndex, orientation, hasTreasure, treasureIndex);
                g.drawImage(tileImage, freeTileX, freeTileY, TILE_SIZE, TILE_SIZE, null);

                // Dessiner le cadre autour de la tuile libre avec des coins arrondis
                Graphics2D g2d = (Graphics2D) g; // Convertir pour Graphics2D
                g2d.setColor(Color.DARK_GRAY); // Couleur du cadre
                g2d.setStroke(new BasicStroke(4)); // Épaisseur du cadre (ajustez à votre goût)
                RoundRectangle2D roundedRectangle = new RoundRectangle2D.Double(
                        freeTileX, freeTileY, TILE_SIZE, TILE_SIZE, 50, 50);  // 50 pixels pour le rayon des coins arrondis
                g2d.draw(roundedRectangle); // Dessiner le cadre arrondi

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println(gameboard.getFreeTile().toString() + " //////////////////////////////// ////////////////////////////////");
    }

    private int getTileIndex(Tile tile) {
        // Détermine l'index de l'image en fonction du type de tuile
        if (tile instanceof main.labyrinth.models.tiles.AngledTile) return 0;
        if (tile instanceof main.labyrinth.models.tiles.TShapedTile) return 2;
        return 1; // Par défaut, pour les tuiles droites ou autres
    }

    private void drawPlayersAndPieces(Graphics g, int xOffset, int yOffset) {
        // Dessiner les joueurs et leurs pièces
        drawPlayers(g, xOffset, yOffset);
        drawPieces(g, xOffset, yOffset);
    }

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
                    x = xOffset + BOARD_SIZE - PLAYER_SIZE / 100 + PLAYER_SPACING; // Décalage vers la droite (ajout de PLAYER_SPACING)
                    y = yOffset - PLAYER_SIZE / 4; // Ajustement vertical
                    break;
                case 2: // Joueur à gauche (index 2)
                    x = xOffset - PLAYER_SIZE / 1 - PLAYER_SPACING; // Décalage vers la gauche (ajout de PLAYER_SPACING)
                    y = yOffset + BOARD_SIZE - PLAYER_SIZE / 1; // Ajustement vertical
                    break;
                case 3: // Joueur à droite (index 3)
                    x = xOffset + BOARD_SIZE - PLAYER_SIZE / 100 + PLAYER_SPACING; // Décalage vers la droite (ajout de PLAYER_SPACING)
                    y = yOffset + BOARD_SIZE - PLAYER_SIZE; // Ajustement vertical
                    break;
            }

            BufferedImage playerImage = imageStore.getPlayerIcons(i);
            if (playerImage != null) {
                // Créer une forme arrondie
                RoundRectangle2D roundedRectangle = new RoundRectangle2D.Double(x, y, PLAYER_SIZE, PLAYER_SIZE, 20, 20); // 20 pixels de rayon pour les coins arrondis

                // Définir le clip pour dessiner l'image avec des coins arrondis
                g2d.setClip(roundedRectangle);

                // Dessiner l'image dans la zone découpée
                g2d.drawImage(playerImage, x, y, PLAYER_SIZE, PLAYER_SIZE, null);

                // Réinitialiser le clip
                g2d.setClip(null);
            }

            g2d.setColor(Color.BLACK);
            g2d.drawString(players[i].getName(), x + 5, y + PLAYER_SIZE + 15);
        }
    }

    public void afficherFelicitation(Player currentPlayer) {
        // Créer une fenêtre pour afficher le message
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(700, 200); // Taille légèrement augmentée pour un meilleur affichage
        frame.setTitle("Félicitations");

        // Créer un panneau principal avec un fond vert
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(new Color(46, 204, 113)); // Vert clair agréable

        // Ajouter un message avec une belle police et couleur
        JLabel label = new JLabel("Félicitations, " + currentPlayer.getName() + " ! Objectif trouvé !");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 20)); // Police plus grande et en gras
        label.setForeground(Color.WHITE); // Texte en blanc pour le contraste

        // Ajouter le label au panneau
        panel.add(label, BorderLayout.CENTER);

        // Ajouter un effet de bordure pour plus d'esthétique
        panel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 5)); // Bordure blanche épaisse

        // Ajouter le panneau à la fenêtre
        frame.add(panel);
        frame.setLocationRelativeTo(null); // Centrer la fenêtre sur l'écran
        frame.setVisible(true);

        // Utiliser un Timer pour fermer la fenêtre après 3 secondes
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                frame.dispose();
            }
        }, 3000);
    }

    public void afficherTourSuivant(Player nextPlayer) {
        // Mettre à jour le texte. Même logique avec HTML.
        String textHtml = "<html><div style='text-align:center; width:50px;'>C'est à toi de jouer "
                + nextPlayer.getName() + " !</div></html>";
        tourLabel.setText(textHtml);

        // Mettre à jour l'image si nécessaire, si le joueur change
        Image originalImage = imageStore.getPlayerIcons(gameFacade.getCurrentPlayerIndex());
        Image scaledImage = originalImage.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        playerIconLabel.setIcon(new ImageIcon(scaledImage));

        repaint();
    }

    private void drawPieces(Graphics g, int xOffset, int yOffset) {
        for (int i = 0; i < playerPositions.length; i++) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // Activer l'anti-aliasing pour améliorer le rendu

            Position position = playerPositions[i];  // Accéder à la position du joueur à l'index i

            // Obtenir les coordonnées x et y depuis l'objet Position
            int x = xOffset + position.getY() * TILE_SIZE;  // position.getY() pour la coordonnée x
            int y = yOffset + position.getX() * TILE_SIZE;  // position.getX() pour la coordonnée y

            // Charger l'image de la pièce du joueur
            BufferedImage pieceImage = imageStore.getPieceImage(i);
            if (pieceImage != null) {
                // Dessiner l'ombre
                g2d.setColor(new Color(0, 0, 0, 100)); // Ombre noire avec transparence
                int shadowOffsetX = 5;
                int shadowOffsetY = 5;
                // Dessiner l'ombre derrière le pion
                g2d.fillOval(x + shadowOffsetX + 30, y + shadowOffsetY + 30, PLAYER_SIZE - 22, PLAYER_SIZE - 24); // Utiliser fillOval pour l'ombre
                // Dessiner l'image de la pièce centrée dans la case
                g.drawImage(pieceImage, x + (TILE_SIZE - PLAYER_SIZE) / 2, y + (TILE_SIZE - PLAYER_SIZE) / 2, PLAYER_SIZE, PLAYER_SIZE, null);
            }
        }
    }

    private void drawPlayerCards(Graphics g, Player player, int xOffset, int yOffset, int cardWidth, int cardHeight, int cardOverlap, int playerIndex) {
        Card[] playerCards = player.getCards();

        int startX = 0, startY = 0;

        switch (playerIndex) {
            case 0:
                startX = xOffset - 150;
                startY = yOffset - 10;
                break;
            case 1:
                startX = xOffset + BOARD_SIZE + 70;
                startY = yOffset - 10;
                break;
            case 2:
                startX = xOffset - 150;
                startY = yOffset + BOARD_SIZE - 90;
                break;
            case 3:
                startX = xOffset + BOARD_SIZE + 80;
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

    @Override
    public void update(Gameboard gameboard) {
        System.out.println("game board updated!");

        this.repaint();     // Redessine le composant
    }

    @Override
    // Méthode pour mettre à jour l'objectif du joueur et redessiner
    public void UpdatePlayerObjectiveChanged(int objective) {
        System.out.println("Objectif du joueur mis à jour : " + objective);
        repaint(); // Cela redessinera les cartes du joueur
    }

    @Override
    public void UpdatePlayerPositionChanged(Position newPosition) {
        System.out.println("\nLa position a été mise à jour : " + newPosition);
        repaint();
    }

    @Override
    public void UpdateCurrentPlayerChanged(Player currentPlayer) {
        System.out.println("\nLe joueur actuel est maintenant : " + currentPlayer.getName());
        afficherTourSuivant(currentPlayer);
    }

    /*private void rotateFreeTile() {
        Tile freeTile = gameboard.getFreeTile();
        ////////////////////////////////
        System.out.println(gameboard.getFreeTile().toString()+" //////////////////////////////// ////////////////////////////////");
        if (freeTile != null) {
            int currentOrientation = freeTile.get_orientation();
            int newOrientation = (currentOrientation + 1) % 4;  // Rotation de 90 degrés
            gameboardController.rotateTile(freeTile, newOrientation);
        }
    }*/

}
