package org.openkawu.jfxium.component.composite;

import javafx.scene.layout.VBox;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("TimelineAnt")
class TimelineAntTest extends JfxTestBase {

    @Test
    @DisplayName("mode(null) / dotColor(null) / content(null) 构建安全")
    void nullValues_safe() {
        VBox timeline = TimelineAnt.create()
                .mode(null)
                .item(null, null, null)
                .pending(null)
                .build();

        assertNotNull(timeline);
        assertEquals(2, timeline.getChildren().size());
    }
}
