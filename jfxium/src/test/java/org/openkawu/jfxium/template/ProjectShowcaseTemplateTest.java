package org.openkawu.jfxium.template;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("ProjectShowcaseTemplate")
class ProjectShowcaseTemplateTest extends JfxTestBase {

    @Test
    @DisplayName("工程展示首页构建与顺序安全")
    void build_and_order() {
        VBox root = ProjectShowcaseTemplate.create()
                .title(null)
                .description(null)
                .hero(section("hero"))
                .sections(
                        section("intro"),
                        section("overview"),
                        section("release"),
                        section("launchpad")
                )
                .build();

        assertNotNull(root);
        assertEquals(6, root.getChildren().size());
        assertEquals("hero", ((Label) root.getChildren().get(1)).getText());
        assertEquals("intro", ((Label) root.getChildren().get(2)).getText());
        assertEquals("launchpad", ((Label) root.getChildren().get(5)).getText());
    }

    private static Node section(String name) {
        return new Label(name);
    }
}
