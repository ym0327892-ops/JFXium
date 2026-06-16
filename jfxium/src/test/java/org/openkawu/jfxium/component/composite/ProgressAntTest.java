package org.openkawu.jfxium.component.composite;

import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openkawu.jfxium.JfxTestBase;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("ProgressAnt")
class ProgressAntTest extends JfxTestBase {

    @Test
    @DisplayName("progress(NaN/Infinity) 钳制为 0")
    void nonFiniteProgress_clampedToZero() {
        HBox bar = ProgressAnt.bar()
                .progress(Double.NaN)
                .build();
        ProgressBar progressBar = (ProgressBar) bar.getChildren().get(0);
        assertEquals(0, progressBar.getProgress(), 0.001);

        VBox circle = ProgressAnt.circle()
                .progress(Double.POSITIVE_INFINITY)
                .build();
        ProgressIndicator indicator = (ProgressIndicator) circle.getChildren().get(0);
        assertEquals(0, indicator.getProgress(), 0.001);
    }

    @Test
    @DisplayName("circle size 非有限值回退默认尺寸")
    void nonFiniteCircleSize_fallbackToDefault() {
        VBox circle = ProgressAnt.circle()
                .size(Double.POSITIVE_INFINITY)
                .build();
        ProgressIndicator indicator = (ProgressIndicator) circle.getChildren().get(0);
        assertEquals(60, indicator.getPrefWidth(), 0.001);
        assertEquals(60, indicator.getPrefHeight(), 0.001);
    }
}
