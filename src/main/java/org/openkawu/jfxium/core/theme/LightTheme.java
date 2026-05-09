package org.openkawu.jfxium.core.theme;

import java.util.Objects;

/**
 * JFXium Light Theme
 * Inspired by AtlantaFX and Ant Design
 */
public class LightTheme implements Theme {

    @Override
    public String getName() {
        return "JFXium Light";
    }

    @Override
    public String getUserAgentStylesheet() {
        return Objects.requireNonNull(
            getClass().getResource("/org/openkawu/jfxium/css/theme-light.css")
        ).toExternalForm();
    }

    @Override
    public ThemeType getType() {
        return ThemeType.LIGHT;
    }
}
