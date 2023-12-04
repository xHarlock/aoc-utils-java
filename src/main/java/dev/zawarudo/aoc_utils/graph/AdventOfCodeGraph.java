package dev.zawarudo.aoc_utils.graph;

import dev.zawarudo.aoc_utils.data.AdventDay;
import dev.zawarudo.aoc_utils.data.AdventOfCodeAPI;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public final class AdventOfCodeGraph {

    private static final Color BOTH_STARS_COLOR = Color.decode("#FFFF66");
    private static final Color ONE_STAR_COLOR = Color.decode("#9999CC");
    private static final Color NO_STARS_COLOR = Color.decode("#ff8080");

    private static final int MAX_PARTICIPANTS = 200;
    private static final int MAX_DAYS = 25;
    private static final int GROUP_SIZE = MAX_PARTICIPANTS / 10;

    /** Width of the whole image. */
    private static final int IMAGE_WIDTH = 1500;
    /** Height of the whole image. */
    private static final int IMAGE_HEIGHT = 1200;
    /** Distance from the left edge. */
    private static final int OFFSET_X1 = 125;
    /** Distance from the right edge. */
    private static final int OFFSET_X2 = 50;
    /** Distance from the top edge. */
    private static final int OFFSET_Y1 = 200;
    /** Distance from the bottom edge. */
    private static final int OFFSET_Y2 = 150;

    private static final float FONT_SIZE = 25f;

    private Color background;
    private final int year;
    private final int leaderboardId;
    private final String sessionKey;

    /**
     * Creates a new graph of Advent of Code.
     *
     * @param year          The year of the Advent of Code.
     * @param leaderboardId The id of the Advent of Code leaderboard.
     */
    public AdventOfCodeGraph(int year, int leaderboardId, String sessionKey) {
        this.background = Color.WHITE;
        this.year = year;
        this.leaderboardId = leaderboardId;
        this.sessionKey = sessionKey;
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
        BufferedImage chart = generateBarChart(days);

        drawChartOnImage(g2d, chart);

        g2d.dispose();
        return result;
    }

    private void drawBackground(Graphics2D g2d, BufferedImage image) {
        if (background != null) {
            g2d.setPaint(background);
            g2d.fillRect(0, 0, image.getWidth(), image.getHeight());
        }
    }

    private void drawChartOnImage(Graphics2D g2d, BufferedImage chart) {
        int x = (IMAGE_WIDTH - chart.getWidth()) / 2;
        int y = (IMAGE_HEIGHT - chart.getHeight()) / 2;
        g2d.drawImage(chart, x, y, chart.getWidth(), chart.getHeight(), null);
    }

    public BufferedImage generateBarChart(List<AdventDay> days) {
        BufferedImage chart = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_ARGB_PRE);
        Graphics2D g2d = chart.createGraphics();

        setSmoothFont(g2d);

        int graphWidth = IMAGE_WIDTH - OFFSET_X1 - OFFSET_X2;
        int graphHeight = IMAGE_HEIGHT - OFFSET_Y1 - OFFSET_Y2;

        drawGrid(g2d, graphWidth, graphHeight);
        drawAxisValues(g2d, graphWidth, graphHeight);
        renderGraphBars(g2d, days, graphWidth, graphHeight);

        drawTitle(g2d);
        drawLegends(g2d, days);
        drawAxisLabels(g2d, graphWidth, graphHeight);

        g2d.dispose();
        return chart;
    }

    private void drawTitle(Graphics2D g2d) {
        Font font = loadFontFromFile(50);
        g2d.setFont(font);
        g2d.setPaint(Color.WHITE);

        String titleString = String.format("Advent of Code %d", year);
        FontMetrics metrics = g2d.getFontMetrics();

        int textWidth = metrics.stringWidth(titleString);

        int startX = (IMAGE_WIDTH - textWidth) / 2;
        int startY = OFFSET_Y1 / 2;

        g2d.drawString(titleString, startX, startY);
    }

    private void drawLegends(Graphics2D g2d, List<AdventDay> days) {
        Font font = loadFontFromFile(FONT_SIZE);
        g2d.setFont(font);
        g2d.setPaint(Color.WHITE);

        FontMetrics metrics = g2d.getFontMetrics(font);

        int legendY = OFFSET_Y1 / 4 * 3 + metrics.getHeight() / 4;

        String leaderboardString = String.format("Leaderboard ID: %d", leaderboardId);
        int participants = days.get(0).goldCount() + days.get(0).silverCount() + days.get(0).grayCount();
        String participantsString = String.format("Participants: %d", participants);

        g2d.drawString(leaderboardString, IMAGE_WIDTH / 3 - metrics.stringWidth(leaderboardString) / 2, legendY);
        g2d.drawString(participantsString, IMAGE_WIDTH / 3 * 2 - metrics.stringWidth(participantsString) / 2, legendY);
    }

    private void drawAxisLabels(Graphics2D g2d, int graphWidth, int graphHeight) {
        Font font = loadFontFromFile(FONT_SIZE);
        g2d.setFont(font);
        g2d.setPaint(Color.WHITE);

        FontMetrics metrics = g2d.getFontMetrics();

        String text = "Day";
        int startX = graphWidth / 2 + OFFSET_X1 - metrics.stringWidth(text) / 2;
        int startY = IMAGE_HEIGHT - (OFFSET_Y2 / 2);

        g2d.drawString(text, startX, startY);

        // TODO: Accurate calculation of position
        text = "People";
        startX = 40;
        startY = graphHeight / 2 + OFFSET_Y1 + metrics.stringWidth(text) / 2;

        g2d.setFont(rotateFont(font));
        g2d.drawString(text, startX, startY);

        drawLegendSquares(g2d, font);
    }

    private void drawLegendSquares(Graphics2D g2d, Font font) {
        g2d.setFont(font);

        int lineHeight = IMAGE_HEIGHT - (OFFSET_Y2 / 6);
        int squareSize = 20;
        int squareHeight = lineHeight - squareSize;

        int gap = IMAGE_WIDTH / 20;
        int x = 5 * gap;

        g2d.setColor(BOTH_STARS_COLOR);
        g2d.fillRect(x, squareHeight, squareSize, squareSize);
        g2d.drawString("Two Stars", x + 2 * squareSize, lineHeight);

        x += 4 * gap;

        g2d.setColor(ONE_STAR_COLOR);
        g2d.fillRect(x, squareHeight, squareSize, squareSize);
        g2d.drawString("One Star", x + 2 * squareSize, lineHeight);

        x += 4 * gap;

        g2d.setColor(NO_STARS_COLOR);
        g2d.fillRect(x, squareHeight, squareSize, squareSize);
        g2d.drawString("No Star", x + 2 * squareSize, lineHeight);
    }

    private void drawGrid(Graphics2D g2d, int graphWidth, int graphHeight) {
        g2d.setPaint(Color.WHITE);

        int x = OFFSET_X1;
        int y = OFFSET_Y1;

        // Consider rounding errors
        x += graphWidth % MAX_DAYS;
        graphWidth -= graphWidth % MAX_DAYS;
        y += graphHeight % GROUP_SIZE;
        graphHeight -= graphHeight % GROUP_SIZE;

        int colWidth = graphWidth / MAX_DAYS;
        int rowHeight = graphHeight / GROUP_SIZE;

        g2d.drawRect(x, y, graphWidth, graphHeight);

        int counter = 0;
        while (counter < GROUP_SIZE) {
            int currentRowY = y + rowHeight * counter;
            g2d.drawLine(x, currentRowY, x + graphWidth, currentRowY);
            counter++;
        }

        counter = 0;
        while (counter < MAX_DAYS) {
            int currentColX = x + colWidth * counter;
            g2d.drawLine(currentColX, y, currentColX, y + graphHeight);
            counter++;
        }
    }

    private void drawAxisValues(Graphics2D g2d, int graphWidth, int graphHeight) {
        Font font = loadFontFromFile(FONT_SIZE);
        g2d.setFont(font);

        g2d.setPaint(Color.WHITE);

        FontMetrics metrics = g2d.getFontMetrics(font);

        int colWidth = graphWidth / MAX_DAYS;
        int rowHeight = graphHeight / GROUP_SIZE;

        int x = OFFSET_X1;
        int y = OFFSET_Y1;

        // Draw day numbers
        int count = 1;
        for (int i = x + colWidth / 2; i < x + graphWidth; i += colWidth) {
            g2d.drawString(String.valueOf(count), i - metrics.stringWidth(String.valueOf(count)) / 2, y + graphHeight + 35);
            count++;
        }

        // Draw numbers of people
        count = 0;
        for (int i = y + graphHeight; i >= y; i -= rowHeight) {
            g2d.drawString(String.valueOf(count), x - 35 - metrics.stringWidth(String.valueOf(count)) / 2, i + 10);
            count += 10;
        }
    }

    private void setSmoothFont(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }

    private Font rotateFont(Font font) {
        AffineTransform affineTransform = new AffineTransform();
        affineTransform.rotate(Math.toRadians(-90), 0, 0);
        return font.deriveFont(affineTransform);
    }

    /** Draws the bars for each AdventDay. */
    private void renderGraphBars(Graphics2D g2d, List<AdventDay> days, int graphWidth, int graphHeight) {
        int colWidth = graphWidth / 25;
        int thickness = colWidth / 5;

        int currentX = OFFSET_X1;

        for (AdventDay day : days) {
            renderStarCountBars(g2d, day, currentX, graphHeight, thickness);
            currentX += colWidth;
        }
    }

    /** Draws bars for two stars, one star and no star of the given day. */
    private void renderStarCountBars(Graphics2D g2d, AdventDay day, int startX, int graphHeight, int thickness) {
        int xPosition = startX + thickness + thickness / 4;

        renderSingleBar(g2d, BOTH_STARS_COLOR, day.goldCount(), xPosition, graphHeight, thickness);
        xPosition += thickness;

        renderSingleBar(g2d, ONE_STAR_COLOR, day.silverCount(), xPosition, graphHeight, thickness);
        xPosition += thickness;

        renderSingleBar(g2d, NO_STARS_COLOR, day.grayCount(), xPosition, graphHeight, thickness);
    }

    /** Draws a single bar on the graph of the given color and given properties. */
    private void renderSingleBar(Graphics2D g2d, Paint color, int count, int x, int graphHeight, int thickness) {
        g2d.setPaint(color);
        int heightBar = graphHeight * count / MAX_PARTICIPANTS;
        g2d.fillRect(x, OFFSET_Y1 + (graphHeight - heightBar), thickness, heightBar);
    }

    private Font loadFontFromFile(float fontSize) {
        try {
            Font font = Font.createFont(Font.TRUETYPE_FONT, new File("./src/main/resources/ComicSansBold.ttf"));
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(font);
            return font.deriveFont(fontSize);
        } catch (FontFormatException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}