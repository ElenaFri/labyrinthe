package labyrinth.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameTest {
    private static Game newGame() {
        return new Game(new Random(7));
    }

    /**
     * Turns two neighbouring tiles so that one can go from one to the other.
     */
    private static void connect(Board board, Position from, Direction direction) {
        Tile a = board.tileAt(from);
        Tile b = board.tileAt(from.step(direction));
        while (!a.isOpen(direction)) {
            a.rotateClockwise();
        }
        while (!b.isOpen(direction.opposite())) {
            b.rotateClockwise();
        }
    }

    @Test
    void startsWithFourPlayersInTheirCornersAndSixTreasuresEach() {
        Game game = newGame();
        assertEquals(4, game.players().size());
        assertEquals(Game.Phase.PUSH, game.phase());
        assertEquals("Alice", game.currentPlayer().name());

        Set<Integer> treasures = new HashSet<>();
        Set<Position> corners = new HashSet<>();
        for (Player player : game.players()) {
            assertEquals(6, player.objectives().size());
            treasures.addAll(player.objectives());
            corners.add(player.position());
            assertEquals(player.home(), player.position());
            assertEquals(0, player.foundCount());
        }
        assertEquals(24, treasures.size());
        assertEquals(4, corners.size());
    }

    @Test
    void aTurnStartsWithAPushThenMovesThenEnds() {
        Game game = newGame();
        assertTrue(game.moveTargets().isEmpty(), "nothing to move to before the push");
        assertFalse(game.moveTo(new Position(0, 0)));

        assertTrue(game.push(Direction.RIGHT, 1));
        assertEquals(Game.Phase.MOVE, game.phase());
        assertFalse(game.push(Direction.RIGHT, 3), "only one push per turn");
        assertTrue(game.moveTargets().contains(game.currentPlayer().position()));

        assertTrue(game.moveTo(game.currentPlayer().position()));
        assertEquals(Game.Phase.PUSH, game.phase());
        assertEquals("Bob", game.currentPlayer().name());
    }

    @Test
    void theTurnsGoRoundTheTable() {
        Game game = newGame();
        for (String expected : List.of("Alice", "Bob", "Charlie", "Eve", "Alice")) {
            assertEquals(expected, game.currentPlayer().name());
            game.push(Direction.RIGHT, 1);
            game.push(Direction.LEFT, 1);
            game.moveTo(game.currentPlayer().position());
        }
    }

    @Test
    void fixedLinesCannotBePushed() {
        Game game = newGame();
        assertFalse(game.canPush(Direction.RIGHT, 0));
        assertFalse(game.push(Direction.DOWN, 2));
        assertFalse(game.push(Direction.UP, 6));
        assertEquals(Game.Phase.PUSH, game.phase());
    }

    @Test
    void thePreviousPushCannotBeUndone() {
        Game game = newGame();
        game.push(Direction.RIGHT, 3);
        game.moveTo(game.currentPlayer().position());

        assertFalse(game.canPush(Direction.LEFT, 3));
        assertFalse(game.push(Direction.LEFT, 3));
        assertTrue(game.canPush(Direction.RIGHT, 3));
        assertTrue(game.canPush(Direction.LEFT, 1));
        assertTrue(game.canPush(Direction.UP, 3));
    }

    @Test
    void pawnsMoveWithTheirRowAndComeBackOnTheOtherSide() {
        Game game = newGame();
        Player bob = game.players().get(1);
        bob.moveTo(new Position(3, 6));
        Player eve = game.players().get(3);
        eve.moveTo(new Position(3, 2));
        Player charlie = game.players().get(2);
        charlie.moveTo(new Position(4, 2));

        game.push(Direction.RIGHT, 3);

        assertEquals(new Position(3, 0), bob.position());
        assertEquals(new Position(3, 3), eve.position());
        assertEquals(new Position(4, 2), charlie.position());
    }

    @Test
    void canOnlyMoveToConnectedNeighboursOrStay() {
        Game game = newGame();
        game.push(Direction.RIGHT, 1);
        Position position = game.currentPlayer().position();

        Set<Position> expected = new HashSet<>(game.board().connectedNeighbours(position));
        expected.add(position);
        assertEquals(expected, game.moveTargets());
        assertFalse(game.moveTo(new Position(3, 3)));
    }

    @Test
    void movesOneTileAtATimeAndCannotGoBackToThePreviousTile() {
        Game game = newGame();
        game.push(Direction.RIGHT, 1);
        Board board = game.board();
        Player alice = game.currentPlayer();
        Position start = new Position(2, 1);
        Position next = new Position(2, 2);
        alice.moveTo(start);
        connect(board, start, Direction.RIGHT);

        assertTrue(game.moveTargets().contains(next));
        assertTrue(game.moveTo(next));
        assertEquals(next, alice.position());
        assertEquals(Game.Phase.MOVE, game.phase());
        assertFalse(game.moveTargets().contains(start), "cannot step back right away");
        assertFalse(game.moveTo(start));
        assertEquals(next, alice.position());
    }

    @Test
    void findingTheCurrentTreasureMovesToTheNextOne() {
        for (long seed = 0; seed < 50; seed++) {
            Game game = new Game(new Random(seed));
            game.push(Direction.RIGHT, 1);
            Player alice = game.currentPlayer();
            Position treasure = game.board().positionOfTreasure(alice.currentObjective());
            if (treasure == null) {
                continue;
            }
            Direction direction = treasure.col() > 0 ? Direction.LEFT : Direction.RIGHT;
            Position from = treasure.step(direction);
            alice.moveTo(from);
            connect(game.board(), from, direction.opposite());

            List<Player> found = new ArrayList<>();
            game.addListener(new GameListener() {
                @Override
                public void gameChanged() {
                }

                @Override
                public void objectiveFound(Player player) {
                    found.add(player);
                }
            });
            int first = alice.currentObjective();

            assertTrue(game.moveTo(treasure));
            assertEquals(1, alice.foundCount());
            assertEquals(List.of(alice), found);
            assertTrue(alice.objectives().indexOf(first) < alice.foundCount());
            assertFalse(alice.hasFoundAll());
            return;
        }
        throw new AssertionError("No suitable game found");
    }

    @Test
    void walkingOnAnotherTreasureDoesNotCount() {
        for (long seed = 0; seed < 50; seed++) {
            Game game = new Game(new Random(seed));
            game.push(Direction.RIGHT, 1);
            Player alice = game.currentPlayer();
            int other = alice.objectives().get(3);
            Position treasure = game.board().positionOfTreasure(other);
            if (treasure == null || treasure.col() == 0) {
                continue;
            }
            Position from = treasure.step(Direction.LEFT);
            alice.moveTo(from);
            connect(game.board(), from, Direction.RIGHT);
            if (treasure.equals(game.board().positionOfTreasure(alice.currentObjective()))) {
                continue;
            }
            game.moveTo(treasure);
            assertEquals(0, alice.foundCount());
            return;
        }
        throw new AssertionError("No suitable game found");
    }

    @Test
    void theFirstPlayerBackHomeWithAllHisTreasuresWins() {
        Game game = newGame();
        game.push(Direction.RIGHT, 1);
        Player alice = game.currentPlayer();
        while (!alice.hasFoundAll()) {
            alice.markObjectiveFound();
        }
        alice.moveTo(new Position(0, 1));
        connect(game.board(), new Position(0, 1), Direction.LEFT);

        assertNull(game.winner());
        assertTrue(game.moveTo(new Position(0, 0)));

        assertEquals(Game.Phase.OVER, game.phase());
        assertSame(alice, game.winner());
        assertTrue(game.moveTargets().isEmpty());
        assertFalse(game.push(Direction.RIGHT, 1));
    }

    @Test
    void comingHomeTooEarlyIsNotEnough() {
        Game game = newGame();
        game.push(Direction.RIGHT, 1);
        Player alice = game.currentPlayer();
        alice.moveTo(new Position(0, 1));
        connect(game.board(), new Position(0, 1), Direction.LEFT);
        game.moveTo(new Position(0, 0));
        assertEquals(Game.Phase.MOVE, game.phase());
        assertNull(game.winner());
    }

    @Test
    void listenersAreToldAboutEveryChange() {
        Game game = newGame();
        int[] changes = {0};
        game.addListener(() -> changes[0]++);
        game.rotateSpare();
        game.push(Direction.RIGHT, 1);
        game.moveTo(game.currentPlayer().position());
        assertEquals(3, changes[0]);
    }

    @Test
    void rotatingTheSpareTileTurnsIt() {
        Game game = newGame();
        Tile spare = game.board().spareTile();
        int before = spare.orientation();
        game.rotateSpare();
        assertEquals((before + 1) % 4, spare.orientation());
    }

    /**
     * Four players who push at random and walk towards their goal always end up finishing the game, and the rules
     * are never broken on the way.
     */
    @Test
    void aWholeGameCanBePlayedUntilSomebodyWins() {
        for (long seed = 1; seed <= 5; seed++) {
            Random random = new Random(seed);
            Game game = new Game(random);
            int actions = 0;
            while (game.phase() != Game.Phase.OVER) {
                assertTrue(actions++ < 100_000, "the game does not end (seed " + seed + ")");
                if (random.nextInt(3) == 0) {
                    game.rotateSpare();
                }
                if (game.phase() == Game.Phase.PUSH) {
                    pushSomewhere(game, random);
                } else {
                    stepTowardsGoalOrEndTurn(game);
                }
                for (Player player : game.players()) {
                    assertTrue(player.position().isOnBoard());
                    assertNotNull(game.board().tileAt(player.position()));
                }
            }
            Player winner = game.winner();
            assertTrue(winner.hasFoundAll());
            assertEquals(winner.home(), winner.position());
        }
    }

    private static void pushSomewhere(Game game, Random random) {
        while (true) {
            Direction direction = Direction.values()[random.nextInt(4)];
            int index = 1 + 2 * random.nextInt(3);
            if (game.push(direction, index)) {
                return;
            }
        }
    }

    private static void stepTowardsGoalOrEndTurn(Game game) {
        Player player = game.currentPlayer();
        Position goal = player.hasFoundAll() ? player.home() : game.board().positionOfTreasure(player.currentObjective());
        Position step = goal == null ? null : firstStepTowards(game, player.position(), goal);
        if (step != null && game.moveTargets().contains(step)) {
            assertTrue(game.moveTo(step));
        } else {
            assertTrue(game.moveTo(player.position()));
        }
    }

    private static Position firstStepTowards(Game game, Position from, Position goal) {
        if (from.equals(goal)) {
            return null;
        }
        Map<Position, Position> firstStep = new HashMap<>();
        Deque<Position> queue = new ArrayDeque<>();
        for (Position neighbour : game.board().connectedNeighbours(from)) {
            firstStep.put(neighbour, neighbour);
            queue.add(neighbour);
        }
        while (!queue.isEmpty()) {
            Position current = queue.poll();
            if (current.equals(goal)) {
                return firstStep.get(current);
            }
            for (Position neighbour : game.board().connectedNeighbours(current)) {
                if (!neighbour.equals(from) && !firstStep.containsKey(neighbour)) {
                    firstStep.put(neighbour, firstStep.get(current));
                    queue.add(neighbour);
                }
            }
        }
        return null;
    }
}
