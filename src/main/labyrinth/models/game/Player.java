package main.labyrinth.models.game;

import main.labyrinth.models.game.Card;
import main.labyrinth.models.geometry.Position;

// Implements a Labyrinth player
public class Player {
    private int _id;
    private String _name;
    private Position _currentTile;
    private Card[] _cards;            // Les 6 cartes d'objectifs du joueur
    private int _currentObjectiveIndex; // L'index de l'objectif en cours (0 à 5)

    /**
     * Constructs a new Player with a specified ID and name.
     *
     * @param id : unique identifier for the player
     * @param name : name of the player
     */
    public Player(int id, String name) {
        _id = id;
        _name = name;
        _cards = new Card[6];
        _currentObjectiveIndex = 0; // Le joueur commence avec l'objectif 0
    }

    /**
     * Retrieves the name of the player.
     * @return name of the player
     */
    public String getName() {
        return _name;
    }

    /**
     * Retrieves the index of the player's current objective.
     * @return current objective index of the player, ranging from 0 to 5
     */
    public int get_currentObjectiveIndex() {
        return _currentObjectiveIndex;
    }

    /**
     * Retrieves the current tile position of the player within the labyrinth.
     * @return : current Position object representing the player's tile coordinates on the gameboard
     */
    public Position getCurrentTile() {
        return _currentTile;
    }

    /**
     * Retrieves the player's current set of objective cards.
     * @return an array of Card objects representing the player's objectives
     */
    public Card[] getCards() {
        return _cards;
    }

    /**
     * Sets the current tile position of the player within the labyrinth.
     * @param position :  Position object representing the new tile coordinates on the gameboard
     */
    public void setCurrentTile(Position position) {
        this._currentTile = new Position(position.getX(), position.getY());
    }

    /**
     * Sets the player's current set of objective cards.
     * @param cards : an array of Card objects representing the new set of objective cards for the player
     */
    public void setCards(Card[] cards) {
        this._cards = cards;
    }

    /**
     * Retrieves the player's current objective card.
     * @return : current Card object representing the player's current objective
     */
    public Card getCurrentObjective() {
        return _cards[_currentObjectiveIndex];  // Retourne l'objectif en cours
    }

    /**
     * Advances the player to the next objective in their list of objective cards.
     * If the current objective is not the last one in the player's list, the current
     * objective index is incremented, allowing progress towards the next objective.
     */
    public void completeCurrentObjective() {
        if (_currentObjectiveIndex < _cards.length - 1) {
            this._currentObjectiveIndex++;  // Passe à l'objectif suivant
        }
    }


}
