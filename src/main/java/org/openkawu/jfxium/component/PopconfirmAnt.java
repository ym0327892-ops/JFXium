package org.openkawu.jfxium.component;

import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.stage.Popup;
import javafx.util.Duration;

import java.util.function.Consumer;

/**
 * JFXium Popconfirm Component
 * Inspired by Ant Design Popconfirm
 * A simple confirmation dialog that appears near a target element.
 */
public class PopconfirmAnt {

    public static class Builder {
        private String title = "";
        private String description = "";
        private String okText = "Yes";
        private String cancelText = "No";
        private Consumer<Boolean> onConfirm = null;
        private Consumer<Boolean> onCancel = null;
        private Node target = null;

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder okText(String okText) {
            this.okText = okText;
            return this;
        }

        public Builder cancelText(String cancelText) {
            this.cancelText = cancelText;
            return this;
        }

        public Builder onConfirm(Consumer<Boolean> onConfirm) {
            this.onConfirm = onConfirm;
            return this;
        }

        public Builder onCancel(Consumer<Boolean> onCancel) {
            this.onCancel = onCancel;
            return this;
        }

        public Builder target(Node target) {
            this.target = target;
            return this;
        }

        public Popconfirm build() {
            return new Popconfirm(this);
        }
    }

    public static class Popconfirm {
        private final Builder config;
        private Popup popup;

        private Popconfirm(Builder config) {
            this.config = config;
        }

        public void show() {
            if (config.target == null) return;

            // Create popup
            popup = new Popup();
            popup.getContent().add(createContent());

            // Position below target
            javafx.geometry.Bounds bounds = config.target.localToScreen(config.target.getBoundsInLocal());
            popup.show(config.target, bounds.getMinX(), bounds.getMaxY() + 8);

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
            VBox box = new VBox(12);
            box.setStyle(
                "-fx-background-color: -color-bg-overlay;" +
                "-fx-padding: 12px 16px;" +
                "-fx-background-radius: 8px;" +
                "-fx-border-radius: 8px;" +
                "-fx-border-color: -color-border-default;" +
                "-fx-border-width: 1px;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 8, 0, 0, 2);" +
                "-fx-min-width: 200px;"
            );

            // Title with icon
            HBox titleBox = new HBox(8);
            titleBox.setAlignment(Pos.CENTER_LEFT);

            SVGPath icon = new SVGPath();
            icon.setContent("M1 21h22L12 2 1 21zm12-3h-2v-2h2v2zm0-4h-2v-4h2v4z");
            icon.setStyle("-fx-fill: -color-warning-emphasis;");
            titleBox.getChildren().add(icon);

            Label titleLabel = new Label(config.title);
            titleLabel.setStyle("-fx-text-fill: -color-fg-default; -fx-font-size: 14px; -fx-font-weight: 500;");
            titleBox.getChildren().add(titleLabel);

            box.getChildren().add(titleBox);

            // Description
            if (!config.description.isEmpty()) {
                Label descLabel = new Label(config.description);
                descLabel.setStyle("-fx-text-fill: -color-fg-muted; -fx-font-size: 12px;");
                descLabel.setWrapText(true);
                box.getChildren().add(descLabel);
            }

            // Buttons
            HBox buttonBox = new HBox(8);
            buttonBox.setAlignment(Pos.CENTER_RIGHT);

            javafx.scene.control.Button cancelBtn = ButtonAnt.create(config.cancelText)
                .type(ButtonAnt.Type.DEFAULT)
                .size(ButtonAnt.Size.SMALL)
                .onClick(e -> {
                    hide();
                    if (config.onCancel != null) {
                        config.onCancel.accept(false);
                    }
                })
                .build();

            javafx.scene.control.Button okBtn = ButtonAnt.create(config.okText)
                .type(ButtonAnt.Type.PRIMARY)
                .size(ButtonAnt.Size.SMALL)
                .onClick(e -> {
                    hide();
                    if (config.onConfirm != null) {
                        config.onConfirm.accept(true);
                    }
                })
                .build();

            buttonBox.getChildren().addAll(cancelBtn, okBtn);
            box.getChildren().add(buttonBox);

            return box;
        }
    }

    public static Builder create() {
        return new Builder();
    }
}
