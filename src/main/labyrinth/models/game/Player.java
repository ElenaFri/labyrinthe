package main.labyrinth.models.game;


import models.game.Card;
import models.geometry.Position;

public class Player {
    private int _id;
    private String _name;
    private Position _currentTile;
    private Card[] _cards;

    public Player(int id, String name) {
        _id = id;
        _name = name;
        _cards = new Card[6];
    }

    public String getName() {
        return _name;
    }

    public Position getCurrentTile() {
        return _currentTile;
    }

    public Card[] getCards() {
        return _cards;
    }

    public void setCurrentTile(int x, int y) {
        _currentTile = new Position(x, y);
    }

    public void setCards(Card[] cards) {
        _cards = cards;
    }
}