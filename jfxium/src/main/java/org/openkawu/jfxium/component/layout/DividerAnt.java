package org.openkawu.jfxium.component.layout;

import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.css.JfxStyles;

import java.util.logging.Logger;

/**
 * JFXium 分割线组件 - 对标 Ant Design Divider。
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

    private static final Logger LOGGER = Logger.getLogger(DividerAnt.class.getName());

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
            this.orientation = orientation != null ? orientation : Orientation.HORIZONTAL;
            return this;
        }

        /** 设置为垂直方向分割线。等价于 {@code orientation(Orientation.VERTICAL)}。*/
        public Builder vertical() {
            return orientation(Orientation.VERTICAL);
        }

        public Builder position(Position position) {
            this.position = position != null ? position : Position.CENTER;
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
                LOGGER.warning("垂直方向暂不支持 text，已忽略 text 参数");
                return buildPlainSeparator();
            }
            return buildSeparatorWithText();
        }

        private Separator buildPlainSeparator() {
            Separator separator = new Separator();
            separator.setOrientation(orientation);
            separator.getStyleClass().addAll(JfxStyles.JFX_SEPARATOR, JfxStyles.DIVIDER);
            separator.getStyleClass().add(orientation == Orientation.VERTICAL
                    ? JfxStyles.DIVIDER_VERTICAL : JfxStyles.DIVIDER_HORIZONTAL);
            applyStyles(separator);
            return separator;
        }

        /**
         * 构建"线-文本-线"水平分割线。
         * Ant Design 行为：position=LEFT 时左线段更短（约 5%），右线段长；CENTER 时两边等长；RIGHT 反之。
         * 这里通过左右 Separator 的 Hgrow 比例近似实现。
         */
        private HBox buildSeparatorWithText() {
            HBox box = new HBox();
            box.setAlignment(Pos.CENTER);
            box.getStyleClass().addAll(JfxStyles.DIVIDER, JfxStyles.DIVIDER_HORIZONTAL);

            Separator left = new Separator(Orientation.HORIZONTAL);
            left.getStyleClass().addAll(JfxStyles.JFX_SEPARATOR, JfxStyles.DIVIDER_LINE);
            Label label = new Label(text);
            label.getStyleClass().add(JfxStyles.DIVIDER_TEXT);
            Separator right = new Separator(Orientation.HORIZONTAL);
            right.getStyleClass().addAll(JfxStyles.JFX_SEPARATOR, JfxStyles.DIVIDER_LINE);

            // 左右线段总是一起占据剩余空间。两个 ALWAYS 平均分时是 CENTER；
            // LEFT/RIGHT 时给"短的一侧"min/pref 较小、Hgrow 仍 ALWAYS，让长边自然占大头。
            HBox.setHgrow(left, Priority.ALWAYS);
            HBox.setHgrow(right, Priority.ALWAYS);
            switch (position) {
                case LEFT -> {
                    // 左线段尽量短（视觉上文本贴左）
                    HBox.setHgrow(left, Priority.SOMETIMES);
                }
                case RIGHT -> {
                    HBox.setHgrow(right, Priority.SOMETIMES);
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
