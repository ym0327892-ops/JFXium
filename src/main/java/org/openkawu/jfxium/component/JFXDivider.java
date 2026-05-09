package org.openkawu.jfxium.component;

import javafx.geometry.Orientation;
import javafx.scene.control.Separator;

/**
 * JFXium Divider Component
 * Inspired by Ant Design Divider
 *
 * Usage:
 * <pre>{@code
 * // 水平分割线
 * Separator divider = JFXDivider.create().build();
 *
 * // 带文本的分割线
 * Separator divider = JFXDivider.create()
 *     .text("OR")
 *     .build();
 *
 * // 垂直分割线
 * Separator divider = JFXDivider.create()
 *     .vertical()
 *     .build();
 * }</pre>
 */
public class JFXDivider {

    public static Builder create() {
        return new Builder();
    }

    public static class Builder {
        private String text = "";
        private boolean vertical = false;
        private String style = "";

        private Builder() {}

        public Builder text(String text) {
            this.text = text;
            return this;
        }

        public Builder vertical() {
            this.vertical = true;
            return this;
        }

        public Builder style(String style) {
            this.style = style;
            return this;
        }

        public Separator build() {
            Separator separator = new Separator();
            separator.setOrientation(vertical ? Orientation.VERTICAL : Orientation.HORIZONTAL);
            separator.getStyleClass().add("jfx-divider");

            // Note: JavaFX Separator doesn't support text directly
            // For text support, you would need a custom implementation
            // This is a basic wrapper for now

            if (!style.isEmpty()) {
                separator.setStyle(style);
            }

            return separator;
        }
    }
}