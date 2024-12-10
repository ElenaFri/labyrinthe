package main.labyrinth.models.tiles;


import main.labyrinth.models.geometry.Sides;
import main.labyrinth.models.tiles.AngledTile;
import main.labyrinth.models.tiles.StraightTile;
import main.labyrinth.models.tiles.TShapedTile;

public class Main {
    public static void main(String[] args) {

        // Tester AngledTile
        AngledTile angledTile = new AngledTile();
        System.out.println("=== AngledTile ===");
        System.out.println("Orientation initiale : " + angledTile.get_orientation());
        System.out.println("Côtés ouverts avant modification : " + angledTile.getOpenSides());

        angledTile.setOrientation(3);
        angledTile.setOpenSides();
        System.out.println("Orientation après modification : " + angledTile.get_orientation());
        System.out.println("Côtés ouverts après modification : " + angledTile.getOpenSides());

        // Tester StraightTile
        StraightTile straightTile = new StraightTile();
        System.out.println("\n=== StraightTile ===");
        System.out.println("Orientation initiale : " + straightTile.get_orientation());
        System.out.println("Côtés ouverts avant modification : " + straightTile.getOpenSides());

        straightTile.setOrientation(2);
        straightTile.setOpenSides();
        System.out.println("Orientation après modification : " + straightTile.get_orientation());
        System.out.println("Côtés ouverts après modification : " + straightTile.getOpenSides());

        // Tester TShapedTile
        TShapedTile tShapedTile = new TShapedTile();
        System.out.println("\n=== TShapedTile ===");
        System.out.println("Orientation initiale : " + tShapedTile.get_orientation());
        System.out.println("Côtés ouverts avant modification : " + tShapedTile.getOpenSides());

        tShapedTile.setOrientation(2);
        tShapedTile.setOpenSides();
        System.out.println("Orientation après modification : " + tShapedTile.get_orientation());
        System.out.println("Côtés ouverts après modification : " + tShapedTile.getOpenSides());
    }
}

