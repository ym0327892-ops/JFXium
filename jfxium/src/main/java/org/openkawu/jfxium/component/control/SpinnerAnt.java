package org.openkawu.jfxium.component.control;

import javafx.scene.control.ProgressIndicator;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;

/**
 * JFXium Spinner Component (Loading)
 * Inspired by Ant Design Spin
 *
 * Usage:
 * <pre>{@code
 * // 默认大小
 * ProgressIndicator spinner = SpinnerAnt.create().build();
 *
 * // 自定义大小
 * ProgressIndicator spinner = SpinnerAnt.create()
 *     .size(40)
 *     .build();
 * }</pre>
 */
public class SpinnerAnt {

    public static Builder create() {
        return new Builder();
    }

    public static class Builder extends AbstractStyleBuilder<Builder> {
        private double size = 32;

        private Builder() {}

        public Builder size(double size) {
            this.size = size;
            return this;
        }

        public ProgressIndicator build() {
            ProgressIndicator spinner = new ProgressIndicator();
            spinner.setPrefSize(size, size);
            spinner.getStyleClass().add("jfx-spinner");
            applyStyles(spinner);
            return spinner;
        }
    }
}
