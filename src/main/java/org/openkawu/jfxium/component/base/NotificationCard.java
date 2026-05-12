package org.openkawu.jfxium.component.base;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;

public class NotificationCard {

    public enum Type {
        SUCCESS, ERROR, WARNING, INFO
    }

    public static class Builder {
        private String title = "";
        private String description = "";
        private Type type = Type.INFO;
        private boolean closable = true;
        private Runnable onClose = null;
        private Node extra = null;
        private String width = "384px";

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder type(Type type) {
            this.type = type;
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

        public Builder extra(Node extra) {
            this.extra = extra;
            return this;
        }

        public Builder content(Node content) {
            this.extra = content;
            return this;
        }

        public Builder width(String width) {
            this.width = width;
            return this;
        }

        public HBox build() {
            HBox card = new HBox(12);
            card.setAlignment(Pos.TOP_LEFT);
            card.setStyle(
                "-fx-background-color: -color-bg-overlay;" +
                "-fx-padding: 16px 24px;" +
                "-fx-background-radius: 8px;" +
                "-fx-border-radius: 8px;" +
                "-fx-border-color: -color-border-default;" +
                "-fx-border-width: 1px;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 8, 0, 0, 2);" +
                "-fx-min-width: " + width + ";" +
                "-fx-max-width: " + width + ";"
            );

            SVGPath icon = new SVGPath();
            icon.setContent(getIconPath(type));
            icon.setStyle("-fx-fill: " + getIconColor(type) + ";");
            icon.setTranslateY(2);
            card.getChildren().add(icon);

            VBox contentBox = new VBox(4);
            HBox.setHgrow(contentBox, Priority.ALWAYS);

            if (!title.isEmpty()) {
                Label titleLabel = new Label(title);
                titleLabel.setStyle("-fx-text-fill: -color-fg-default; -fx-font-size: 16px; -fx-font-weight: 600;");
                contentBox.getChildren().add(titleLabel);
            }

            if (!description.isEmpty()) {
                Label descLabel = new Label(description);
                descLabel.setStyle("-fx-text-fill: -color-fg-muted; -fx-font-size: 14px;");
                descLabel.setWrapText(true);
                contentBox.getChildren().add(descLabel);
            }

            if (extra != null) {
                contentBox.getChildren().add(extra);
            }

            card.getChildren().add(contentBox);

            if (closable && onClose != null) {
                CloseButton closeBtn = new CloseButton(onClose);
                card.getChildren().add(closeBtn);
            }

            return card;
        }

        private String getIconPath(Type type) {
            return switch (type) {
                case SUCCESS -> "M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z";
                case ERROR -> "M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z";
                case WARNING -> "M1 21h22L12 2 1 21zm12-3h-2v-2h2v2zm0-4h-2v-4h2v4z";
                case INFO -> "M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-6h2v6zm0-8h-2V7h2v2z";
            };
        }

        private String getIconColor(Type type) {
            return switch (type) {
                case SUCCESS -> "-color-success-emphasis";
                case ERROR -> "-color-danger-emphasis";
                case WARNING -> "-color-warning-emphasis";
                case INFO -> "-color-accent-emphasis";
            };
        }
    }

    public static Builder create() {
        return new Builder();
    }
}
