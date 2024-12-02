package main.labyrinth;

import javax.swing.*;
import java.awt.*;
import main.labyrinth.models.game.Gameboard;
import main.labyrinth.models.tiles.*;
import main.labyrinth.models.geometry.Position;
import main.labyrinth.models.data.*;
import java.io.IOException;  // Pour les erreurs liées aux entrées/sorties (fichiers, etc.)
import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;  // Si vous utilisez des objets BufferedImage pour le chargement et la manipulation d'images
import java.io.File;  // Importation pour résoudre la classe File



class GamePanel extends JPanel {
    private Gameboard gameboard;
    private BufferedImage background;

    public GamePanel(Gameboard gameboard) {
        this.gameboard = gameboard;
        try {
            // Charger l'image de fond
            background = ImageIO.read(new File("/home/elena/Documents/a-31-labyrinthe/res/img/screens/gameboard.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int tileSize = getWidth() / 7;

        ImageStore imageStore = new ImageStore();

            if (background != null) {
                g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
            }

        for (int row = 0; row < 7; row++) {
            for (int col = 0; col < 7; col++) {
                Tile tile = gameboard.getTile(new Position(row, col));
                if (tile != null) {
                    int tileIndex = getTileIndex(tile);
                    int orientation = tile.get_orientation();
                    boolean hasTreasure = tile._hasTreasure;
                    int treasureIndex = tile.getTreasure();

                    try {
                        // Débogage pour vérifier l'image retournée
                        BufferedImage tileImage = imageStore.getTileImage(tileIndex, orientation, hasTreasure, treasureIndex);
                        System.out.println("Image obtenue pour la tuile (" + row + "," + col + ")");
                        g.drawImage(tileImage, col * tileSize, row * tileSize, tileSize, tileSize, null);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    // Méthode pour obtenir l'index de la tuile (implémentez selon votre logique de création de tuiles)
    private int getTileIndex(Tile tile) {
        // Exemple de logique pour obtenir l'index de la tuile selon son type
        if (tile instanceof AngledTile) {
            return 0; // Par exemple, index 0 pour les tuiles angulaires
        } else if (tile instanceof TShapedTile) {
            return 2; // Par exemple, index 2 pour les tuiles en T
        }
        return 1; // Par défaut pour d'autres types de tuiles
    }

}