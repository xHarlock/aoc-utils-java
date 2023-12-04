package dev.zawarudo.aoc_utils.graph;

import dev.zawarudo.aoc_utils.data.AdventDay;

import java.awt.image.BufferedImage;
import java.util.List;

public class AreaChart extends AdventOfCodeGraph {

    AreaChart(int year, int leaderboardId, String sessionKey) {
        super(year, leaderboardId, sessionKey);
    }

    @Override
    protected BufferedImage generateChart(List<AdventDay> days) {
        return null;
    }
}