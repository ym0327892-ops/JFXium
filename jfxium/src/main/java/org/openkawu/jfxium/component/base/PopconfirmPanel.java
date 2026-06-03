package org.openkawu.jfxium.component.base;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import org.openkawu.jfxium.component.control.ButtonAnt;

public class PopconfirmPanel {

    public static class Builder {
        private String title = "";
        private String description = "";
        private String okText = "Yes";
        private String cancelText = "No";
        private Runnable onConfirm = null;
        private Runnable onCancel = null;
        private String minWidth = "200px";

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

        public Builder onConfirm(Runnable onConfirm) {
            this.onConfirm = onConfirm;
            return this;
        }

        public Builder onCancel(Runnable onCancel) {
            this.onCancel = onCancel;
            return this;
        }

        public Builder minWidth(String minWidth) {
            this.minWidth = minWidth;
            return this;
        }

        public VBox build() {
            VBox panel = new VBox(12);
            panel.setStyle(
                "-fx-background-color: -color-bg-overlay;" +
                "-fx-padding: 12px 16px;" +
                "-fx-background-radius: 8px;" +
                "-fx-border-radius: 8px;" +
                "-fx-border-color: -color-border-default;" +
                "-fx-border-width: 1px;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 8, 0, 0, 2);" +
                "-fx-min-width: " + minWidth + ";"
            );

            HBox titleBox = new HBox(8);
            titleBox.setAlignment(Pos.CENTER_LEFT);

            SVGPath icon = new SVGPath();
            icon.setContent("M1 21h22L12 2 1 21zm12-3h-2v-2h2v2zm0-4h-2v-4h2v4z");
            icon.setStyle("-fx-fill: -color-warning-emphasis;");
            titleBox.getChildren().add(icon);

            Label titleLabel = new Label(title);
            titleLabel.setStyle("-fx-text-fill: -color-fg-default; -fx-font-size: 14px; -fx-font-weight: 500;");
            titleBox.getChildren().add(titleLabel);

            panel.getChildren().add(titleBox);

            if (!description.isEmpty()) {
                Label descLabel = new Label(description);
                descLabel.setStyle("-fx-text-fill: -color-fg-muted; -fx-font-size: 12px;");
                descLabel.setWrapText(true);
                panel.getChildren().add(descLabel);
            }

            HBox buttonBox = new HBox(8);
            buttonBox.setAlignment(Pos.CENTER_RIGHT);

            Button cancelBtn = ButtonAnt.create(cancelText)
                .type(ButtonAnt.Type.DEFAULT)
                .size(ButtonAnt.Size.SMALL)
                .onClick(e -> {
                    if (onCancel != null) {
                        onCancel.run();
                    }
                })
                .build();

            Button okBtn = ButtonAnt.create(okText)
                .type(ButtonAnt.Type.PRIMARY)
                .size(ButtonAnt.Size.SMALL)
                .onClick(e -> {
                    if (onConfirm != null) {
                        onConfirm.run();
                    }
                })
                .build();

            buttonBox.getChildren().addAll(cancelBtn, okBtn);
            panel.getChildren().add(buttonBox);

            return panel;
        }
    }

    public static Builder create() {
        return new Builder();
    }
}
