package org.openkawu.jfxium.core.theme;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Region;

import java.util.ArrayList;
import java.util.List;

/**
 * JFXium Theme Manager
 * Inspired by AtlantaFX and Ant Design
 */
public class ThemeManager {

    private static ThemeManager instance;

    private Theme currentTheme;
    private ThemeColor currentThemeColor;
    private final List<Scene> registeredScenes = new ArrayList<>();
    private final List<Region> registeredRegions = new ArrayList<>();

    private ThemeManager() {
        this.currentTheme = new LightTheme();
        this.currentThemeColor = new ThemeColor(ThemeColor.Preset.BLUE);
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
     * Get the current theme color
     */
    public ThemeColor getCurrentThemeColor() {
        return currentThemeColor;
    }

    /**
     * Register a scene for dynamic theme color updates.
     * Call this after setting the scene on the stage.
     */
    public void registerScene(Scene scene) {
        if (!registeredScenes.contains(scene)) {
            registeredScenes.add(scene);
        }
    }

    /**
     * Register a region for dynamic theme color updates.
     */
    public void registerRegion(Region region) {
        if (!registeredRegions.contains(region)) {
            registeredRegions.add(region);
        }
    }

    /**
     * Apply a theme globally using Application.setUserAgentStylesheet()
     */
    public void applyTheme(Theme theme) {
        this.currentTheme = theme;
        Application.setUserAgentStylesheet(theme.getUserAgentStylesheet());
    }

    /**
     * Change the primary accent color dynamically.
     * This updates CSS variables on all registered scenes and regions.
     *
     * @param color Hex color string (e.g., "#1677ff", "#722ed1")
     */
    public void setPrimaryColor(String color) {
        this.currentThemeColor.setHexColor(color);
        applyPrimaryColorToAll();
    }

    /**
     * Change the primary accent color using a preset.
     */
    public void setPrimaryColor(ThemeColor.Preset preset) {
        setPrimaryColor(preset.getHexColor());
    }

    /**
     * Apply the current primary color to all registered scenes and regions.
     */
    private void applyPrimaryColorToAll() {
        String[] lightScale = currentThemeColor.generateColorScale();
        String[] darkScale = currentThemeColor.generateDarkColorScale();

        boolean isDark = currentTheme.getType() == Theme.ThemeType.DARK;
        String[] scale = isDark ? darkScale : lightScale;

        // Build CSS string for accent colors
        StringBuilder css = new StringBuilder();
        css.append(".root {");
        for (int i = 0; i < 10; i++) {
            css.append(String.format("  -color-accent-%d: %s;", i, scale[i]));
        }
        css.append("  -color-accent-emphasis: ").append(scale[5]).append(";");
        css.append("  -color-accent-muted: ").append(scale[2]).append(";");
        css.append("  -color-accent-subtle: ").append(scale[0]).append(";");
        css.append("}");

        String style = css.toString();

        // Apply to all registered scenes
        for (Scene scene : registeredScenes) {
            if (scene.getRoot() != null) {
                scene.getRoot().setStyle(style);
            }
        }

        // Apply to all registered regions
        for (Region region : registeredRegions) {
            region.setStyle(style);
        }
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

    /**
     * Toggle between default and compact density for the current theme type.
     * Supports Light, Dark, and MUI theme families.
     */
    public void toggleCompact() {
        Theme newTheme;
        if (currentTheme instanceof LightCompactTheme) {
            newTheme = new LightTheme();
        } else if (currentTheme instanceof DarkCompactTheme) {
            newTheme = new DarkTheme();
        } else if (currentTheme instanceof MuiCompactTheme) {
            newTheme = new MuiTheme();
        } else if (currentTheme instanceof MuiDarkCompactTheme) {
            newTheme = new MuiDarkTheme();
        } else if (currentTheme instanceof MuiTheme) {
            newTheme = new MuiCompactTheme();
        } else if (currentTheme instanceof MuiDarkTheme) {
            newTheme = new MuiDarkCompactTheme();
        } else if (currentTheme.getType() == Theme.ThemeType.LIGHT) {
            newTheme = new LightCompactTheme();
        } else {
            newTheme = new DarkCompactTheme();
        }
        applyTheme(newTheme);
    }

    /**
     * Check if the current theme is compact density.
     */
    public boolean isCompact() {
        return currentTheme instanceof LightCompactTheme
            || currentTheme instanceof DarkCompactTheme
            || currentTheme instanceof MuiCompactTheme
            || currentTheme instanceof MuiDarkCompactTheme;
    }

    /**
     * Switch to MUI theme family (Light).
     */
    public void switchToMui() {
        applyTheme(new MuiTheme());
    }

    /**
     * Switch to MUI Dark theme family.
     */
    public void switchToMuiDark() {
        applyTheme(new MuiDarkTheme());
    }

    /**
     * Get the current theme family name.
     */
    public String getCurrentThemeFamily() {
        if (currentTheme instanceof MuiTheme || currentTheme instanceof MuiCompactTheme
            || currentTheme instanceof MuiDarkTheme || currentTheme instanceof MuiDarkCompactTheme) {
            return "MUI";
        }
        return "Ant Design";
    }
}
