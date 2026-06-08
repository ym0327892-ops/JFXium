package org.openkawu.jfxium.component.composite;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.css.JfxStyles;

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
            container.getStyleClass().add(JfxStyles.FLOAT_BUTTON);

            Button button = new Button();
            button.setPrefSize(size, size);
            button.setMinSize(size, size);
            button.setMaxSize(size, size);

            // 类型样式：通过 styleClass 应用 CSS 变量（避免 setStyle 中 CSS 变量导致 ClassCastException）
            button.getStyleClass().add(
                type == Type.PRIMARY ? JfxStyles.FLOAT_BUTTON_PRIMARY : JfxStyles.FLOAT_BUTTON_DEFAULT
            );

            // ── 红线#1 必要例外 ───────────────────────────────────
            // 圆角 = size / 2 是运行时计算值（size 是 Builder 入参），
            // LESS 无法预定义所有可能的动态值。此处只设纯几何属性
            // （border-radius），颜色/背景完全由 LESS styleClass 控制。
            button.setStyle(
                "-fx-background-radius: " + (size / 2) + "px;" +
                "-fx-border-radius: " + (size / 2) + "px;"
            );

            if (icon != null) {
                button.setGraphic(icon);
            }

            // hover 效果已在 LESS :hover 伪类中定义，无需手动处理

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
