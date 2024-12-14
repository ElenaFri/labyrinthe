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
    private static final int PLAYER_SPACING = 40; // Espace supplémentaire entre les joueurs
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
    private static final int BOARD_SIZE = 896;
    private static final int PADDING = 50;
    private static final int PLAYER_SIZE = 90;
    // Variable pour suivre le bouton sélectionné
    private JButton selectedButton;

    // Positions de certains éléments
    private Position[] playerPositions;
    private Position freeTilePosition;

    // Couleurs utilisées
    Color beige = new Color(222, 198, 150);
    Color navy = new Color(0, 0, 90);
    Color shadow = new Color(0,0,0,100);

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

        // Dessiner l'ombre du plateau
        Graphics2D g2d = (Graphics2D) g;  // Convertir Graphics en Graphics2D
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(new Color(0, 0, 0, 100)); // Ombre noire avec transparence
        RoundRectangle2D shadowRect = new RoundRectangle2D.Double(xOffset +18, yOffset + 20, BOARD_SIZE, BOARD_SIZE, 15, 15); // Arrondie de 3px
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

    private void updateRotateButtonPosition(Position freeTilePosition, int xOffset, int yOffset) {
        if (freeTilePosition != null) {
            int buttonX = xOffset + (freeTilePosition.getY() * TILE_SIZE) + TILE_SIZE - 4;
            int buttonY = yOffset + (freeTilePosition.getX() * TILE_SIZE) + TILE_SIZE + 150; // Calculer Y pour le positionner en bas

            rotateTileButton.setBounds(buttonX, buttonY, 130, 30); // Mettre à jour la position et la taille du bouton
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
            JButton rightButton = createArrowButton("../res/img/arrows/arrowD.png", rowIndex, "droite");
            rightButton.setBounds(rightPositions[i][0], rightPositions[i][1], 50, 50);
            rightButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            add(rightButton);
            arrowButtons.add(rightButton);

            // Gauche
            JButton leftButton = createArrowButton("../res/img/arrows/arrowG.png", rowIndex, "gauche");
            leftButton.setBounds(leftPositions[i][0], leftPositions[i][1], 50, 50);
            leftButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            add(leftButton);
            arrowButtons.add(leftButton);

            // Haut
            JButton upButton = createArrowButton("../res/img/arrows/arrowH.png", rowIndex, "haut");
            upButton.setBounds(upPositions[i][0], upPositions[i][1], 50, 50);
            upButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            add(upButton);
            arrowButtons.add(upButton);

            // Bas
            JButton downButton = createArrowButton("../res/img/arrows/arrowB.png", rowIndex, "bas");
            downButton.setBounds(downPositions[i][0], downPositions[i][1], 50, 50);
            downButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // Rétablir le curseur pointer
            add(downButton);
            arrowButtons.add(downButton);
        }
    }

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
