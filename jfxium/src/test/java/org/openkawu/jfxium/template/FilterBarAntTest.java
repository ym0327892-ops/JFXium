package org.openkawu.jfxium.template;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;
import org.openkawu.jfxium.core.style.JfxStyles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("FilterBarAnt")
class FilterBarAntTest extends JfxTestBase {

    @Test
    @DisplayName("null control / spacing / width 构建安全")
    void invalidInputs_safe() {
        HBox bar = FilterBarAnt.create()
                .search("Search", Double.NaN, null)
                .filter("Label", new Label("Value"))
                .filter(null)
                .action("Refresh", null)
                .actionPrimary(null, "+", null)
                .actionNode(null)
                .spacing(Double.NaN)
                .padding(-8)
                .build();

        assertNotNull(bar);
        assertEquals(5, bar.getChildren().size());
        assertEquals(0.0, bar.getSpacing(), 0.001);
        assertTrue(bar.getStyleClass().contains(JfxStyles.FILTER_BAR));
    }
}
