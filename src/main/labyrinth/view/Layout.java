package labyrinth.view;

import labyrinth.model.Board;
import labyrinth.model.Direction;
import labyrinth.model.Position;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Where everything is drawn. The game is designed for a 1920x1080 screen; the coordinates below are expressed in
 * this "canvas", which is scaled to the actual size of the window.
 */
final class Layout {
    static final int WIDTH = 1920;
    static final int HEIGHT = 1080;
    static final int TILE_SIZE = 128;
    static final int BOARD_SIZE = 896;
    static final int BOARD_X = (WIDTH - BOARD_SIZE) / 2;
    static final int BOARD_Y = (HEIGHT - BOARD_SIZE) / 2;
    /**
     * Size of a card of a hand, in the proportions of the picture of the card (150x237).
     */
    static final int CARD_WIDTH = 72;
    static final int CARD_HEIGHT = 114;
    /**
     * Gap between the cards of a hand.
     */
    static final int CARD_STEP = -9;
    static final int PORTRAIT_SIZE = 90;
    static final int ARROW_SIZE = 50;
    /**
     * Space between the border of a tile and the frame showing that the pawn can go there.
     */
    static final int FRAME_INSET = 10;

    /**
     * Where the spare tile is shown.
     */
    static final Rectangle SPARE_TILE = new Rectangle(BOARD_X + BOARD_SIZE + 120, (HEIGHT - TILE_SIZE) / 2, TILE_SIZE, TILE_SIZE);
    static final Rectangle ROTATE_BUTTON = new Rectangle(1532, 626, 130, 30);
    static final Rectangle EXIT_BUTTON = new Rectangle(1870, 13, 35, 35);
    /**
     * The panel telling who has to play.
     */
    static final Rectangle TURN_PANEL = new Rectangle(125, 400, 200, 275);
    /**
     * Distance between the left of the window and the line explaining the keys of the keyboard.
     */
    static final int HINT_MARGIN_LEFT = 8;
    /**
     * Distance between the bottom of the window and the middle of that line. The sound button of the web page is
     * at the same level, on the other side.
     */
    static final int HINT_MIDDLE_ABOVE_BOTTOM = 22;

    /**
     * The twelve arrows around the board, in clockwise order from the top left: this is the order in which the
     * keyboard goes through them.
     */
    static final List<Target.Push> PUSH_ARROWS;

    private static final Map<Target.Push, Rectangle> ARROW_BOUNDS = new LinkedHashMap<>();

    static {
        int last = Board.SIZE - 2;
        List<Target.Push> arrows = new ArrayList<>();
        for (int i = 1; i <= last; i += 2) {
            arrows.add(new Target.Push(Direction.DOWN, i));
        }
        for (int i = 1; i <= last; i += 2) {
            arrows.add(new Target.Push(Direction.LEFT, i));
        }
        for (int i = last; i >= 1; i -= 2) {
            arrows.add(new Target.Push(Direction.UP, i));
        }
        for (int i = last; i >= 1; i -= 2) {
            arrows.add(new Target.Push(Direction.RIGHT, i));
        }
        for (Target.Push arrow : arrows) {
            ARROW_BOUNDS.put(arrow, computeArrowBounds(arrow));
        }
        PUSH_ARROWS = Collections.unmodifiableList(arrows);
    }

    private Layout() {
    }

    /**
     * @param position : a position on the board
     * @return the area of the tile at this position
     */
    static Rectangle tileBounds(Position position) {
        return new Rectangle(BOARD_X + position.col() * TILE_SIZE, BOARD_Y + position.row() * TILE_SIZE, TILE_SIZE, TILE_SIZE);
    }

    /**
     * @param position : a position on the board
     * @return the frame drawn on the tile at this position when the pawn can go there; it is also the area
     * which reacts to the mouse
     */
    static Rectangle moveFrame(Position position) {
        Rectangle tile = tileBounds(position);
        tile.grow(-FRAME_INSET, -FRAME_INSET);
        return tile;
    }

    /**
     * @param player : the index of a player (0 and 2 are on the left, 1 and 3 on the right; 0 and 1 are at the
     *               top, 2 and 3 at the bottom)
     * @return the top left corner of his portrait
     */
    static Point portraitOrigin(int player) {
        int x = player % 2 == 0 ? BOARD_X - PORTRAIT_SIZE - 40 : BOARD_X + BOARD_SIZE + 40;
        int y = player < 2 ? BOARD_Y - PORTRAIT_SIZE / 4 : BOARD_Y + 18 + BOARD_SIZE - PORTRAIT_SIZE;
        return new Point(x, y);
    }

    /**
     * @param player : the index of a player
     * @return the top left corner of the first card of his hand
     */
    static Point handOrigin(int player) {
        int x = player % 2 == 0 ? BOARD_X - 196 : BOARD_X + BOARD_SIZE + 120;
        int y = player < 2 ? BOARD_Y + 30 : BOARD_Y + BOARD_SIZE - 96;
        return new Point(x, y);
    }

    /**
     * @param arrow : one of the {@link #PUSH_ARROWS}
     * @return the area of the arrow button
     */
    static Rectangle arrowBounds(Target.Push arrow) {
        return new Rectangle(ARROW_BOUNDS.get(arrow));
    }

    /**
     * @param arrow : one of the {@link #PUSH_ARROWS}
     * @return the area of the board covered by the row or column that this arrow pushes
     */
    static Rectangle lineBounds(Target.Push arrow) {
        boolean horizontal = arrow.direction() == Direction.LEFT || arrow.direction() == Direction.RIGHT;
        int along = arrow.index() * TILE_SIZE;
        return horizontal
                ? new Rectangle(BOARD_X, BOARD_Y + along, BOARD_SIZE, TILE_SIZE)
                : new Rectangle(BOARD_X + along, BOARD_Y, TILE_SIZE, BOARD_SIZE);
    }

    /**
     * The arrow is outside of the board, at the side from which the spare tile enters.
     */
    private static Rectangle computeArrowBounds(Target.Push arrow) {
        int along = arrow.index() * TILE_SIZE + (TILE_SIZE - ARROW_SIZE) / 2;
        return switch (arrow.direction()) {
            case RIGHT -> new Rectangle(BOARD_X - 62, BOARD_Y + along, ARROW_SIZE, ARROW_SIZE);
            case LEFT -> new Rectangle(BOARD_X + BOARD_SIZE + 9, BOARD_Y + along, ARROW_SIZE, ARROW_SIZE);
            case DOWN -> new Rectangle(BOARD_X + along, BOARD_Y - 62, ARROW_SIZE, ARROW_SIZE);
            case UP -> new Rectangle(BOARD_X + along, BOARD_Y + BOARD_SIZE + 12, ARROW_SIZE, ARROW_SIZE);
        };
    }
}
