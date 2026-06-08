package org.openkawu.jfxium.core.theme;

import java.util.Objects;

/**
 * JFXium Dark Compact Theme.
 * Combines dark colors with compact spacing.
 */
public class DarkCompactTheme implements Theme {

    @Override
    public String getName() {
        return "JFXium Dark Compact";
    }

    @Override
    public String getUserAgentStylesheet() {
        return Objects.requireNonNull(
            getClass().getResource("/org/openkawu/jfxium/css/theme-dark-compact.css")
        ).toExternalForm();
    }

    @Override
    public ThemeType getType() {
        return ThemeType.DARK;
    }

    /**
     * Get the density mode for this theme.
     */
    @Override
    public ThemeDensity getDensity() {
        return ThemeDensity.COMPACT;
    }
}
