package labyrinth.view;

import labyrinth.Resources;
import labyrinth.model.Board;
import labyrinth.model.Tile;
import labyrinth.model.TileType;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * All the pictures of the game, loaded once and kept in the size in which they are drawn, so that drawing a
 * frame never has to read, merge, rotate or scale an image.
 */
final class Images {
    private static final String IMG = "res/img/";
    private static final int RAW_CARD_WIDTH = 150;
    private static final int RAW_CARD_HEIGHT = 237;
    private static final int RAW_TREASURE_SIZE = 128;

    private final BufferedImage background;
    private final BufferedImage board;
    private final BufferedImage[] tileBases = new BufferedImage[TileType.values().length];
    private final BufferedImage[] tileTreasures = new BufferedImage[Board.TREASURE_COUNT];
    private final BufferedImage[] cardTreasures = new BufferedImage[Board.TREASURE_COUNT];
    private final BufferedImage[] tiles = new BufferedImage[TileType.values().length * 4 * (Board.TREASURE_COUNT + 1)];
    private final BufferedImage[] cards = new BufferedImage[Board.TREASURE_COUNT];
    private final BufferedImage[] portraits = new BufferedImage[4];
    private final BufferedImage[] pieces = new BufferedImage[4];
    private final Map<Integer, BufferedImage> portraitsBySize = new HashMap<>();
    private final Map<Integer, BufferedImage> piecesBySize = new HashMap<>();
    private final BufferedImage cardFace;

    /**
     * Loads the pictures which are always needed.
     */
    Images() {
        background = scale(Resources.image(IMG + "screens/main_screen.jpg"), Layout.WIDTH, Layout.HEIGHT);
        board = scale(Resources.image(IMG + "screens/gameboard.png"), Layout.BOARD_SIZE, Layout.BOARD_SIZE);
        for (TileType type : TileType.values()) {
            tileBases[type.ordinal()] = scale(Resources.image(IMG + "tiles/tile_" + tileFileIndex(type) + ".png"),
                    Layout.TILE_SIZE, Layout.TILE_SIZE);
        }
        String[] colours = {"red", "green", "blue", "yellow"};
        for (int i = 0; i < portraits.length; i++) {
            portraits[i] = Resources.image(IMG + "players/" + colours[i] + "player.png");
            pieces[i] = Resources.image(IMG + "pieces/piece_" + i + ".png");
        }
        cardFace = scale(Resources.image(IMG + "cards/cardFront.png"), Layout.CARD_WIDTH, Layout.CARD_HEIGHT);
    }

    /**
     * @return the wooden table, in the size of the game
     */
    BufferedImage background() {
        return background;
    }

    /**
     * @return the board, in its drawing size
     */
    BufferedImage board() {
        return board;
    }

    /**
     * @param tile : a tile
     * @return the picture of this tile, with its treasure if any, turned according to its orientation,
     * in the size of the tiles of the board
     */
    BufferedImage tile(Tile tile) {
        int key = tileKey(tile);
        BufferedImage image = tiles[key];
        if (image == null) {
            image = composeTile(tile);
            tiles[key] = image;
        }
        return image;
    }

    /**
     * @param tile : a tile
     * @return a number which is different for each look of a tile (shape, orientation, treasure)
     */
    static int tileKey(Tile tile) {
        return (tile.type().ordinal() * 4 + tile.orientation()) * (Board.TREASURE_COUNT + 1) + tile.treasure() + 1;
    }

    /**
     * @param player : the index of a player
     * @param size   : the width and height of the picture to get
     * @return the portrait of the player
     */
    BufferedImage portrait(int player, int size) {
        return portraitsBySize.computeIfAbsent(size * 10 + player, k -> scale(portraits[player], size, size));
    }

    /**
     * @param player : the index of a player
     * @param size   : the width and height of the picture to get
     * @return the pawn of the player
     */
    BufferedImage piece(int player, int size) {
        return piecesBySize.computeIfAbsent(size * 10 + player, k -> scale(pieces[player], size, size));
    }

    /**
     * @return a card showing nothing: the cards of a hand but the first one are partly hidden by the next
     * one, so that they do not need to show their treasure
     */
    BufferedImage blankCard() {
        return cardFace;
    }

    /**
     * @param treasure : the number of a treasure
     * @return the card of this treasure, in the size of the cards of a hand
     */
    BufferedImage card(int treasure) {
        BufferedImage image = cards[treasure];
        if (image == null) {
            if (cardTreasures[treasure] == null) {
                // The picture of the treasure is 128x128 on a 150x237 card: same proportions on the card of a hand.
                cardTreasures[treasure] = scale(rawTreasure(treasure),
                        Math.round(RAW_TREASURE_SIZE * (float) Layout.CARD_WIDTH / RAW_CARD_WIDTH),
                        Math.round(RAW_TREASURE_SIZE * (float) Layout.CARD_HEIGHT / RAW_CARD_HEIGHT));
            }
            BufferedImage symbol = cardTreasures[treasure];
            image = new BufferedImage(cardFace.getWidth(), cardFace.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = image.createGraphics();
            g.drawImage(cardFace, 0, 0, null);
            g.drawImage(symbol, (image.getWidth() - symbol.getWidth()) / 2, (image.getHeight() - symbol.getHeight()) / 2, null);
            g.dispose();
            cards[treasure] = image;
        }
        return image;
    }

    private BufferedImage rawTreasure(int treasure) {
        return Resources.image(IMG + "treasures/treasure" + treasure + ".png");
    }

    private BufferedImage composeTile(Tile tile) {
        int size = Layout.TILE_SIZE;
        BufferedImage composed = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = composed.createGraphics();
        g.setTransform(AffineTransform.getQuadrantRotateInstance(tile.orientation(), size / 2.0, size / 2.0));
        g.drawImage(tileBases[tile.type().ordinal()], 0, 0, null);
        if (tile.hasTreasure()) {
            if (tileTreasures[tile.treasure()] == null) {
                // The picture of the treasure is 128x128 on a 160x160 tile, which is drawn in 128x128.
                tileTreasures[tile.treasure()] = scale(rawTreasure(tile.treasure()), 102, 102);
            }
            BufferedImage symbol = tileTreasures[tile.treasure()];
            g.drawImage(symbol, (size - symbol.getWidth()) / 2, (size - symbol.getHeight()) / 2, null);
        }
        g.dispose();
        return composed;
    }

    /**
     * The file of the picture of a shape of tile.
     */
    private static int tileFileIndex(TileType type) {
        return switch (type) {
            case ANGLED -> 0;
            case STRAIGHT -> 1;
            case T_SHAPED -> 2;
        };
    }

    /**
     * Scales an image with a good quality, by halving it as long as possible before the last step.
     */
    private static BufferedImage scale(BufferedImage source, int width, int height) {
        BufferedImage current = source;
        while (current.getWidth() / 2 >= width && current.getHeight() / 2 >= height) {
            current = resize(current, current.getWidth() / 2, current.getHeight() / 2);
        }
        return current.getWidth() == width && current.getHeight() == height ? current : resize(current, width, height);
    }

    private static BufferedImage resize(BufferedImage source, int width, int height) {
        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = resized.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.drawImage(source, 0, 0, width, height, null);
        g.dispose();
        return resized;
    }
}
