package dev.zawarudo.graph;

import dev.zawarudo.data.AdventDay;
import dev.zawarudo.data.AdventOfCodeAPI;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.List;

public final class AdventOfCodeGraph {

    private static final Color GOLD = new Color(255, 215, 0);
    private static final Color SILVER = new Color(205, 205, 205);
    private static final Color RED = new Color(255, 132, 132);

    private static final int MAX_PARTICIPANTS = 200;
    private static final int MAX_DAYS = 25;

    // TODO: Adjust to participant count of given leaderboard
    private static final int GROUP_SIZE = 20;

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

    public BufferedImage generateGraph() {
        BufferedImage result = new BufferedImage(1500, 1150, BufferedImage.TYPE_INT_ARGB_PRE);
        Graphics2D g2d = result.createGraphics();

        // Draw background
        if (background != null) {
            g2d.setPaint(background);
            g2d.fillRect(0, 0, result.getWidth(), result.getHeight());
        }

        // Generate chart
        List<AdventDay> days = AdventOfCodeAPI.getAdventDays(year, leaderboardId, sessionKey);
        BufferedImage chart = generateBarChart(days, 1500, 1150);

        // Put the whole graph together
        int x = (result.getWidth() - chart.getWidth()) / 2;
        int y = (result.getHeight() - chart.getHeight()) / 2;
        g2d.drawImage(chart, x, y, chart.getWidth(), chart.getHeight(), null);

        return result;
    }

    public BufferedImage generateBarChart(List<AdventDay> days, int width, int height) {
        BufferedImage chart = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB_PRE);
        Graphics2D g2d = chart.createGraphics();

        setSmoothFont(g2d);

        int offsetX1 = 125; // Distance from the left edge
        int offsetX2 = 50; // Distance from the right edge
        int offsetY1 = 100; // Distance from the top edge
        int offsetY2 = 150; // Distance from the bottom edge

        int graphWidth = width - offsetX1 - offsetX2;
        int graphHeight = height - offsetY1 - offsetY2;

        drawGrid(g2d, offsetX1, offsetY1, graphWidth, graphHeight);
        drawAxisValues(g2d, offsetX1, offsetY1, graphWidth, graphHeight);

        drawBars(g2d, days, offsetX1, offsetY1, graphWidth, graphHeight);

        Font font = new Font("Comic Sans MS", Font.BOLD, 25);
        FontMetrics metrics = g2d.getFontMetrics(font);

        g2d.setPaint(Color.WHITE);

        // Draw legends
        int legendY = offsetY1 / 2 + metrics.getHeight() / 4;

        g2d.drawString("Leaderboard ID: " + leaderboardId, graphWidth / 4, legendY);

        int participants = days.get(0).goldCount() + days.get(0).silverCount() + days.get(0).grayCount();
        g2d.drawString("Participants: " + participants, graphWidth / 4 * 3, legendY);

        // Draw axis labels
        g2d.setFont(font);
        g2d.drawString("Day", width / 2, height - (offsetY2 / 2));

        g2d.setFont(rotateFont(font, -90));
        g2d.drawString("People", 40, height / 2);

        g2d.setFont(font);

        int lineHeight = height - (offsetY2 / 6);
        int squareSize = 20;
        int squareHeight = lineHeight - squareSize;

        int gap = width / 20;
        int x = 5 * gap;

        g2d.setColor(GOLD);
        g2d.fillRect(x, squareHeight, squareSize, squareSize);
        g2d.drawString("Two Stars", x + 2 * squareSize, lineHeight);

        x += 4 * gap;

        g2d.setColor(SILVER);
        g2d.fillRect(x, squareHeight, squareSize, squareSize);
        g2d.drawString("One Star", x + 2 * squareSize, lineHeight);

        x += 4 * gap;

        g2d.setColor(RED);
        g2d.fillRect(x, squareHeight, squareSize, squareSize);
        g2d.drawString("No Star", x + 2 * squareSize, lineHeight);

        return chart;
    }

    private void drawGrid(Graphics2D g2d, int x, int y, int width, int height) {
        g2d.setPaint(Color.BLACK);

        // Consider rounding errors
        x += width % MAX_DAYS;
        width -= width % MAX_DAYS;
        y += height % GROUP_SIZE;
        height -= height % GROUP_SIZE;

        int colWidth = width / MAX_DAYS;
        int rowHeight = height / GROUP_SIZE;

        g2d.drawRect(x, y, width, height);

        int counter = 0;
        while (counter < GROUP_SIZE) {
            int currentRowY = y + rowHeight * counter;
            g2d.drawLine(x, currentRowY, x + width, currentRowY);
            counter++;
        }

        counter = 0;
        while (counter < MAX_DAYS) {
            int currentColX = x + colWidth * counter;
            g2d.drawLine(currentColX, y, currentColX, y + height);
            counter++;
        }
    }

    private void drawAxisValues(Graphics2D g2d, int x, int y, int width, int height) {
        // TODO: Make font size dynamic and adjust it on the chart
        Font font = new Font("Comic Sans MS", Font.BOLD, 25);
        g2d.setPaint(Color.WHITE);
        g2d.setFont(font);

        FontMetrics metrics = g2d.getFontMetrics(font);

        int colWidth = width / 25;
        int rowHeight = height / 20;

        // Day numbers
        int count = 1;
        for (int i = x + colWidth / 2; i < x + width; i += colWidth) {
            g2d.drawString(String.valueOf(count), i - metrics.stringWidth(String.valueOf(count)) / 2, y + height + 35);
            count++;
        }

        // Number of people
        count = 0;
        for (int i = y + height; i >= y; i -= rowHeight) {
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

    private void drawBars(Graphics2D g2d, List<AdventDay> days, int x, int y, int width, int height) {
        int colWidth = width / 25;

        int i = x;
        int thickness = colWidth / 5;

        for (AdventDay day : days) {
            int currX = i + thickness + thickness / 4;

            // Two stars
            g2d.setPaint(GOLD);
            int heightBar = calculateBarHeight(height, day.goldCount());
            g2d.fillRect(currX, y + (height - heightBar), thickness, heightBar);

            currX += thickness;

            // One star
            g2d.setPaint(SILVER);
            heightBar = calculateBarHeight(height, day.silverCount());
            g2d.fillRect(currX, y + (height - heightBar), thickness, heightBar);

            currX += thickness;

            // No stars
            g2d.setPaint(RED);
            heightBar = calculateBarHeight(height, day.grayCount());
            g2d.fillRect(currX, y + (height - heightBar), thickness, heightBar);

            i += width / 25;
        }
    }

    private int calculateBarHeight(int maxHeight, int count) {
        return maxHeight * count / MAX_PARTICIPANTS;
    }
}