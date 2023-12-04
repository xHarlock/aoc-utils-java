package dev.zawarudo.aoc_utils.graph;

public class GraphFactory {

    private GraphFactory() {
    }

    public static AdventOfCodeGraph createGraph(ChartType type, int year, int leaderboardId, String sessionKey) {
        return switch (type) {
            case BAR_CHART -> new BarChart(year, leaderboardId, sessionKey);
            case AREA_CHART -> new AreaChart(year, leaderboardId, sessionKey);
            case STACKED_BAR_CHART -> new StackedBarChart(year, leaderboardId, sessionKey);
        };
    }
}