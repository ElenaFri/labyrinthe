package labyrinth.model;

import java.util.List;

/**
 * A player: a pawn on the board, and a hand of treasures to find in order.
 */
public final class Player {
    private final int id;
    private final String name;
    private final Position home;
    private final List<Integer> objectives;
    private Position position;
    private int found;

    /**
     * Creates a player, standing on his starting corner with none of his treasures found.
     *
     * @param id         : index of the player, which also gives the colour of the pawn
     * @param name       : name of the player
     * @param home       : starting position, where the player must come back after having found all the treasures
     * @param objectives : the treasures to find, in the order in which they must be found
     */
    public Player(int id, String name, Position home, List<Integer> objectives) {
        this.id = id;
        this.name = name;
        this.home = home;
        this.position = home;
        this.objectives = List.copyOf(objectives);
    }

    /**
     * Gives the index of the player.
     *
     * @return the index of the player
     */
    public int id() {
        return id;
    }

    /**
     * Gives the name of the player.
     *
     * @return the name of the player
     */
    public String name() {
        return name;
    }

    /**
     * Gives the starting corner of the player.
     *
     * @return the starting position
     */
    public Position home() {
        return home;
    }

    /**
     * Gives the position of the pawn.
     *
     * @return the position of the pawn
     */
    public Position position() {
        return position;
    }

    void moveTo(Position position) {
        this.position = position;
    }

    /**
     * Gives the hand of the player.
     *
     * @return all the treasures of the player, in the order in which they must be found
     */
    public List<Integer> objectives() {
        return objectives;
    }

    /**
     * Counts the treasures that the player has found.
     *
     * @return how many of the treasures have already been found
     */
    public int foundCount() {
        return found;
    }

    /**
     * Tells whether the player has found all of his treasures.
     *
     * @return true if all the treasures have been found
     */
    public boolean hasFoundAll() {
        return found == objectives.size();
    }

    /**
     * Gives the treasure that the player is looking for.
     *
     * @return the number of the next treasure to find; it must not be called if all of them have been found
     */
    public int currentObjective() {
        return objectives.get(found);
    }

    void markObjectiveFound() {
        found++;
    }
}
