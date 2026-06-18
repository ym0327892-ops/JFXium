package org.openkawu.jfxium.template;

import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("ProjectConsoleTemplate")
class ProjectConsoleTemplateTest extends JfxTestBase {

    @Test
    @DisplayName("headerActions 统一收口设置 + 用户入口")
    void headerActions_composeSettingsAndUserMenu() {
        HBox actions = (HBox) ProjectConsoleTemplate.headerActions("开发者", key -> {});

        assertEquals(2, actions.getChildren().size());
        assertTrue(actions.getChildren().get(0) instanceof Button);
        assertTrue(actions.getChildren().get(1) instanceof HBox);
    }
}
