package main.labyrinth.models.game;

import java.util.ArrayList;
import java.util.List;

public class Card {
    private final Integer treasure;  // Trésor associé à la carte (null si "dos")
    private boolean isFound;         // Indique si le trésor a été trouvé

    /**
     * Constructeur pour une carte avec un trésor.
     * @param treasure trésor associé à la carte (null pour une carte "dos").
     */
    public Card(Integer treasure) {
        this.treasure = treasure;
        this.isFound = false;
    }

    /**
     * Retourne le trésor associé à cette carte.
     * @return l'ID du trésor ou null si c'est une carte "dos".
     */
    public Integer getTreasure() {
        return treasure;
    }

    /**
     * Vérifie si le trésor a été trouvé.
     * @return true si le trésor est trouvé, false sinon.
     */
    public boolean isFound() {
        return isFound;
    }

    /**
     * Marque le trésor comme trouvé.
     */
    public void setFound(boolean found) {
        this.isFound = found;
    }

    /**
     * Vérifie si la carte est une carte "dos".
     * @return true si la carte n'a pas de trésor, false sinon.
     */
    public boolean isBackCard() {
        return treasure == 24;
    }

    /**
     * Retourne le nom de la carte (utile pour l'affichage ou le débogage).
     * @return "Back" si c'est une carte "dos", sinon "Treasure X" où X est l'ID du trésor.
     */
    public String getName() {
        return isBackCard() ? "Back" : "Treasure " + treasure;
    }

    /**
     * Crée un deck de cartes avec des trésors numérotés de 0 à 23 et une carte "dos".
     * @return une liste de cartes.
     */
    public static List<Card> createDeck() {
        List<Card> deck = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            deck.add(new Card(i));
        }
        deck.add(new Card(24)); // Ajout de la carte dos
        System.out.println("Deck créé : " + deck);
        return deck;
    }


}
