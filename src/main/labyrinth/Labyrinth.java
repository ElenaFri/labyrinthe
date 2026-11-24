package labyrinth;

import labyrinth.view.GameWindow;

import javax.swing.SwingUtilities;

/**
 * Implements the classical Labyrinth game.
 * See here for details: <a href="https://www.regledujeu.fr/labyrinthe/">...</a>.
 *
 * @author Yasmine Chettati
 * @author Elena Frison
 */
public class Labyrinth {
    /**
     * Plays the Labyrinth game.
     * <p>
     * When the system property <code>labyrinth.web</code> is true (the game runs in a web page), the game does not
     * play its own music nor takes the full screen: the page does it.
     */
    public static void main(String[] args) {
        boolean web = Boolean.getBoolean("labyrinth.web");
        SwingUtilities.invokeLater(() -> new GameWindow(web));
        if (!web) {
            MusicPlayer.playInBackground("res/snd/la_moldau.wav");
        }
    }
}
