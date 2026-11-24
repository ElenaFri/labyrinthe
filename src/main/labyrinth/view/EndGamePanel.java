package labyrinth.view;

import labyrinth.model.Player;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
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
final class EndGamePanel extends ScaledPanel {
    private static final int PORTRAIT_SIZE = 200;
    private static final int PORTRAIT_Y = 300;
    private static final Rectangle PANEL = new Rectangle(660, 250, 600, 530);
    private static final Color VEIL = new Color(0, 0, 40, 150);
    private static final Font MESSAGE_FONT = new Font("Arial", Font.BOLD, 30);

    private final Player winner;
    private final Images images;
    private final PanelButton newGameButton = new PanelButton(new Rectangle(860, 550, 200, 50), "Nouvelle Partie", 20, Palette.GOLD_LIGHT, Palette.GOLD_DARK, Palette.NAVY);
    private final PanelButton quitButton = new PanelButton(new Rectangle(860, 620, 200, 50), "Quitter", 20, Palette.GOLD_LIGHT, Palette.GOLD_DARK, Palette.NAVY);
    private final Runnable onNewGame;
    private final Runnable onQuit;

    /**
     * @param winner    : the player who has won
     * @param images    : the pictures of the game
     * @param onNewGame : what to do when the players want to play again
     * @param onQuit    : what to do when the players want to quit
     */
    EndGamePanel(Player winner, Images images, Runnable onNewGame, Runnable onQuit) {
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
        // The table is darkened so that the panel stands out.
        g.setColor(VEIL);
        g.fillRect(0, 0, Layout.WIDTH, Layout.HEIGHT);
        Sprites.paintGoldPanel(g, PANEL.x, PANEL.y, PANEL.width, PANEL.height, 40);

        int x = (Layout.WIDTH - PORTRAIT_SIZE) / 2;
        paintPortraitFrame(g, x, PORTRAIT_Y);
        g.setPaint(new TexturePaint(images.portrait(winner.id(), PORTRAIT_SIZE), new Rectangle2D.Double(x, PORTRAIT_Y, PORTRAIT_SIZE, PORTRAIT_SIZE)));
        g.fill(new RoundRectangle2D.Double(x, PORTRAIT_Y, PORTRAIT_SIZE, PORTRAIT_SIZE, 20, 20));

        newGameButton.paint(g);
        quitButton.paint(g);

        String message = "Félicitations " + winner.name() + " ! Tu as gagné !";
        g.setFont(MESSAGE_FONT);
        FontMetrics metrics = g.getFontMetrics();
        int textX = (Layout.WIDTH - metrics.stringWidth(message)) / 2;
        int textY = 700 + (50 - metrics.getHeight()) / 2 + metrics.getAscent();
        g.setColor(new Color(0, 0, 0, 170));
        g.drawString(message, textX + 2, textY + 2);
        g.setColor(Palette.GOLD_LIGHT);
        g.drawString(message, textX, textY);
    }

    /**
     * A gold frame with a soft glow around the portrait of the winner.
     */
    private static void paintPortraitFrame(Graphics2D g, int x, int y) {
        RoundRectangle2D frame = new RoundRectangle2D.Double(x - 4, y - 4, PORTRAIT_SIZE + 8, PORTRAIT_SIZE + 8, 26, 26);
        g.setColor(new Color(255, 220, 90, 55));
        g.setStroke(new BasicStroke(18));
        g.draw(frame);
        g.setColor(new Color(255, 220, 90, 110));
        g.setStroke(new BasicStroke(10));
        g.draw(frame);
        g.setPaint(new GradientPaint(0, y, Palette.GOLD_LIGHT, 0, y + PORTRAIT_SIZE, Palette.GOLD_DARK));
        g.setStroke(new BasicStroke(6));
        g.draw(frame);
    }
}
