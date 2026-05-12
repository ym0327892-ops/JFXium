package org.openkawu.jfxium.component.base;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;

public class AlertBanner {

    public enum Type {
        SUCCESS, INFO, WARNING, ERROR
    }

    public static class Builder {
        private Type type = Type.INFO;
        private String message = "";
        private Node content = null;
        private Node action = null;
        private boolean closable = false;
        private boolean banner = false;
        private Runnable onClose = null;

        public Builder type(Type type) {
            this.type = type;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder content(Node content) {
            this.content = content;
            return this;
        }

        public Builder action(Node action) {
            this.action = action;
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

        public Builder banner(boolean banner) {
            this.banner = banner;
            return this;
        }

        public HBox build() {
            HBox alert = new HBox(12);
            alert.setAlignment(Pos.CENTER_LEFT);
            alert.setStyle(getAlertStyle());

            SVGPath icon = new SVGPath();
            icon.setContent(getIconPath(type));
            icon.setStyle("-fx-fill: " + getIconColor(type) + ";");
            icon.setTranslateY(2);
            alert.getChildren().add(icon);

            VBox contentBox = new VBox(4);
            HBox.setHgrow(contentBox, Priority.ALWAYS);

            if (!message.isEmpty()) {
                Label messageLabel = new Label(message);
                messageLabel.setStyle("-fx-text-fill: -color-fg-default; -fx-font-size: 14px;");
                contentBox.getChildren().add(messageLabel);
            }

            if (content != null) {
                contentBox.getChildren().add(content);
            }

            alert.getChildren().add(contentBox);

            if (action != null) {
                HBox actionBox = new HBox(action);
                actionBox.setAlignment(Pos.CENTER_RIGHT);
                alert.getChildren().add(actionBox);
            }

            if (closable && onClose != null) {
                CloseButton closeBtn = new CloseButton(onClose);
                alert.getChildren().add(closeBtn);
            }

            return alert;
        }

        private String getAlertStyle() {
            StringBuilder style = new StringBuilder();
            style.append("-fx-background-color: " + getBgColor(type) + ";");
            style.append("-fx-padding: 16px 24px;");
            
            if (banner) {
                style.append("-fx-background-radius: 0;");
            } else {
                style.append("-fx-background-radius: 8px;");
                style.append("-fx-border-radius: 8px;");
                style.append("-fx-border-color: " + getBorderColor(type) + ";");
                style.append("-fx-border-width: 1px;");
            }
            
            return style.toString();
        }

        private String getIconPath(Type type) {
            return switch (type) {
                case SUCCESS -> "M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z";
                case INFO -> "M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-6h2v6zm0-8h-2V7h2v2z";
                case WARNING -> "M1 21h22L12 2 1 21zm12-3h-2v-2h2v2zm0-4h-2v-4h2v4z";
                case ERROR -> "M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z";
            };
        }

        private String getIconColor(Type type) {
            return switch (type) {
                case SUCCESS -> "-color-success";
                case INFO -> "-color-accent";
                case WARNING -> "-color-warning";
                case ERROR -> "-color-danger";
            };
        }

        private String getBgColor(Type type) {
            return switch (type) {
                case SUCCESS -> "-color-success-bg";
                case INFO -> "-color-accent-bg";
                case WARNING -> "-color-warning-bg";
                case ERROR -> "-color-danger-bg";
            };
        }

        private String getBorderColor(Type type) {
            return switch (type) {
                case SUCCESS -> "-color-success-border";
                case INFO -> "-color-accent-border";
                case WARNING -> "-color-warning-border";
                case ERROR -> "-color-danger-border";
            };
        }
    }

    public static Builder create() {
        return new Builder();
    }
}
