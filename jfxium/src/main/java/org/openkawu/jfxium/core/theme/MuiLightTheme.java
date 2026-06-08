package org.openkawu.jfxium.core.theme;

import java.util.Objects;

/**
 * JFXium MUI Light Theme.
 * Material Design inspired theme with blue primary color.
 */
public class MuiLightTheme implements Theme {

    @Override
    public String getName() {
        return "JFXium MUI";
    }

    @Override
    public String getUserAgentStylesheet() {
        return Objects.requireNonNull(
            getClass().getResource("/org/openkawu/jfxium/css/theme-mui.css")
        ).toExternalForm();
    }

    @Override
    public ThemeType getType() {
        return ThemeType.LIGHT;
    }
}
