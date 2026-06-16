package org.openkawu.jfxium.component.composite;

import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;
import org.openkawu.jfxium.core.css.JfxStyles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("FloatButtonAnt")
class FloatButtonAntTest extends JfxTestBase {

    @Test
    @DisplayName("type(null) 回退默认类型")
    void nullType_fallbackToDefault() {
        StackPane container = FloatButtonAnt.create()
                .type(null)
                .build();

        Button button = (Button) container.getChildren().get(0);
        assertTrue(button.getStyleClass().contains(JfxStyles.FLOAT_BUTTON_DEFAULT));
    }

    @Test
    @DisplayName("非有限 size 回退默认尺寸")
    void nonFiniteSize_fallbackToDefault() {
        StackPane container = FloatButtonAnt.create()
                .size(Double.NaN)
                .build();

        Button button = (Button) container.getChildren().get(0);
        assertEquals(56, button.getPrefWidth(), 0.001);
        assertEquals(56, button.getPrefHeight(), 0.001);
    }
}
