package labyrinth.view;

import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * A panel drawing a {@link Layout#WIDTH} x {@link Layout#HEIGHT} canvas, scaled to fit its size and centred.
 * Subclasses draw and handle the mouse in the coordinates of the canvas, whatever the size of the window.
 */
@SuppressWarnings("serial")
abstract class ScaledPanel extends JPanel {
    private double scale = 1;
    private int offsetX;
    private int offsetY;

    ScaledPanel() {
        setOpaque(true);
        setBackground(Color.BLACK);
        MouseAdapter mouse = new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                canvasMouseMoved(toCanvas(e.getPoint()));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                canvasMouseExited();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    canvasMousePressed(toCanvas(e.getPoint()));
                }
            }
        };
        addMouseListener(mouse);
        addMouseMotionListener(mouse);
    }

    /**
     * Makes a key trigger an action, whichever component has the focus in the window.
     *
     * @param key    : the key, as in {@link KeyStroke#getKeyStroke(String)} (for example "LEFT" or "ENTER")
     * @param action : what to do when the key is pressed
     */
    protected final void bindKey(String key, Runnable action) {
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(key), key);
        getActionMap().put(key, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                action.run();
            }
        });
    }

    /**
     * Draws the canvas.
     *
     * @param g : graphics in the coordinates of the canvas, already clipped to it
     */
    protected abstract void paintCanvas(Graphics2D g);

    /**
     * Draws what must keep its size and its place in the window, whatever the scale of the canvas (a line of
     * text, for example). It is drawn over the canvas.
     *
     * @param g      : graphics in the coordinates of the window
     * @param width  : width of the window
     * @param height : height of the window
     */
    protected void paintOverlay(Graphics2D g, int width, int height) {
    }

    /**
     * Called when the mouse moves.
     *
     * @param point : position of the mouse in the canvas
     */
    protected void canvasMouseMoved(Point point) {
    }

    /**
     * Called when the mouse leaves the panel.
     */
    protected void canvasMouseExited() {
    }

    /**
     * Called when the main button of the mouse is pressed.
     *
     * @param point : position of the mouse in the canvas
     */
    protected void canvasMousePressed(Point point) {
    }

    /**
     * Asks for the redraw of an area of the canvas only.
     *
     * @param area : the area to redraw, in the coordinates of the canvas
     */
    protected final void repaintCanvas(Rectangle area) {
        updateTransform();
        int x = offsetX + (int) Math.floor(area.x * scale);
        int y = offsetY + (int) Math.floor(area.y * scale);
        int right = offsetX + (int) Math.ceil((area.x + area.width) * scale);
        int bottom = offsetY + (int) Math.ceil((area.y + area.height) * scale);
        // One more pixel on each side, for the anti-aliasing.
        repaint(x - 1, y - 1, right - x + 2, bottom - y + 2);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        updateTransform();

        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.translate(offsetX, offsetY);
            g2.scale(scale, scale);
            g2.clipRect(0, 0, Layout.WIDTH, Layout.HEIGHT);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            paintCanvas(g2);
        } finally {
            g2.dispose();
        }

        Graphics2D overlay = (Graphics2D) g.create();
        try {
            overlay.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            paintOverlay(overlay, getWidth(), getHeight());
        } finally {
            overlay.dispose();
        }
    }

    private void updateTransform() {
        scale = Math.min((double) getWidth() / Layout.WIDTH, (double) getHeight() / Layout.HEIGHT);
        offsetX = (int) Math.round((getWidth() - Layout.WIDTH * scale) / 2);
        offsetY = (int) Math.round((getHeight() - Layout.HEIGHT * scale) / 2);
    }

    private Point toCanvas(Point point) {
        updateTransform();
        return new Point((int) Math.round((point.x - offsetX) / scale), (int) Math.round((point.y - offsetY) / scale));
    }
}
