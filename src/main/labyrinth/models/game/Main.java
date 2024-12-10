package main.labyrinth.models.game;

import main.labyrinth.models.geometry.Position;
import main.labyrinth.models.geometry.Sides;
import main.labyrinth.models.tiles.AngledTile;
import main.labyrinth.models.tiles.StraightTile;
import main.labyrinth.models.tiles.Tile;

public class Main {
    public static void main(String[] args) {
        // Initialiser le plateau de jeu
        Gameboard gameboard = new Gameboard();

Position position=new Position(2,0);
        Tile tuille= gameboard.getTile(position);
        System.out.println("la tuile  a une orienttaion"+tuille.get_orientation()+"  et type :"+tuille.getType());
        Sides sides=tuille.getOpenSides();
        System.out.println(sides);
        Position positionn=new Position(2,1);
        Tile voisie= gameboard.getTile(positionn);
        System.out.println("la tuile  a une orienttaion"+tuille.get_orientation()+"  et type :"+tuille.getType());

        int oppositeSide = 1; // haut
        boolean isOpen = gameboard.isOppositeSideOpen(positionn, oppositeSide);


        System.out.println("Le côté opposé est " + (isOpen ? "ouvert" : "fermé"));
    }
}
