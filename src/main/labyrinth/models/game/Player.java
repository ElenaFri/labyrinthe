package main.labyrinth.models.game;

import main.labyrinth.models.game.Card;
import main.labyrinth.models.geometry.Position;

public class Player {
    private int _id;
    private String _name;
    private Position _currentTile;
    private Card[] _cards;            // Les 6 cartes d'objectifs du joueur
    private int _currentObjectiveIndex; // L'index de l'objectif en cours (0 à 5)

    public Player(int id, String name) {
        _id = id;
        _name = name;
        _cards = new Card[6];
        _currentObjectiveIndex = 0; // Le joueur commence avec l'objectif 0
    }
    // Méthode pour setter la position actuelle


    // Getters
    public String getName() {
        return _name;
    }

    public int get_currentObjectiveIndex() {
        return _currentObjectiveIndex;
    }

    public Position getCurrentTile() {
        return _currentTile;
    }

    public Card[] getCards() {
        return _cards;
    }


    // Setter de la position actuelle
    public void setCurrentTile(Position position) {
        this._currentTile = new Position(position.getX(), position.getY());
    }

    // Setter des cartes d'objectifs
    public void setCards(Card[] cards) {
        this._cards = cards;
    }

    // Getter de l'objectif actuel
    public Card getCurrentObjective() {
        return _cards[_currentObjectiveIndex];  // Retourne l'objectif en cours
    }

    // Méthode pour marquer un objectif comme atteint
    public void completeCurrentObjective() {
        if (_currentObjectiveIndex < _cards.length - 1) {
            this._currentObjectiveIndex++;  // Passe à l'objectif suivant
        }
    }


}
