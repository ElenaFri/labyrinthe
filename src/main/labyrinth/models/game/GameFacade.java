package game;

import models.game.Card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameFacade {
    private Player[] _players; // Tableau des joueurs

    public GameFacade() {
        _players = new Player[4];

        for (int i = 0; i < 4; i++) {
            _players[i] = new Player(i, "Player " + (i + 1));
        }
        deal();
    }

    // Méthode pour générer et distribuer les cartes
    public void deal() {
        List<Card> cards = generateCards(); // Créer les cartes (avec trésor et carte dos)
        Collections.shuffle(cards); // Mélanger les cartes de manière aléatoire

        int cardsPerPlayer = cards.size() / _players.length;

        // Distribution des cartes
        int index = 0;
        for (Player player : _players) {
            Card[] playerCards = new Card[cardsPerPlayer];
            for (int j = 0; j < cardsPerPlayer; j++) {
                playerCards[j] = cards.remove(index); // Retirer la carte de la liste après l'avoir attribuée
            }
            player.setCards(playerCards); // Attribuer les cartes au joueur
        }
    }


    // Méthode pour générer un jeu de cartes avec un trésor spécifique et une carte dos
    private List<Card> generateCards() {
        List<Card> cards = new ArrayList<>();
        List<Integer> treasurePool = Card.createTreasurePool();  // Créer le pool de trésors

        // Créer 24 cartes avec un trésor spécifique attribué
        for (int i = 0; i < 24; i++) {
            Card card = new Card();
            card.generateTreasure(treasurePool);  // Générer un trésor spécifique à partir du pool
            cards.add(card);
        }

        // Ajouter la carte du dos (sans trésor)
        Card backCard = new Card();  // La carte du dos n'a pas de trésor
        cards.add(backCard);

        return cards;
    }

    // Méthode pour obtenir un joueur par son index
    public Player getPlayer(int n) {
        return _players[n];
    }
}
