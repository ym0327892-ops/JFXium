package org.openkawu.jfxium.component.control;

import javafx.scene.control.TextArea;

import java.util.function.Consumer;

/**
 * JFXium TextArea Component
 * Inspired by Ant Design Input.TextArea
 *
 * Usage:
 * <pre>{@code
 * TextArea textArea = TextAreaAnt.create()
 *     .placeholder("Enter description...")
 *     .rows(4)
 *     .onChange((oldVal, newVal) -> System.out.println("Changed: " + newVal))
 *     .build();
 *
 * // 只读文本（用于显示错误信息）
 * TextArea readOnly = TextAreaAnt.readOnly("Error message here...")
 *     .build();
 * }</pre>
 */
public class TextAreaAnt {

    public static Builder create() {
        return new Builder();
    }

    public static class Builder {
        private String placeholder = "";
        private String text = "";
        private int rows = 3;
        private boolean wrapText = true;
        private boolean disabled = false;
        private boolean editable = true;
        private boolean showCharCount = false;
        private Consumer<String> onChange;
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

        public Builder rows(int rows) {
            this.rows = rows;
            return this;
        }

        public Builder wrapText(boolean wrapText) {
            this.wrapText = wrapText;
            return this;
        }

        public Builder disabled(boolean disabled) {
            this.disabled = disabled;
            return this;
        }

        public Builder editable(boolean editable) {
            this.editable = editable;
            return this;
        }

        public Builder showCharCount(boolean show) {
            this.showCharCount = show;
            return this;
        }

        public Builder onChange(Consumer<String> handler) {
            this.onChange = handler;
            return this;
        }

        public Builder style(String style) {
            this.style = style;
            return this;
        }

        public TextArea build() {
            TextArea textArea = new TextArea(text);
            textArea.setPromptText(placeholder);
            textArea.setWrapText(wrapText);
            textArea.setDisable(disabled);
            textArea.setEditable(editable);
            textArea.setPrefRowCount(rows);

            if (onChange != null) {
                textArea.textProperty().addListener((obs, oldVal, newVal) -> {
                    onChange.accept(newVal);
                });
            }

            textArea.getStyleClass().add("jfx-text-area");

            if (!style.isEmpty()) {
                textArea.setStyle(style);
            }

            return textArea;
        }
    }

    public static TextArea readOnly(String message) {
        TextArea textArea = new TextArea(message);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setDisable(false);
        textArea.getStyleClass().addAll("jfx-text-area", "jfx-text-area-read-only");
        textArea.setStyle(
            "-fx-background-color: -color-danger-bg;" +
            "-fx-border-color: -color-danger-border;" +
            "-fx-text-fill: -color-danger;" +
            "-fx-font-size: 14px;" +
            "-fx-padding: 12px;" +
            "-fx-border-radius: 6px;" +
            "-fx-background-radius: 6px;" +
            "-fx-border-width: 1px;"
        );
        return textArea;
    }
}
