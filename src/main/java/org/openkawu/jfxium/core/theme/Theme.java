package org.openkawu.jfxium.core.theme;

/**
 * JFXium Theme Interface
 * Inspired by AtlantaFX
 */
public interface Theme {

    /**
     * Get the theme name
     */
    String getName();

    /**
     * Get the path to the CSS user agent stylesheet
     */
    String getUserAgentStylesheet();

    /**
     * Get the theme type (light/dark)
     */
    ThemeType getType();

    enum ThemeType {
        LIGHT,
        DARK
    }
}
