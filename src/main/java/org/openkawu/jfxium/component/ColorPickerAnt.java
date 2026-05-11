package org.openkawu.jfxium.component;

import javafx.scene.control.ColorPicker;
import javafx.scene.paint.Color;

import java.util.function.Consumer;

/**
 * JFXium ColorPicker Component
 * 封装 JavaFX ColorPicker
 *
 * Usage:
 * <pre>{@code
 * ColorPicker colorPicker = ColorPickerAnt.create()
 *     .value(Color.BLUE)
 *     .onChange(color -> System.out.println("Color: " + color))
 *     .build();
 * }</pre>
 */
public class ColorPickerAnt {

    public static Builder create() {
        return new Builder();
    }

    public static class Builder {
        private Color value = Color.WHITE;
        private boolean disabled = false;
        private Consumer<Color> onChange;
        private String style = "";

        private Builder() {}

        public Builder value(Color value) {
            this.value = value;
            return this;
        }

        public Builder disabled(boolean disabled) {
            this.disabled = disabled;
            return this;
        }

        public Builder onChange(Consumer<Color> handler) {
            this.onChange = handler;
            return this;
        }

        public Builder style(String style) {
            this.style = style;
            return this;
        }

        public ColorPicker build() {
            ColorPicker colorPicker = new ColorPicker(value);
            colorPicker.setDisable(disabled);
            colorPicker.getStyleClass().add("jfx-color-picker");

            if (onChange != null) {
                colorPicker.valueProperty().addListener((obs, oldVal, newVal) -> {
                    onChange.accept(newVal);
                });
            }

            if (!style.isEmpty()) {
                colorPicker.setStyle(style);
            }

            return colorPicker;
        }
    }
}
