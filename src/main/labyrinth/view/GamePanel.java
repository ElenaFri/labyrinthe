package labyrinth.view;

import labyrinth.model.Board;
import labyrinth.model.Direction;
import labyrinth.model.Game;
import labyrinth.model.GameListener;
import labyrinth.model.Player;
import labyrinth.model.Position;

import javax.swing.Timer;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The main screen: draws a {@link Game} and turns the clicks of the players into moves.
 * <p>
 * Everything which never changes (table, board, portraits) is drawn once in an image; each frame then only
 * draws pictures which are already in the right size, so a frame is very cheap.
 */
@SuppressWarnings("serial")
public final class GamePanel extends ScaledPanel implements GameListener {
    private static final Color NAVY = Palette.NAVY;
    private static final Color BEIGE = Palette.BEIGE;
    private static final Color SHADOW = Palette.SHADOW;

    private static final int TOAST_DURATION_MS = 4000;

    private record Arrow(Direction direction, int index, Rectangle bounds) {
    }

    private final Game game;
    private final Images images;
    private final Sprites sprites;
    private final Runnable onQuit;
    private final List<Arrow> arrows = new ArrayList<>();
    private final PanelButton rotateButton = new PanelButton(Layout.ROTATE_BUTTON, "TOURNER", 20, BEIGE, NAVY);
    private final PanelButton exitButton = new PanelButton(Layout.EXIT_BUTTON, "x", 10, SHADOW, BEIGE);
    private final Timer toastTimer = new Timer(TOAST_DURATION_MS, e -> hideToast());
    private BufferedImage staticLayer;
    private Point mouse;
    private Object hovered;
    private Arrow selectedArrow;
    private String toast;

    /**
     * @param game   : the game to show; the panel keeps itself up to date when the game changes
     * @param images : the pictures of the game
     * @param onQuit : what to do when the player asks to quit
     */
    public GamePanel(Game game, Images images, Runnable onQuit) {
        this.game = game;
        this.images = images;
        this.sprites = new Sprites(images);
        this.onQuit = onQuit;
        toastTimer.setRepeats(false);

        // The arrows in clockwise order from the top left, which is the order of the keyboard selection.
        int last = Board.SIZE - 2;
        for (int i = 1; i <= last; i += 2) {
            addArrow(Direction.DOWN, i);
        }
        for (int i = 1; i <= last; i += 2) {
            addArrow(Direction.LEFT, i);
        }
        for (int i = last; i >= 1; i -= 2) {
            addArrow(Direction.UP, i);
        }
        for (int i = last; i >= 1; i -= 2) {
            addArrow(Direction.RIGHT, i);
        }
        game.addListener(this);
        bindKeys();
    }

    private void addArrow(Direction direction, int index) {
        arrows.add(new Arrow(direction, index, Layout.arrowBounds(direction, index)));
    }

    /**
     * The keyboard does everything the mouse does. The arrow keys (or WASD, or ZQSD) choose where to push the spare
     * tile, then move the pawn; Enter or Space pushes the spare tile, then ends the turn; R turns the spare tile.
     */
    private void bindKeys() {
        bindDirectionKeys(Direction.UP, "UP", "W", "Z");
        bindDirectionKeys(Direction.DOWN, "DOWN", "S");
        bindDirectionKeys(Direction.LEFT, "LEFT", "A", "Q");
        bindDirectionKeys(Direction.RIGHT, "RIGHT", "D");
        bindKey("ENTER", this::confirm);
        bindKey("SPACE", this::confirm);
        bindKey("R", game::rotateSpare);
    }

    private void bindDirectionKeys(Direction direction, String... keys) {
        for (String key : keys) {
            bindKey(key, () -> onDirectionKey(direction));
        }
    }

    private void onDirectionKey(Direction direction) {
        if (game.phase() == Game.Phase.MOVE) {
            game.step(direction);
        } else if (game.phase() == Game.Phase.PUSH) {
            // Up and left go to the previous arrow, down and right to the next one.
            selectArrow(direction == Direction.UP || direction == Direction.LEFT ? -1 : 1);
        }
    }

    private void confirm() {
        if (game.phase() == Game.Phase.MOVE) {
            game.endTurn();
        } else if (game.phase() == Game.Phase.PUSH && selectedArrow != null) {
            game.push(selectedArrow.direction(), selectedArrow.index());
        }
    }

    /**
     * Selects the next (or previous) arrow among the ones which can be used.
     */
    private void selectArrow(int step) {
        List<Arrow> usable = arrows.stream().filter(this::canUse).toList();
        if (usable.isEmpty()) {
            return;
        }
        int current = usable.indexOf(selectedArrow);
        int next = current < 0 ? (step > 0 ? 0 : usable.size() - 1) : Math.floorMod(current + step, usable.size());
        selectedArrow = usable.get(next);
        repaint();
    }

    private boolean canUse(Arrow arrow) {
        return game.canPush(arrow.direction(), arrow.index());
    }

    @Override
    public void gameChanged() {
        if (selectedArrow != null && !canUse(selectedArrow)) {
            selectedArrow = null;
        }
        updateHover();
        repaint();
    }

    @Override
    public void objectiveFound(Player player) {
        toast = "Félicitations, " + player.name() + " ! Objectif trouvé !";
        toastTimer.restart();
    }

    @Override
    public void removeNotify() {
        toastTimer.stop();
        super.removeNotify();
    }

    private void hideToast() {
        toast = null;
        repaint();
    }

    @Override
    protected void canvasMouseMoved(Point point) {
        mouse = point;
        updateHover();
    }

    @Override
    protected void canvasMouseExited() {
        mouse = null;
        updateHover();
    }

