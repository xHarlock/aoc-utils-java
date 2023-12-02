package dev.zawarudo.aoc_utils.graph;

import dev.zawarudo.aoc_utils.data.AdventDay;
import dev.zawarudo.aoc_utils.data.AdventOfCodeAPI;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.List;

public final class AdventOfCodeGraph {

    private static final Color GOLD = Color.decode("#FFFF66");
    private static final Color SILVER = Color.decode("#9999CC");
    private static final Color GREY = Color.decode("#333333");

    private static final int MAX_PARTICIPANTS = 200;
    private static final int MAX_DAYS = 25;
    private static final int GROUP_SIZE = MAX_PARTICIPANTS / 10;

    /** Width of the whole image. */
    private static final int IMAGE_WIDTH = 1500;
    /** Height of the whole image. */
    private static final int IMAGE_HEIGHT = 1150;
    /** Distance from the top edge. */
    private static final int OFFSET_X1 = 125;
    /** Distance from the bottom edge. */
    private static final int OFFSET_X2 = 50;
    /** Distance from the left edge. */
    private static final int OFFSET_Y1 = 100;
    /** Distance from the right edge. */
    private static final int OFFSET_Y2 = 150;
    private static final String FONT_NAME = "Comic Sans MS";
    private static final int FONT_SIZE = 25;

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
        drawBars(g2d, days, graphWidth, graphHeight);

        drawLegends(g2d, graphWidth, days);
        drawAxisLabels(g2d);

        g2d.dispose();
        return chart;
    }

    private void drawLegends(Graphics2D g2d, int graphWidth, List<AdventDay> days) {
        Font font = new Font(FONT_NAME, Font.BOLD, FONT_SIZE);
        FontMetrics metrics = g2d.getFontMetrics(font);

        g2d.setPaint(Color.WHITE);
        int legendY = OFFSET_Y1 / 2 + metrics.getHeight() / 4;

        g2d.drawString("Leaderboard ID: " + leaderboardId, graphWidth / 4, legendY);
        int participants = days.get(0).goldCount() + days.get(0).silverCount() + days.get(0).grayCount();
        g2d.drawString("Participants: " + participants, graphWidth / 4 * 3, legendY);
    }

    private void drawAxisLabels(Graphics2D g2d) {
        Font font = new Font(FONT_NAME, Font.BOLD, FONT_SIZE);
        g2d.setFont(font);
        g2d.setPaint(Color.WHITE);
        g2d.drawString("Day", IMAGE_WIDTH / 2, IMAGE_HEIGHT - (OFFSET_Y2 / 2));

        g2d.setFont(rotateFont(font, -90));
        g2d.drawString("People", 40, IMAGE_HEIGHT / 2);

        drawLegendSquares(g2d, font);
    }

    private void drawLegendSquares(Graphics2D g2d, Font font) {
        g2d.setFont(font);

        int lineHeight = IMAGE_HEIGHT - (OFFSET_Y2 / 6);
        int squareSize = 20;
        int squareHeight = lineHeight - squareSize;

        int gap = IMAGE_WIDTH / 20;
        int x = 5 * gap;

        g2d.setColor(GOLD);
        g2d.fillRect(x, squareHeight, squareSize, squareSize);
        g2d.drawString("Two Stars", x + 2 * squareSize, lineHeight);

        x += 4 * gap;

        g2d.setColor(SILVER);
        g2d.fillRect(x, squareHeight, squareSize, squareSize);
        g2d.drawString("One Star", x + 2 * squareSize, lineHeight);

        x += 4 * gap;

        g2d.setColor(GREY);
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
        Font font = new Font("Comic Sans MS", Font.BOLD, 25);
        g2d.setPaint(Color.WHITE);
        g2d.setFont(font);

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

    private Font rotateFont(Font font, double angDeg) {
        AffineTransform affineTransform = new AffineTransform();
        affineTransform.rotate(Math.toRadians(angDeg), 0, 0);
        return font.deriveFont(affineTransform);
    }

    private void drawBars(Graphics2D g2d, List<AdventDay> days, int graphWidth, int graphHeight) {
        int colWidth = graphWidth / 25;

        int i = OFFSET_X1;
        int thickness = colWidth / 5;

        for (AdventDay day : days) {
            int currX = i + thickness + thickness / 4;

            // Two stars
            g2d.setPaint(GOLD);
            int heightBar = calculateBarHeight(graphHeight, day.goldCount());
            g2d.fillRect(currX, OFFSET_Y1 + (graphHeight - heightBar), thickness, heightBar);

            currX += thickness;

            // One star
            g2d.setPaint(SILVER);
            heightBar = calculateBarHeight(graphHeight, day.silverCount());
            g2d.fillRect(currX, OFFSET_Y1 + (graphHeight - heightBar), thickness, heightBar);

            currX += thickness;

            // No stars
            g2d.setPaint(GREY);
            heightBar = calculateBarHeight(graphHeight, day.grayCount());
            g2d.fillRect(currX, OFFSET_Y1 + (graphHeight - heightBar), thickness, heightBar);

            i += graphWidth / 25;
        }
    }

    private int calculateBarHeight(int maxHeight, int count) {
        return maxHeight * count / MAX_PARTICIPANTS;
    }
}