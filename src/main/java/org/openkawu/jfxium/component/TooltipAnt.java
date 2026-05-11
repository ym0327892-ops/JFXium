package org.openkawu.jfxium.component;

import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;

/**
 * JFXium Tooltip Component
 * 封装 JavaFX Tooltip
 *
 * Usage:
 * <pre>{@code
 * Button btn = ButtonAnt.create("Hover me").build();
 * TooltipAnt.create("This is a tooltip")
 *     .delay(Duration.millis(200))
 *     .install(btn);
 * }</pre>
 */
public class TooltipAnt {

    public static Builder create(String text) {
        return new Builder(text);
    }

    public static class Builder {
        private final String text;
        private Duration showDelay = Duration.millis(200);
        private Duration showDuration = Duration.seconds(10);
        private Duration hideDelay = Duration.millis(200);
        private String style = "";

        private Builder(String text) {
            this.text = text;
        }

        public Builder delay(Duration delay) {
            this.showDelay = delay;
            return this;
        }

        public Builder duration(Duration duration) {
            this.showDuration = duration;
            return this;
        }

        public Builder hideDelay(Duration delay) {
            this.hideDelay = delay;
            return this;
        }

        public Builder style(String style) {
            this.style = style;
            return this;
        }

        public Tooltip build() {
            Tooltip tooltip = new Tooltip(text);
            tooltip.setShowDelay(showDelay);
            tooltip.setShowDuration(showDuration);
            tooltip.setHideDelay(hideDelay);
            tooltip.getStyleClass().add("jfx-tooltip");

            if (!style.isEmpty()) {
                tooltip.setStyle(style);
            }

            return tooltip;
        }

        public void install(Node node) {
            Tooltip.install(node, build());
        }
    }
}