    @Override
    protected void canvasMousePressed(Point point) {
        Object target = targetAt(point);
        if (target == exitButton) {
            onQuit.run();
        } else if (target == rotateButton) {
            game.rotateSpare();
        } else if (target instanceof Arrow arrow) {
            game.push(arrow.direction(), arrow.index());
        } else if (target instanceof Position position) {
            game.moveTo(position);
        }
    }

    /**
     * @return what is under a point and can be clicked: a button, an arrow which can be used, or a tile to move to
     */
    private Object targetAt(Point point) {
        if (point == null) {
            return null;
        }
        if (exitButton.contains(point)) {
            return exitButton;
        }
        if (rotateButton.contains(point)) {
            return rotateButton;
        }
        for (Arrow arrow : arrows) {
            if (arrow.bounds().contains(point) && canUse(arrow)) {
                return arrow;
            }
        }
        for (Position position : game.moveTargets()) {
            if (frameOf(position).contains(point)) {
                return position;
            }
        }
        return null;
    }

    private void updateHover() {
        Object target = targetAt(mouse);
        if (!Objects.equals(target, hovered)) {
            repaintFrame(hovered);
            hovered = target;
            repaintFrame(hovered);
            setCursor(Cursor.getPredefinedCursor(target == null ? Cursor.DEFAULT_CURSOR : Cursor.HAND_CURSOR));
        }
    }

    /**
     * The only thing which changes in the look of the screen when the mouse moves is the colour of the frame of the
     * tile under the mouse, so nothing else needs to be redrawn.
     */
    private void repaintFrame(Object target) {
        if (target instanceof Position position) {
            repaintCanvas(frameOf(position));
        }
    }

    private static Rectangle frameOf(Position position) {
        Rectangle tile = Layout.tileBounds(position);
        return new Rectangle(tile.x + 10, tile.y + 10, tile.width - 20, tile.height - 20);
    }

    @Override
    protected void paintCanvas(Graphics2D g) {
        if (staticLayer == null) {
            staticLayer = drawStaticLayer();
        }
        g.drawImage(staticLayer, 0, 0, null);
        paintTiles(g);
        paintSpareTile(g);
        paintPawns(g);
        paintHands(g);
        paintMoveTargets(g);
        paintArrows(g);
        paintTurnPanel(g);
        paintKeyboardHint(g);
        rotateButton.paint(g);
        exitButton.paint(g);
        paintToast(g);
    }

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
        g.setColor(SHADOW);
        g.fill(new RoundRectangle2D.Double(x + 18, y + 20, size, size, 15, 15));
        g.setColor(NAVY);
        g.setStroke(new BasicStroke(10));
        g.draw(new RoundRectangle2D.Double(x - 5, y - 5, size + 10, size + 10, 15, 15));
        g.drawImage(images.board(), x, y, null);

        // Players: portrait with rounded corners and name tag.
        g.setFont(new Font("Arial", Font.BOLD, 12));
        FontMetrics metrics = g.getFontMetrics();
        int portraitSize = Layout.PORTRAIT_SIZE;
        for (Player player : game.players()) {
            Point origin = Layout.portraitOrigin(player.id());
            g.setColor(SHADOW);
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
            g.setColor(BEIGE);
            g.fill(new RoundRectangle2D.Double(labelX, labelY, labelWidth, labelHeight, 10, 10));
            g.setColor(NAVY);
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

    private void paintMoveTargets(Graphics2D g) {
        g.setStroke(new BasicStroke(2));
        for (Position position : game.moveTargets()) {
            Rectangle frame = frameOf(position);
            g.setColor(position.equals(hovered) ? Color.GREEN : Color.RED);
            g.drawRect(frame.x + 1, frame.y + 1, frame.width - 2, frame.height - 2);
        }
    }

    private void paintArrows(Graphics2D g) {
        for (Arrow arrow : arrows) {
            g.drawImage(sprites.arrow(arrow.direction(), canUse(arrow)), arrow.bounds().x, arrow.bounds().y, null);
        }
        if (selectedArrow != null) {
            Rectangle bounds = selectedArrow.bounds();
            g.setColor(Color.GREEN);
            g.setStroke(new BasicStroke(3));
            g.drawRoundRect(bounds.x, bounds.y, bounds.width - 1, bounds.height - 1, 10, 10);
        }
    }

    private void paintKeyboardHint(Graphics2D g) {
        if (game.phase() != Game.Phase.OVER) {
            g.drawImage(sprites.keyboardHint(game.phase() == Game.Phase.PUSH), 18, Layout.HEIGHT - 36, null);
        }
    }

    private void paintTurnPanel(Graphics2D g) {
        g.drawImage(sprites.turnPanel(game.currentPlayer()), Layout.TURN_PANEL.x, Layout.TURN_PANEL.y, null);
    }

    private void paintToast(Graphics2D g) {
        if (toast == null) {
            return;
        }
        int width = 700;
        int height = 120;
        int x = (Layout.WIDTH - width) / 2;
        int y = (Layout.HEIGHT - height) / 2;
        g.setColor(Palette.ORANGE);
        g.fillRoundRect(x, y, width, height, 20, 20);
        g.setColor(Palette.MINT);
        g.setStroke(new BasicStroke(5));
        g.drawRoundRect(x + 2, y + 2, width - 4, height - 4, 20, 20);

        g.setFont(new Font("Arial", Font.BOLD, 22));
        g.setColor(NAVY);
        FontMetrics metrics = g.getFontMetrics();
        g.drawString(toast, x + (width - metrics.stringWidth(toast)) / 2, y + (height - metrics.getHeight()) / 2 + metrics.getAscent());
    }
}
