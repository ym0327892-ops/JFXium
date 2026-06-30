package org.openkawu.jfxium.core.theme;

import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("ThemeManager")
class ThemeManagerTest extends JfxTestBase {

    @Test
    @DisplayName("切换主色时只保留一份 accent data URI stylesheet")
    void setPrimaryColor_keepsSingleAccentStylesheet() {
        ThemeManager manager = ThemeManager.getInstance();
        Scene scene = new Scene(new StackPane());

        runOnFxThreadAndWait(() -> {
            manager.registerScene(scene);
            manager.setPrimaryColor("#ff5722");
            manager.setPrimaryColor("#722ed1");
        });

        long accentStylesheetCount = scene.getStylesheets().stream()
                .filter(stylesheet -> stylesheet.startsWith("data:text/css;base64,"))
                .count();

        assertEquals(1, accentStylesheetCount);

        runOnFxThreadAndWait(() -> manager.setPrimaryColor(ThemeColor.Preset.BLUE));
    }
}
