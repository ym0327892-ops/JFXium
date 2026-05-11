package org.openkawu.jfxium.component;

import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

/**
 * JFXium Alert Component
 * Inspired by Ant Design Alert
 *
 * Usage:
 * <pre>{@code
 * // 成功提示
 * Node alert = AlertAnt.success("操作成功", "数据已成功保存到数据库。").build();
 *
 * // 错误提示
 * Node alert = AlertAnt.error("操作失败", "请检查网络连接后重试。").closable(true).build();
 *
 * // 警告提示
 * Node alert = AlertAnt.warning("注意", "此操作不可撤销。").build();
 *
 * // 信息提示
 * Node alert = AlertAnt.info("提示", "请仔细阅读使用说明。").build();
 * }</pre>
 */
public class AlertAnt {

    public enum Type {
        SUCCESS, INFO, WARNING, ERROR
    }

    public static Builder success(String message) {
        return new Builder(Type.SUCCESS, "Success", message);
    }

    public static Builder success(String title, String message) {
        return new Builder(Type.SUCCESS, title, message);
    }

    public static Builder info(String message) {
        return new Builder(Type.INFO, "Info", message);
    }

    public static Builder info(String title, String message) {
        return new Builder(Type.INFO, title, message);
    }

    public static Builder warning(String message) {
        return new Builder(Type.WARNING, "Warning", message);
    }

    public static Builder warning(String title, String message) {
        return new Builder(Type.WARNING, title, message);
    }

    public static Builder error(String message) {
        return new Builder(Type.ERROR, "Error", message);
    }

    public static Builder error(String title, String message) {
        return new Builder(Type.ERROR, title, message);
    }

    public static class Builder {
        private final Type type;
        private final String title;
        private final String message;
        private boolean closable = false;
        private boolean showIcon = true;
        private boolean banner = false;
        private Node action = null;
        private Runnable onClose;
        private String style = "";
        private final List<String> extraStyleClasses = new ArrayList<>();

        private Builder(Type type, String title, String message) {
            this.type = type;
            this.title = title;
            this.message = message;
        }

        public Builder closable(boolean closable) {
            this.closable = closable;
            return this;
        }

        public Builder showIcon(boolean show) {
            this.showIcon = show;
            return this;
        }

        /**
         * 顶部公告模式 - 全宽显示，默认 warning 类型
         * 对标 Ant Design banner 属性
         */
        public Builder banner(boolean banner) {
            this.banner = banner;
            return this;
        }

        public Builder banner() {
            return banner(true);
        }

        /**
         * 自定义操作区域 - 右上角操作按钮
         * 对标 Ant Design action 属性
         */
        public Builder action(Node action) {
            this.action = action;
            return this;
        }

        public Builder onClose(Runnable handler) {
            this.onClose = handler;
            return this;
        }

        public Builder style(String style) {
            this.style = style;
            return this;
        }

        public Builder styleClass(String styleClass) {
            this.extraStyleClasses.add(styleClass);
            return this;
        }

        public VBox build() {
            VBox alert = new VBox(4);
            alert.setPadding(new Insets(12, 16, 12, 16));
            alert.setAlignment(Pos.CENTER_LEFT);

            // Get colors based on type
            String bgColor = getBackgroundColor(type);
            String borderColor = getBorderColor(type);
            String textColor = getTextColor(type);
            String icon = getIcon(type);

            // Style
            alert.setStyle(
                "-fx-background-color: " + bgColor + ";" +
                "-fx-border-color: " + borderColor + ";" +
                "-fx-border-width: 1px;" +
                "-fx-border-radius: 6px;" +
                "-fx-background-radius: 6px;"
            );

            // Banner 模式 - 全宽显示，无边框圆角
            if (banner) {
                alert.setStyle(
                    "-fx-background-color: " + bgColor + ";" +
                    "-fx-border-width: 0;" +
                    "-fx-border-radius: 0;" +
                    "-fx-background-radius: 0;"
                );
                HBox.setHgrow(alert, Priority.ALWAYS);
            }

            // Header with icon and title
            HBox header = new HBox(8);
            header.setAlignment(Pos.CENTER_LEFT);

            if (showIcon) {
                Label iconLabel = new Label(icon);
                iconLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: " + textColor + ";");
                header.getChildren().add(iconLabel);
            }

            Label titleLabel = new Label(title);
            titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: 600; -fx-text-fill: " + textColor + ";");
            header.getChildren().add(titleLabel);

            // Action 区域 - 右上角操作按钮，对标 Ant Design action
            if (action != null) {
                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);
                header.getChildren().addAll(spacer, action);
            }

            // Close button
            if (closable) {
                if (action == null) {
                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);
                    header.getChildren().add(spacer);
                }

                javafx.scene.control.Button closeBtn = new javafx.scene.control.Button("✕");
                closeBtn.setStyle(
                    "-fx-background-color: transparent;" +
                    "-fx-text-fill: " + textColor + ";" +
                    "-fx-font-size: 12px;" +
                    "-fx-padding: 0;" +
                    "-fx-min-width: 20px;" +
                    "-fx-min-height: 20px;" +
                    "-fx-cursor: hand;"
                );
                closeBtn.setOnAction(e -> {
                    // Fade out animation
                    FadeTransition fadeOut = new FadeTransition(Duration.millis(200), alert);
                    fadeOut.setFromValue(1);
                    fadeOut.setToValue(0);
                    fadeOut.setOnFinished(event -> {
                        if (alert.getParent() instanceof javafx.scene.layout.Pane) {
                            ((javafx.scene.layout.Pane) alert.getParent()).getChildren().remove(alert);
                        }
                        if (onClose != null) {
                            onClose.run();
                        }
                    });
                    fadeOut.play();
                });

                header.getChildren().add(closeBtn);
            }

            alert.getChildren().add(header);

            // Message
            if (message != null && !message.isEmpty()) {
                Label messageLabel = new Label(message);
                messageLabel.setWrapText(true);
                messageLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: " + textColor + ";");
                if (showIcon) {
                    messageLabel.setPadding(new Insets(0, 0, 0, 24));
                }
                alert.getChildren().add(messageLabel);
            }

            alert.getStyleClass().add("jfx-alert");
            alert.getStyleClass().addAll(extraStyleClasses);

            if (!style.isEmpty()) {
                alert.setStyle(alert.getStyle() + style);
            }

            return alert;
        }

        private String getBackgroundColor(Type type) {
            switch (type) {
                case SUCCESS: return "-color-success-subtle";
                case WARNING: return "-color-warning-subtle";
                case ERROR: return "-color-danger-subtle";
                case INFO:
                default: return "-color-accent-subtle";
            }
        }

        private String getBorderColor(Type type) {
            switch (type) {
                case SUCCESS: return "-color-success-muted";
                case WARNING: return "-color-warning-muted";
                case ERROR: return "-color-danger-muted";
                case INFO:
                default: return "-color-accent-muted";
            }
        }

        private String getTextColor(Type type) {
            switch (type) {
                case SUCCESS: return "-color-success-emphasis";
                case WARNING: return "-color-warning-emphasis";
                case ERROR: return "-color-danger-emphasis";
                case INFO:
                default: return "-color-accent-emphasis";
            }
        }

        private String getIcon(Type type) {
            switch (type) {
                case SUCCESS: return "✓";
                case WARNING: return "⚠";
                case ERROR: return "✕";
                case INFO:
                default: return "ℹ";
            }
        }
    }
}