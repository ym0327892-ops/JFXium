package org.openkawu.jfxium.template;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("ProjectHeroTemplate")
class ProjectHeroTemplateTest extends JfxTestBase {

    @Test
    @DisplayName("工程门面模板构建与结构安全")
    void build_and_structure() {
        VBox root = ProjectHeroTemplate.create()
                .title("hero")
                .subtitle("subtitle")
                .description("description")
                .status("ready")
                .meta("k", "v")
                .action(new Label("action"))
                .build();

        assertNotNull(root);
        assertEquals(2, root.getChildren().size());
        assertTrue(root.getChildren().get(0) instanceof HBox);

        VBox body = (VBox) root.getChildren().get(1);
        assertEquals(3, body.getChildren().size());
        assertTrue(body.getChildren().get(0) instanceof HBox);
        assertTrue(body.getChildren().get(1) instanceof HBox);
        assertTrue(body.getChildren().get(2) instanceof VBox);
    }

    @Test
    @DisplayName("头像下拉入口可直接复用")
    void userMenu_buildsDropdownTrigger() {
        assertTrue(ProjectHeroTemplate.userMenu("开发者", key -> {}).getTrigger() instanceof HBox);
    }
}
