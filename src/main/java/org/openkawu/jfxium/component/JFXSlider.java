package org.openkawu.jfxium.component;

import javafx.scene.control.Slider;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * JFXium Slider Component
 * Inspired by Ant Design Slider
 *
 * Usage:
 * <pre>{@code
 * Slider slider = JFXSlider.create()
 *     .min(0)
 *     .max(100)
 *     .value(50)
 *     .step(10)
 *     .onChange(value -> System.out.println("Value: " + value))
 *     .build();
 * }</pre>
 */
public class JFXSlider {

    public static Builder create() {
        return new Builder();
    }

    public static class Builder {
        private double min = 0;
        private double max = 100;
        private double value = 0;
        private double step = 1;
        private boolean showTickLabels = false;
        private boolean showTickMarks = false;
        private boolean vertical = false;
        private Consumer<Double> onChange;
        private String style = "";
        private final List<String> extraStyleClasses = new ArrayList<>();

        private Builder() {}

        public Builder min(double min) {
            this.min = min;
            return this;
        }

        public Builder max(double max) {
            this.max = max;
            return this;
        }

        public Builder value(double value) {
            this.value = value;
            return this;
        }

        public Builder step(double step) {
            this.step = step;
            return this;
        }

        public Builder showTickLabels(boolean show) {
            this.showTickLabels = show;
            return this;
        }

        public Builder showTickMarks(boolean show) {
            this.showTickMarks = show;
            return this;
        }

        public Builder vertical(boolean vertical) {
            this.vertical = vertical;
            return this;
        }

        public Builder onChange(Consumer<Double> handler) {
            this.onChange = handler;
            return this;
        }

        public Builder style(String style) {
            this.style = style;
            return this;
        }

        public Builder styleClass(String styleClass) {
            this.extraStyleClasses.add(styleClass);
            return this;
        }

        public Slider build() {
            Slider slider = new Slider(min, max, value);
            slider.setBlockIncrement(step);
            slider.setShowTickLabels(showTickLabels);
            slider.setShowTickMarks(showTickMarks);
            slider.setOrientation(vertical ? javafx.geometry.Orientation.VERTICAL : javafx.geometry.Orientation.HORIZONTAL);

            if (onChange != null) {
                slider.valueProperty().addListener((obs, oldVal, newVal) -> {
                    onChange.accept(newVal.doubleValue());
                });
            }

            slider.getStyleClass().add("jfx-slider");
            slider.getStyleClass().addAll(extraStyleClasses);

            if (!style.isEmpty()) {
                slider.setStyle(style);
            }

            return slider;
        }
    }
}