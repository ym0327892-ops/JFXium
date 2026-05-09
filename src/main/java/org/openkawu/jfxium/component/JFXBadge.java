package org.openkawu.jfxium.component;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

/**
 * JFXium Badge Component
 * Inspired by Ant Design Badge
 *
 * Usage:
 * <pre>{@code
 * // 数字徽标
 * Node badge = JFXBadge.create()
 *     .content(new Button("Messages"))
 *     .count(5)
 *     .build();
 *
 * // 状态点
 * Node statusBadge = JFXBadge.create()
 *     .content(new Label("Online"))
 *     .status(JFXBadge.Status.SUCCESS)
 *     .build();
 * }</pre>
 */
public class JFXBadge {

    public enum Status {
        SUCCESS, WARNING, ERROR, DEFAULT
    }

    public static Builder create() {
        return new Builder();
    }

    public static class Builder {
        private Node content;
        private int count = 0;
        private boolean dot = false;
        private Status status = null;
        private String style = "";

        private Builder() {}

        public Builder content(Node content) {
            this.content = content;
            return this;
        }

        public Builder count(int count) {
            this.count = count;
            return this;
        }

        public Builder dot(boolean dot) {
            this.dot = dot;
            return this;
        }

        public Builder status(Status status) {
            this.status = status;
            return this;
        }

        public Builder style(String style) {
            this.style = style;
            return this;
        }

        public StackPane build() {
            StackPane badge = new StackPane();
            badge.getStyleClass().add("jfx-badge");

            if (content != null) {
                badge.getChildren().add(content);
            }

            // Add count or dot
            if (count > 0 || dot || status != null) {
                Label indicator = new Label();
                indicator.setAlignment(Pos.CENTER);

                if (count > 0) {
                    indicator.setText(String.valueOf(count));
                    indicator.setStyle(
                        "-fx-background-color: -color-danger-emphasis;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 10px;" +
                        "-fx-padding: 2px 6px;" +
                        "-fx-background-radius: 10px;" +
                        "-fx-min-width: 16px;" +
                        "-fx-min-height: 16px;"
                    );
                    StackPane.setAlignment(indicator, Pos.TOP_RIGHT);
                } else if (dot) {
                    indicator.setPrefSize(8, 8);
                    indicator.setStyle(
                        "-fx-background-color: -color-danger-emphasis;" +
                        "-fx-background-radius: 4px;"
                    );
                    StackPane.setAlignment(indicator, Pos.TOP_RIGHT);
                } else if (status != null) {
                    indicator.setPrefSize(8, 8);
                    String color = getStatusColor(status);
                    indicator.setStyle(
                        "-fx-background-color: " + color + ";" +
                        "-fx-background-radius: 4px;"
                    );
                    StackPane.setAlignment(indicator, Pos.CENTER_LEFT);
                    indicator.setTranslateX(-12);
                }

                badge.getChildren().add(indicator);
            }

            if (!style.isEmpty()) {
                badge.setStyle(style);
            }

            return badge;
        }

        private String getStatusColor(Status status) {
            switch (status) {
                case SUCCESS: return "-color-success-emphasis";
                case WARNING: return "-color-warning-emphasis";
                case ERROR: return "-color-danger-emphasis";
                case DEFAULT:
                default: return "-color-fg-muted";
            }
        }
    }
}