package dev.zawarudo.aoc_utils.graph;

import dev.zawarudo.aoc_utils.data.AdventDay;
import dev.zawarudo.aoc_utils.data.AdventOfCodeAPI;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public abstract class AdventOfCodeGraph {

    protected static final Color BOTH_STARS_COLOR = Color.decode("#FFFF66");
    protected static final Color ONE_STAR_COLOR = Color.decode("#9999CC");
    protected static final Color NO_STARS_COLOR = Color.decode("#ff8080");

    protected static final int MAX_PARTICIPANTS = 200;
    protected static final int MAX_DAYS = 25;
    protected static final int ROWS = MAX_PARTICIPANTS / 10;

    /** Width of the whole image. */
    protected static final int IMAGE_WIDTH = 1500;
    /** Height of the whole image. */
    protected static final int IMAGE_HEIGHT = 1200;
    /** Distance from the left edge. */
    protected static final int OFFSET_X1 = 150;
    /** Distance from the right edge. */
    protected static final int OFFSET_X2 = 50;
    /** Distance from the top edge. */
    protected static final int OFFSET_Y1 = 200;
    /** Distance from the bottom edge. */
    protected static final int OFFSET_Y2 = 150;

    protected static final float FONT_SIZE = 25f;

    protected int year;
    protected int leaderboardId;
    protected String sessionKey;

    protected Color background;

    protected AdventOfCodeGraph(int year, int leaderboardId, String sessionKey) {
        this.background = Color.WHITE;
        this.year = year;
        this.leaderboardId = leaderboardId;
        this.sessionKey = sessionKey;
    }

    public static AdventOfCodeGraph createGraph(ChartType type, int year, int leaderboardId, String sessionKey) {
        return GraphFactory.createGraph(type, year, leaderboardId, sessionKey);
    }

    /**
     * Sets the background color of the graph. The default background color is white.
     *
     * @param color The {@link Color} to set the background to.
     */
    public void setBackground(Color color) {
        background = color;
    }

    public BufferedImage generateImage() {
        BufferedImage result = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_ARGB_PRE);
        Graphics2D g2d = result.createGraphics();

        drawBackground(g2d, result);

        List<AdventDay> days = AdventOfCodeAPI.getAdventDays(year, leaderboardId, sessionKey);
        BufferedImage chart = generateChart(days);

        g2d.drawImage(chart, 0, 0, chart.getWidth(), chart.getHeight(), null);

        g2d.dispose();
        return result;
    }

    protected abstract BufferedImage generateChart(List<AdventDay> days);

    protected void drawBackground(Graphics2D g2d, BufferedImage image) {
        if (background != null) {
            g2d.setPaint(background);
            g2d.fillRect(0, 0, image.getWidth(), image.getHeight());
        }
    }

    protected void setSmoothFont(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }

    protected Font rotateFont(Font font, double ang) {
        AffineTransform affineTransform = new AffineTransform();
        affineTransform.rotate(Math.toRadians(ang), 0, 0);
        return font.deriveFont(affineTransform);
    }

    protected Font loadFontFromFile(float fontSize) {
        try (InputStream is = AdventOfCodeGraph.class.getResourceAsStream("/ComicSansBold.ttf")) {
            if (is == null) {
                throw new IllegalStateException("Font file not found at ./src/main/resources/ComicSansBold.ttf");
            }
            return Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(fontSize);
        } catch (IOException | FontFormatException e) {
            throw new IllegalStateException("Error loading font file!", e);
        }
    }
}