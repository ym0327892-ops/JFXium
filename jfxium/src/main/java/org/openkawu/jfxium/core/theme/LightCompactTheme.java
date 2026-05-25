package org.openkawu.jfxium.core.theme;

import java.util.Objects;

/**
 * JFXium Light Compact Theme.
 * Inspired by Ant Design Compact Algorithm.
 * Uses reduced spacing and smaller component sizes.
 */
public class LightCompactTheme implements Theme {

    @Override
    public String getName() {
        return "JFXium Light Compact";
    }

    @Override
    public String getUserAgentStylesheet() {
        return Objects.requireNonNull(
            getClass().getResource("/org/openkawu/jfxium/css/theme-light-compact.css")
        ).toExternalForm();
    }

    @Override
    public ThemeType getType() {
        return ThemeType.LIGHT;
    }

    /**
     * Get the density mode for this theme.
     */
    public ThemeDensity getDensity() {
        return ThemeDensity.COMPACT;
    }
}
