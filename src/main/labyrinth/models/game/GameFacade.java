package main.labyrinth.models.game;

import main.labyrinth.models.geometry.Position;
import main.labyrinth.models.observers.GameFacadeObserver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GameFacade {
    private List<GameFacadeObserver> gameFacadeObservers = new ArrayList<>();
    private Player[] _players; // Tableau des joueurs
    private int currentPlayerIndex = 0;  // L'indice du joueur en cours

    public GameFacade() {
        _players = new Player[4];

        for (int i = 0; i < 4; i++) {
            _players[i] = new Player(i, "Player " + (i + 1));
        }

        deal(); // Distribuer les cartes au démarrage du jeu
    }
    // Méthode pour ajouter des observateurs
    public void addGameFacadeObserver(GameFacadeObserver observer) {
        gameFacadeObservers.add(observer);
    }

    // Méthode pour mettre à jour l'objectif du joueur
    public void PlayernextObjective() {
        // Récupère le joueur actuel
        Player currentPlayer = getCurrentPlayer();

        // Marque l'objectif actuel comme atteint et passe à l'objectif suivant
        currentPlayer.completeCurrentObjective();

        // Notifie les observateurs du changement d'objectif
        notifyPlayerObjectiveChange(currentPlayer.get_currentObjectiveIndex());
    }


    // Méthode pour déplacer le joueur
    public void movePlayer(Position newPosition) {
        Player currentPlayer = _players[currentPlayerIndex];
        currentPlayer.setCurrentTile(newPosition);  // Met à jour la position du joueur actuel

        // Notifie les observateurs de la nouvelle position du joueur
        notifyPlayerPositionChange(newPosition);
    }

    // Méthode pour notifier les observateurs de la position du joueur
    public void notifyPlayerPositionChange(Position newPosition) {
        for (GameFacadeObserver observer : gameFacadeObservers) {
            observer.UpdatePlayerPositionChanged(newPosition);
        }
    }

    // Méthode pour notifier les observateurs de l'objectif du joueur
    public void notifyPlayerObjectiveChange(int objective) {
        for (GameFacadeObserver observer : gameFacadeObservers) {
            observer.UpdatePlayerObjectiveChanged(objective);
        }
    }
    // Méthode pour passer au joueur suivant
    public void nextPlayer() {
        // Terminer le tour actuel, puis passer au joueur suivant
        this.currentPlayerIndex = (currentPlayerIndex + 1) % _players.length; // Boucle sur les joueurs
        notifyCurrentPlayerChange();
    }
    // Méthode pour notifier le changement de joueur
    private void notifyCurrentPlayerChange() {
        Player currentPlayer = _players[currentPlayerIndex];
        for (GameFacadeObserver observer : gameFacadeObservers) {
            observer.UpdateCurrentPlayerChanged(currentPlayer);
        }
    }
    // Retourne le joueur en cours
    public Player getCurrentPlayer() {
        return _players[currentPlayerIndex];
    }

    public Player[] get_players() {
        return _players;
    }

    // Méthode pour générer et distribuer les cartes
    public void deal() {
        List<Card> deck = Card.createDeck(); // Créer le deck
        Card backCard = deck.remove(deck.size() - 1); // Retirer la carte dos
        Collections.shuffle(deck); // Mélanger les autres cartes

        int cardsPerPlayer = 6; // Chaque joueur reçoit 6 cartes

        for (int i = 0; i < 4; i++) {
            Card[] playerCards = new Card[cardsPerPlayer];
            for (int j = 0; j < cardsPerPlayer; j++) {
                playerCards[j] = deck.remove(0); // Attribuer la carte et la retirer du deck
            }
            _players[i].setCards(playerCards); // Attribuer les cartes au joueur
        }

        // Vérification de la carte restante
        if (!deck.isEmpty()) {
            throw new IllegalStateException("Le deck devrait être vide après la distribution.");
        }

        System.out.println("Carte dos conservée : " + backCard);
    }




    // Méthode pour obtenir un joueur par son index
    public Player getPlayer(int n) {
        if (n < 0 || n >= _players.length) {
            throw new IllegalArgumentException("Invalid player index: " + n);
        }
        return _players[n];
    }
}
