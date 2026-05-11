package org.openkawu.jfxium.component;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

/**
 * JFXium 间距组件 - 对标 Ant Design Space
 *
 * 设置组件之间的间距
 *
 * 使用示例：
 * <pre>{@code
 * // 水平间距
 * HBox space = SpaceAnt.create()
 *     .size(16)
 *     .children(node1, node2, node3)
 *     .build();
 *
 * // 垂直间距
 * VBox verticalSpace = SpaceAnt.createVertical()
 *     .size(8)
 *     .children(node1, node2)
 *     .build();
 *
 * // 自动换行
 * HBox wrapSpace = SpaceAnt.create()
 *     .size(12)
 *     .wrap(true)
 *     .children(node1, node2, node3, node4, node5)
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

    public static class Builder {
        private List<Node> children = new ArrayList<>();
        private Direction direction = Direction.HORIZONTAL;
        private double size = 8;
        private Align align = Align.CENTER;
        private boolean wrap = false;
        private boolean split = false;
        private Node splitNode = null;

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

        public Builder wrap(boolean wrap) {
            this.wrap = wrap;
            return this;
        }

        public Builder split(boolean split) {
            this.split = split;
            return this;
        }

        public Builder split(Node splitNode) {
            this.split = true;
            this.splitNode = splitNode;
            return this;
        }

        public Builder children(Node... nodes) {
            for (Node node : nodes) {
                this.children.add(node);
            }
            return this;
        }

        public Node build() {
            if (direction == Direction.VERTICAL) {
                VBox container = new VBox();
                container.getStyleClass().add("space");
                container.setSpacing(size);

                switch (align) {
                    case START -> container.setAlignment(Pos.TOP_LEFT);
                    case END -> container.setAlignment(Pos.BOTTOM_LEFT);
                    case CENTER -> container.setAlignment(Pos.CENTER_LEFT);
                    case BASELINE -> container.setAlignment(Pos.BASELINE_LEFT);
                }

                for (int i = 0; i < children.size(); i++) {
                    container.getChildren().add(children.get(i));
                    if (split && i < children.size() - 1 && splitNode != null) {
                        container.getChildren().add(splitNode);
                    }
                }

                return container;
            } else {
                HBox container = new HBox();
                container.getStyleClass().add("space");
                container.setSpacing(size);

                switch (align) {
                    case START -> container.setAlignment(Pos.CENTER_LEFT);
                    case END -> container.setAlignment(Pos.CENTER_RIGHT);
                    case CENTER -> container.setAlignment(Pos.CENTER);
                    case BASELINE -> container.setAlignment(Pos.BASELINE_CENTER);
                }

                for (int i = 0; i < children.size(); i++) {
                    container.getChildren().add(children.get(i));
                    if (split && i < children.size() - 1 && splitNode != null) {
                        container.getChildren().add(splitNode);
                    }
                }

                return container;
            }
        }
    }

    public static Builder create() {
        return new Builder();
    }

    public static Builder createVertical() {
        return new Builder().direction(Direction.VERTICAL);
    }
}
