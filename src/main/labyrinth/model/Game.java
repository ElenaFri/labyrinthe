package labyrinth.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * The rules of the game, and its state: the board, the four players and whose turn it is.
 * <p>
 * A turn has two phases. First the player may {@linkplain #rotateSpare() turn the spare tile}, then must
 * {@linkplain #push(Direction, int) push it into the board}. Then the player {@linkplain #moveTo(Position) moves
 * the pawn}, one tile at a time, and {@linkplain #endTurn() ends the turn}.
 * <p>
 * The first player who has found all of his treasures and comes back to his starting corner wins.
 * <p>
 * A game is not thread safe: use it from a single thread (the Swing thread, in the application).
 */
public final class Game {
    /**
     * The phases of a turn, then the end of the game.
     */
    public enum Phase {
        /**
         * The current player has to push the spare tile into the board.
         */
        PUSH,
        /**
         * The current player moves the pawn, then ends the turn.
         */
        MOVE,
        /**
         * Somebody has won.
         */
        OVER
    }

    /**
     * A push of the spare tile.
     *
     * @param direction : the direction in which the tiles moved
     * @param index     : the index of the pushed row or column
     */
    public record Push(Direction direction, int index) {
        Push reverse() {
            return new Push(direction.opposite(), index);
        }
    }

    private static final String[] PLAYER_NAMES = {"Alice", "Bob", "Charlie", "Eve"};
    private static final int OBJECTIVES_PER_PLAYER = Board.TREASURE_COUNT / PLAYER_NAMES.length;

    private final Board board;
    private final List<Player> players;
    private final List<GameListener> listeners = new CopyOnWriteArrayList<>();
    private int currentIndex;
    private Phase phase = Phase.PUSH;
    private Player winner;
    private Push lastPush;
    private Position previousPosition;

    /**
     * Starts a new game with a random board and random hands.
     */
    public Game() {
        this(new Random());
    }

    /**
     * Starts a new game.
     *
     * @param random : source of randomness, to get reproducible games
     */
    public Game(Random random) {
        board = new Board(random);

        List<Integer> treasures = new ArrayList<>();
        for (int i = 0; i < Board.TREASURE_COUNT; i++) {
            treasures.add(i);
        }
        Collections.shuffle(treasures, random);

        int last = Board.SIZE - 1;
        List<Player> newPlayers = new ArrayList<>();
        Position[] corners = {new Position(0, 0), new Position(0, last), new Position(last, 0), new Position(last, last)};
        for (int i = 0; i < PLAYER_NAMES.length; i++) {
            List<Integer> hand = treasures.subList(i * OBJECTIVES_PER_PLAYER, (i + 1) * OBJECTIVES_PER_PLAYER);
            newPlayers.add(new Player(i, PLAYER_NAMES[i], corners[i], hand));
        }
        players = List.copyOf(newPlayers);
    }

    /**
     * Registers an observer, which is told about any change of the game.
     *
     * @param listener : will be told about any change
     */
    public void addListener(GameListener listener) {
        listeners.add(listener);
    }

    /**
     * Gives the board.
     *
     * @return the board
     */
    public Board board() {
        return board;
    }

    /**
     * Gives the players, in the order of play.
     *
     * @return the players, in the order of play
     */
    public List<Player> players() {
        return players;
    }

    /**
     * Gives the player whose turn it is.
     *
     * @return the player whose turn it is
     */
    public Player currentPlayer() {
        return players.get(currentIndex);
    }

    /**
     * Gives the current phase of the turn.
     *
     * @return the current phase
     */
    public Phase phase() {
        return phase;
    }

    /**
     * Gives the winner, once the game is over.
     *
     * @return the winner, or nothing while the game is not {@linkplain Phase#OVER over}
     */
    public Optional<Player> winner() {
        return Optional.ofNullable(winner);
    }

    /**
     * Lists the tiles to which the current player can move the pawn.
     *
     * @return the tiles on which the current player may click: the neighbouring tiles to which the pawn can go,
     * and the tile of the pawn itself to end the turn. It is empty unless the phase is {@link Phase#MOVE}.
     */
    public Set<Position> moveTargets() {
        if (phase != Phase.MOVE) {
            return Set.of();
        }
        Position position = currentPlayer().position();
        Set<Position> targets = new HashSet<>(board.connectedNeighbours(position));
        targets.remove(previousPosition);
        targets.add(position);
        return Collections.unmodifiableSet(targets);
    }

    /**
     * Turns the spare tile by a quarter turn, clockwise.
     */
    public void rotateSpare() {
        if (phase == Phase.OVER) {
            return;
        }
        board.spareTile().rotateClockwise();
        fireChanged();
    }

    /**
     * Tells whether the current player can push the spare tile somewhere right now.
     *
     * @param direction : the direction in which the tiles would move
     * @param index     : the index of the row or of the column
     * @return true if the current player can push the spare tile there right now: it must be the right phase,
     * the row or column must be pushable, and the previous push must not be undone
     */
    public boolean canPush(Direction direction, int index) {
        return phase == Phase.PUSH && Board.isPushable(index) && !undoesLastPush(direction, index);
    }

    /**
     * Tells whether a push would put the spare tile back where the previous player has just taken it out, which
     * the rules forbid.
     *
     * @param direction : the direction in which the tiles would move
     * @param index     : the index of the row or of the column
     * @return true if this push is the opposite of the previous one
     */
    public boolean undoesLastPush(Direction direction, int index) {
        return lastPush != null && new Push(direction, index).equals(lastPush.reverse());
    }

    /**
     * Pushes the spare tile into the board. The pawns on the pushed row or column move with their tiles.
     *
     * @param direction : the direction in which the tiles move
     * @param index     : the index of the row or of the column
     * @return false (and nothing happens) if the push is not allowed, see {@link #canPush(Direction, int)}
     */
    public boolean push(Direction direction, int index) {
        if (!canPush(direction, index)) {
            return false;
        }
        board.push(direction, index);
        for (Player player : players) {
            player.moveTo(board.positionAfterPush(player.position(), direction, index));
        }
        lastPush = new Push(direction, index);
        previousPosition = null;
        phase = Phase.MOVE;
        collectTreasureAndCheckVictory();
        fireChanged();
        return true;
    }

    /**
     * Moves the pawn of the current player by one tile. Moving to the tile where the pawn already stands ends
     * the turn.
     *
     * @param target : one of the {@linkplain #moveTargets() targets}
     * @return false (and nothing happens) if the move is not allowed
     */
    public boolean moveTo(Position target) {
        if (!moveTargets().contains(target)) {
            return false;
        }
        Player player = currentPlayer();
        if (target.equals(player.position())) {
            currentIndex = (currentIndex + 1) % players.size();
            phase = Phase.PUSH;
            previousPosition = null;
        } else {
            previousPosition = player.position();
            player.moveTo(target);
            collectTreasureAndCheckVictory();
        }
        fireChanged();
        return true;
    }

    /**
     * Moves the pawn of the current player by one tile, in a direction.
     *
     * @param direction : the direction in which to go
     * @return false (and nothing happens) if the pawn cannot go there
     */
    public boolean step(Direction direction) {
        return phase == Phase.MOVE && moveTo(currentPlayer().position().step(direction));
    }

    /**
     * Ends the turn of the current player, leaving the pawn where it is.
     *
     * @return false (and nothing happens) if the turn cannot end yet, because the spare tile has not been pushed
     */
    public boolean endTurn() {
        return phase == Phase.MOVE && moveTo(currentPlayer().position());
    }

    private void collectTreasureAndCheckVictory() {
        Player player = currentPlayer();
        if (!player.hasFoundAll() && board.positionOfTreasure(player.currentObjective()).equals(Optional.of(player.position()))) {
            player.markObjectiveFound();
            for (GameListener listener : listeners) {
                listener.objectiveFound(player);
            }
        }
        if (player.hasFoundAll() && player.position().equals(player.home())) {
            phase = Phase.OVER;
            winner = player;
        }
    }

    private void fireChanged() {
        for (GameListener listener : listeners) {
            listener.gameChanged();
        }
    }
}
