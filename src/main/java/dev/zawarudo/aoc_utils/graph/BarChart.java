package dev.zawarudo.aoc_utils.graph;

import dev.zawarudo.aoc_utils.data.AdventDay;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class BarChart extends AdventOfCodeGraph {

    BarChart(int year, int leaderboardId, String sessionKey) {
        super(year, leaderboardId, sessionKey);
    }

    @Override
    protected BufferedImage generateChart(List<AdventDay> days) {
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
        Font font = loadFontFromFile(FONT_SIZE * 2);
        g2d.setFont(font);
        g2d.setPaint(Color.WHITE);

        String titleString = String.format("Advent of Code %d", year);
        FontMetrics metrics = g2d.getFontMetrics();

        int textWidth = metrics.stringWidth(titleString);
        int startX = (IMAGE_WIDTH - textWidth) / 2;

        int topArea = OFFSET_Y1 / 4 * 3;
        int startY = topArea / 2 - metrics.getHeight() / 2 + metrics.getAscent();

        g2d.drawString(titleString, startX, startY);
    }

    private void drawLegends(Graphics2D g2d, List<AdventDay> days) {
        Font font = loadFontFromFile(FONT_SIZE);
        g2d.setFont(font);
        g2d.setPaint(Color.WHITE);

        FontMetrics metrics = g2d.getFontMetrics(font);

        int legendY = OFFSET_Y1 - OFFSET_Y1 / 5 - metrics.getHeight() / 2 + metrics.getAscent();

        String leaderboardString = String.format("Leaderboard ID: %d", leaderboardId);
        int participants = days.get(0).goldCount() + days.get(0).silverCount() + days.get(0).grayCount();
        String participantsString = String.format("Participants: %d", participants);

        g2d.drawString(leaderboardString, IMAGE_WIDTH / 3 - metrics.stringWidth(leaderboardString) / 2, legendY);
        g2d.drawString(participantsString, IMAGE_WIDTH / 3 * 2 - metrics.stringWidth(participantsString) / 2, legendY);
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

        int daysY = IMAGE_HEIGHT - OFFSET_Y2 / 6 * 5 - metrics.getHeight() / 2 + metrics.getAscent();

        // Draw day numbers
        int count = 1;
        for (int i = x + colWidth / 2; i < x + graphWidth; i += colWidth) {
            g2d.drawString(String.valueOf(count), i - metrics.stringWidth(String.valueOf(count)) / 2, daysY);
            count++;
        }

        // Draw numbers of people
        count = 0;
        for (int i = y + graphHeight; i >= y; i -= rowHeight) {
            g2d.drawString(String.valueOf(count), x - 35 - metrics.stringWidth(String.valueOf(count)) / 2, i + 10);
            count += 10;
        }
    }

    private void drawAxisLabels(Graphics2D g2d, int graphWidth, int graphHeight) {
        Font font = loadFontFromFile(FONT_SIZE);
        g2d.setFont(font);
        g2d.setPaint(Color.WHITE);

        FontMetrics metrics = g2d.getFontMetrics();

        String text = "Day";
        int startX = graphWidth / 2 + OFFSET_X1 - metrics.stringWidth(text) / 2;
        int startY = IMAGE_HEIGHT - OFFSET_Y2 / 2 - metrics.getHeight() / 2 + metrics.getAscent();

        g2d.drawString(text, startX, startY);

        text = "People";
        startX = 40; // TODO: Don't use magic numbers
        startY = graphHeight / 2 + OFFSET_Y1 + metrics.stringWidth(text) / 2;

        g2d.setFont(rotateFont(font, -90));
        g2d.drawString(text, startX, startY);

        g2d.setFont(font);
        drawLegendSquares(g2d, graphWidth);
    }

    private void drawLegendSquares(Graphics2D g2d, int graphWidth) {
        FontMetrics metrics = g2d.getFontMetrics();

        int lineHeight = IMAGE_HEIGHT - OFFSET_Y2 / 5;
        int textHeight = lineHeight - metrics.getHeight() / 2 + metrics.getAscent();

        int squareSize = (int) FONT_SIZE;
        int squareHeight = lineHeight - squareSize / 2;

        int startX = graphWidth / 6 + OFFSET_X1;
        int separation = graphWidth / 3;

        String text = "Two Stars";
        int elementWidth = (2 * squareSize + metrics.stringWidth(text)); // Width of square + text

        int positionX = startX - elementWidth / 2; // Position of square + text

        g2d.setColor(BOTH_STARS_COLOR);
        g2d.fillRect(positionX, squareHeight, squareSize, squareSize);
        g2d.drawString(text, positionX + 2 * squareSize, textHeight);

        text = "One Star";
        elementWidth = (2 * squareSize + metrics.stringWidth(text));

        positionX = startX + separation - elementWidth / 2;

        g2d.setColor(ONE_STAR_COLOR);
        g2d.fillRect(positionX, squareHeight, squareSize, squareSize);
        g2d.drawString(text, positionX + 2 * squareSize, textHeight);

        text = "No Star";
        elementWidth = (2 * squareSize + metrics.stringWidth(text));

        positionX = startX + 2 * separation - elementWidth / 2;

        g2d.setColor(NO_STARS_COLOR);
        g2d.fillRect(positionX, squareHeight, squareSize, squareSize);
        g2d.drawString(text, positionX + 2 * squareSize, textHeight);
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
}