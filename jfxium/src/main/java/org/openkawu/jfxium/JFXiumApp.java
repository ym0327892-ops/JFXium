package org.openkawu.jfxium;

import javafx.application.Application;
import org.openkawu.jfxium.core.theme.Theme;
import org.openkawu.jfxium.core.theme.ThemeManager;

public class JFXiumApp extends Application {
    @Override
    public void start(javafx.stage.Stage stage) throws Exception {
        throw new UnsupportedOperationException("Use JFXiumApp as a base class and override start() method");
    }

    public static void applyTheme(Theme theme) {
        ThemeManager.getInstance().applyTheme(theme);
    }

    public static void toggleTheme() {
        ThemeManager.getInstance().toggleTheme();
    }
}
