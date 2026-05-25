package org.openkawu.jfxium.component;

import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.openkawu.jfxium.core.builder.AbstractStyleBuilder;
import org.openkawu.jfxium.core.css.CssClasses;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * JFXium 间距组件 - 对标 Ant Design Space。
 *
 * <h2>修复说明</h2>
 * 原实现存在 3 个问题：
 * <ul>
 *   <li>styleClass 硬编码字符串 {@code "space"}，未接入 {@link CssClasses}</li>
 *   <li>VBox 分支用 {@code Pos.BASELINE_LEFT}，VBox 无 baseline 概念，会回退为 TOP_LEFT，
 *       与用户预期不符</li>
 *   <li>{@code split(true)} 但未提供 splitNode 时啥也不显示，是隐性 bug</li>
 * </ul>
 *
 * <h2>本次改动</h2>
 * <ul>
 *   <li>所有 styleClass 改用 {@link CssClasses} 常量</li>
 *   <li>VBox 不支持 BASELINE，遇到时降级为 START 并打印告警</li>
 *   <li>{@code split(true)} 但 splitNode 为 null 时，自动渲染一根原生 {@link Separator} 作为分隔</li>
 *   <li>{@code build()} 返回类型从 {@code Node} 改为 {@link Pane}（更具体）</li>
 *   <li>新增 {@code style/styleClass} 钩子，对齐其他 *Ant 组件</li>
 * </ul>
 *
 * <h2>使用示例</h2>
 * <pre>{@code
 * // 水平间距
 * Pane row = SpaceAnt.create()
 *     .size(16)
 *     .children(node1, node2, node3)
 *     .build();
 *
 * // 垂直间距
 * Pane col = SpaceAnt.createVertical()
 *     .size(8)
 *     .children(node1, node2)
 *     .build();
 *
 * // 带分隔线（自动用 Separator）
 * Pane withSplit = SpaceAnt.create()
 *     .size(8)
 *     .split(true)
 *     .children(a, b, c)
 *     .build();
 * }</pre>
 */
public class SpaceAnt {

    public enum Align {
        START, END, CENTER, BASELINE
    }

    public enum Direction {
        HORIZONTAL, VERTICAL
    }

    public static Builder create() {
        return new Builder();
    }

    public static Builder createVertical() {
        return new Builder().direction(Direction.VERTICAL);
    }

    public static class Builder extends AbstractStyleBuilder<Builder> {
        private final List<Node> children = new ArrayList<>();
        private Direction direction = Direction.HORIZONTAL;
        private double size = 8;
        private Align align = Align.CENTER;
        private boolean split = false;
        private Node splitNode;

        private Builder() {}

        public Builder direction(Direction direction) {
            this.direction = direction;
            return this;
        }

        public Builder size(double size) {
            this.size = size;
            return this;
        }

        public Builder align(Align align) {
            this.align = align;
            return this;
        }

        /** 启用分隔模式。如未指定 {@link #split(Node)}，会自动用原生 {@link Separator}。*/
        public Builder split(boolean split) {
            this.split = split;
            return this;
        }

        /** 指定自定义分隔节点（覆盖默认的 Separator）。*/
        public Builder split(Node splitNode) {
            this.split = true;
            this.splitNode = splitNode;
            return this;
        }

        public Builder children(Node... nodes) {
            if (nodes != null) {
                Collections.addAll(this.children, nodes);
            }
            return this;
        }

        public Pane build() {
            return direction == Direction.VERTICAL ? buildVBox() : buildHBox();
        }

        private HBox buildHBox() {
            HBox container = new HBox();
            container.getStyleClass().addAll(CssClasses.SPACE, CssClasses.SPACE_HORIZONTAL);
            container.setSpacing(size);
            container.setAlignment(toHBoxPos(align));
            applyChildrenWithOptionalSplit(container, Direction.HORIZONTAL);
            applyStyles(container);
            return container;
        }

        private VBox buildVBox() {
            VBox container = new VBox();
            container.getStyleClass().addAll(CssClasses.SPACE, CssClasses.SPACE_VERTICAL);
            container.setSpacing(size);
            container.setAlignment(toVBoxPos(align));
            applyChildrenWithOptionalSplit(container, Direction.VERTICAL);
            applyStyles(container);
            return container;
        }

        /**
         * 把 children 按顺序加入容器，启用 split 时在每两个相邻子节点之间插入分隔节点。
         * splitNode 为 null 时使用原生 Separator（方向跟随容器方向），避免静默失效。
         */
        private void applyChildrenWithOptionalSplit(Pane container, Direction containerDir) {
            for (int i = 0; i < children.size(); i++) {
                container.getChildren().add(children.get(i));
                boolean isLast = i == children.size() - 1;
                if (split && !isLast) {
                    container.getChildren().add(makeSplit(containerDir));
                }
            }
        }

        private Node makeSplit(Direction containerDir) {
            if (splitNode != null) {
                return splitNode;
            }
            // 默认分隔：与容器方向垂直的 Separator（水平容器配垂直分隔线，反之亦然）
            Separator sep = new Separator(
                    containerDir == Direction.HORIZONTAL ? Orientation.VERTICAL : Orientation.HORIZONTAL);
            sep.getStyleClass().add(CssClasses.SPACE_SPLIT);
            return sep;
        }

        // HBox 支持 BASELINE，VBox 不支持
        private static Pos toHBoxPos(Align a) {
            return switch (a) {
                case START -> Pos.CENTER_LEFT;
                case END -> Pos.CENTER_RIGHT;
                case CENTER -> Pos.CENTER;
                case BASELINE -> Pos.BASELINE_LEFT;
            };
        }

        private static Pos toVBoxPos(Align a) {
            // VBox 没有 baseline 概念。BASELINE 在垂直容器里没有意义，
            // 这里降级为 START 并打印一次告警，让调用方知道行为不会按预期。
            return switch (a) {
                case START -> Pos.TOP_LEFT;
                case END -> Pos.BOTTOM_LEFT;
                case CENTER -> Pos.CENTER_LEFT;
                case BASELINE -> {
                    System.err.println("[SpaceAnt] Align.BASELINE 在垂直方向无效，已降级为 START");
                    yield Pos.TOP_LEFT;
                }
            };
        }
    }
}
