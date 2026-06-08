package org.openkawu.jfxium.core.theme;

import java.util.Objects;

/**
 * JFXium MUI Compact Theme.
 * Material Design inspired theme with compact density.
 */
public class MuiCompactTheme implements Theme {

    @Override
    public String getName() {
        return "JFXium MUI Compact";
    }

    @Override
    public String getUserAgentStylesheet() {
        return Objects.requireNonNull(
            getClass().getResource("/org/openkawu/jfxium/css/theme-mui-compact.css")
        ).toExternalForm();
    }

    @Override
    public ThemeType getType() {
        return ThemeType.LIGHT;
    }

    /**
     * Get the density mode for this theme.
     */
    @Override
    public ThemeDensity getDensity() {
        return ThemeDensity.COMPACT;
    }
}
