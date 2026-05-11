package org.openkawu.jfxium.core.theme;

import java.util.Objects;

/**
 * JFXium MUI Dark Theme.
 * Material Design inspired dark theme.
 */
public class MuiDarkTheme implements Theme {

    @Override
    public String getName() {
        return "JFXium MUI Dark";
    }

    @Override
    public String getUserAgentStylesheet() {
        return Objects.requireNonNull(
            getClass().getResource("/org/openkawu/jfxium/css/theme-mui-dark.css")
        ).toExternalForm();
    }

    @Override
    public ThemeType getType() {
        return ThemeType.DARK;
    }
}
