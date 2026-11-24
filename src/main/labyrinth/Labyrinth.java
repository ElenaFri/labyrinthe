package labyrinth;

import labyrinth.view.GameWindow;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * Implements the classical Labyrinth game.
 * See here for details: <a href="https://www.regledujeu.fr/labyrinthe/">...</a>.
 *
 * @author Yasmine Chettati
 * @author Elena Frison
 */
public final class Labyrinth {
    private Labyrinth() {
    }

    /**
     * Plays the Labyrinth game.
     * <p>
     * When the system property <code>labyrinth.web</code> is true (the game runs in a web page), the game does not
     * play its own music nor takes the full screen: the page does it.
     *
     * @param args : not used
     */
    public static void main(String[] args) {
        boolean web = Boolean.getBoolean("labyrinth.web");
        SwingUtilities.invokeLater(() -> start(web));
        if (!web) {
            MusicPlayer.playInBackground("res/snd/la_moldau.wav");
        }
    }

    private static void start(boolean web) {
        try {
            new GameWindow(web);
        } catch (RuntimeException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Impossible de démarrer le jeu : " + e.getMessage(), "Labyrinthe", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
}
