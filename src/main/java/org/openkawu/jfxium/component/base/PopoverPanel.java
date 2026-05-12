package org.openkawu.jfxium.component.base;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class PopoverPanel {

    public static class Builder {
        private String title = "";
        private Node content = null;
        private boolean closable = false;
        private Runnable onClose = null;
        private String minWidth = "200px";
        private String maxWidth = "300px";

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder content(Node content) {
            this.content = content;
            return this;
        }

        public Builder closable(boolean closable) {
            this.closable = closable;
            return this;
        }

        public Builder onClose(Runnable onClose) {
            this.onClose = onClose;
            return this;
        }

        public Builder minWidth(String minWidth) {
            this.minWidth = minWidth;
            return this;
        }

        public Builder maxWidth(String maxWidth) {
            this.maxWidth = maxWidth;
            return this;
        }

        public VBox build() {
            VBox panel = new VBox(0);
            panel.setStyle(
                "-fx-background-color: -color-bg-overlay;" +
                "-fx-background-radius: 8px;" +
                "-fx-border-radius: 8px;" +
                "-fx-border-color: -color-border-default;" +
                "-fx-border-width: 1px;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 8, 0, 0, 2);" +
                "-fx-min-width: " + minWidth + ";" +
                "-fx-max-width: " + maxWidth + ";"
            );

            if (!title.isEmpty()) {
                VBox titleBox = new VBox(0);
                titleBox.setAlignment(Pos.CENTER_LEFT);
                titleBox.setStyle("-fx-padding: 12px 16px; -fx-border-color: transparent transparent -color-border-muted transparent; -fx-border-width: 0 0 1px 0;");

                if (closable && onClose != null) {
                    HBox titleRow = new HBox();
                    titleRow.setAlignment(Pos.CENTER_LEFT);
                    HBox.setHgrow(titleRow, Priority.ALWAYS);

                    Label titleLabel = new Label(title);
                    titleLabel.setStyle("-fx-text-fill: -color-fg-default; -fx-font-size: 14px; -fx-font-weight: 600;");
                    HBox.setHgrow(titleLabel, Priority.ALWAYS);
                    titleRow.getChildren().add(titleLabel);

                    CloseButton closeBtn = new CloseButton(onClose);
                    titleRow.getChildren().add(closeBtn);
                    titleBox.getChildren().add(titleRow);
                } else {
                    Label titleLabel = new Label(title);
                    titleLabel.setStyle("-fx-text-fill: -color-fg-default; -fx-font-size: 14px; -fx-font-weight: 600;");
                    titleBox.getChildren().add(titleLabel);
                }
                panel.getChildren().add(titleBox);
            }

            if (content != null) {
                VBox contentBox = new VBox(content);
                contentBox.setStyle("-fx-padding: 12px 16px;");
                panel.getChildren().add(contentBox);
            }

            return panel;
        }
    }

    public static Builder create() {
        return new Builder();
    }
}
