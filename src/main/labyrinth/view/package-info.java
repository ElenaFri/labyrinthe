/**
 * The graphical interface, in Swing: the window, the main screen and the end screen.
 * <p>
 * The screens draw a fixed 1920x1080 canvas which is scaled to the size of the window, so the layout is expressed
 * in a single place ({@code Layout}) whatever the screen. Pictures are loaded and prepared once ({@code Images},
 * {@code Sprites}) so that drawing a frame only copies them.
 */
package labyrinth.view;
