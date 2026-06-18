package org.openkawu.jfxium.template;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("ShowcaseSectionTemplate")
class ShowcaseSectionTemplateTest extends JfxTestBase {

    @Test
    @DisplayName("title + description + body 组合为紧凑 section")
    void build_composeSection() {
        VBox section = ShowcaseSectionTemplate.create()
                .title("标题")
                .description("说明")
                .body(new Label("内容"))
                .build();

        assertEquals(2, section.getChildren().size());
        assertTrue(section.getChildren().get(0) instanceof VBox);
        assertTrue(section.getChildren().get(1) instanceof VBox);
    }
}
