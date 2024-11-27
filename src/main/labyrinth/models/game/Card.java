package main.labyrinth.models.game;

import java.util.ArrayList;
import java.util.List;

public class Card {
    private Integer _treasure;  // Le trésor associé à cette carte (null si carte "dos")
    private boolean _isFound;   // Si le trésor de cette carte a été trouvé

    /**
     * Constructeur pour créer une carte avec un trésor spécifique.
     * @param treasure le trésor associé à la carte (peut être null pour une carte "dos").
     */
    public Card(Integer treasure) {
        this._treasure = treasure;  // Associe un trésor ou laisse null pour une carte "dos"
        this._isFound = false;
    }

    /**
     * Accéder au trésor associé à la carte.
     * @return l'ID du trésor ou null si la carte est une carte "dos".
     */
    public Integer getTreasure() {
        return _treasure;
    }

    /**
     * Vérifier si le trésor a été trouvé.
     * @return true si le trésor est trouvé, false sinon.
     */
    public boolean checkIfFound() {
        return _isFound;
    }

    /**
     * Marquer le trésor comme trouvé.
     */
    public void setFound() {
        _isFound = true;
    }

    /**
     * Vérifier si la carte est une carte "dos".
     * @return true si la carte n'a pas de trésor, false sinon.
     */
    public boolean isBackCard() {
        return _treasure == null;
    }

    /**
     * Statique : créer un deck avec des trésors de 0 à 23 et une carte "dos".
     * @return une liste de cartes avec des trésors associés.
     */
    public static List<Card> createDeck() {
        List<Card> deck = new ArrayList<>();

        // Créer des cartes avec des trésors de 0 à 23
        for (int i = 0; i < 24; i++) {
            deck.add(new Card(i)); // Carte avec un trésor spécifique
        }

        // Ajouter une carte "dos" (sans trésor)
        deck.add(new Card(null));

        return deck;
    }
}
