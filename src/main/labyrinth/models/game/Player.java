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

    // Getters
    public String getName() {
        return _name;
    }

    public Position getCurrentTile() {
        return _currentTile;
    }

    public Card[] getCards() {
        return _cards;
    }


    // Setter de la position actuelle
    public void setCurrentTile(int x, int y) {
        _currentTile = new Position(x, y);
    }

    // Setter des cartes d'objectifs
    public void setCards(Card[] cards) {
        _cards = cards;
    }

    // Getter de l'objectif actuel
    public Card getCurrentObjective() {
        return _cards[_currentObjectiveIndex];  // Retourne l'objectif en cours
    }

    // Méthode pour marquer un objectif comme atteint
    public void completeCurrentObjective() {
        if (_currentObjectiveIndex < _cards.length - 1) {
            _currentObjectiveIndex++;  // Passe à l'objectif suivant
        }
    }


}
