package org.openkawu.jfxium.component;

import javafx.scene.control.ToggleButton;

import java.util.function.Consumer;

/**
 * JFXium Switch Component
 * 封装 JavaFX ToggleButton 作为 Switch 使用
 *
 * Usage:
 * <pre>{@code
 * ToggleButton switchBtn = SwitchAnt.create()
 *     .selected(true)
 *     .onChange(checked -> System.out.println("Switched: " + checked))
 *     .build();
 * }</pre>
 */
public class SwitchAnt {

    public static Builder create() {
        return new Builder();
    }

    public static class Builder {
        private boolean selected = false;
        private boolean disabled = false;
        private String text = "";
        private Consumer<Boolean> onChange;
        private String style = "";

        private Builder() {}

        public Builder selected(boolean selected) {
            this.selected = selected;
            return this;
        }

        public Builder disabled(boolean disabled) {
            this.disabled = disabled;
            return this;
        }

        public Builder text(String text) {
            this.text = text;
            return this;
        }

        public Builder onChange(Consumer<Boolean> handler) {
            this.onChange = handler;
            return this;
        }

        public Builder style(String style) {
            this.style = style;
            return this;
        }

        public ToggleButton build() {
            ToggleButton toggleButton = new ToggleButton(text);
            toggleButton.setSelected(selected);
            toggleButton.setDisable(disabled);
            toggleButton.getStyleClass().add("jfx-switch");

            if (onChange != null) {
                toggleButton.selectedProperty().addListener((obs, oldVal, newVal) -> {
                    onChange.accept(newVal);
                });
            }

            if (!style.isEmpty()) {
                toggleButton.setStyle(style);
            }

            return toggleButton;
        }
    }
}
