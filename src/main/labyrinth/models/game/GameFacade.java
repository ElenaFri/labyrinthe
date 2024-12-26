package labyrinth.models.game;

import labyrinth.models.geometry.Position;
import labyrinth.models.observers.GameFacadeObserver;

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

    /**
     * Retrieves the current positions of all players.
     * This method iterates through the list of players and collects their current
     * positions as an array of Position objects. Each position corresponds
     * to the tile on which a player is currently located.
     * @return an array of Position objects representing the current positions of all players in the game
     */
    public Position[] getPlayersPositions() {
        Position[] positions = new Position[_players.length];
        for (int i = 0; i < _players.length; i++) {
            positions[i] = _players[i].getCurrentTile(); // Récupère la position actuelle du joueur
        }
        return positions;
    }

    /**
     * Retrieves the index of the current player in the game.
     * The current player index represents the position of the active player in the array of players.
     * @return an integer value representing the index of the current player. The index is zero-based and corresponds to the position of the current player in the players' list.
     */
    public int getCurrentPlayerIndex(){
        return this.currentPlayerIndex;
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
     * Notifies all registered observers about changes in the positions of players.
     * This method iterates through the list of `GameFacadeObserver` instances and
     * calls their `UpdatePlayerPositionChanged` method, passing the updated positions.
     * @param positions : an array of `Position` objects representing the updated coordinates of all players on the game board.
     */
    public void notifyPlayerPositionChange(Position[] positions) {
        for (GameFacadeObserver observer : gameFacadeObservers) {
            observer.UpdatePlayerPositionChanged(positions);
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
        // Incrémente l'indice du joueur actuel
        this.currentPlayerIndex++;

        // Si on dépasse l'index du dernier joueur, on revient au premier
        if (this.currentPlayerIndex >= 4) {
            ////////////////////////////////////////////////////////////////
           // getCurrentPlayer().setLastPosition(null);
            this.currentPlayerIndex = 0;
        }

        // Notifie que le joueur courant a changé
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
        for (int i = 0; i < _players.length; i++) {
            Player player = _players[i];
            Position initialPosition = getInitialPositionForIndex(i);

            if (player.hasCompletedAllObjectives() && player.getCurrentTile().equals(initialPosition)) {
                return true; // La partie est terminée
            }
        }
        return false; // La partie continue
    }

    /**
     * Determines the winner of the game by checking if any player has completed all objectives
     * and returned to their initial position on the gameboard.
     * This method iterates through the players in the game, checking the following conditions:
     *  - The player must have completed all their objectives (as determined by the `hasCompletedAllObjectives` method).
     *  - The player must currently be located at their initial starting position.
     * If multiple players meet the criteria (which is unlikely in a properly implemented game),
     * only the first one found in the iteration order is returned as the winner.
     * @return Player object representing the winner if a player meets the criteria; otherwise, returns null if no player has completed all objectives
     *          and returned to their starting position.
     */
    public Player getWinner() {
        for (int i = 0; i < _players.length; i++) {
            Player player = _players[i];
            Position initialPosition = getInitialPositionForIndex(i);

            if (player.hasCompletedAllObjectives() && player.getCurrentTile().equals(initialPosition)) {
                return player;
            }
        }
        return null;
    }

    /**
     * Determines the initial position of a player based on their index.
     * This method assigns a unique starting position on the game board
     * to each player, as identified by their index (0 to 3). Throws an exception
     * if the index is outside the expected range.
     * @param index the player index, ranging from 0 to 3, to determine the initial position.
     *              - Index 0: top-left position (0, 0).
     *              - Index 1: top-right position (0, 6).
     *              - Index 2: bottom-left position (6, 0).
     *              - Index 3: bottom-right position (6, 6).
     *
     * @return Position object representing the player's initial position on the board.
     * @throws IllegalArgumentException if the provided index is not within the range of 0 to 3.
     */
    private Position getInitialPositionForIndex(int index) {
        switch (index) {
            case 0: return new Position(0, 0);
            case 1: return new Position(0, 6);
            case 2: return new Position(6, 0);
            case 3: return new Position(6, 6);
            default:
                throw new IllegalArgumentException("Index de joueur inconnu : " + index);
        }
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

        for (int i = 0; i < 4; i++) {
            //int cardsToDeal = (i == 0) ? 1 : 6; // Premier joueur reçoit 1 carte, les autres 6
            int cardsToDeal = 6;
            Card[] playerCards = new Card[cardsToDeal];
            for (int j = 0; j < cardsToDeal; j++) {
                if (!deck.isEmpty()) {
                    playerCards[j] = deck.remove(0); // Attribuer la carte et la retirer du deck
                } else {
                    throw new IllegalStateException("Le deck est épuisé avant la distribution complète.");
                }
            }
            _players[i].setCards(playerCards); // Attribuer les cartes au joueur
        }

        // Vérification des cartes restantes
        if (!deck.isEmpty()) {
            System.out.println("Le deck a " + deck.size() + " cartes restantes."); // Log au lieu de lancer une exception
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

    /**
     * Sets the array of players for the game.
     * This method initializes the players participating in the game.
     * @param players : an array of Player objects representing the players in the game. Each element in the array is an instance of the Player class.
     */
    public void setPlayers(Player[] players) {
        this._players = players;
    }
}
