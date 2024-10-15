package dev.expx.ctrlctr.center.util;

import net.kyori.adventure.text.TextComponent;

/**
 * Represents a progress bar.
 */
@SuppressWarnings("unused")
public class ProgressBar {

    /**
     * Utility class, do not instantiate.
     */
    private ProgressBar() {}

    /**
     * Get a progress bar.
     * @param current Current value
     * @param max Maximum value
     * @param totalBars Total bars
     * @param color Color
     * @return A {@link TextComponent} object.
     */
    public static TextComponent getProgressBar(int current, int max, int totalBars, String color) {
        float percent = (float) current / max;
        int progressBars = (int) (totalBars * percent);

        return TextUtil.translate(color + "▌".repeat(Math.max(0, progressBars)) + "&7" + "▌".repeat(Math.max(0, totalBars - progressBars)));
    }

}
