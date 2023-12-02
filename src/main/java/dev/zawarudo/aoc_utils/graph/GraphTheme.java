package dev.zawarudo.aoc_utils.graph;

import com.google.gson.annotations.SerializedName;

import java.awt.*;

public class GraphTheme {

    @SerializedName("background")
    private Color background;

    @SerializedName("bar_gold")
    private Color gold;
    @SerializedName("bar_silver")
    private Color silver;
    @SerializedName("bar_grey")
    private Color grey;

    public Color getBackgroundColor() {
        return background;
    }

    public Color getGoldColor() {
        return gold;
    }

    public Color getSilverColor() {
        return silver;
    }

    public Color getGreyColor() {
        return grey;
    }
}