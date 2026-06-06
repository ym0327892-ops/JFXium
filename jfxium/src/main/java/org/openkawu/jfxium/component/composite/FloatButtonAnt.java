package org.openkawu.jfxium.component.composite;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;

/**
 * JFXium 悬浮按钮组件 - 对标 Ant Design FloatButton
 *
 * 固定在页面右下角的快捷操作按钮
 *
 * 使用示例：
 * <pre>{@code
 * // 基础悬浮按钮
 * Node floatBtn = FloatButtonAnt.create()
 *     .icon(new Label("+"))
 *     .onClick(() -> System.out.println("点击了悬浮按钮"))
 *     .build();
 *
 * // 带类型的悬浮按钮
 * Node floatBtn = FloatButtonAnt.create()
 *     .icon(new Label("↑"))
 *     .type(FloatButtonAnt.Type.PRIMARY)
 *     .tooltip("回到顶部")
 *     .build();
 * }</pre>
 */
public class FloatButtonAnt {

    public enum Type {
        DEFAULT, PRIMARY
    }

    public static class Builder extends AbstractStyleBuilder<Builder> {
        private Node icon;
        private String tooltip = null;
        private Type type = Type.DEFAULT;
        private Runnable onClick = null;
        private double size = 56;

        public Builder icon(Node icon) {
            this.icon = icon;
            return this;
        }

        public Builder tooltip(String tooltip) {
            this.tooltip = tooltip;
            return this;
        }

        public Builder type(Type type) {
            this.type = type;
            return this;
        }

        public Builder onClick(Runnable onClick) {
            this.onClick = onClick;
            return this;
        }

        public Builder size(double size) {
            this.size = size;
            return this;
        }

        public StackPane build() {
            StackPane container = new StackPane();
            container.setAlignment(Pos.CENTER);
            container.getStyleClass().add("float-button");

            Button button = new Button();
            button.setPrefSize(size, size);
            button.setMinSize(size, size);
            button.setMaxSize(size, size);

            String bgColor = type == Type.PRIMARY ? "-color-accent-emphasis" : "-color-bg-default";
            String textColor = type == Type.PRIMARY ? "-color-fg-on-emphasis" : "-color-fg-default";
            String borderColor = type == Type.PRIMARY ? "transparent" : "-color-border-default";

            button.setStyle(
                "-fx-background-color: " + bgColor + ";" +
                "-fx-text-fill: " + textColor + ";" +
                "-fx-border-color: " + borderColor + ";" +
                "-fx-border-width: 1px;" +
                "-fx-background-radius: " + (size / 2) + "px;" +
                "-fx-border-radius: " + (size / 2) + "px;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 6, 0, 0, 2);"
            );

            if (icon != null) {
                button.setGraphic(icon);
            }

            button.setOnMouseEntered(e -> {
                button.setStyle(button.getStyle().replace(
                    "dropshadow(gaussian, rgba(0,0,0,0.15), 6, 0, 0, 2)",
                    "dropshadow(gaussian, rgba(0,0,0,0.25), 8, 0, 0, 4)"
                ));
            });

            button.setOnMouseExited(e -> {
                button.setStyle(button.getStyle().replace(
                    "dropshadow(gaussian, rgba(0,0,0,0.25), 8, 0, 0, 4)",
                    "dropshadow(gaussian, rgba(0,0,0,0.15), 6, 0, 0, 2)"
                ));
            });

            if (onClick != null) {
                button.setOnAction(e -> onClick.run());
            }

            if (tooltip != null) {
                javafx.scene.control.Tooltip t = new javafx.scene.control.Tooltip(tooltip);
                javafx.scene.control.Tooltip.install(button, t);
            }

            container.getChildren().add(button);
            return container;
        }
    }

    public static Builder create() {
        return new Builder();
    }
}
