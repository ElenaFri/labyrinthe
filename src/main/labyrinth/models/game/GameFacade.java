package main.labyrinth.models.game;

import main.labyrinth.models.geometry.Position;
import main.labyrinth.models.observers.GameFacadeObserver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

// Implements a complex representation of the four players and their hands.
public class GameFacade {
    private List<GameFacadeObserver> gameFacadeObservers = new ArrayList<>();
    private Player[] _players; // Tableau des joueurs
    private int currentPlayerIndex = 0;  // L'indice du joueur en cours

    /**
     * Constructs a new GameFacade instance, initializing the game state by creating four players
     * and dealing cards to them at the start of the game.
     * Initializes four players and assigns them a unique ID and default name ("Player 1", "Player 2", etc.).
     * Each player is represented by an instance of the Player class.
     * Calls the deal method to distribute the initial set of cards among the players for gameplay.
     */
    public GameFacade() {
        _players = new Player[4];

        String[] playerNames = {"Alice", "Bob", "Charlie", "Eve"};

        for (int i = 0; i < 4; i++) {
            _players[i] = new Player(i, playerNames[i]);
        }

        deal(); // Distribuer les cartes au démarrage du jeu
    }

    /**
     * Adds a GameFacadeObserver to the list of observers.
     * This observer will be notified of various game events,
     * such as changes in the current player or player positions.
     * @param observer : the GameFacadeObserver to be added. It must implement
     *                 the required methods to respond to game state changes.
     */
    public void addGameFacadeObserver(GameFacadeObserver observer) {
        gameFacadeObservers.add(observer);
    }

    /**
     * Advances the current player's objective to the next in the sequence.
     * This method marks the current objective of the active player as completed
     * and transitions them to their next objective, updating their current objective index.
     * Observers are notified about the change in the player's objective state, providing
     * them with the updated objective index.
     */
    public void playerNextObjective() {
        // Récupère le joueur actuel
        Player currentPlayer = getCurrentPlayer();

        // Marque l'objectif actuel comme atteint et passe à l'objectif suivant
        currentPlayer.completeCurrentObjective();

        // Notifie les observateurs du changement d'objectif
        notifyPlayerObjectiveChange(currentPlayer.get_currentObjectiveIndex());
    }
    // Renvoie un tableau de positions des joueurs
    public Position[] getPlayersPositions() {
        Position[] positions = new Position[_players.length];
        for (int i = 0; i < _players.length; i++) {
            positions[i] = _players[i].getCurrentTile(); // Récupère la position actuelle du joueur
        }
        return positions;
    }

    /**
     * Moves the current player to a new position on the game board.
     * This method updates the player's current position to the specified new position
     * and notifies all observers of this change.
     * @param newPosition : new Position object representing the coordinates
     *                    on the game board where the current player will be moved
     */
    public void movePlayer(Position newPosition, Player player) {
      //  Player currentPlayer = _players[currentPlayerIndex];
        player.setCurrentTile(newPosition);  // Met à jour la position du joueur actuel

        // Notifie les observateurs de la nouvelle position du joueur
        notifyPlayerPositionChange(newPosition);
    }

    /**
     * Notifies all registered observers about the change in the player's position.
     * This method iterates through the list of GameFacadeObserver instances,
     * calling their UpdatePlayerPositionChanged method with the new position.
     * @param newPosition : new Position object that represents the updated
     *                    coordinates of the player on the game board
     */
    public void notifyPlayerPositionChange(Position newPosition) {
        for (GameFacadeObserver observer : gameFacadeObservers) {
            observer.UpdatePlayerPositionChanged(newPosition);
        }
    }

    /**
     * Notifies all registered observers about a change in the player's objective.
     * This method iterates through the list of GameFacadeObserver instances and calls
     * their UpdatePlayerObjectiveChanged method, passing the updated objective.
     * @param objective : new objective index for the player, indicating which objective
     *                  has been changed or advanced to. This integer represents the current
     *                  state or progress of the player's objectives within the game.
     */
    public void notifyPlayerObjectiveChange(int objective) {
        for (GameFacadeObserver observer : gameFacadeObservers) {
            observer.UpdatePlayerObjectiveChanged(objective);
        }
    }

