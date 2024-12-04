package main.labyrinth;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import main.labyrinth.models.game.*;
import main.labyrinth.models.tiles.*;
import main.labyrinth.models.geometry.Position;
import main.labyrinth.models.data.ImageStore;

public class GamePanel extends JPanel {
    private Gameboard gameboard;
    private GameFacade gameFacade;  // Gestionnaire du jeu
    private ImageStore imageStore;

    private static final int TILE_SIZE = 143;  // Taille des tuiles
    private static final int PLAYER_SIZE = 90; // Taille des joueurs
    private static final int BOARD_SIZE = 1000; // Taille du plateau de jeu
    private static final int PADDING = 50;     // Espacement autour du plateau et des joueurs

    // Constructeur qui prend en charge le Gameboard et le GameFacade
    public GamePanel(Gameboard gameboard, GameFacade gameFacade) {
        this.gameboard = gameboard;
        this.gameFacade = gameFacade;
        this.imageStore = new ImageStore();  // Initialisation de l'ImageStore
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int xOffset = (getWidth() - BOARD_SIZE) / 2; // Centrage du plateau dans la fenêtre
        int yOffset = (getHeight() - BOARD_SIZE) / 2; // Centrage du plateau dans la fenêtre

        // Dessiner les tuiles du plateau
        for (int row = 0; row < 7; row++) {
            for (int col = 0; col < 7; col++) {
                Tile tile = gameboard.getTile(new Position(row, col));
                if (tile != null) {
                    int tileIndex = getTileIndex(tile);
                    int orientation = tile.get_orientation();
                    boolean hasTreasure = tile._hasTreasure;
                    int treasureIndex = tile.getTreasure();

                    try {
                        // Dessiner l'image de la tuile
                        BufferedImage tileImage = imageStore.getTileImage(tileIndex, orientation, hasTreasure, treasureIndex);
                        g.drawImage(tileImage, xOffset + col * TILE_SIZE, yOffset + row * TILE_SIZE, TILE_SIZE, TILE_SIZE, null);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        // Dessiner les joueurs autour du plateau
        drawPlayers(g, xOffset, yOffset);
    }

    // Méthode pour dessiner les joueurs autour du plateau
    private void drawPlayers(Graphics g, int xOffset, int yOffset) {
        Player[] players = gameFacade.get_players(); // Récupérer les joueurs depuis le GameFacade
        int playerSize = PLAYER_SIZE;
        int cardWidth = 70;
        int cardHeight = 100;
        int cardOverlap = 0;

        for (int i = 0; i < players.length; i++) {
            int x = 0, y = 0;

            switch (i) {
                case 0: // Joueur 1 : en haut à gauche
                    x = xOffset - playerSize / 1;
                    y = yOffset - playerSize / 4;
                    break;
                case 1: // Joueur 2 : en haut à droite
                    x = xOffset + BOARD_SIZE - playerSize / 100;
                    y = yOffset - playerSize / 4;
                    break;
                case 2: // Joueur 3 : en bas à gauche
                    x = xOffset - playerSize / 1;
                    y = yOffset + BOARD_SIZE - playerSize / 1;
                    break;
                case 3: // Joueur 4 : en bas à droite
                    x = xOffset + BOARD_SIZE - playerSize / 100;
                    y = yOffset + BOARD_SIZE - playerSize / 1;
                    break;
            }

            BufferedImage playerImage = imageStore.getPlayerIcons(i);
            if (playerImage != null) {
                g.drawImage(playerImage, x, y, playerSize, playerSize, null);
            }

            g.setColor(Color.BLACK);
            g.drawString(players[i].getName(), x + 5, y + playerSize + 15);

            drawPlayerCards(g, players[i], xOffset, yOffset, cardWidth, cardHeight, cardOverlap, i);
        }
    }

    // Méthode pour dessiner les cartes d'un joueur
    private void drawPlayerCards(Graphics g, Player player, int xOffset, int yOffset, int cardWidth, int cardHeight, int cardOverlap, int playerIndex) {
        Card[] playerCards = player.getCards(); // Récupérer les cartes du joueur

        int startX = 0, startY = 0;

        switch (playerIndex) {
            case 0:
                startX = xOffset - 180;
                startY = yOffset - 10;
                break;
            case 1:
                startX = xOffset + BOARD_SIZE + 70;
                startY = yOffset - 10;
                break;
            case 2:
                startX = xOffset - 180;
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

            if (cardImage != null) {
                int x = startX + i * (cardWidth - cardOverlap);
                int y = startY;
                g.drawImage(cardImage, x, y, cardWidth, cardHeight, null);
            }
        }
    }

    // Méthode pour obtenir l'index de la tuile
    private int getTileIndex(Tile tile) {
        if (tile instanceof AngledTile) return 0;
        if (tile instanceof TShapedTile) return 2;
        return 1;
    }
}
