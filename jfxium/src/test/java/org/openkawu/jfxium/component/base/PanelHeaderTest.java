package org.openkawu.jfxium.component.base;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;
import org.openkawu.jfxium.core.css.JfxStyles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("PanelHeader")
class PanelHeaderTest extends JfxTestBase {

    @Test
    @DisplayName("title(null) / padding 非有限值 / close button 构建安全")
    void nullAndInvalidValues_safe() {
        HBox header = PanelHeader.create()
                .title(null)
                .padding(Double.NaN, -8, 8, 12)
                .onClose(() -> {})
                .build();

        assertNotNull(header);
        assertEquals(2, header.getChildren().size());

        Label title = (Label) header.getChildren().get(0);
        assertEquals("", title.getText());

        Insets padding = header.getPadding();
        assertEquals(0.0, padding.getTop(), 0.001);
        assertEquals(0.0, padding.getRight(), 0.001);
        assertEquals(8.0, padding.getBottom(), 0.001);
        assertEquals(12.0, padding.getLeft(), 0.001);

        assertTrue(header.getChildren().get(1) instanceof CloseButton);
        assertTrue(header.getChildren().get(1).getStyleClass().contains(JfxStyles.PANEL_CLOSE_BTN));
    }
}
