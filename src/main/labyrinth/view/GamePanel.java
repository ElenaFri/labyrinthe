package labyrinth.view;

import labyrinth.model.Direction;
import labyrinth.model.Game;
import labyrinth.model.GameListener;
import labyrinth.model.Player;
import labyrinth.model.Position;

import javax.swing.Timer;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.List;
import java.util.Objects;

/**
 * The main screen: shows a {@link Game} (drawn by a {@link GameRenderer}) and turns the mouse clicks and the keys
 * of the players into moves.
 */
@SuppressWarnings("serial")
final class GamePanel extends ScaledPanel implements GameListener {
    private static final int TOAST_DURATION_MS = 4000;

    private final Game game;
    private final GameRenderer renderer;
    private final Runnable onQuit;
    private final Timer toastTimer = new Timer(TOAST_DURATION_MS, e -> hideToast());
    private Point mouse;
    private Target hovered;
    private Target.Push selectedArrow;
    private String toast;

    /**
     * @param game   : the game to show; the panel keeps itself up to date when the game changes
     * @param images : the pictures of the game
     * @param onQuit : what to do when the player asks to quit
     */
    GamePanel(Game game, Images images, Runnable onQuit) {
        this.game = game;
        this.renderer = new GameRenderer(game, images);
        this.onQuit = onQuit;
        toastTimer.setRepeats(false);
        game.addListener(this);
        bindKeys();
    }

    // ---- The game changes

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

    // ---- The mouse

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
        activate(targetAt(point));
    }

    /**
     * @return what is under a point and can be clicked: a button, an arrow which can be used, or a tile to move to
     */
    private Target targetAt(Point point) {
        if (point == null) {
            return null;
        }
        if (Layout.EXIT_BUTTON.contains(point)) {
            return Target.Button.EXIT;
        }
        if (Layout.ROTATE_BUTTON.contains(point)) {
            return Target.Button.ROTATE;
        }
        for (Target.Push arrow : Layout.PUSH_ARROWS) {
            if (canUse(arrow) && Layout.arrowBounds(arrow).contains(point)) {
                return arrow;
            }
        }
        for (Position position : game.moveTargets()) {
            if (Layout.moveFrame(position).contains(point)) {
                return new Target.Move(position);
            }
        }
        return null;
    }

    private void updateHover() {
        Target target = targetAt(mouse);
        if (!Objects.equals(target, hovered)) {
            repaintTarget(hovered);
            hovered = target;
            repaintTarget(hovered);
            setCursor(Cursor.getPredefinedCursor(target == null ? Cursor.DEFAULT_CURSOR : Cursor.HAND_CURSOR));
        }
    }

    /**
     * What changes in the look of the screen when the mouse (or the keyboard selection) moves is limited to the frame
     * of a tile, or to an arrow and the row or column that it shows, so only this is redrawn.
     */
    private void repaintTarget(Target target) {
        if (target instanceof Target.Move move) {
            repaintCanvas(Layout.moveFrame(move.position()));
        } else if (target instanceof Target.Push arrow) {
            repaintCanvas(Layout.lineBounds(arrow).union(Layout.arrowBounds(arrow)));
        }
    }

    // ---- The keyboard

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
        } else {
            activate(selectedArrow);
        }
    }

    /**
     * Selects the next (or previous) arrow among the ones which can be used.
     */
    private void selectArrow(int step) {
        List<Target.Push> usable = Layout.PUSH_ARROWS.stream().filter(this::canUse).toList();
        if (usable.isEmpty()) {
            return;
        }
        int current = usable.indexOf(selectedArrow);
        int next = current < 0 ? (step > 0 ? 0 : usable.size() - 1) : Math.floorMod(current + step, usable.size());
        repaintTarget(selectedArrow);
        selectedArrow = usable.get(next);
        repaintTarget(selectedArrow);
    }

    // ---- Doing things

    /**
     * Does what a click on a target does.
     */
    private void activate(Target target) {
        if (target == Target.Button.EXIT) {
            onQuit.run();
        } else if (target == Target.Button.ROTATE) {
            game.rotateSpare();
        } else if (target instanceof Target.Push push) {
            game.push(push.direction(), push.index());
        } else if (target instanceof Target.Move move) {
            game.moveTo(move.position());
        }
    }

    private boolean canUse(Target.Push arrow) {
        return game.canPush(arrow.direction(), arrow.index());
    }

    @Override
    protected void paintCanvas(Graphics2D g) {
        renderer.paint(g, new GameRenderer.ViewState(hovered, selectedArrow, toast));
    }
}
