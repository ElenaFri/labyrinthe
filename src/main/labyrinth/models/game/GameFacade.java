package main.labyrinth.models.game;

import main.labyrinth.models.geometry.Position;
import main.labyrinth.models.observers.GameFacadeObserver;

import java.util.ArrayList;
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


    // Méthode pour notifier les observateurs de la position du joueur
    public void notifyPlayerPositionChange(Position newPosition) {
        for (GameFacadeObserver observer : gameFacadeObservers) {
            observer.onPlayerPositionChanged(newPosition);
        }
    }

    // Méthode pour notifier les observateurs de l'objectif du joueur
    public void notifyPlayerObjectiveChange(int objective) {
        for (GameFacadeObserver observer : gameFacadeObservers) {
            observer.onPlayerObjectiveChanged(objective);
        }
    }
    // Méthode pour passer au joueur suivant
    public void nextPlayer() {
        // Terminer le tour actuel, puis passer au joueur suivant
        currentPlayerIndex = (currentPlayerIndex + 1) % _players.length; // Boucle sur les joueurs
        notifyCurrentPlayerChange();
    }
    // Méthode pour notifier le changement de joueur
    private void notifyCurrentPlayerChange() {
        Player currentPlayer = _players[currentPlayerIndex];
        for (GameFacadeObserver observer : gameFacadeObservers) {
            observer.onCurrentPlayerChanged(currentPlayer);
        }
    }
    // Retourne le joueur en cours
    public Player getCurrentPlayer() {
        return _players[currentPlayerIndex];
    }


    // Méthode pour générer et distribuer les cartes
    public void deal() {
        List<Card> deck = Card.createDeck(); // Créer toutes les cartes (24 trésors + 1 carte dos)
        Collections.shuffle(deck); // Mélanger les cartes de manière aléatoire

        int cardsPerPlayer = 24 / _players.length; // Chaque joueur reçoit 6 cartes

        for (int i = 0; i < _players.length; i++) {
            Card[] playerCards = new Card[cardsPerPlayer];
            for (int j = 0; j < cardsPerPlayer; j++) {
                playerCards[j] = deck.remove(0); // Attribuer la carte et la retirer du deck
            }
            _players[i].setCards(playerCards); // Attribuer les cartes au joueur
        }

        // La carte restante est la carte "dos"
        assert deck.size() == 1 && deck.get(0).isBackCard();
    }

    // Méthode pour obtenir un joueur par son index
    public Player getPlayer(int n) {
        if (n < 0 || n >= _players.length) {
            throw new IllegalArgumentException("Invalid player index: " + n);
        }
        return _players[n];
    }
}
