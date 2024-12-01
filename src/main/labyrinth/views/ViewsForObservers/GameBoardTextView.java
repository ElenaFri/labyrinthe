package main.labyrinth.views.ViewsForObservers;

import main.labyrinth.models.geometry.Position;
import main.labyrinth.models.game.Gameboard;
import main.labyrinth.models.observers.GameBoardObserver;
import main.labyrinth.models.tiles.Tile;

public class GameBoardTextView implements GameBoardObserver {
    private final Gameboard gameboard; // Modèle observé

    /**
     * Constructeur de la vue textuelle du plateau.
     * @param gameboard le plateau de jeu à observer.
     */
    public GameBoardTextView(Gameboard gameboard) {
        this.gameboard = gameboard;
    }

    /**
     * Méthode appelée lorsque le modèle est mis à jour.
     * @param updatedGameboard le plateau de jeu mis à jour.
     */
    @Override
    public void update(Gameboard updatedGameboard) {
        displayBoard();
    }

    /**
     * Méthode pour afficher le plateau de jeu en mode texte.
     */
    public void displayBoard() {
        System.out.println("\n=== Plateau de jeu ===");
        for (int row = 0; row < 7; row++) {
            for (int col = 0; col < 7; col++) {
                // Récupérer la tuile à la position (row, col)
                Tile tile = gameboard.getTile(new Position(row, col));

                // Afficher des informations sur la tuile
                System.out.print(formatTile(tile));
            }
            System.out.println(); // Saut de ligne après chaque rangée
        }

        // Afficher la tuile libre
        System.out.println("\n=== Tuile Libre ===");
        Tile freeTile = gameboard.getFreeTile();
        System.out.println(formatTile(freeTile));
    }

    /**
     * Formate une tuile pour l'affichage en texte.
     * @param tile la tuile à formater.
     * @return une chaîne représentant la tuile.
     */
    private String formatTile(Tile tile) {
        if (tile == null) {
            return "[   ] "; // Case vide si aucune tuile
        }

        StringBuilder sb = new StringBuilder();
        sb.append("[T").append(tile.getType());          // Type de tuile
        sb.append(" O").append(tile.get_orientation());  // Orientation de la tuile
        if (tile._hasTreasure) {                       // Vérification de la présence d'un trésor
            sb.append(" *");
        }
        sb.append("] ");
        return sb.toString();
    }
}
