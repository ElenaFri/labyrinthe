package labyrinth.view;

import labyrinth.model.Board;
import labyrinth.model.Direction;
import labyrinth.model.Player;
import labyrinth.model.Tile;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * Things which are drawn with shapes and text (shadows, frames, labels), drawn once in images. Drawing a frame
 * of the game then only means copying images, which is much cheaper than rasterizing anti-aliased shapes again
 * and again.
 */
final class Sprites {
    /**
     * Extra pixels around the tile in the image returned by {@link #spareTile(Tile)}.
     */
    static final int SPARE_MARGIN = 4;
    /**
     * Extra pixels around the tile in the image returned by {@link #pawn(int, boolean)}.
     */
    static final int PAWN_MARGIN = 2;

    private static final int PAWN_SIZE = 90;
    private static final int CURRENT_PAWN_SIZE = 130;
    private static final int TURN_PORTRAIT_SIZE = 176;
    private static final float DISABLED_ARROW_OPACITY = 0.35f;

    private final Images images;
    private final Map<Integer, BufferedImage> spareTiles = new HashMap<>();
    private final Map<Integer, BufferedImage> pawns = new HashMap<>();
    private final Map<Integer, BufferedImage> turnPanels = new HashMap<>();
    private final Map<Integer, BufferedImage> arrows = new HashMap<>();
    private final Map<Integer, BufferedImage> hints = new HashMap<>();

    Sprites(Images images) {
        this.images = images;
    }

    /**
     * @param tile : the spare tile
     * @return the tile with its shadow and its frame, to draw {@value #SPARE_MARGIN} pixels left of and above the
     * place of the tile
     */
    BufferedImage spareTile(Tile tile) {
        int key = (tile.type().ordinal() * 4 + tile.orientation()) * (Board.TREASURE_COUNT + 1) + tile.treasure() + 1;
        return spareTiles.computeIfAbsent(key, k -> {
            int size = Layout.TILE_SIZE;
            BufferedImage sprite = newImage(size + 2 * SPARE_MARGIN + 10, size + 2 * SPARE_MARGIN + 12);
            Graphics2D g = antialiased(sprite);
            g.setColor(Palette.SHADOW);
            g.fill(new RoundRectangle2D.Double(SPARE_MARGIN + 10, SPARE_MARGIN + 12, size, size, 20, 20));
            g.drawImage(images.tile(tile), SPARE_MARGIN, SPARE_MARGIN, null);
            g.setColor(Palette.NAVY);
            g.setStroke(new BasicStroke(4));
            g.draw(new RoundRectangle2D.Double(SPARE_MARGIN, SPARE_MARGIN, size, size, 50, 50));
            g.dispose();
            return sprite;
        });
    }

    /**
     * @param player  : the index of a player
     * @param current : whether it is the player who has to play: his pawn is bigger
     * @return the pawn and its shadow, to draw {@value #PAWN_MARGIN} pixels left of and above the tile
     */
    BufferedImage pawn(int player, boolean current) {
        return pawns.computeIfAbsent(player * 2 + (current ? 1 : 0), k -> {
            int size = Layout.TILE_SIZE;
            BufferedImage sprite = newImage(size + 2 * PAWN_MARGIN, size + 2 * PAWN_MARGIN);
            Graphics2D g = antialiased(sprite);
            g.setColor(Palette.SHADOW);
            g.fillOval(PAWN_MARGIN + 35, PAWN_MARGIN + 35, PAWN_SIZE - 22, PAWN_SIZE - 24);
            if (current) {
                int x = PAWN_MARGIN + (size - CURRENT_PAWN_SIZE) / 2;
                g.fillOval(x + 20, x + 20, CURRENT_PAWN_SIZE - 30, CURRENT_PAWN_SIZE - 32);
                g.drawImage(images.piece(player, CURRENT_PAWN_SIZE), x, x, null);
            } else {
                int x = PAWN_MARGIN + (size - PAWN_SIZE) / 2;
                g.drawImage(images.piece(player, PAWN_SIZE), x, x, null);
            }
            g.dispose();
            return sprite;
        });
    }

    /**
     * @param player : a player
     * @return the panel telling that it is the turn of this player, of the size {@link Layout#TURN_PANEL}
     */
    BufferedImage turnPanel(Player player) {
        return turnPanels.computeIfAbsent(player.id(), k -> {
            int width = Layout.TURN_PANEL.width;
            int height = Layout.TURN_PANEL.height;
            BufferedImage sprite = newImage(width, height);
            Graphics2D g = antialiased(sprite);
            g.setColor(Palette.BEIGE);
            g.fillRect(0, 0, width, height);
            g.setColor(Palette.PANEL_BORDER);
            g.setStroke(new BasicStroke(2));
            g.drawRect(1, 1, width - 2, height - 2);
            g.drawImage(images.portrait(player.id(), TURN_PORTRAIT_SIZE), (width - TURN_PORTRAIT_SIZE) / 2, 12, null);

            g.setFont(new Font("Arial", Font.PLAIN, 18));
            g.setColor(Palette.NAVY);
            FontMetrics metrics = g.getFontMetrics();
            String[] lines = {"C'est à toi de jouer,", player.name() + " !"};
            int y = 12 + TURN_PORTRAIT_SIZE + 12 + metrics.getAscent();
            for (String line : lines) {
                g.drawString(line, (width - metrics.stringWidth(line)) / 2, y);
                y += metrics.getHeight();
            }
            g.dispose();
            return sprite;
        });
    }

    /**
     * @param direction : a direction of push
     * @param enabled   : false for the faded arrow, shown when the push is not possible
     * @return the arrow, centred in an area of the size {@link Layout#ARROW_SIZE}
     */
    BufferedImage arrow(Direction direction, boolean enabled) {
        return arrows.computeIfAbsent(direction.ordinal() * 2 + (enabled ? 1 : 0), k -> {
            BufferedImage sprite = newImage(Layout.ARROW_SIZE, Layout.ARROW_SIZE);
            Graphics2D g = sprite.createGraphics();
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, enabled ? 1f : DISABLED_ARROW_OPACITY));
            int margin = (Layout.ARROW_SIZE - Layout.ARROW_ICON_SIZE) / 2;
            g.drawImage(images.arrow(direction), margin, margin, null);
            g.dispose();
            return sprite;
        });
    }

    /**
     * @param push : true to explain how to push the spare tile with the keyboard, false to explain how to move
     * @return a line of text telling which keys to use
     */
    BufferedImage keyboardHint(boolean push) {
        return hints.computeIfAbsent(push ? 1 : 0, k -> {
            String text = push
                    ? "Clavier : flèches = choisir où pousser · Entrée = pousser · R = tourner la tuile"
                    : "Clavier : flèches = déplacer le pion · Entrée = finir le tour";
            Font font = new Font("Arial", Font.PLAIN, 16);
            BufferedImage probe = newImage(1, 1);
            Graphics2D measure = probe.createGraphics();
            int width = measure.getFontMetrics(font).stringWidth(text) + 24;
            measure.dispose();

            BufferedImage sprite = newImage(width, 24);
            Graphics2D g = antialiased(sprite);
            g.setFont(font);
            g.setColor(new Color(0, 0, 0, 190));
            g.drawString(text, 3, 19);
            g.setColor(Color.WHITE);
            g.drawString(text, 2, 18);
            g.dispose();
            return sprite;
        });
    }

    private static BufferedImage newImage(int width, int height) {
        return new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    }

    private static Graphics2D antialiased(BufferedImage image) {
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        return g;
    }
}
