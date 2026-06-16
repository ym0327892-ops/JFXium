package org.openkawu.jfxium.component.composite;

import javafx.scene.layout.HBox;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;
import org.openkawu.jfxium.core.css.JfxStyles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("RateAnt")
class RateAntTest extends JfxTestBase {

    @Test
    @DisplayName("count 小于 1 时钳制为 1")
    void invalidCount_clampedToOne() {
        HBox rate = RateAnt.create()
                .count(-3)
                .build();

        assertEquals(1, rate.getChildren().size());
    }

    @Test
    @DisplayName("size(null) 回退默认尺寸")
    void nullSize_fallbackToDefault() {
        HBox rate = RateAnt.create()
                .size(null)
                .build();

        assertEquals(5, rate.getChildren().size());
    }

    @Test
    @DisplayName("非有限 value 回退为 0")
    void nonFiniteValue_fallbackToZero() {
        HBox rate = RateAnt.create()
                .value(Double.NaN)
                .build();

        assertFalse(rate.getChildren().get(0).getStyleClass().contains(JfxStyles.RATE_ACTIVE));
        assertTrue(rate.getChildren().get(0).getStyleClass().contains(JfxStyles.RATE_INACTIVE));
    }
}
