package labyrinth.view;

import labyrinth.model.Player;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

/**
 * The last screen: announces the winner, and proposes to play again or to quit.
 */
@SuppressWarnings("serial")
public final class EndGamePanel extends ScaledPanel {
    private static final int PORTRAIT_SIZE = 200;

    private final Player winner;
    private final Images images;
    private final PanelButton newGameButton = new PanelButton(new Rectangle(860, 550, 200, 50), "Nouvelle Partie", 20, Palette.BEIGE, Palette.NAVY);
    private final PanelButton quitButton = new PanelButton(new Rectangle(860, 620, 200, 50), "Quitter", 20, Palette.BEIGE, Palette.NAVY);
    private final Runnable onNewGame;
    private final Runnable onQuit;

    /**
     * @param winner    : the player who has won
     * @param images    : the pictures of the game
     * @param onNewGame : what to do when the players want to play again
     * @param onQuit    : what to do when the players want to quit
     */
    public EndGamePanel(Player winner, Images images, Runnable onNewGame, Runnable onQuit) {
        this.winner = winner;
        this.images = images;
        this.onNewGame = onNewGame;
        this.onQuit = onQuit;
        bindKey("ENTER", onNewGame);
        bindKey("ESCAPE", onQuit);
    }

    @Override
    protected void canvasMouseMoved(Point point) {
        boolean overButton = newGameButton.contains(point) || quitButton.contains(point);
        setCursor(Cursor.getPredefinedCursor(overButton ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
    }

    @Override
    protected void canvasMousePressed(Point point) {
        if (newGameButton.contains(point)) {
            onNewGame.run();
        } else if (quitButton.contains(point)) {
            onQuit.run();
        }
    }

    @Override
    protected void paintCanvas(Graphics2D g) {
        g.drawImage(images.background(), 0, 0, null);

        int x = (Layout.WIDTH - PORTRAIT_SIZE) / 2;
        g.setPaint(new TexturePaint(images.portrait(winner.id(), PORTRAIT_SIZE), new Rectangle2D.Double(x, 300, PORTRAIT_SIZE, PORTRAIT_SIZE)));
        g.fill(new RoundRectangle2D.Double(x, 300, PORTRAIT_SIZE, PORTRAIT_SIZE, 20, 20));

        newGameButton.paint(g);
        quitButton.paint(g);

        String message = "Félicitations " + winner.name() + " ! Tu as gagné !";
        g.setFont(new Font("Arial", Font.BOLD, 30));
        g.setColor(Color.WHITE);
        FontMetrics metrics = g.getFontMetrics();
        g.drawString(message, (Layout.WIDTH - metrics.stringWidth(message)) / 2, 700 + (50 - metrics.getHeight()) / 2 + metrics.getAscent());
    }
}
