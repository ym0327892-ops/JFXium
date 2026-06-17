package org.openkawu.jfxium.template;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("CrudTemplate")
class CrudTemplateTest extends JfxTestBase {

    @Test
    @DisplayName("spacing / 节点空值构建安全")
    void invalidSpacing_safe() {
        BorderPane page = CrudTemplate.create()
                .title("Title")
                .topLeft(null, new Label("L"))
                .topRight(new Label("R"))
                .bottomLeft(new Label("BL"))
                .bottomRight(null, new Label("BR"))
                .body(new VBox())
                .topbarSpacing(Double.NaN)
                .bottombarSpacing(-4)
                .sectionGap(Double.NaN)
                .build();

        assertNotNull(page);
        VBox top = (VBox) page.getTop();
        HBox topbar = (HBox) top.getChildren().get(1);
        HBox bottombar = (HBox) page.getBottom();

        assertEquals(0.0, top.getSpacing(), 0.001);
        assertEquals(0.0, topbar.getSpacing(), 0.001);
        assertEquals(0.0, bottombar.getSpacing(), 0.001);
        assertEquals(Insets.EMPTY, BorderPane.getMargin(top));
        assertEquals(Insets.EMPTY, BorderPane.getMargin(bottombar));
    }
}
