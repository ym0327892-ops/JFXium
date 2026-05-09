package org.openkawu.jfxium.core.theme;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public final class ThemeContext {

    private static final ThemeContext INSTANCE = new ThemeContext();

    private final ObjectProperty<Theme> currentTheme = new SimpleObjectProperty<>(new LightTheme());

    private ThemeContext() {}

    public static ThemeContext getInstance() {
        return INSTANCE;
    }

    public Theme getCurrentTheme() {
        return currentTheme.get();
    }

    public void setCurrentTheme(Theme theme) {
        currentTheme.set(theme);
    }

    public ObjectProperty<Theme> currentThemeProperty() {
        return currentTheme;
    }
}
