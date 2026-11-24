package labyrinth.view;

import labyrinth.model.Direction;
import labyrinth.model.Position;

import java.awt.Point;
import java.awt.Rectangle;

/**
 * Where everything is drawn. The game is designed for a 1920x1080 screen; the coordinates below are expressed in
 * this "canvas", which is scaled to the actual size of the window.
 */
public final class Layout {
    /**
     * Width of the canvas.
     */
    public static final int WIDTH = 1920;
    /**
     * Height of the canvas.
     */
    public static final int HEIGHT = 1080;
    /**
     * Width and height of a tile.
     */
    public static final int TILE_SIZE = 128;
    /**
     * Width and height of the board.
     */
    public static final int BOARD_SIZE = 896;
    /**
     * Left side of the board.
     */
    public static final int BOARD_X = (WIDTH - BOARD_SIZE) / 2;
    /**
     * Top side of the board.
     */
    public static final int BOARD_Y = (HEIGHT - BOARD_SIZE) / 2;
    /**
     * Width of a card in a hand.
     */
    public static final int CARD_WIDTH = 60;
    /**
     * Height of a card in a hand.
     */
    public static final int CARD_HEIGHT = 100;
    /**
     * Gap between the cards of a hand.
     */
    public static final int CARD_STEP = -8;
    /**
     * Width and height of the portrait of a player.
     */
    public static final int PORTRAIT_SIZE = 90;
    /**
     * Width and height of the arrow buttons.
     */
    public static final int ARROW_SIZE = 50;
    /**
     * Width and height of the picture drawn in an arrow button.
     */
    public static final int ARROW_ICON_SIZE = 32;

    /**
     * Where the spare tile is shown.
     */
    public static final Rectangle SPARE_TILE = new Rectangle(BOARD_X + BOARD_SIZE + 120, (HEIGHT - TILE_SIZE) / 2, TILE_SIZE, TILE_SIZE);
    /**
     * The button turning the spare tile.
     */
    public static final Rectangle ROTATE_BUTTON = new Rectangle(1532, 626, 130, 30);
    /**
     * The button quitting the game.
     */
    public static final Rectangle EXIT_BUTTON = new Rectangle(1870, 13, 35, 35);
    /**
     * The panel telling who has to play.
     */
    public static final Rectangle TURN_PANEL = new Rectangle(125, 400, 200, 275);

    private Layout() {
    }

    /**
     * @param position : a position on the board
     * @return the area of the tile at this position
     */
    public static Rectangle tileBounds(Position position) {
        return new Rectangle(BOARD_X + position.col() * TILE_SIZE, BOARD_Y + position.row() * TILE_SIZE, TILE_SIZE, TILE_SIZE);
    }

    /**
     * @param player : the index of a player (0 and 2 are on the left, 1 and 3 on the right; 0 and 1 are at the
     *               top, 2 and 3 at the bottom)
     * @return the top left corner of his portrait
     */
    public static Point portraitOrigin(int player) {
        int x = player % 2 == 0 ? BOARD_X - PORTRAIT_SIZE - 40 : BOARD_X + BOARD_SIZE + 40;
        int y = player < 2 ? BOARD_Y - PORTRAIT_SIZE / 4 : BOARD_Y + 18 + BOARD_SIZE - PORTRAIT_SIZE;
        return new Point(x, y);
    }

    /**
     * @param player : the index of a player
     * @return the top left corner of the first card of his hand
     */
    public static Point handOrigin(int player) {
        int x = player % 2 == 0 ? BOARD_X - 180 : BOARD_X + BOARD_SIZE + 120;
        int y = player < 2 ? BOARD_Y + 30 : BOARD_Y + BOARD_SIZE - 90;
        return new Point(x, y);
    }

    /**
     * @param direction : the direction of push
     * @param index     : the index of the pushed row or column
     * @return the area of the arrow button, outside of the board: it is at the side from which the spare tile enters
     */
    public static Rectangle arrowBounds(Direction direction, int index) {
        int along = index * TILE_SIZE + (TILE_SIZE - ARROW_SIZE) / 2;
        return switch (direction) {
            case RIGHT -> new Rectangle(BOARD_X - 62, BOARD_Y + along, ARROW_SIZE, ARROW_SIZE);
            case LEFT -> new Rectangle(BOARD_X + BOARD_SIZE + 9, BOARD_Y + along, ARROW_SIZE, ARROW_SIZE);
            case DOWN -> new Rectangle(BOARD_X + along, BOARD_Y - 62, ARROW_SIZE, ARROW_SIZE);
            case UP -> new Rectangle(BOARD_X + along, BOARD_Y + BOARD_SIZE + 12, ARROW_SIZE, ARROW_SIZE);
        };
    }
}
