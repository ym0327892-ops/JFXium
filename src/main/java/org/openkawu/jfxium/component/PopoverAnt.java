package org.openkawu.jfxium.component;

import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.util.Duration;

/**
 * JFXium Popover Component
 * Inspired by Ant Design Popover
 * A floating card that appears near a target element.
 */
public class PopoverAnt {

    public static class Builder {
        private String title = "";
        private Node content = null;
        private Node target = null;
        private Pos placement = Pos.BOTTOM_CENTER;

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder content(Node content) {
            this.content = content;
            return this;
        }

        public Builder target(Node target) {
            this.target = target;
            return this;
        }

        public Builder placement(Pos placement) {
            this.placement = placement;
            return this;
        }

        public Popover build() {
            return new Popover(this);
        }
    }

    public static class Popover {
        private final Builder config;
        private Popup popup;

        private Popover(Builder config) {
            this.config = config;
        }

        public void show() {
            if (config.target == null) return;

            // Create popup
            popup = new Popup();
            popup.getContent().add(createContent());

            // Calculate position
            javafx.geometry.Bounds bounds = config.target.localToScreen(config.target.getBoundsInLocal());
            double x, y;

            switch (config.placement) {
                case TOP_CENTER -> {
                    x = bounds.getMinX() + (bounds.getWidth() / 2) - 100;
                    y = bounds.getMinY() - 8;
                }
                case BOTTOM_CENTER -> {
                    x = bounds.getMinX() + (bounds.getWidth() / 2) - 100;
                    y = bounds.getMaxY() + 8;
                }
                case TOP_LEFT -> {
                    x = bounds.getMinX();
                    y = bounds.getMinY() - 8;
                }
                case TOP_RIGHT -> {
                    x = bounds.getMaxX() - 200;
                    y = bounds.getMinY() - 8;
                }
                default -> {
                    x = bounds.getMinX();
                    y = bounds.getMaxY() + 8;
                }
            }

            popup.show(config.target, x, y);

            // Animate in
            FadeTransition fade = new FadeTransition(Duration.millis(150), popup.getContent().get(0));
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.play();
        }

        public void hide() {
            if (popup != null) {
                popup.hide();
            }
        }

        private VBox createContent() {
            VBox box = new VBox(0);
            box.setStyle(
                "-fx-background-color: -color-bg-overlay;" +
                "-fx-background-radius: 8px;" +
                "-fx-border-radius: 8px;" +
                "-fx-border-color: -color-border-default;" +
                "-fx-border-width: 1px;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 8, 0, 0, 2);" +
                "-fx-min-width: 200px;" +
                "-fx-max-width: 300px;"
            );

            // Title
            if (!config.title.isEmpty()) {
                javafx.scene.control.Label titleLabel = new javafx.scene.control.Label(config.title);
                titleLabel.setStyle("-fx-text-fill: -color-fg-default; -fx-font-size: 14px; -fx-font-weight: 600; -fx-padding: 12px 16px;");
                box.getChildren().add(titleLabel);
            }

            // Content
            if (config.content != null) {
                VBox contentBox = new VBox(config.content);
                contentBox.setStyle("-fx-padding: 12px 16px;");
                box.getChildren().add(contentBox);
            }

            return box;
        }
    }

    public static Builder create() {
        return new Builder();
    }
}
