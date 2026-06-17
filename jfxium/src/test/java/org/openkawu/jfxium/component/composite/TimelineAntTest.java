package org.openkawu.jfxium.component.composite;

import javafx.scene.layout.VBox;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;
import org.openkawu.jfxium.core.css.JfxStyles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("TimelineAnt")
class TimelineAntTest extends JfxTestBase {

    @Test
    @DisplayName("mode(null) / dotColor(null) / content(null) 构建安全")
    void nullValues_safe() {
        VBox timeline = TimelineAnt.create()
                .mode(TimelineAnt.Mode.RIGHT)
                .item(null, null, null)
                .pending(null)
                .build();

        assertNotNull(timeline);
        assertEquals(2, timeline.getChildren().size());
        javafx.scene.layout.HBox row = (javafx.scene.layout.HBox) timeline.getChildren().get(0);
        VBox leftBox = (VBox) row.getChildren().get(0);
        VBox centerBox = (VBox) row.getChildren().get(1);
        VBox rightBox = (VBox) row.getChildren().get(2);
        assertTrue(leftBox.getStyleClass().contains(JfxStyles.TIMELINE_LABEL_BOX));
        assertTrue(centerBox.getStyleClass().contains(JfxStyles.TIMELINE_CENTER_BOX));
        assertTrue(rightBox.getStyleClass().contains(JfxStyles.TIMELINE_CONTENT_BOX));
    }
}
