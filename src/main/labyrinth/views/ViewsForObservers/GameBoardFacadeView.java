package main.labyrinth.views.ViewsForObservers;

import main.labyrinth.controllers.GameboardController;
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
import java.util.List;
import java.util.Map;


public class GameBoardFacadeView extends JPanel implements GameBoardObserver, GameFacadeObserver {
    private JButton arrowButton;  // Le bouton de flèche
    private JButton rotateTileButton;
    private final Gameboard gameboard;
    private final GameFacade gameFacade;
    private final ImageStore imageStore;
    private final GameboardController gameboardController;

    private static final int TILE_SIZE = 128;
    private static final int BOARD_SIZE = 900;
    private static final int PADDING = 50;
    private static final int PLAYER_SIZE = 90;
    // Variable pour suivre le bouton sélectionné
    private JButton selectedButton;

    private int[][] playerPositions = {
            {0, 0}, {0, 6}, {6, 0}, {6, 6}
    };


    public GameBoardFacadeView(Gameboard gameboard, GameFacade gameFacade, ImageStore imageStore) {

        this.gameboard = gameboard;
        gameboard.addGameboardObserver(this);
        this.gameFacade = gameFacade;
        this.imageStore = imageStore;
        this.gameboardController = new GameboardController(gameboard);
        setLayout(null);  // Layout absolu pour la gestion de la position des éléments
        setPreferredSize(new Dimension(BOARD_SIZE + 100, BOARD_SIZE + 100)); // Marge supplémentaire pour l'affichage
        // Initialisation du bouton Rotate Tile
        rotateTileButton = new JButton("Rotate Tile");
       rotateTileButton.setBounds(1520, 600, 140, 30);  // Position du bouton à l'écran
      rotateTileButton.addActionListener(e -> rotateFreeTile());
     setLayout(null); // Utilisation d'un layout absolu pour le positionnement
        add(rotateTileButton);

        createArrowButtons();
       // System.out.println("///////////////////////////////////////////////"


        Position position = new Position(3, 3);

// Affichage des tuiles accessibles à partir de la position donnée
        List<Position> accessibleTiles = gameboard.getAccessibleTiles(position);
        System.out.println("Tuiles accessibles à partir de la position " + position + ": " + accessibleTiles);

// Boucle pour rendre chaque tuile accessible cliquable
        for (Position accessibleTile : accessibleTiles) {
            makeTileClickable(accessibleTile);  // Rend la tuile cliquable en appelant la méthode makeTileClickable
        }

        System.out.println("////////////////////////////////////////////////clique");



    }
    private void makeTileClickable(Position position) {
        JButton tileButton = new JButton();

        // Définir la taille et la position du bouton
        tileButton.setBounds(1920-900-510+10+position.getY() * TILE_SIZE, 1080-900-100-5+position.getX() * TILE_SIZE, TILE_SIZE-20, TILE_SIZE-20);

        // Rendre le bouton transparent
        tileButton.setOpaque(false);
        tileButton.setContentAreaFilled(false); // Empêche le remplissage du bouton
        tileButton.setBorderPainted(true); // Ajoute une bordure si vous le souhaitez

        // Modifier la bordure pour qu'elle soit visible (optionnel, pour donner une indication visuelle)
        tileButton.setBorder(BorderFactory.createLineBorder(Color.RED, 2));  // Ajoute une bordure rouge

        // Ajouter l'effet au survol (quand la souris passe dessus)
        tileButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                tileButton.setBorder(BorderFactory.createLineBorder(Color.GREEN, 2));  // Change la bordure au survol
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                tileButton.setBorder(BorderFactory.createLineBorder(Color.RED, 2));  // Revenir à la bordure rouge après le survol
            }
        });

        // Ajouter l'action au clic
        tileButton.addActionListener(e -> {
            System.out.println("hollaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
            System.exit(0);
        });

        // Ajouter le bouton à votre container (comme un JPanel ou autre)
        add(tileButton);
    }



    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int xOffset = (getWidth() - BOARD_SIZE) / 2;
        int yOffset = (getHeight() - BOARD_SIZE) / 2;

        // Dessiner le plateau de jeu
        drawGameboard(g, xOffset, yOffset);
        // Dessiner les joueurs et leurs pièces
        drawPlayersAndPieces(g, xOffset, yOffset);
        // Dessiner les cartes pour chaque joueur
        Player[] players = gameFacade.get_players();
        for (int i = 0; i < players.length; i++) {
            drawPlayerCards(g, players[i], xOffset, yOffset, 60, 100, 10, i);
        }
    }
    private JButton createArrowButton(String imagePath, int index, String direction) {
        JButton button = new JButton(new ImageIcon(imagePath));
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);

        // Ajouter un ActionListener pour gérer la sélection du bouton
        button.addActionListener(e -> {
            // Si un bouton est déjà sélectionné, le désactiver
            if (selectedButton != null) {
                selectedButton.setEnabled(true);  // Réactiver le bouton sélectionné précédemment
            }
            // Désactiver tous les autres boutons
            selectedButton = button;  // Mettre à jour le bouton sélectionné
            selectedButton.setEnabled(false);  // Désactiver le bouton actuel

            // Appeler la méthode appropriée selon la direction du bouton
            switch (direction) {
                case "droite":
                    gameboardController.shiftRow(index,1);
                    break;
                case "gauche":
                    gameboardController.shiftRow(index,3);
                    break;
                case "haut":
                    gameboardController.shiftColumn(index,0);
                    break;
                case "bas":
                    gameboardController.shiftColumn(index,2);
                    break;
            }

            System.out.println("Action exécutée pour " + direction + " !");
        });

        return button;
    }
    // Méthode pour récupérer et afficher les voisins disponibles à la position actuelle du joueur


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
            JButton rightButton = createArrowButton("/home/ychettati/Bureau/a-31-labyrinthe/res/img/flecheD.png", rowIndex, "droite");
            rightButton.setBounds(rightPositions[i][0], rightPositions[i][1], 50, 50);
            add(rightButton);

            // Gauche
            JButton leftButton = createArrowButton("/home/ychettati/Bureau/a-31-labyrinthe/res/img/flecheG.png", rowIndex, "gauche");
            leftButton.setBounds(leftPositions[i][0], leftPositions[i][1], 50, 50);
            add(leftButton);

            // Haut
            JButton upButton = createArrowButton("/home/ychettati/Bureau/a-31-labyrinthe/res/img/flecheH.jpeg", rowIndex, "haut");
            upButton.setBounds(upPositions[i][0], upPositions[i][1], 50, 50);
            add(upButton);

            // Bas
            JButton downButton = createArrowButton("/home/ychettati/Bureau/a-31-labyrinthe/res/img/flecheB.png", rowIndex, "bas");
            downButton.setBounds(downPositions[i][0], downPositions[i][1], 50, 50);
            add(downButton);
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
        int freeTileX = xOffset + BOARD_SIZE + PADDING+70;
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

                g.setColor(Color.BLACK);
                g.drawRect(freeTileX, freeTileY, TILE_SIZE, TILE_SIZE); // Encadrer la tuile libre
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
        for (int i = 0; i < players.length; i++) {
            int x = 0, y = 0;
            switch (i) {
                case 0:
                    x = xOffset - PLAYER_SIZE / 1;
                    y = yOffset - PLAYER_SIZE / 4;
                    break;
                case 1:
                    x = xOffset + BOARD_SIZE - PLAYER_SIZE / 100;
                    y = yOffset - PLAYER_SIZE / 4;
                    break;
                case 2:
                    x = xOffset - PLAYER_SIZE / 1;
                    y = yOffset + BOARD_SIZE - PLAYER_SIZE / 1;
                    break;
                case 3:
                    x = xOffset + BOARD_SIZE - PLAYER_SIZE / 100;
                    y = yOffset + BOARD_SIZE - PLAYER_SIZE;
                    break;
            }

            BufferedImage playerImage = imageStore.getPlayerIcons(i);
            if (playerImage != null) {
                g.drawImage(playerImage, x, y, PLAYER_SIZE, PLAYER_SIZE, null);
            }
            g.setColor(Color.BLACK);
            g.drawString(players[i].getName(), x + 5, y + PLAYER_SIZE + 15);
        }
    }

    private void drawPieces(Graphics g, int xOffset, int yOffset) {
        for (int i = 0; i < playerPositions.length; i++) {
            int[] position = playerPositions[i];
            int x = xOffset + position[1] * TILE_SIZE;
            int y = yOffset + position[0] * TILE_SIZE;

            BufferedImage pieceImage = imageStore.getPieceImage(i);
            if (pieceImage != null) {
                g.drawImage(pieceImage, x + (TILE_SIZE - PLAYER_SIZE) / 2, y + (TILE_SIZE - PLAYER_SIZE) / 2, PLAYER_SIZE, PLAYER_SIZE, null);
            }
        }
    }

    private void drawPlayerCards(Graphics g, Player player, int xOffset, int yOffset, int cardWidth, int cardHeight, int cardOverlap, int playerIndex) {
        Card[] playerCards = player.getCards();

        int startX = 0, startY = 0;

        switch (playerIndex) {
            case 0:
                startX = xOffset - 400;
                startY = yOffset - 10;
                break;
            case 1:
                startX = xOffset + BOARD_SIZE + 70;
                startY = yOffset - 10;
                break;
            case 2:
                startX = xOffset - 400;
                startY = yOffset + BOARD_SIZE - 90;
                break;
            case 3:
                startX = xOffset + BOARD_SIZE + 80;
                startY = yOffset + BOARD_SIZE - 90;
                break;
        }

        for (int i = 0; i < playerCards.length; i++) {
            Card card = playerCards[i];
            int cardId = card.getTreasure();
            BufferedImage cardImage = imageStore.getCardWithTreasure(cardId, true);

            if (cardImage != null && !card.isFound()) {
                int x = startX + i * (cardWidth - cardOverlap);
                int y = startY;
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
        repaint();
    }
   private void rotateFreeTile() {
        Tile freeTile = gameboard.getFreeTile();
        if (freeTile != null) {
            int currentOrientation = freeTile.get_orientation();
            int newOrientation = (currentOrientation + 1) % 4;  // Rotation de 90 degrés
            gameboardController.rotateTile(freeTile, newOrientation);  // Utilisation du contrôleur pour effectuer la rotation
            repaint();  // Redessiner le panneau après la rotation
        }
    }
    public static void main(String[] args) {
        // Initialisation du modèle et des composants nécessaires
        Gameboard gameboard = new Gameboard();
        GameFacade gameFacade = new GameFacade();
        ImageStore imageStore = new ImageStore();

        // Créez l'interface graphique
        JFrame frame = new JFrame("Labyrinth Game");
        GameBoardFacadeView view = new GameBoardFacadeView(gameboard, gameFacade, imageStore);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(view);
        frame.pack();
        frame.setSize(1920, 1080);
        frame.setLocationRelativeTo(null); // Centrer la fenêtre
        frame.setVisible(true);
    }
}
