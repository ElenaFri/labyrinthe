package main.labyrinth.views.ViewsForObservers;

import main.labyrinth.controllers.GameFacadeController;
import main.labyrinth.controllers.GameboardController;
import main.labyrinth.controllers.TourController;
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
    private static final int PLAYER_SPACING = 20; // Espace supplémentaire entre les joueurs
    private static final int CARD_SPACING = 5; // Distance entre l'image du joueur et la première carte.

    private List<Position> clickedTiles = new ArrayList<>();

    private List<JButton> arrowButtons = new ArrayList<>();// les bottons fleches

    private JButton rotateTileButton;
    private final Gameboard gameboard;
    //private final TourController tourController;
    private final GameFacade gameFacade;
    private final ImageStore imageStore;
    private final GameboardController gameboardController;
    private final GameFacadeController gameFacadeController;

    private static final int TILE_SIZE = 128;
    private static final int BOARD_SIZE = 900;
    private static final int PADDING = 50;
    private static final int PLAYER_SIZE = 90;
    // Variable pour suivre le bouton sélectionné
    private JButton selectedButton;

    private Position[] playerPositions;


    // Tableau de positions


    // Définir les nouvelles positions des joueurs
    public void setPlayerPositions(Position[] playerPositions) {
        // Vérifie si les positions sont valides (optionnel)
        if (playerPositions.length != 4) {
            throw new IllegalArgumentException("Il doit y avoir 4 joueurs.");
        }
        this.playerPositions = playerPositions;

    }






    public GameBoardFacadeView(Gameboard gameboard, GameFacade gameFacade, ImageStore imageStore) {
        // Initialisation du tableau playerPositions
        playerPositions = new Position[4];

        // Remplissage du tableau playerPositions avec les positions des joueurs
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

        this.gameFacadeController=new GameFacadeController(gameFacade);
       // this.tourController=new TourController(gameFacade.getCurrentPlayer(),this.gameboard,this.gameFacadeController,this);
        setLayout(null);  // Layout absolu pour la gestion de la position des éléments
        setPreferredSize(new Dimension(BOARD_SIZE + 100, BOARD_SIZE + 100)); // Marge supplémentaire pour l'affichage
        // Initialisation du bouton Rotate Tile
        rotateTileButton = new JButton("Tourner la tuile");
       rotateTileButton.setBounds(1520, 600, 140, 30);  // Position du bouton à l'écran
      rotateTileButton.addActionListener(e -> rotateFreeTile());
     setLayout(null); // Utilisation d'un layout absolu pour le positionnement
        add(rotateTileButton);

        createArrowButtons();
        // Ajouter un bouton sur toutes les tuiles du plateau
       /* for (int x = 0; x < 7; x++) { // Parcourir les lignes
            for (int y = 0; y < 7; y++) { // Parcourir les colonnes
                Position position = new Position(x, y);
                makeTileClickable(position);
            }
        }
       Position start = gameFacade.getCurrentPlayer().getCurrentTile();
        showAdjacentAccessibleTiles(start); // Méthode pour activer/désactiver les boutons*/







//////////////////////////////////////////////////////////////
      /*  Position start = gameFacade.getCurrentPlayer().getCurrentTile();  // Exemple de position de départ
       List<Position> accessibleTiles = gameboard.getAllAccessibleTiles(start);
      //  System.out.println("Tuiles accessibles  : " + accessibleTiles);
        for (Position accessibleTile : accessibleTiles) {
            makeTileClickable(accessibleTile);  // Rend la tuile cliquable en appelant la méthode makeTileClickable
        }*/
///////////////////////////////////////////////////////////////






    }
  /*  private void makeTileClickable(Position position) {
        JButton tileButton = new JButton();

        // Définir la taille et la position du bouton
        tileButton.setBounds(1920-900-510+10+position.getY() * TILE_SIZE, 1080-900-100-5+position.getX() * TILE_SIZE, TILE_SIZE-20, TILE_SIZE-20);

        // Rendre le bouton transparent
        tileButton.setOpaque(false);
        tileButton.setContentAreaFilled(false); // Empêche le remplissage du bouton
        tileButton.setBorderPainted(true); // Ajoute une bordure

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
         //   System.exit(0);
            gameFacadeController.changePlayerPosition(position,this);
            gameFacadeController.changePlayerLastPosition(position);
           //aut exclure la position d avant du joueur
            //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
          //////////////// gameFacade.getCurrentPlayer().completeCurrentObjective();
           //repaint();

        });

        // Ajouter le bouton à  container (comme un JPanel ou autre)
        add(tileButton);
    }*/
  private Map<Position, JButton> tileButtons = new HashMap<>();

    private void makeTileClickable(Position position) {
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
            System.out.println("Tuile cliquée : " + position);

            // Mettre à jour la position du joueur et la dernière position
            gameFacade.getCurrentPlayer().setLastPosition(gameFacade.getCurrentPlayer().getCurrentTile()); // Stocker l'ancienne position
            gameFacadeController.changePlayerPosition(position, this); // Changer la position actuelle
            gameFacadeController.changePlayerObjective(this.gameboard,this);

            ///////:ici modifier a isFound

            System.out.println("/////////////////////////////////////////////////////////////////////: l index de l objectif current est "+ gameFacade.getCurrentPlayer().getCurrentObjective());
            // Afficher les nouvelles tuiles accessibles
            showAdjacentAccessibleTiles(position);

        });

        // Ajouter le bouton à l'interface
        add(tileButton);
        tileButtons.put(position, tileButton); // Stocker le bouton dans la map
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
            /////////////////////////////////////////    gameFacade.nextPlayer();
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








    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int xOffset = (getWidth() - BOARD_SIZE) / 2;
        int yOffset = (getHeight() - BOARD_SIZE) / 2;

        // Dessiner la bordure noire autour du plateau
        Graphics2D g2d = (Graphics2D) g;  // Convertir Graphics en Graphics2D
        g2d.setColor(Color.DARK_GRAY);  // Couleur de la bordure
        g2d.setStroke(new BasicStroke(5)); // Épaisseur de la bordure (5 pixels)
        g2d.drawRect(xOffset - 2, yOffset - 2, BOARD_SIZE + 4, BOARD_SIZE + 4); // Dessine le rectangle

        // Dessiner le plateau de jeu
        drawGameboard(g, xOffset, yOffset);

        // Dessiner les joueurs et leurs pièces
        drawPlayersAndPieces(g, xOffset, yOffset);

        // Dessiner les cartes pour chaque joueur
        Player[] players = gameFacade.get_players();
        for (int i = 0; i < players.length; i++) {
            drawPlayerCards(g, players[i], xOffset, yOffset, 60, 100, 85, i);
        }
    }


    private JButton createArrowButton(String imagePath, int index, String direction) {
        JButton button = new JButton(new ImageIcon(imagePath));
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);

        // Ajouter un ActionListener pour gérer la sélection du bouton
        button.addActionListener(e -> {
            // Désactiver tous les autres boutons flèches
            for (JButton b : arrowButtons) {
                  // Si ce n'est pas le bouton cliqué
                    b.setEnabled(false);

            }



            // Appeler la méthode appropriée selon la direction du bouton
            switch (direction) {
                case "droite":
                    gameboardController.shiftRow(index, 1);

                    break;
                case "gauche":
                    gameboardController.shiftRow(index, 3);

                    break;
                case "haut":
                    gameboardController.shiftColumn(index, 0);

                    break;
                case "bas":
                    gameboardController.shiftColumn(index, 2);

                    break;
            }

            /////////////////////////////////////////////////////////newwwww//////////////////////////////////////////

            for (int x = 0; x < 7; x++) { // Parcourir les lignes
                for (int y = 0; y < 7; y++) { // Parcourir les colonnes
                    Position position = new Position(x, y);
                    makeTileClickable(position);
                }
            }
            Position start = gameFacade.getCurrentPlayer().getCurrentTile();


            showAdjacentAccessibleTiles(start); // Méthode pour activer/désactiver les boutons
            //////////////////////////////////////////////////////////////////////////////////////////////////////////

            System.out.println("Action exécutée pour " + direction + " !");
        });



        return button;
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
        System.out.println(gameboard.getFreeTile().toString()+" //////////////////////////////// ////////////////////////////////");
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

    private void drawPieces(Graphics g, int xOffset, int yOffset) {
        for (int i = 0; i < playerPositions.length; i++) {
            Position position = playerPositions[i];  // Accéder à la position du joueur à l'index i

            // Obtenir les coordonnées x et y depuis l'objet Position
            int x = xOffset + position.getY() * TILE_SIZE;  // position.getY() pour la coordonnée x
            int y = yOffset + position.getX() * TILE_SIZE;  // position.getX() pour la coordonnée y

            // Charger l'image de la pièce du joueur
            BufferedImage pieceImage = imageStore.getPieceImage(i);
            if (pieceImage != null) {
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
                startX = xOffset -150;
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

        int currentIndex = player.get_currentObjectiveIndex();  // Récupère l'index de la carte à afficher
        if (currentIndex < playerCards.length) {
            Card card = playerCards[currentIndex];
            int cardId = card.getTreasure();
            BufferedImage cardImage = imageStore.getCardWithTreasure(cardId, true);

            if (cardImage != null && !card.isFound()) {
                int x = startX;
                int y = startY;
                g.drawImage(cardImage, x, y, cardWidth, cardHeight, null);
            }
        }
    }




    @Override
    public void update(Gameboard gameboard) {
        System.out.println("game board updated!yoooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo");

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
        ////////////////////////////////
       System.out.println(gameboard.getFreeTile().toString()+" //////////////////////////////// ////////////////////////////////");
        if (freeTile != null) {
            int currentOrientation = freeTile.get_orientation();
            int newOrientation = (currentOrientation + 1) % 4;  // Rotation de 90 degrés
            gameboardController.rotateTile(freeTile, newOrientation);
            freeTile.setOpenSides();// Utilisation du contrôleur pour effectuer la rotation
            //repaint();  // Redessiner le panneau après la rotation
        }
    }

}
