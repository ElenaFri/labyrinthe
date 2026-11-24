package labyrinth.view;

import labyrinth.model.Board;
import labyrinth.model.Game;
import labyrinth.model.Player;
import labyrinth.model.Position;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * Draws the main screen of a {@link Game}, in the coordinates of the canvas.
 * <p>
 * Everything which never changes (table, board, portraits) is drawn once in an image; each frame then only
 * copies pictures which are already in the right size, so a frame is very cheap.
 */
final class GameRenderer {
    private static final Font NAME_FONT = new Font("Arial", Font.BOLD, 12);
    private static final Font TOAST_FONT = new Font("Arial", Font.BOLD, 22);
    private static final Color LINE_FILL = new Color(255, 215, 90, 60);
    private static final Color LINE_BORDER = new Color(255, 215, 90, 170);
    private static final int TOAST_WIDTH = 700;
    private static final int TOAST_HEIGHT = 120;

    /**
     * What the screen shows besides the game itself.
     *
     * @param hovered       : what is under the mouse, if anything
     * @param selectedArrow : the arrow selected with the keyboard, if any
     * @param toast         : a message to show over the board, if any
     */
    record ViewState(Target hovered, Target.Push selectedArrow, String toast) {
    }

    private final Game game;
    private final Images images;
    private final Sprites sprites;
    private final PanelButton rotateButton = new PanelButton(Layout.ROTATE_BUTTON, "TOURNER", 20, Palette.BEIGE, Palette.NAVY);
    private final PanelButton exitButton = new PanelButton(Layout.EXIT_BUTTON, "x", 10, Palette.SHADOW, Palette.BEIGE);
    private BufferedImage staticLayer;

    GameRenderer(Game game, Images images) {
        this.game = game;
        this.images = images;
        this.sprites = new Sprites(images);
    }

    /**
     * Draws the screen, or only the part of it which is inside of the clip of the graphics.
     *
     * @param g     : graphics in the coordinates of the canvas
     * @param state : what to show besides the game
     */
    void paint(Graphics2D g, ViewState state) {
        if (staticLayer == null) {
            staticLayer = drawStaticLayer();
        }
        g.drawImage(staticLayer, 0, 0, null);
        paintTiles(g);
        Target.Push highlighted = state.hovered() instanceof Target.Push push ? push : state.selectedArrow();
        if (highlighted != null) {
            paintLineHighlight(g, highlighted);
        }
        paintSpareTile(g);
        paintPawns(g);
        paintHands(g);
        paintMoveFrames(g, state.hovered());
        paintArrows(g, highlighted);
        g.drawImage(sprites.turnPanel(game.currentPlayer()), Layout.TURN_PANEL.x, Layout.TURN_PANEL.y, null);
        rotateButton.paint(g);
        exitButton.paint(g);
        if (state.toast() != null) {
            paintToast(g, state.toast());
        }
    }

