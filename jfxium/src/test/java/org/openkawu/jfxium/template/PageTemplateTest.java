package org.openkawu.jfxium.template;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("PageTemplate")
class PageTemplateTest extends JfxTestBase {

    @Test
    @DisplayName("spacing / padding 非有限值构建安全")
    void invalidSpacing_safe() {
        VBox body = new VBox();
        VBox section = new VBox();
        VBox section2 = new VBox();

        VBox page = PageTemplate.create()
                .title("Title")
                .description("Description")
                .body(body)
                .section(section)
                .section(section2)
                .headerGap(Double.NaN)
                .sectionGap(-8)
                .headerToBodyGap(Double.NaN)
                .padding(Double.NaN)
                .build();

        assertNotNull(page);
        assertEquals(4, page.getChildren().size());
        assertEquals(Insets.EMPTY, page.getPadding());

        VBox header = (VBox) page.getChildren().get(0);
        assertEquals(0.0, header.getSpacing(), 0.001);
        assertTrue(VBox.getMargin(header).equals(Insets.EMPTY));
        assertEquals(0.0, VBox.getMargin(section).getBottom(), 0.001);
    }
}
