package org.openkawu.jfxium.component;

import javafx.scene.control.ProgressIndicator;

/**
 * JFXium Spinner Component (Loading)
 * Inspired by Ant Design Spin
 *
 * Usage:
 * <pre>{@code
 * // 默认大小
 * ProgressIndicator spinner = JFXSpinner.create().build();
 *
 * // 自定义大小
 * ProgressIndicator spinner = JFXSpinner.create()
 *     .size(40)
 *     .build();
 * }</pre>
 */
public class JFXSpinner {

    public static Builder create() {
        return new Builder();
    }

    public static class Builder {
        private double size = 32;
        private String style = "";

        private Builder() {}

        public Builder size(double size) {
            this.size = size;
            return this;
        }

        public Builder style(String style) {
            this.style = style;
            return this;
        }

        public ProgressIndicator build() {
            ProgressIndicator spinner = new ProgressIndicator();
            spinner.setPrefSize(size, size);
            spinner.getStyleClass().add("jfx-spinner");

            if (!style.isEmpty()) {
                spinner.setStyle(style);
            }

            return spinner;
        }
    }
}