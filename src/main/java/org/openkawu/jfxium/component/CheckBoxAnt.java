package org.openkawu.jfxium.component;

import javafx.scene.control.CheckBox;

import java.util.function.Consumer;

/**
 * JFXium CheckBox Component
 * Inspired by Ant Design Checkbox
 *
 * Usage:
 * <pre>{@code
 * CheckBox checkBox = CheckBoxAnt.create("Remember me")
 *     .selected(true)
 *     .onChange(checked -> System.out.println("Checked: " + checked))
 *     .build();
 * }</pre>
 */
public class CheckBoxAnt {

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
        private boolean indeterminate = false;
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

        public Builder indeterminate(boolean indeterminate) {
            this.indeterminate = indeterminate;
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

        public CheckBox build() {
            CheckBox checkBox = new CheckBox(text);
            checkBox.setSelected(selected);
            checkBox.setDisable(disabled);
            checkBox.setIndeterminate(indeterminate);

            if (onChange != null) {
                checkBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
                    onChange.accept(newVal);
                });
            }

            checkBox.getStyleClass().add("jfx-check-box");

            if (!style.isEmpty()) {
                checkBox.setStyle(style);
            }

            return checkBox;
        }
    }
}