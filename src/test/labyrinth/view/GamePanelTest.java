package labyrinth.view;

import labyrinth.model.Direction;
import labyrinth.model.Game;
import labyrinth.model.Position;
import labyrinth.model.Tile;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests of the main screen, without any window: the panel is drawn in an image and fed with mouse and key events.
 * They must be run from the root of the project, where the pictures are.
 */
class GamePanelTest {
    private static Images images;

    private Game game;
    private GamePanel panel;
    private int quitRequests;

    @BeforeAll
    static void loadImages() {
        System.setProperty("java.awt.headless", "true");
        images = new Images();
    }

    @BeforeEach
    void openGame() {
        game = new Game(new Random(7));
        panel = new GamePanel(game, images, () -> quitRequests++);
        panel.setSize(Layout.WIDTH, Layout.HEIGHT);
    }

    private BufferedImage draw(int width, int height) {
        panel.setSize(width, height);
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        panel.paint(g);
        g.dispose();
        return image;
    }

    private void click(double canvasX, double canvasY) {
        double scale = Math.min((double) panel.getWidth() / Layout.WIDTH, (double) panel.getHeight() / Layout.HEIGHT);
        double offsetX = (panel.getWidth() - Layout.WIDTH * scale) / 2;
        double offsetY = (panel.getHeight() - Layout.HEIGHT * scale) / 2;
        int x = (int) Math.round(offsetX + canvasX * scale);
        int y = (int) Math.round(offsetY + canvasY * scale);
        panel.dispatchEvent(new MouseEvent(panel, MouseEvent.MOUSE_PRESSED, 0L, 0, x, y, 1, false, MouseEvent.BUTTON1));
    }

    private void clickOn(Rectangle area) {
        click(area.getCenterX(), area.getCenterY());
    }

    private void press(String key) {
        panel.getActionMap().get(key).actionPerformed(new ActionEvent(panel, ActionEvent.ACTION_PERFORMED, key));
    }

    @Test
    void drawsTheGameInAnySize() {
        BufferedImage big = draw(Layout.WIDTH, Layout.HEIGHT);
        // The table is not the same colour as the board, which is not the same as the spare tile...
        int table = big.getRGB(5, 5);
        int board = big.getRGB(Layout.BOARD_X + 64, Layout.BOARD_Y + 64);
        int spare = big.getRGB((int) Layout.SPARE_TILE.getCenterX(), (int) Layout.SPARE_TILE.getCenterY());
        assertNotEquals(table, board);
        assertNotEquals(table, spare);

        draw(1280, 720);
        draw(400, 700);
    }

    private void moveMouseTo(Rectangle area) {
        panel.dispatchEvent(new MouseEvent(panel, MouseEvent.MOUSE_MOVED, 0L, 0, (int) area.getCenterX(), (int) area.getCenterY(), 0, false));
    }

    @Test
    void pointingAtAnArrowShowsTheRowItWouldPush() {
        Target.Push arrow = new Target.Push(Direction.RIGHT, 3);
        Rectangle row = Layout.lineBounds(arrow);
        int insideX = row.x + row.width / 2;
        int insideY = row.y + 2;     // the border of the highlight, whatever the tile below
        int outsideY = Layout.lineBounds(new Target.Push(Direction.RIGHT, 1)).y + 2;

        BufferedImage before = draw(Layout.WIDTH, Layout.HEIGHT);
        moveMouseTo(Layout.arrowBounds(arrow));
        BufferedImage during = draw(Layout.WIDTH, Layout.HEIGHT);
        panel.dispatchEvent(new MouseEvent(panel, MouseEvent.MOUSE_EXITED, 0L, 0, 0, 0, 0, false));
        BufferedImage after = draw(Layout.WIDTH, Layout.HEIGHT);

        assertNotEquals(before.getRGB(insideX, insideY), during.getRGB(insideX, insideY));
        assertEquals(before.getRGB(insideX, outsideY), during.getRGB(insideX, outsideY));
        assertEquals(before.getRGB(insideX, insideY), after.getRGB(insideX, insideY));
    }

    @Test
    void theArrowSelectedWithTheKeyboardShowsItsRowToo() {
        Rectangle row = Layout.lineBounds(new Target.Push(Direction.DOWN, 1));
        int x = row.x + 2;
        int y = row.y + row.height / 2;
        BufferedImage before = draw(Layout.WIDTH, Layout.HEIGHT);

        press("RIGHT");   // the first arrow: the top one of column 1

        assertNotEquals(before.getRGB(x, y), draw(Layout.WIDTH, Layout.HEIGHT).getRGB(x, y));
    }

