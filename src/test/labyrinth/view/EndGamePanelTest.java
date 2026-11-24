package labyrinth.view;

import labyrinth.model.Game;
import labyrinth.model.Player;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * Tests of the end screen. They must be run from the root of the project, where the pictures are.
 */
class EndGamePanelTest {
    private static Images images;

    private int newGames;
    private int quits;

    @BeforeAll
    static void loadImages() {
        System.setProperty("java.awt.headless", "true");
        images = new Images();
    }

    private EndGamePanel panel() {
        Player winner = new Game().players().get(2);
        EndGamePanel panel = new EndGamePanel(winner, images, () -> newGames++, () -> quits++);
        panel.setSize(Layout.WIDTH, Layout.HEIGHT);
        return panel;
    }

    private static void press(EndGamePanel panel, String key) {
        panel.getActionMap().get(key).actionPerformed(new ActionEvent(panel, ActionEvent.ACTION_PERFORMED, key));
    }

    private static void click(EndGamePanel panel, int x, int y) {
        panel.dispatchEvent(new MouseEvent(panel, MouseEvent.MOUSE_PRESSED, 0L, 0, x, y, 1, false, MouseEvent.BUTTON1));
    }

    @Test
    void drawsTheWinner() {
        EndGamePanel panel = panel();
        BufferedImage image = new BufferedImage(Layout.WIDTH, Layout.HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        panel.paint(g);
        g.dispose();
        // The portrait of the winner is in the middle of the table.
        assertNotEquals(image.getRGB(5, 5), image.getRGB(Layout.WIDTH / 2, 400));
    }

    @Test
    void enterStartsANewGameAndEscapeQuits() {
        EndGamePanel panel = panel();
        press(panel, "ENTER");
        assertEquals(1, newGames);
        press(panel, "ESCAPE");
        assertEquals(1, quits);
    }

    @Test
    void theButtonsWork() {
        EndGamePanel panel = panel();
        click(panel, 960, 575);   // "Nouvelle Partie"
        click(panel, 960, 645);   // "Quitter"
        click(panel, 100, 100);   // nothing
        assertEquals(1, newGames);
        assertEquals(1, quits);
    }
}
