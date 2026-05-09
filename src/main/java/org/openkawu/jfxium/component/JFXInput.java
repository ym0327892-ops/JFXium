package org.openkawu.jfxium.component;

import javafx.scene.control.TextField;
import org.openkawu.jfxium.core.css.CssClasses;

/**
 * JFXium Input Component
 * Inspired by AtlantaFX and Ant Design
 *
 * Usage:
 * <pre>{@code
 * TextField input = JFXInput.create()
 *     .placeholder("Enter your name")
 *     .size(JFXInput.Size.LARGE)
 *     .build();
 * }</pre>
 */
public class JFXInput {

    public enum Size {
        DEFAULT,
        SMALL,
        LARGE
    }

    public static Builder create() {
        return new Builder();
    }

    public static class Builder {
        private String placeholder = "";
        private String text = "";
        private Size size = Size.DEFAULT;
        private boolean disabled = false;
        private boolean readOnly = false;
        private String style = "";

        private Builder() {}

        public Builder placeholder(String placeholder) {
            this.placeholder = placeholder;
            return this;
        }

        public Builder text(String text) {
            this.text = text;
            return this;
        }

        public Builder size(Size size) {
            this.size = size;
            return this;
        }

        public Builder disabled(boolean disabled) {
            this.disabled = disabled;
            return this;
        }

        public Builder readOnly(boolean readOnly) {
            this.readOnly = readOnly;
            return this;
        }

        public Builder style(String style) {
            this.style = style;
            return this;
        }

        public TextField build() {
            TextField textField = new TextField(text);

            // Size
            if (size == Size.SMALL) {
                textField.getStyleClass().add(CssClasses.SIZE_SMALL);
            } else if (size == Size.LARGE) {
                textField.getStyleClass().add(CssClasses.SIZE_LARGE);
            }

            // Inline style
            if (!style.isEmpty()) {
                textField.setStyle(style);
            }

            // Properties
            textField.setPromptText(placeholder);
            textField.setDisable(disabled);
            textField.setEditable(!readOnly);

            return textField;
        }
    }
}
