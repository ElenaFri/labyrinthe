package labyrinth.models.game;

import java.util.ArrayList;
import java.util.List;

// Implements an objective, or "treasure" card.
public class Card {
    private final Integer treasure;  // Trésor associé à la carte (null si "dos")
    private boolean isFound;         // Indique si le trésor a été trouvé

    /**
     * Constructs a new Card with a specified treasure value.
     *
     * @param treasure : integer value representing the treasure associated with this card.
     *                 It uniquely identifies the treasure, or indicates a special card state
     */
    public Card(Integer treasure) {

        this.treasure = treasure;
        this.isFound = false;
    }

    /**
     * Creates a new deck of cards for the game, consisting of 24 numbered treasure cards
     * and a special "back" card with the number 24.
     *
     * @return a list of Card objects representing the complete deck for the game, including
     * numbered treasure cards and one "back" card
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

    /**
     * Retrieves the treasure associated with this card.
     *
     * @return an Integer representing the treasure, or null if the card is a "back" card
     */
    public Integer getTreasure() {
        return treasure;
    }

    /**
     * Checks if the treasure associated with this card has been found.
     *
     * @return true if the treasure has been found, false otherwise
     */
    public boolean isFound() {
        return isFound;
    }

    /**
     * Sets the found status of the card's treasure. This status indicates whether the treasure
     * associated with this card has been located.
     *
     * @param found a boolean value representing the found status of the card's treasure.
     *              If true, the treasure is marked as found; if false, it is marked as not found
     */
    public void setFound(boolean found) {
        this.isFound = found;
    }

    /**
     * Determines whether the card is a "back" card.
     *
     * @return true if the card's treasure value is 24, indicating it is a "back" card; false otherwise
     */
    public boolean isBackCard() {
        return treasure == 24;
    }

    /**
     * Retrieves the "name", or status of the card, indicating whether it is a "back" card or a specific treasure card.
     * If the card is a "back" card, it returns "Back"; otherwise, it returns "Treasure" followed by the treasure value.
     *
     * @return status of the card as a String, either "Back" or "Treasure" followed by the treasure value
     */
    public String getName() {
        return isBackCard() ? "Back" : "Treasure " + treasure;
    }
}
