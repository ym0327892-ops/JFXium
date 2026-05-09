package org.openkawu.jfxium.core.theme;

import java.util.Objects;

/**
 * JFXium Dark Theme.
 * Inspired by AtlantaFX and Ant Design.
 */
public class DarkTheme implements Theme {

    @Override
    public String getName() {
        return "JFXium Dark";
    }

    @Override
    public String getUserAgentStylesheet() {
        return Objects.requireNonNull(
            getClass().getResource("/org/openkawu/jfxium/css/theme-dark.css")
        ).toExternalForm();
    }

    @Override
    public ThemeType getType() {
        return ThemeType.DARK;
    }
}
