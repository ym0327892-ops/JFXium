package org.openkawu.jfxium.component;

import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;

import java.util.function.Consumer;

/**
 * JFXium RadioButton Component
 * Inspired by Ant Design Radio
 *
 * Usage:
 * <pre>{@code
 * ToggleGroup group = new ToggleGroup();
 *
 * RadioButton option1 = RadioButtonAnt.create("Option 1")
 *     .toggleGroup(group)
 *     .selected(true)
 *     .build();
 *
 * RadioButton option2 = RadioButtonAnt.create("Option 2")
 *     .toggleGroup(group)
 *     .build();
 * }</pre>
 */
public class RadioButtonAnt {

    public static Builder create(String text) {
        return new Builder(text);
    }

    public static Builder create() {
        return new Builder("");
    }

    public static class Builder {
        private final String text;
        private boolean selected = false;
        private boolean disabled = false;
        private ToggleGroup toggleGroup;
        private Consumer<Boolean> onChange;
        private String style = "";

        private Builder(String text) {
            this.text = text;
        }

        public Builder selected(boolean selected) {
            this.selected = selected;
            return this;
        }

        public Builder disabled(boolean disabled) {
            this.disabled = disabled;
            return this;
        }

        public Builder toggleGroup(ToggleGroup toggleGroup) {
            this.toggleGroup = toggleGroup;
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

        public RadioButton build() {
            RadioButton radioButton = new RadioButton(text);
            radioButton.setSelected(selected);
            radioButton.setDisable(disabled);

            if (toggleGroup != null) {
                radioButton.setToggleGroup(toggleGroup);
            }

            if (onChange != null) {
                radioButton.selectedProperty().addListener((obs, oldVal, newVal) -> {
                    onChange.accept(newVal);
                });
            }

            radioButton.getStyleClass().add("jfx-radio-button");

            if (!style.isEmpty()) {
                radioButton.setStyle(style);
            }

            return radioButton;
        }
    }
}