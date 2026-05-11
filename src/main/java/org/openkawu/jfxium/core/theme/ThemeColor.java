package org.openkawu.jfxium.core.theme;

import javafx.scene.paint.Color;

/**
 * Theme Color utility for dynamic color changes.
 * Allows runtime modification of the primary accent color.
 */
public class ThemeColor {

    /**
     * Predefined color presets inspired by popular design systems.
     */
    public enum Preset {
        BLUE("#1677ff", "Ant Design Blue"),
        PURPLE("#722ed1", "Ant Design Purple"),
        CYAN("#13c2c2", "Ant Design Cyan"),
        GREEN("#52c41a", "Ant Design Green"),
        MAGENTA("#eb2f96", "Ant Design Magenta"),
        RED("#f5222d", "Ant Design Red"),
        ORANGE("#fa8c16", "Ant Design Orange"),
        GOLD("#faad14", "Ant Design Gold"),
        LIME("#a0d911", "Ant Design Lime"),
        GEEKBLUE("#2f54eb", "Ant Design GeekBlue"),
        VOLCANO("#fa541c", "Ant Design Volcano");

        private final String hexColor;
        private final String displayName;

        Preset(String hexColor, String displayName) {
            this.hexColor = hexColor;
            this.displayName = displayName;
        }

        public String getHexColor() {
            return hexColor;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    private String hexColor;

    public ThemeColor(String hexColor) {
        this.hexColor = hexColor;
    }

    public ThemeColor(Preset preset) {
        this.hexColor = preset.getHexColor();
    }

    public String getHexColor() {
        return hexColor;
    }

    public void setHexColor(String hexColor) {
        this.hexColor = hexColor;
    }

    /**
     * Generate a color scale (0-9) from a base color.
     * Uses color interpolation to generate lighter/darker shades.
     */
    public String[] generateColorScale() {
        Color base = Color.web(hexColor);
        String[] scale = new String[10];

        // Generate lighter shades (0-4)
        for (int i = 0; i < 5; i++) {
            double factor = 1 - (i * 0.18);
            Color c = base.interpolate(Color.WHITE, 1 - factor);
            scale[i] = toHex(c);
        }

        // Base color at index 5
        scale[5] = hexColor;

        // Generate darker shades (6-9)
        for (int i = 6; i < 10; i++) {
            double factor = (i - 5) * 0.15;
            Color c = base.interpolate(Color.BLACK, factor);
            scale[i] = toHex(c);
        }

        return scale;
    }

    /**
     * Generate a color scale suitable for dark themes.
     * Dark themes need lighter accent colors for visibility.
     */
    public String[] generateDarkColorScale() {
        Color base = Color.web(hexColor);
        String[] scale = new String[10];

        // Dark themes: darker shades at 0-4, lighter at 5-9
        for (int i = 0; i < 5; i++) {
            double factor = (5 - i) * 0.15;
            Color c = base.interpolate(Color.BLACK, factor);
            scale[i] = toHex(c);
        }

        scale[5] = toHex(base.interpolate(Color.WHITE, 0.2));
        scale[6] = toHex(base.interpolate(Color.WHITE, 0.35));
        scale[7] = toHex(base.interpolate(Color.WHITE, 0.5));
        scale[8] = toHex(base.interpolate(Color.WHITE, 0.65));
        scale[9] = toHex(base.interpolate(Color.WHITE, 0.8));

        return scale;
    }

    private static String toHex(Color color) {
        return String.format("#%02x%02x%02x",
            (int) (color.getRed() * 255),
            (int) (color.getGreen() * 255),
            (int) (color.getBlue() * 255));
    }
}