    @Test
    void theKeyboardHintIsAtTheBottomLeftOfTheWindowWhateverItsShape() {
        // In a window which is not as wide as the canvas, there are black bars above and below the canvas: the hint
        // is the only thing which can be drawn in the one at the bottom.
        int width = 800;
        int height = 600;
        int y = height - Layout.HINT_MIDDLE_ABOVE_BOTTOM;
        assertTrue(y > (height + Layout.HEIGHT * width / Layout.WIDTH) / 2, "the line is in the bar");

        BufferedImage push = draw(width, height);
        clickOn(Layout.arrowBounds(new Target.Push(Direction.RIGHT, 3)));
        BufferedImage move = draw(width, height);

        int black = 0xFF000000;
        int[] pushPixels = push.getRGB(0, y - 12, 480, 24, null, 0, 480);
        int[] movePixels = move.getRGB(0, y - 12, 480, 24, null, 0, 480);
        assertTrue(Arrays.stream(pushPixels).anyMatch(p -> p != black), "the hint is drawn");
        assertFalse(Arrays.equals(pushPixels, movePixels), "it depends on the phase of the turn");
        assertEquals(black, push.getRGB(width - 5, y));
    }

    @Test
    void aPartialRedrawGivesTheSamePixelsAsAFullOne() {
        for (int[] size : new int[][]{{Layout.WIDTH, Layout.HEIGHT}, {1280, 720}, {700, 650}}) {
            panel.setSize(size[0], size[1]);
            Rectangle area = new Rectangle(size[0] / 4, size[1] / 5, size[0] / 3, size[1] / 4);
            BufferedImage full = draw(size[0], size[1]);

            int untouched = 0xFF123456;
            BufferedImage partial = new BufferedImage(size[0], size[1], BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = partial.createGraphics();
            g.setColor(new java.awt.Color(untouched));
            g.fillRect(0, 0, size[0], size[1]);
            g.setClip(area);
            panel.paint(g);
            g.dispose();

            for (int y = 0; y < size[1]; y += 3) {
                for (int x = 0; x < size[0]; x += 3) {
                    int expected = area.contains(x, y) ? full.getRGB(x, y) : untouched;
                    assertEquals(expected, partial.getRGB(x, y), size[0] + "x" + size[1] + " at " + x + "," + y);
                }
            }
        }
    }

    @Test
    void theDrawingFollowsTheChangesOfTheGame() {
        BufferedImage before = draw(Layout.WIDTH, Layout.HEIGHT);
        Rectangle row = Layout.lineBounds(new Target.Push(Direction.RIGHT, 3));
        int[] rowBefore = before.getRGB(row.x, row.y, row.width, row.height, null, 0, row.width);

        clickOn(Layout.arrowBounds(new Target.Push(Direction.RIGHT, 3)));
        BufferedImage after = draw(Layout.WIDTH, Layout.HEIGHT);

        assertFalse(Arrays.equals(rowBefore, after.getRGB(row.x, row.y, row.width, row.height, null, 0, row.width)),
                "the pushed row is drawn again");
        Rectangle untouched = Layout.lineBounds(new Target.Push(Direction.RIGHT, 5));
        assertTrue(Arrays.equals(before.getRGB(untouched.x, untouched.y, untouched.width, 30, null, 0, untouched.width),
                after.getRGB(untouched.x, untouched.y, untouched.width, 30, null, 0, untouched.width)));
    }

    @Test
    void aPanelWhichIsRemovedCanBeGarbageCollected() throws InterruptedException {
        // The message shown when a treasure is found has a timer: it must not keep the panel (and its big images
        // in memory) alive once the panel has been removed from the window.
        GamePanel removed = new GamePanel(new Game(new Random(1)), images, () -> { });
        removed.setSize(Layout.WIDTH, Layout.HEIGHT);
        removed.objectiveFound(game.currentPlayer());
        removed.removeNotify();
        java.lang.ref.WeakReference<GamePanel> reference = new java.lang.ref.WeakReference<>(removed);
        removed = null;

        for (int i = 0; i < 20 && reference.get() != null; i++) {
            System.gc();
            Thread.sleep(50);
        }
        assertEquals(null, reference.get());
    }

    @Test
    void findingATreasureShowsAMessageOverTheBoard() {
        int x = Layout.WIDTH / 2;
        int y = Layout.HEIGHT / 2;
        BufferedImage hidden = draw(Layout.WIDTH, Layout.HEIGHT);
        int before = hidden.getRGB(x, y);

        panel.objectiveFound(game.currentPlayer());
        BufferedImage shown = draw(Layout.WIDTH, Layout.HEIGHT);

        // The message is a navy panel with a gold frame, drawn in the middle of the screen, in any size of window.
        assertNotEquals(before, shown.getRGB(x, y));
        assertNotEquals(before, shown.getRGB(x - 300, y - 20));
        int blue = shown.getRGB(x - 300, y - 40) & 0xFF;
        int red = (shown.getRGB(x - 300, y - 40) >> 16) & 0xFF;
        assertTrue(blue > red, "navy, not orange");
        assertEquals(hidden.getRGB(5, 5), shown.getRGB(5, 5), "nothing else changes");
        panel.removeNotify();
    }

    @Test
    void aClickOnAnArrowPushesTheSpareTile() {
        Tile spare = game.board().spareTile();
        clickOn(Layout.arrowBounds(new Target.Push(Direction.RIGHT, 3)));

        assertEquals(Game.Phase.MOVE, game.phase());
        assertSame(spare, game.board().tileAt(new Position(3, 0)));
    }

    @Test
    void clicksFollowTheScaleAndTheCentringOfTheCanvas() {
        for (int[] size : new int[][]{{960, 540}, {960, 700}, {2560, 1440}, {600, 1000}}) {
            Game fresh = new Game(new Random(7));
            panel = new GamePanel(fresh, images, () -> quitRequests++);
            game = fresh;
            panel.setSize(size[0], size[1]);

            clickOn(Layout.arrowBounds(new Target.Push(Direction.DOWN, 3)));

            assertEquals(Game.Phase.MOVE, game.phase(), size[0] + "x" + size[1]);
        }
    }

    @Test
    void aClickBesideTheArrowsDoesNothing() {
        click(5, 5);
        click(Layout.BOARD_X + 10, Layout.BOARD_Y + 10);
        assertEquals(Game.Phase.PUSH, game.phase());
    }

    @Test
    void anArrowCanOnlyBeUsedOncePerTurn() {
        clickOn(Layout.arrowBounds(new Target.Push(Direction.RIGHT, 3)));
        Tile spare = game.board().spareTile();

        clickOn(Layout.arrowBounds(new Target.Push(Direction.DOWN, 1)));

        assertSame(spare, game.board().spareTile());
    }

    @Test
    void theRotateButtonTurnsTheSpareTile() {
        int before = game.board().spareTile().orientation();
        clickOn(Layout.ROTATE_BUTTON);
        assertEquals((before + 1) % 4, game.board().spareTile().orientation());
    }

    @Test
    void theExitButtonAsksToQuit() {
        clickOn(Layout.EXIT_BUTTON);
        assertEquals(1, quitRequests);
    }

    @Test
    void theKeyboardChoosesAnArrowThenPushes() {
        Tile spare = game.board().spareTile();
        press("LEFT");   // the first press selects the last arrow in clockwise order: the one pushing row 1 to the right
        press("ENTER");

        assertEquals(Game.Phase.MOVE, game.phase());
        assertSame(spare, game.board().tileAt(new Position(1, 0)));
    }

    @Test
    void theKeyboardGoesThroughTheArrowsInClockwiseOrder() {
        Tile spare = game.board().spareTile();
        press("RIGHT");  // the first arrow: top of the board, column 1, pushing down
        press("RIGHT");  // the second one: column 3
        press("SPACE");

        assertSame(spare, game.board().tileAt(new Position(0, 3)));
    }

    @Test
    void enterDoesNothingWithoutASelectedArrow() {
        press("ENTER");
        assertEquals(Game.Phase.PUSH, game.phase());
    }

    @Test
    void theKeyboardTurnsTheSpareTile() {
        int before = game.board().spareTile().orientation();
        press("R");
        assertEquals((before + 1) % 4, game.board().spareTile().orientation());
    }

    @Test
    void theKeyboardMovesThePawnAndEndsTheTurn() {
        // A game where Alice can leave her corner whatever the push of the third row.
        Game found = null;
        for (long seed = 0; seed < 200 && found == null; seed++) {
            Game candidate = new Game(new Random(seed));
            Position corner = new Position(0, 0);
            if (candidate.board().canPass(corner, Direction.RIGHT)) {
                found = candidate;
            }
        }
        game = found;
        panel = new GamePanel(game, images, () -> quitRequests++);
        panel.setSize(Layout.WIDTH, Layout.HEIGHT);

        clickOn(Layout.arrowBounds(new Target.Push(Direction.RIGHT, 3)));
        press("D");
        assertEquals(new Position(0, 1), game.currentPlayer().position());

        press("ENTER");
        assertEquals("Bob", game.currentPlayer().name());
        assertEquals(Game.Phase.PUSH, game.phase());
    }

    @Test
    void theKeyboardCannotWalkThroughWalls() {
        clickOn(Layout.arrowBounds(new Target.Push(Direction.RIGHT, 3)));
        Position start = game.currentPlayer().position();
        // Alice is in the top left corner: nothing can be at her left or above her.
        press("LEFT");
        press("UP");
        assertEquals(start, game.currentPlayer().position());
        assertTrue(game.moveTargets().contains(start));
    }
}
