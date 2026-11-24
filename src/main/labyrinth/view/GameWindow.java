package labyrinth.view;

import labyrinth.Resources;
import labyrinth.model.Game;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;

/**
 * The window of the game. It shows the game being played, then the end screen, then a new game if the players
 * want to play again.
 */
public final class GameWindow {
    private static final int END_SCREEN_DELAY_MS = 1500;

    private final JFrame frame = new JFrame("Labyrinthe");
    private final Images images = new Images();

    /**
     * Opens the window and starts a game. It must be called on the Swing thread.
     *
     * @param embedded : true if the game runs in a web page, which already provides the full screen: the window
     *                 then has no decoration and just fills the display. Otherwise the window takes the full
     *                 screen if possible.
     */
    public GameWindow(boolean embedded) {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setIconImage(Resources.image("res/icon.png"));
        startNewGame();

        GraphicsDevice screen = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        if (!embedded && screen.isFullScreenSupported()) {
            frame.setUndecorated(true);
            frame.setVisible(true);
            screen.setFullScreenWindow(frame);
        } else {
            frame.setUndecorated(embedded);
            frame.setSize(Toolkit.getDefaultToolkit().getScreenSize());
            frame.setLocation(0, 0);
            frame.setVisible(true);
        }
    }

    private void startNewGame() {
        Game game = new Game();
        game.addListener(() -> {
            if (game.phase() == Game.Phase.OVER) {
                // Leave a moment to see the last move before announcing the winner.
                Timer timer = new Timer(END_SCREEN_DELAY_MS, e -> show(new EndGamePanel(game.winner(), images, this::startNewGame, GameWindow::quit)));
                timer.setRepeats(false);
                timer.start();
            }
        });
        show(new GamePanel(game, images, GameWindow::quit));
    }

    private void show(JPanel panel) {
        frame.setContentPane(panel);
        frame.revalidate();
        frame.repaint();
    }

    private static void quit() {
        System.exit(0);
    }
}
