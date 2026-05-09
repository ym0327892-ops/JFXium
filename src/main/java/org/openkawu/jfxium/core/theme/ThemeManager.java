package org.openkawu.jfxium.core.theme;

import javafx.application.Application;

/**
 * JFXium Theme Manager
 * Inspired by AtlantaFX
 */
public class ThemeManager {

    private static ThemeManager instance;

    private Theme currentTheme;

    private ThemeManager() {
        this.currentTheme = new LightTheme();
    }

    public static ThemeManager getInstance() {
        if (instance == null) {
            synchronized (ThemeManager.class) {
                if (instance == null) {
                    instance = new ThemeManager();
                }
            }
        }
        return instance;
    }

    /**
     * Get the current theme
     */
    public Theme getCurrentTheme() {
        return currentTheme;
    }

    /**
     * Apply a theme globally using Application.setUserAgentStylesheet()
     */
    public void applyTheme(Theme theme) {
        this.currentTheme = theme;
        Application.setUserAgentStylesheet(theme.getUserAgentStylesheet());
    }

    /**
     * Toggle between light and dark themes
     */
    public void toggleTheme() {
        Theme newTheme = (currentTheme.getType() == Theme.ThemeType.LIGHT)
            ? new DarkTheme()
            : new LightTheme();
        applyTheme(newTheme);
    }
}
