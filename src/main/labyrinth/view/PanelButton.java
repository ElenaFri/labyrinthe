package labyrinth.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

/**
 * A button drawn on a {@link ScaledPanel}: a beige slab with a raised border and a label.
 */
final class PanelButton {
    private final Rectangle bounds;
    private final String label;
    private final Font font;
    private final Color background;
    private final Color foreground;

    PanelButton(Rectangle bounds, String label, int fontSize, Color background, Color foreground) {
        this.bounds = bounds;
        this.label = label;
        this.font = new Font("Arial", Font.BOLD, fontSize);
        this.background = background;
        this.foreground = foreground;
    }

    boolean contains(Point point) {
        return bounds.contains(point);
    }

    void paint(Graphics2D g) {
        g.setColor(background);
        g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);

        // Raised border: light on the top and left sides, dark on the bottom and right sides.
        Color light = Palette.BEIGE.brighter();
        Color dark = Palette.BEIGE.darker();
        int right = bounds.x + bounds.width;
        int bottom = bounds.y + bounds.height;
        g.setColor(light);
        g.fillRect(bounds.x, bounds.y, bounds.width, 2);
        g.fillRect(bounds.x, bounds.y, 2, bounds.height);
        g.setColor(dark);
        g.fillRect(bounds.x, bottom - 2, bounds.width, 2);
        g.fillRect(right - 2, bounds.y, 2, bounds.height);

        g.setFont(font);
        g.setColor(foreground);
        FontMetrics metrics = g.getFontMetrics();
        int x = bounds.x + (bounds.width - metrics.stringWidth(label)) / 2;
        int y = bounds.y + (bounds.height - metrics.getHeight()) / 2 + metrics.getAscent();
        g.drawString(label, x, y);
    }
}
