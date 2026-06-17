package org.openkawu.jfxium.component.composite;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;
import org.openkawu.jfxium.core.css.JfxStyles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("AlertAnt")
class AlertAntTest extends JfxTestBase {

    @Test
    @DisplayName("null title/message 构建安全且 icon/type 接线正确")
    void nullContent_safe() {
        VBox alert = AlertAnt.success(null, null)
                .closable(true)
                .build();

        assertNotNull(alert);
        assertTrue(alert.getStyleClass().contains(JfxStyles.ALERT_SUCCESS));
        assertEquals(1, alert.getChildren().size());

        HBox header = (HBox) alert.getChildren().get(0);
        Label icon = (Label) header.getChildren().get(0);
        Label title = (Label) header.getChildren().get(1);
        assertTrue(icon.getStyleClass().contains(JfxStyles.ALERT_ICON));
        assertTrue(icon.getStyleClass().contains("success"));
        assertEquals("", title.getText());
    }
}
