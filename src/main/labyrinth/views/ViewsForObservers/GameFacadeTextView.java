package main.labyrinth.views.ViewsForObservers;

import main.labyrinth.models.game.GameFacade;
import main.labyrinth.models.game.Player;
import main.labyrinth.models.geometry.Position;
import main.labyrinth.models.observers.GameFacadeObserver;

public class GameFacadeTextView implements GameFacadeObserver {
    private final GameFacade gameFacade; // Façade du jeu

    /**
     * Constructeur de la vue textuelle pour la façade du jeu.
     * @param gameFacade la façade du jeu à afficher.
     */
    public GameFacadeTextView(GameFacade gameFacade) {
        this.gameFacade = gameFacade;

    }

    /**
     * Méthode principale pour afficher l'état global du jeu.
     */
    public void displayGameState() {
        // Afficher les informations des joueurs
        System.out.println("\n=== État du jeu ===");
        System.out.println("\nJoueurs :");
        displayPlayers();
    }

    /**
     * Affiche les informations des joueurs.
     */
    private void displayPlayers() {
        Player[] players = gameFacade.get_players(); // Utilisation de la méthode get_players
        for (Player player : players) {
            // Affichage du nom du joueur, de sa position et de l'objectif actuel sous forme de numéro
            System.out.println("- " + player.getName() +
                    " : position " + player.getCurrentTile() +
                    ", objectif actuel : " + player.get_currentObjectiveIndex());
        }
    }

    // Implémentation de l'interface GameFacadeObserver
    @Override
    public void UpdateCurrentPlayerChanged(Player currentPlayer) {
        System.out.println("\nLe joueur actuel est maintenant : " + currentPlayer.getName());
    }

    @Override
    public void UpdatePlayerPositionChanged(Position newPosition) {
        System.out.println("\nLa position a été mise à jour : " + newPosition);
    }

    @Override
    public void UpdatePlayerObjectiveChanged(int objective) {
        System.out.println("\nL'objectif du joueur a changé, nouvel objectif : " + objective);
    }
}