    /**
     * The table, the board and the portraits of the players.
     */
    private BufferedImage drawStaticLayer() {
        BufferedImage layer = new BufferedImage(Layout.WIDTH, Layout.HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = layer.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g.drawImage(images.background(), 0, 0, null);

        // Board: shadow, border, then the board itself.
        int x = Layout.BOARD_X;
        int y = Layout.BOARD_Y;
        int size = Layout.BOARD_SIZE;
        g.setColor(Palette.SHADOW);
        g.fill(new RoundRectangle2D.Double(x + 18, y + 20, size, size, 15, 15));
        g.setColor(Palette.NAVY);
        g.setStroke(new BasicStroke(10));
        g.draw(new RoundRectangle2D.Double(x - 5, y - 5, size + 10, size + 10, 15, 15));
        g.drawImage(images.board(), x, y, null);

        // Players: portrait with rounded corners and name tag.
        g.setFont(NAME_FONT);
        FontMetrics metrics = g.getFontMetrics();
        int portraitSize = Layout.PORTRAIT_SIZE;
        for (Player player : game.players()) {
            Point origin = Layout.portraitOrigin(player.id());
            g.setColor(Palette.SHADOW);
            g.fill(new RoundRectangle2D.Double(origin.x + 8, origin.y + 10, portraitSize, portraitSize, 20, 20));
            g.setPaint(new TexturePaint(images.portrait(player.id(), portraitSize),
                    new Rectangle2D.Double(origin.x, origin.y, portraitSize, portraitSize)));
            g.fill(new RoundRectangle2D.Double(origin.x, origin.y, portraitSize, portraitSize, 20, 20));

            String name = player.name().toUpperCase();
            int textWidth = metrics.stringWidth(name);
            int labelX = origin.x + (portraitSize - textWidth) / 2 - 10;
            int labelY = origin.y + portraitSize + 5;
            int labelWidth = textWidth + 20;
            int labelHeight = 25;
            g.setColor(Palette.BEIGE);
            g.fill(new RoundRectangle2D.Double(labelX, labelY, labelWidth, labelHeight, 10, 10));
            g.setColor(Palette.NAVY);
            g.drawString(name, labelX + (labelWidth - textWidth) / 2, labelY + labelHeight / 2 + metrics.getAscent() / 2 - 2);
        }
        g.dispose();
        return layer;
    }

    private void paintTiles(Graphics2D g) {
        Rectangle clip = g.getClipBounds();
        Board board = game.board();
        for (int row = 0; row < Board.SIZE; row++) {
            for (int col = 0; col < Board.SIZE; col++) {
                int x = Layout.BOARD_X + col * Layout.TILE_SIZE;
                int y = Layout.BOARD_Y + row * Layout.TILE_SIZE;
                if (clip.intersects(x, y, Layout.TILE_SIZE, Layout.TILE_SIZE)) {
                    g.drawImage(images.tile(board.tileAt(new Position(row, col))), x, y, null);
                }
            }
        }
    }

    private void paintSpareTile(Graphics2D g) {
        Rectangle bounds = Layout.SPARE_TILE;
        g.drawImage(sprites.spareTile(game.board().spareTile()), bounds.x - Sprites.SPARE_MARGIN, bounds.y - Sprites.SPARE_MARGIN, null);
    }

    /**
     * The pawn of the player who has to play is drawn last, so that it is never hidden by another one.
     */
    private void paintPawns(Graphics2D g) {
        Player current = game.currentPlayer();
        for (Player player : game.players()) {
            if (player != current) {
                paintPawn(g, player, false);
            }
        }
        paintPawn(g, current, true);
    }

    private void paintPawn(Graphics2D g, Player player, boolean current) {
        Rectangle tile = Layout.tileBounds(player.position());
        g.drawImage(sprites.pawn(player.id(), current), tile.x - Sprites.PAWN_MARGIN, tile.y - Sprites.PAWN_MARGIN, null);
    }

    /**
     * Draws the treasures that the players still have to find, as piles of cards: only the first one of each
     * hand can be seen, the others are covered by the next card but for their top edge.
     */
    private void paintHands(Graphics2D g) {
        for (Player player : game.players()) {
            Point origin = Layout.handOrigin(player.id());
            List<Integer> objectives = player.objectives();
            for (int i = objectives.size() - 1; i > player.foundCount(); i--) {
                g.drawImage(images.blankCard(), origin.x, origin.y + i * Layout.CARD_STEP, null);
            }
            if (!player.hasFoundAll()) {
                int first = player.foundCount();
                g.drawImage(images.card(objectives.get(first)), origin.x, origin.y + first * Layout.CARD_STEP, null);
            }
        }
    }

    private void paintMoveFrames(Graphics2D g, Target hovered) {
        g.setStroke(new BasicStroke(2));
        for (Position position : game.moveTargets()) {
            Rectangle frame = Layout.moveFrame(position);
            g.setColor(new Target.Move(position).equals(hovered) ? Color.GREEN : Color.RED);
            g.drawRect(frame.x + 1, frame.y + 1, frame.width - 2, frame.height - 2);
        }
    }

    /**
     * Shows which row or column an arrow would push, before the player chooses it.
     */
    private void paintLineHighlight(Graphics2D g, Target.Push arrow) {
        Rectangle line = Layout.lineBounds(arrow);
        g.setColor(LINE_FILL);
        g.fill(line);
        g.setColor(LINE_BORDER);
        g.setStroke(new BasicStroke(3));
        g.drawRect(line.x + 1, line.y + 1, line.width - 2, line.height - 2);
    }

    private void paintArrows(Graphics2D g, Target.Push highlighted) {
        for (Target.Push arrow : Layout.PUSH_ARROWS) {
            Rectangle bounds = Layout.arrowBounds(arrow);
            g.drawImage(sprites.arrow(arrow.direction(), arrowState(arrow, highlighted)), bounds.x, bounds.y, null);
        }
    }

    private Sprites.ArrowState arrowState(Target.Push arrow, Target.Push highlighted) {
        if (game.canPush(arrow.direction(), arrow.index())) {
            return arrow.equals(highlighted) ? Sprites.ArrowState.HIGHLIGHTED : Sprites.ArrowState.NORMAL;
        }
        boolean forbidden = game.phase() == Game.Phase.PUSH && game.undoesLastPush(arrow.direction(), arrow.index());
        return forbidden ? Sprites.ArrowState.FORBIDDEN : Sprites.ArrowState.DISABLED;
    }

    /**
     * Draws the line explaining the keys of the keyboard, at the bottom left of the window (not of the canvas, which
     * may be smaller than the window), at the level of the sound button of the web page.
     *
     * @param g      : graphics in the coordinates of the window
     * @param height : height of the window
     */
    void paintKeyboardHint(Graphics2D g, int height) {
        if (game.phase() != Game.Phase.OVER) {
            BufferedImage hint = sprites.keyboardHint(game.phase() == Game.Phase.PUSH);
            g.drawImage(hint, Layout.HINT_MARGIN_LEFT, height - Layout.HINT_MIDDLE_ABOVE_BOTTOM - hint.getHeight() / 2, null);
        }
    }

    private void paintToast(Graphics2D g, String message) {
        int x = (Layout.WIDTH - TOAST_WIDTH) / 2;
        int y = (Layout.HEIGHT - TOAST_HEIGHT) / 2;
        g.setColor(Palette.ORANGE);
        g.fillRoundRect(x, y, TOAST_WIDTH, TOAST_HEIGHT, 20, 20);
        g.setColor(Palette.MINT);
        g.setStroke(new BasicStroke(5));
        g.drawRoundRect(x + 2, y + 2, TOAST_WIDTH - 4, TOAST_HEIGHT - 4, 20, 20);

        g.setFont(TOAST_FONT);
        g.setColor(Palette.NAVY);
        FontMetrics metrics = g.getFontMetrics();
        g.drawString(message, x + (TOAST_WIDTH - metrics.stringWidth(message)) / 2,
                y + (TOAST_HEIGHT - metrics.getHeight()) / 2 + metrics.getAscent());
    }
}