    /**
     * Advances the game to the next player in the sequence.
     * This method updates the current player index to point to the next player
     * in the players' array, allowing the game to cycle through multiple players.
     * If the current player is the last in the array, the index wraps around to the first player.
     * It also triggers a notification to observers that the current player has changed.
     */
    public void nextPlayer() {
        // Terminer le tour actuel, puis passer au joueur suivant
        this.currentPlayerIndex = (currentPlayerIndex + 1) % 4; // Boucle sur les joueurs
        notifyCurrentPlayerChange();
    }

    /**
     * Notifies all registered observers that the current player has changed.
     * This method identifies the current player based on the current player index
     * and informs each observer in the list of gameFacadeObservers about the change.
     * The observers are notified by calling their UpdateCurrentPlayerChanged method
     * with the current player as an argument.
     */
    private void notifyCurrentPlayerChange() {
        Player currentPlayer = _players[currentPlayerIndex];
        for (GameFacadeObserver observer : gameFacadeObservers) {
            observer.UpdateCurrentPlayerChanged(currentPlayer);
        }
    }

    /**
     * Retrieves the current player in the sequence of players.
     * @return the Player object that represents the current player in the game.
     *         The current player is determined by the currentPlayerIndex.
     */
    public Player getCurrentPlayer() {
        return _players[currentPlayerIndex];
    }

    /**
     * Retrieves the array of players currently participating in the game.
     * @return an array of Player objects that represents the players in the game.
     *         Each player in the array is an instance of the Player class,
     *         participating in the current game session.
     */
    public Player[] get_players() {
        return _players;
    }
    // Méthode pour vérifier si la partie est terminée
    public boolean isGameOver() {
        for (Player player : _players) {
            Position initialPosition;

            // Définir les positions initiales en fonction du joueur
            switch (player.getName()) { // Identifier le joueur
                case "Joueur 1":
                    initialPosition = new Position(0, 0);
                    break;
                case "Joueur 2":
                    initialPosition = new Position(0, 6);
                    break;
                case "Joueur 3":
                    initialPosition = new Position(6, 0);
                    break;
                case "Joueur 4":
                    initialPosition = new Position(6, 6);
                    break;
                default:
                    throw new IllegalArgumentException("Joueur inconnu : " + player.getName());
            }

            // Vérifier si le joueur a complété ses objectifs et est retourné à sa position initiale
            if (player.hasCompletedAllObjectives() && player.getCurrentTile().equals(initialPosition)) {
                return true; // La partie est terminée
            }
        }
        return false; // La partie continue
    }




    /**
     * Distributes cards to players at the beginning of the game. This method shuffles
     * a deck of cards, removes a special "back" card to be kept aside, and allocates
     * a specified number of cards to each player. The method ensures that each player
     * receives the appropriate number of cards and raises an exception if the deck
     * is not empty after distribution.
     * Behavior:
     * - Creates a deck of cards using the Card class.
     * - Removes the "back" card from the deck.
     * - Shuffles the remaining deck.
     * - Deals a fixed number of cards to each player.
     * Exception:
     * - Throws IllegalStateException if there are leftover cards in the deck after dealing.
     */
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

    /**
     * Retrieves the player at the specified index from the list of players.
     * @param n : index of the player to retrieve; must be a non-negative integer
     *          and less than the total number of players
     * @return Player object at the specified index
     * @throws IllegalArgumentException if the index is out of the valid range
     */
    public Player getPlayer(int n) {
        if (n < 0 || n >= _players.length) {
            throw new IllegalArgumentException("Invalid player index: " + n);
        }
        return _players[n];
    }
    // Setter pour mettre à jour l'ordre des joueurs
    public void setPlayers(Player[] players) {
        this._players = players;
    }
}
