package org.openkawu.jfxium.component.layout;

import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.css.CssClasses;

/**
 * JFXium 分割线组件 - 对标 Ant Design Divider。
 *
 * <h2>修复说明</h2>
 * 原实现（60 行）只是 {@link Separator} 的薄包装，不支持文本，
 * 但 javadoc 与 README 都声称 "{@code .text("OR")}" 可用，属于文档与代码不一致。
 *
 * <h2>本次改动</h2>
 * <ul>
 *   <li>有 text 时返回 {@link HBox}：{@code Separator | Label | Separator}，对齐 Ant Divider</li>
 *   <li>无 text 时仍返回单个 {@link Separator}（保持轻量）</li>
 *   <li>{@code build()} 返回 {@link Node}，统一接口（实际是 {@link Separator} 或 {@link HBox}）</li>
 *   <li>styleClass 接 {@link CssClasses}</li>
 *   <li>新增 {@code orientation()} 显式 API；保留 {@code vertical()} 向下兼容</li>
 *   <li>新增 {@code position()} 控制文本位置（LEFT/CENTER/RIGHT），对齐 Ant Divider {@code orientation} 属性</li>
 * </ul>
 *
 * <h2>使用示例</h2>
 * <pre>{@code
 * // 水平分割线
 * Node d1 = DividerAnt.create().build();
 *
 * // 带文本（默认居中）
 * Node d2 = DividerAnt.create().text("OR").build();
 *
 * // 文本靠左
 * Node d3 = DividerAnt.create().text("章节 1").position(DividerAnt.Position.LEFT).build();
 *
 * // 垂直分割线
 * Node d4 = DividerAnt.create().vertical().build();
 * }</pre>
 */
public class DividerAnt {

    /** 文本位置，对齐 Ant Divider 的 orientation 属性（默认 CENTER）*/
    public enum Position {
        LEFT, CENTER, RIGHT
    }

    public static Builder create() {
        return new Builder();
    }

    public static class Builder extends AbstractStyleBuilder<Builder> {
        private String text = "";
        private Orientation orientation = Orientation.HORIZONTAL;
        private Position position = Position.CENTER;

        private Builder() {}

        public Builder text(String text) {
            this.text = text != null ? text : "";
            return this;
        }

        public Builder orientation(Orientation orientation) {
            this.orientation = orientation;
            return this;
        }

        /** 兼容老 API。等价于 {@code orientation(Orientation.VERTICAL)}。*/
        public Builder vertical() {
            return orientation(Orientation.VERTICAL);
        }

        public Builder position(Position position) {
            this.position = position;
            return this;
        }

        public Node build() {
            // 没有文本时返回轻量 Separator（最常见情况，避免不必要的容器开销）
            if (text.isEmpty()) {
                return buildPlainSeparator();
            }
            // 有文本时返回 HBox: [线段] [文本] [线段]，左右线段宽度根据 position 调整
            // 垂直方向暂不支持文本（Ant Divider 也不支持垂直 + 文本组合）
            if (orientation == Orientation.VERTICAL) {
                System.err.println("[DividerAnt] 垂直方向暂不支持 text，已忽略 text 参数");
                return buildPlainSeparator();
            }
            return buildSeparatorWithText();
        }

        private Separator buildPlainSeparator() {
            Separator separator = new Separator();
            separator.setOrientation(orientation);
            separator.getStyleClass().add(CssClasses.DIVIDER);
            separator.getStyleClass().add(orientation == Orientation.VERTICAL
                    ? CssClasses.DIVIDER_VERTICAL : CssClasses.DIVIDER_HORIZONTAL);
            applyStyles(separator);
            return separator;
        }

        /**
         * 构建"线-文本-线"水平分割线。
         * Ant Design 行为：position=LEFT 时左线段更短（约 5%），右线段长；CENTER 时两边等长；RIGHT 反之。
         * 这里通过左右 Separator 的 Hgrow 比例近似实现。
         */
        private HBox buildSeparatorWithText() {
            HBox box = new HBox(8);
            box.setAlignment(Pos.CENTER);
            box.getStyleClass().addAll(CssClasses.DIVIDER, CssClasses.DIVIDER_HORIZONTAL);

            Separator left = new Separator(Orientation.HORIZONTAL);
            left.getStyleClass().add(CssClasses.DIVIDER_LINE);
            Label label = new Label(text);
            label.getStyleClass().add(CssClasses.DIVIDER_TEXT);
            Separator right = new Separator(Orientation.HORIZONTAL);
            right.getStyleClass().add(CssClasses.DIVIDER_LINE);

            // 左右线段总是一起占据剩余空间。两个 ALWAYS 平均分时是 CENTER；
            // LEFT/RIGHT 时给"短的一侧"min/pref 较小、Hgrow 仍 ALWAYS，让长边自然占大头。
            HBox.setHgrow(left, Priority.ALWAYS);
            HBox.setHgrow(right, Priority.ALWAYS);
            switch (position) {
                case LEFT -> {
                    // 左线段尽量短（视觉上文本贴左）
                    left.setMaxWidth(24);
                    left.setMinWidth(8);
                }
                case RIGHT -> {
                    right.setMaxWidth(24);
                    right.setMinWidth(8);
                }
                case CENTER -> {
                    // 两边等长，使用默认行为
                }
            }

            box.getChildren().addAll(left, label, right);
            applyStyles(box);
            return box;
        }
    }
}
