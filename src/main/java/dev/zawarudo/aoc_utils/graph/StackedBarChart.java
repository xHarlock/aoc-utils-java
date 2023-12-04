package dev.zawarudo.aoc_utils.graph;

import dev.zawarudo.aoc_utils.data.AdventDay;

import java.awt.*;

public class StackedBarChart extends BarChart {

    StackedBarChart(int year, int leaderboardId, String sessionKey) {
        super(year, leaderboardId, sessionKey);
    }

    @Override
    protected void renderStarCountBars(Graphics2D g2d, AdventDay day, int startX, int graphHeight, int thickness) {
        int xPosition = startX + thickness + thickness / 4;
        int yPos = 0;
        thickness *= 3;

        yPos = renderSingleBar(g2d, BOTH_STARS_COLOR, day.goldCount(), xPosition, yPos, graphHeight, thickness);
        yPos = renderSingleBar(g2d, ONE_STAR_COLOR, day.silverCount(), xPosition, yPos, graphHeight, thickness);
        renderSingleBar(g2d, NO_STARS_COLOR, day.grayCount(), xPosition, yPos, graphHeight, thickness);
    }

    private int renderSingleBar(Graphics2D g2d, Paint color, int count, int x, int y, int graphHeight, int thickness) {
        g2d.setPaint(color);
        int heightBar = graphHeight * count / MAX_PARTICIPANTS;
        g2d.fillRect(x, OFFSET_Y1 + (graphHeight - heightBar - y), thickness, heightBar);
        y += heightBar;
        return y;
    }
}